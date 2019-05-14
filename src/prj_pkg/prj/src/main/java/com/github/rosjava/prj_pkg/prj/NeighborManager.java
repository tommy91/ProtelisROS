package com.github.rosjava.prj_pkg.prj;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.protelis.lang.datatype.DeviceUID;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import com.google.gson.Gson;

public class NeighborManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	
	private DeviceUID deviceUID;
	private String deviceStringID;
	private String deviceGsonID;
	private Gson gson = new Gson();
	
	// My neighbors
	private Set<DeviceUID> neighbors = new HashSet<>();

	private final int discoverySleepTime = 30000;
	
	private String listenerTopic;
	private final String listenerTopicPrefix = "prj_state_node_";
	private final String neighborDiscoveryTopic = "prj_neighbor_discovery";
	
	// Neighbors publishers variables
	private Publisher<std_msgs.String> neighborDiscoveryPublisher;
	private CancellableLoop neighborDiscoveryPublisherLoop;
	private Map<DeviceUID, Publisher<rosjava_custom_msg.StateWithID>> publishers = new HashMap<>();
	
	public NeighborManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		this.connectedNode = prjNode.getConnectedNode();
		
		deviceUID = prjNode.getDeviceUID();
		deviceGsonID = gson.toJson(deviceUID);
		deviceStringID = prjNode.getDeviceStringID();
		
		// Setup publisher and listener for new neighbors
		setupNeighborDiscovery();
		
		// Topic where to listen messages from neighbors
		listenerTopic = listenerTopicPrefix + deviceStringID;
		
		// Setup one single listener to get data from neighbors
		// every neighbor is sending to this node on one topic 
		setupNeighborsListener();
	}
	
	private void printLog(String message) {
		prjNode.printLog(message);
	}
	
	private String getNeighborTopic(DeviceUID neighborUID) {
		return listenerTopicPrefix + ((IntegerUID) neighborUID).toString();
	}
	
	private void setupNeighborDiscovery() {
		printLog("Setting up neighbor discovery");
		setupNeighborDiscoveryPublisher();
		setupNeighborDiscoveryListener();
	}
	
	private void setupNeighborDiscoveryPublisher() {
		neighborDiscoveryPublisher = connectedNode.newPublisher(neighborDiscoveryTopic, std_msgs.String._TYPE);
		neighborDiscoveryPublisherLoop = new CancellableLoop() {
			@Override
			protected void loop() throws InterruptedException {
				publishDiscoveryMsg();
				Thread.sleep(discoverySleepTime);
			}
		};
		connectedNode.executeCancellableLoop(neighborDiscoveryPublisherLoop);
	}
	
	private void publishDiscoveryMsg() {
		std_msgs.String msg = neighborDiscoveryPublisher.newMessage();
		msg.setData(deviceGsonID);
		neighborDiscoveryPublisher.publish(msg);
//		printLog("Publishing discovery msg");
	}
	
	private void setupNeighborDiscoveryListener() {
		Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(neighborDiscoveryTopic,
				std_msgs.String._TYPE);
		subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
			@Override
			public void onNewMessage(std_msgs.String msg) {
//				printLog("Receiving data from discovery neighbor");
				String neighborGsonUID = msg.getData();
				IntegerUID neighborUID = gson.fromJson(neighborGsonUID, IntegerUID.class);
				if ((!Objects.equals(neighborUID,deviceUID)) && (!neighbors.contains(neighborUID))) {
					printLog("Discovered new neighbor with UID: " + neighborUID.toString());
					neighbors.add(neighborUID);
					setupNeighborPublisher(neighborUID);
					// TODO: Stop the loop after the first neighbor?
//					if (neighborDiscoveryPublisherLoop != null) {
//						neighborDiscoveryPublisherLoop.cancel();
//					}
					publishDiscoveryMsg();
				}
			}
		});
	}

	private void setupNeighborsListener() {
		Subscriber<rosjava_custom_msg.StateWithID> subscriber = connectedNode.newSubscriber(listenerTopic,
				rosjava_custom_msg.StateWithID._TYPE);
		subscriber.addMessageListener(new MessageListener<rosjava_custom_msg.StateWithID>() {
			@Override
			public void onNewMessage(rosjava_custom_msg.StateWithID msg) {
//				printLog("Receiving state from neighbor");
				String neighborID = msg.getId();
				String neighborState = msg.getState();
				prjNode.accessNetworkManager().receiveFromNeighbor(neighborID, neighborState);
			}
		});
	}
	
	public void printNeighbors() {
		String neighborsUIDs = "";
		for (DeviceUID neighbor : neighbors) {
			neighborsUIDs += neighbor.toString() + "  ";
		}
		printLog("Neighbors: " + neighborsUIDs);
	}
	
	private void setupNeighborPublisher(DeviceUID neighborUID) {
		// Every neighbor has its publisher with a specific topic
		String neighborTopic = getNeighborTopic(neighborUID);
		Publisher<rosjava_custom_msg.StateWithID> neighborPublisher = connectedNode.newPublisher(neighborTopic, rosjava_custom_msg.StateWithID._TYPE);
		publishers.put(neighborUID, neighborPublisher);
	}
	
	public void sendToNeighbors(String state) {
		// Deliver shared-state updates over the network
		for (DeviceUID neighborID : publishers.keySet()) {
			Publisher<rosjava_custom_msg.StateWithID> pub = publishers.get(neighborID);
			rosjava_custom_msg.StateWithID msg = pub.newMessage();
			msg.setId(deviceGsonID);
			msg.setState(state);
			pub.publish(msg);
		}
	}

}
