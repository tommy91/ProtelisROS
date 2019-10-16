package com.github.rosjava.prj_pkg.prj;

import java.lang.reflect.Type;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.util.CodePath;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ParametersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ServicesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Abstraction of networking used by the virtual machine: at each execution round, the VM needs
 * to be able to access the most recent state received from neighbors and to be able to update
 * the state that it is exporting to neighbors.
 * 
 * Note, however, that there is no requirement that state actually be sent or received in each
 * round: it is up to the individual implementation of a NetworkManager to best optimize in order
 * to best trade off between effective state sharing and efficiency.
 */

public class PrjNetworkManager implements NetworkManager {
	/** 
	 * The protelis state will be shared every 'shareEveryNRounds' rounds of protelis, starting from round 1
	 * meaning that if shareEveryNRounds=5 then the state will be shared at rounds 1,6,11,16,.. 
	 */
	private Integer share_state_every_n_rounds;
	private int rounds_counter = 0;

	private final Map<DeviceUID, Map<CodePath, Object>> receiveCache = new HashMap<>();
	
	/* Using enableComplexMapKeySerialization() to prevent using default toString() on map keys */
	private Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();
	
	private PrjNode prjNode;
	private ConnectedNode connectedNode;
	private ServicesManager servicesManager;
	
	private PrjUID deviceUID;
	private String mc_group;
	
	/* Adhoc_communication node variables */
	private String adhoc_communication_prefix;
	private String adhoc_communication_node_name;
	private String adhoc_communication_mc_prefix;
	private String topic_new_robot_suffix;
	private String topic_remove_robot_suffix;
	/** 
	 * Topic where adhoc_communication node will publish the state received from the other nodes.
	 * You need to pass this topic in the request when sending your state. 
	 **/
	private String topic_state_suffix;
	
	/* Adhoc_communication mc_group status variables */
	private Boolean activated;
	private Boolean connected;
	private List<String> downlinks;
	private Boolean joining;
	private Boolean member;
	private Boolean root;
	private String route_uplink;
	
	Subscriber<std_msgs.String> subscriber_new_robot;
	Subscriber<std_msgs.String> subscriber_remove_robot;
	Subscriber<adhoc_communication.RecvString> subscriber_state;
	
	public PrjUID setup(PrjNode prjNode) {
		this.prjNode = prjNode;
		
		/* Read the input parameters from the Parameter Server */
		readInputParameters();
		
		connectedNode = prjNode.getConnectedNode();
		servicesManager = prjNode.getServicesManager();
		
		adhoc_communication_prefix = prjNode.getNamespace().toString() + "/" + adhoc_communication_node_name + "/";
		
		/** 
		 * Setup publisher and listener for new neighbors
		 * the adhoc_communication module is waiting for subscribers to new robot and remove robot
		 * so call this two functions first!
		 */
		setupNewRobotListener();
		setupRemoveRobotListener();
		
		/* Next we can get the id and setup the state listener */
		setupDeviceUID();
		setupStateListener();

		updateMcGroupState();
		
		return deviceUID;
	}
	
	private void readInputParameters() {
		prjNode.logInfo("Reading the parameters:");
		ParametersManager mpm = prjNode.getParametersManager();
		share_state_every_n_rounds = mpm.getIntegerParam("~adhoc_communication/share_state_every_n_rounds");
		if (share_state_every_n_rounds == null) {
			prjNode.exitWithError("Parameter share_state_every_n_rounds is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("share_state_every_n_rounds: " + share_state_every_n_rounds.toString());
		adhoc_communication_node_name = mpm.getStringParam("~adhoc_communication/adhoc_communication_node_name");
		if (adhoc_communication_node_name == null) {
			prjNode.exitWithError("Parameter adhoc_communication_node_name is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("adhoc_communication_node_name: " + adhoc_communication_node_name);
		adhoc_communication_mc_prefix = mpm.getStringParam("~adhoc_communication/adhoc_communication_mc_prefix");
		if (adhoc_communication_mc_prefix == null) {
			prjNode.exitWithError("Parameter adhoc_communication_mc_prefix is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("adhoc_communication_mc_prefix: " + adhoc_communication_mc_prefix);
		topic_new_robot_suffix = mpm.getStringParam("~adhoc_communication/topic_new_robot_suffix");
		if (topic_new_robot_suffix == null) {
			prjNode.exitWithError("Parameter topic_new_robot_suffix is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("topic_new_robot_suffix: " + topic_new_robot_suffix);
		topic_remove_robot_suffix = mpm.getStringParam("~adhoc_communication/topic_remove_robot_suffix");
		if (topic_remove_robot_suffix == null) {
			prjNode.exitWithError("Parameter topic_remove_robot_suffix is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("topic_remove_robot_suffix: " + topic_remove_robot_suffix);
		topic_state_suffix = mpm.getStringParam("~adhoc_communication/topic_state_suffix");
		if (topic_state_suffix == null) {
			prjNode.exitWithError("Parameter topic_state_suffix is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("topic_state_suffix: " + topic_state_suffix);
	}
	
	private void setupDeviceUID() {
		adhoc_communication.GetIDRequest getIDReq = servicesManager.get_get_id().newRequest();
		String uid = servicesManager.get_get_id().callSynch(getIDReq, 1000).getId();
		deviceUID = new PrjUID(uid);
		prjNode.logInfo("The deviceUID is " + deviceUID.toString());
		mc_group = adhoc_communication_mc_prefix + deviceUID.toString();
		prjNode.logInfo("The mc group is " + mc_group);
	}
	
	public boolean joinMcGroup(String group) {
		prjNode.logInfo("Sending request to join group " + group);
		adhoc_communication.ChangeMCMembershipRequest request = servicesManager.get_join_mc_group().newRequest();
		request.setAction((byte)1);
		request.setGroupName(group);
		int response = servicesManager.get_join_mc_group().callSynch(request, 1000).getStatus();
		if (response == 1) {
			prjNode.logInfo("Group joined");
			updateMcGroupState();
			return true;
		}
		else {	
			prjNode.logError("Error: " + Integer.toString(response));
			return false;
		}
	}
	
	public List<String> getNeighbors(String group) {
		prjNode.logInfo("Getting neighbors..");
		adhoc_communication.GetNeighborsRequest request = servicesManager.get_get_neighbors().newRequest();
		List<String> response = servicesManager.get_get_neighbors().callSynch(request, 1000).getNeighbors();
		return response;
	}
	
	public PrjUID getUID() {
		return deviceUID;
	}
	
	private void setupNewRobotListener() {
		subscriber_new_robot = connectedNode.newSubscriber(adhoc_communication_prefix + topic_new_robot_suffix,
				std_msgs.String._TYPE);
		subscriber_new_robot.addMessageListener(new MessageListener<std_msgs.String>() {
			/**
			 * A new robot message means that adhoc_communication heard a new neighbor or reconnect to an old one
			 * so when we receive one, we want to join that neighbor multicast group so we can receive his state.
			 * He will do the same thing when he heard us. 
			 */
			@Override
			public void onNewMessage(std_msgs.String msg) {
				String neighbor = msg.getData();
				prjNode.logInfo("Heard new neighbor: " + neighbor);
				String neighbor_mc_group = adhoc_communication_mc_prefix + neighbor;
				joinMcGroup(neighbor_mc_group);
			}
		});
	}
	
	private void setupRemoveRobotListener() {
		subscriber_remove_robot = connectedNode.newSubscriber(adhoc_communication_prefix + topic_remove_robot_suffix,
				std_msgs.String._TYPE);
		subscriber_remove_robot.addMessageListener(new MessageListener<std_msgs.String>() {
			/**
			 * A remove robot message for a neighbor means that adhoc_communication is not receiving beacons 
			 * from that neighbor anymore, so we can remove his state because it could be outdated 
			 */
			@Override
			public void onNewMessage(std_msgs.String msg) {
				String neighbor = msg.getData();
				prjNode.logInfo("Neighbor to remove: " + neighbor);
				PrjUID neighborUID = new PrjUID(neighbor);
				if (receiveCache.remove(neighborUID) == null) {
					prjNode.logError("Neighbor '" + neighbor + "' not found");
				}
				updateMcGroupState();
			}
		});
	}

	private void setupStateListener() {
		subscriber_state = connectedNode.newSubscriber(adhoc_communication_prefix + topic_state_suffix,
				adhoc_communication.RecvString._TYPE);
		subscriber_state.addMessageListener(new MessageListener<adhoc_communication.RecvString>() {
			/**
			 * When we receive a new state from a neighbor we need to unserialize it and 
			 * put it in the receive cache, eventually updating the old one 
			 */
			@Override
			public void onNewMessage(adhoc_communication.RecvString msg) {
				prjNode.logInfo("Receiving state from neighbor");
				String neighbor = msg.getSrcRobot();
				PrjUID neighborUID = new PrjUID(neighbor);
				String gsonState = msg.getData();
				prjNode.logInfo("Neighbor: " + neighbor);
				prjNode.logInfo("State: " + gsonState);
				Type typeOfState = new TypeToken<LinkedHashMap<CodePath, Object>>() { }.getType();
				Map<CodePath, Object> state = gson.fromJson(gsonState, typeOfState);
				receiveCache.put(neighborUID, state);
			}
		});
	}
	
	public void printNeighbors() {
		String neighborsUIDs = "";
		for (DeviceUID neighbor : receiveCache.keySet()) {
			neighborsUIDs += neighbor.toString() + "  ";
		}
		prjNode.logInfo("Neighbors: " + neighborsUIDs);
	}
	
	/**
	 * Called by {@link ProtelisVM} during execution to collect the most recent information available 
	 * from neighbors.  The call is serial within the execution, so this should probably poll 
	 * state maintained by a separate thread, rather than gathering state during this call.
	 * @return A map associating each neighbor with its shared state.  
	 * 		The object returned should not be modified, and {@link ProtelisVM} will not change it either.
	 */
	@Override
	public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
		return receiveCache;
	}

	/**
	 * Called by {@link ProtelisVM} during execution to send its current shared state to neighbors.
	 * The call is serial within the execution, so this should probably queue up a message to
	 * be sent, rather than actually carrying out a lengthy operations during this call.
	 * @param toSend 
	 * 		Shared state to be transmitted to neighbors.
	 */
	@Override
	public void shareState(Map<CodePath, Object> toSend) {
		if (rounds_counter++ == 0) {
			// prjNode.logInfo("send state to neighbors (" + mc_group + ")");
			Type typeOfState = new TypeToken<LinkedHashMap<CodePath, Object>>() { }.getType();
			String gsonState = gson.toJson(toSend, typeOfState);
			sendMcString(gsonState, mc_group, topic_state_suffix);
		}
		/* Keeping rounds_counter small to prevent overflows when an high number of rounds is needed */
		rounds_counter %= share_state_every_n_rounds;
	}
	
	public void sendMcString(String message, String group, String topic_suffix) {
		adhoc_communication.SendStringRequest req = servicesManager.get_send_string().newRequest();
		req.setDstRobot(group);
		req.setData(message);
		req.setTopic(topic_suffix);
		servicesManager.get_send_string().call(req);
	}
	
	public void broadcastString(String message, Byte hop_limit, String topic_suffix) {
		adhoc_communication.BroadcastStringRequest req = servicesManager.get_broadcast_string().newRequest();
		req.setData(message);
		req.setHopLimit(hop_limit);
		req.setTopic(topic_suffix);
		servicesManager.get_broadcast_string().call(req);
	}
	
	public adhoc_communication.GetGroupStateResponse getMcGroupState(String group) {
		adhoc_communication.GetGroupStateRequest req = servicesManager.get_get_group_state().newRequest();
		req.setGroupName(group);
		return servicesManager.get_get_group_state().callSynch(req, 1000);
	}
	
	private void updateMcGroupState() {
		adhoc_communication.GetGroupStateResponse response = getMcGroupState(mc_group);
		activated = response.getActivated();
		connected = response.getConnected();
		downlinks = response.getDownlinks();
		joining = response.getJoining();
		member = response.getMember();
		root = response.getRoot();
		route_uplink = response.getRouteUplink();
		prjNode.logInfo("Updating '" + mc_group + "' state:");
		prjNode.logInfo("activated: " + Boolean.toString(activated));
		prjNode.logInfo("connected: " + Boolean.toString(connected));
		prjNode.logInfo("downlinks: " + downlinks.toString());
		prjNode.logInfo("joining: " + Boolean.toString(joining));
		prjNode.logInfo("member: " + Boolean.toString(member));
		prjNode.logInfo("root: " + Boolean.toString(root));
		prjNode.logInfo("route uplink: " + route_uplink);
	}
	
	public void shutDownAdhocCommunicationNode() {
		adhoc_communication.ShutDownRequest req = servicesManager.get_shut_down().newRequest();
		servicesManager.get_shut_down().call(req);
	}

}

