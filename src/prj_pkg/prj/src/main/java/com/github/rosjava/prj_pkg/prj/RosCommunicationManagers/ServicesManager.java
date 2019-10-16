package com.github.rosjava.prj_pkg.prj.RosCommunicationManagers;

import java.util.HashMap;
import java.util.Map;

import org.ros.exception.RosRuntimeException;

import com.github.rosjava.prj_pkg.prj.PrjNode;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.PrjService;

public class ServicesManager {
	
	private final PrjNode prjNode;
	private final String mavros_prefix;
	private String mavros_node_name;
	private final String adhoc_communication_prefix;
	private String adhoc_communication_node_name;
	private Map<String,PrjService<?,?>> services = new HashMap<>();

	public ServicesManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		
		/* Read the input parameters from the Parameter Server */
		readInputParameters();
		
		// The complete service name is like: '/NAMESPACE/NODE_NAME/SERVICE_NAME'
		String namespace = prjNode.getNamespace().toString();
		this.mavros_prefix = namespace + "/" + mavros_node_name + "/";
		this.adhoc_communication_prefix = namespace + "/" + adhoc_communication_node_name + "/";
	}
	
	private void readInputParameters() {
		prjNode.logInfo("Reading the parameters:");
		ParametersManager mpm = prjNode.getParametersManager();
		mavros_node_name = mpm.getStringParam("~mavros/mavrosNodeName");
		if (mavros_node_name == null) {
			prjNode.exitWithError("Parameter mavrosNodeName is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("mavros_node_name: " + mavros_node_name);
		adhoc_communication_node_name = mpm.getStringParam("~adhoc_communication/adhoc_communication_node_name");
		if (adhoc_communication_node_name == null) {
			prjNode.exitWithError("Parameter adhoc_communication_node_name is null.. review the prj_configuration file.");
		}
		prjNode.logInfo("adhoc_communication_node_name: " + adhoc_communication_node_name);
	}
	
	public void setupPrjServices() {
		
		// Mavros services
		prjNode.logInfo("Setup " + mavros_node_name + " services.. ");
		services.put("cmd/command", new PrjService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>(prjNode, mavros_prefix + "cmd/command", mavros_msgs.CommandLong._TYPE));
		services.put("cmd/command_int", new PrjService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>(prjNode, mavros_prefix + "cmd/command_int", mavros_msgs.CommandInt._TYPE));
		services.put("cmd/arming", new PrjService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>(prjNode, mavros_prefix + "cmd/arming", mavros_msgs.CommandBool._TYPE));
		services.put("cmd/set_home", new PrjService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>(prjNode, mavros_prefix + "cmd/set_home", mavros_msgs.CommandHome._TYPE));
		services.put("cmd/takeoff", new PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(prjNode, mavros_prefix + "cmd/takeoff", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/land", new PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(prjNode, mavros_prefix + "cmd/land", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/trigger_control", new PrjService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>(prjNode, mavros_prefix + "cmd/trigger_control", mavros_msgs.CommandTriggerControl._TYPE));
		services.put("param/pull", new PrjService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>(prjNode, mavros_prefix + "param/pull", mavros_msgs.ParamPull._TYPE));
		services.put("param/push", new PrjService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>(prjNode, mavros_prefix + "param/push", mavros_msgs.ParamPush._TYPE));
		services.put("param/get", new PrjService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>(prjNode, mavros_prefix + "param/get", mavros_msgs.ParamGet._TYPE));
		services.put("param/set", new PrjService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>(prjNode, mavros_prefix + "param/set", mavros_msgs.ParamSet._TYPE));
		services.put("set_stream_rate", new PrjService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>(prjNode, mavros_prefix + "set_stream_rate", mavros_msgs.StreamRate._TYPE));
		services.put("set_mode", new PrjService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>(prjNode, mavros_prefix + "set_mode", mavros_msgs.SetMode._TYPE));
		services.put("mission/pull", new PrjService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>(prjNode, mavros_prefix + "mission/pull", mavros_msgs.WaypointPull._TYPE));
		services.put("mission/push", new PrjService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>(prjNode, mavros_prefix + "mission/push", mavros_msgs.WaypointPush._TYPE));
		services.put("mission/clear", new PrjService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>(prjNode, mavros_prefix + "mission/clear", mavros_msgs.WaypointClear._TYPE));
		services.put("mission/set_current", new PrjService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>(prjNode, mavros_prefix + "mission/set_current", mavros_msgs.WaypointSetCurrent._TYPE));

//		// MISSING:
//		services.put("ftp/open", new PrjService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>(prjNode, mavros_prefix + "ftp/open", mavros_msgs.FileOpen._TYPE));
//		services.put("ftp/close", new PrjService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>(prjNode, mavros_prefix + "ftp/close", mavros_msgs.FileClose._TYPE));
//		services.put("ftp/read", new PrjService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>(prjNode, mavros_prefix + "ftp/read", mavros_msgs.FileRead._TYPE));
//		services.put("ftp/write", new PrjService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>(prjNode, mavros_prefix + "ftp/write", mavros_msgs.FileWrite._TYPE));
//		services.put("ftp/list", new PrjService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>(prjNode, mavros_prefix + "ftp/list", mavros_msgs.FileList._TYPE));
//		services.put("ftp/truncate", new PrjService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>(prjNode, mavros_prefix + "ftp/truncate", mavros_msgs.FileTruncate._TYPE));
//		services.put("ftp/remove", new PrjService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>(prjNode, mavros_prefix + "ftp/remove", mavros_msgs.FileRemove._TYPE));
//		services.put("ftp/rename", new PrjService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>(prjNode, mavros_prefix + "ftp/rename", mavros_msgs.FileRename._TYPE));
//		services.put("ftp/mkdir", new PrjService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>(prjNode, mavros_prefix + "ftp/mkdir", mavros_msgs.FileMakeDir._TYPE));
//		services.put("ftp/rmdir", new PrjService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>(prjNode, mavros_prefix + "ftp/rmdir", mavros_msgs.FileRemoveDir._TYPE));
//		services.put("ftp/checksum", new PrjService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>(prjNode, mavros_prefix + "ftp/checksum", mavros_msgs.FileChecksum._TYPE));
//		services.put("ftp/reset", new PrjService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>(prjNode, mavros_prefix + "ftp/reset", std_srvs.Empty._TYPE));
		
		// Adhoc_communication services
		prjNode.logInfo("Setup " + adhoc_communication_node_name + " services.. ");
		services.put("get_id", new PrjService<adhoc_communication.GetIDRequest, adhoc_communication.GetIDResponse>(prjNode, adhoc_communication_prefix + "get_id", adhoc_communication.GetID._TYPE));
		services.put("send_string", new PrjService<adhoc_communication.SendStringRequest, adhoc_communication.SendStringResponse>(prjNode, adhoc_communication_prefix + "send_string", adhoc_communication.SendString._TYPE));
		services.put("get_neighbors", new PrjService<adhoc_communication.GetNeighborsRequest, adhoc_communication.GetNeighborsResponse>(prjNode, adhoc_communication_prefix + "get_neighbors", adhoc_communication.GetNeighbors._TYPE));
		services.put("join_mc_group", new PrjService<adhoc_communication.ChangeMCMembershipRequest, adhoc_communication.ChangeMCMembershipResponse>(prjNode, adhoc_communication_prefix + "join_mc_group", adhoc_communication.ChangeMCMembership._TYPE));
		services.put("broadcast_string", new PrjService<adhoc_communication.BroadcastStringRequest, adhoc_communication.BroadcastStringResponse>(prjNode, adhoc_communication_prefix + "broadcast_string", adhoc_communication.BroadcastString._TYPE));
		services.put("shut_down", new PrjService<adhoc_communication.ShutDownRequest, adhoc_communication.ShutDownResponse>(prjNode, adhoc_communication_prefix + "shut_down", adhoc_communication.ShutDown._TYPE));
		services.put("get_group_state", new PrjService<adhoc_communication.GetGroupStateRequest, adhoc_communication.GetGroupStateResponse>(prjNode, adhoc_communication_prefix + "get_group_state", adhoc_communication.GetGroupState._TYPE));
		
		/** Wait for all services to be connected **/
		waitServicesConnection();
	}
	
	private void waitServicesConnection() {
		prjNode.logInfo("Wait for all services to be connected..");
		for (String key : services.keySet()) {
			if (!services.get(key).waitServiceConnection()) {
				throw new RosRuntimeException("Service '" + key + "' NOT found");
			}
		}
		prjNode.logInfo("All services have been connected");
	}
	
	/**
	 * Service: 'cmd/command'
	 * Message type: mavros_msgs.CommandLong
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse> get_CmdCommand() {
		return (PrjService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>) services.get("cmd/command");
	}

	/**
	 * Service: 'cmd/command_int'
	 * Message type: mavros_msgs.CommandInt
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse> get_CmdCommandInt() {
		return (PrjService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>) services.get("cmd/command_int");
	}

	/**
	 * Service: 'cmd/arming'
	 * Message type: mavros_msgs.CommandBool
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse> get_CmdArming() {
		return (PrjService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>) services.get("cmd/arming");
	}

	/**
	 * Service: 'cmd/set_home'
	 * Message type: mavros_msgs.CommandHome
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse> get_CmdSetHome() {
		return (PrjService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>) services.get("cmd/set_home");
	}

	/**
	 * Service: 'cmd/takeoff'
	 * Message type: mavros_msgs.CommandTOL
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> get_CmdTakeoff() {
		return (PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/takeoff");
	}

	/**
	 * Service: 'cmd/land'
	 * Message type: mavros_msgs.CommandTOL
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> get_CmdLand() {
		return (PrjService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/land");
	}

	/**
	 * Service: 'cmd/trigger_control'
	 * Message type: mavros_msgs.CommandTriggerControl
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse> get_CmdTriggerControl() {
		return (PrjService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>) services.get("cmd/trigger_control");
	}

	/**
	 * Service: 'ftp/open'
	 * Message type: mavros_msgs.FileOpen
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse> get_FtpOpen() {
		return (PrjService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>) services.get("ftp/open");
	}

	/**
	 * Service: 'ftp/close'
	 * Message type: mavros_msgs.FileClose
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse> get_FtpClose() {
		return (PrjService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>) services.get("ftp/close");
	}

	/**
	 * Service: 'ftp/read'
	 * Message type: mavros_msgs.FileRead
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse> get_FtpRead() {
		return (PrjService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>) services.get("ftp/read");
	}

	/**
	 * Service: 'ftp/write'
	 * Message type: mavros_msgs.FileWrite
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse> get_FtpWrite() {
		return (PrjService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>) services.get("ftp/write");
	}

	/**
	 * Service: 'ftp/list'
	 * Message type: mavros_msgs.FileList
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse> get_FtpList() {
		return (PrjService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>) services.get("ftp/list");
	}

	/**
	 * Service: 'ftp/truncate'
	 * Message type: mavros_msgs.FileTruncate
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse> get_FtpTruncate() {
		return (PrjService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>) services.get("ftp/truncate");
	}

	/**
	 * Service: 'ftp/remove'
	 * Message type: mavros_msgs.FileRemove
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse> get_FtpRemove() {
		return (PrjService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>) services.get("ftp/remove");
	}

	/**
	 * Service: 'ftp/rename'
	 * Message type: mavros_msgs.FileRename
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse> get_FtpRename() {
		return (PrjService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>) services.get("ftp/rename");
	}

	/**
	 * Service: 'ftp/mkdir'
	 * Message type: mavros_msgs.FileMakeDir
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse> get_FtpMkdir() {
		return (PrjService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>) services.get("ftp/mkdir");
	}

	/**
	 * Service: 'ftp/rmdir'
	 * Message type: mavros_msgs.FileRemoveDir
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse> get_FtpRmdir() {
		return (PrjService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>) services.get("ftp/rmdir");
	}

	/**
	 * Service: 'ftp/checksum'
	 * Message type: mavros_msgs.FileChecksum
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse> get_FtpChecksum() {
		return (PrjService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>) services.get("ftp/checksum");
	}

	/**
	 * Service: 'ftp/reset'
	 * Message type: std_srvs.Empty
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<std_srvs.EmptyRequest, std_srvs.EmptyResponse> get_FtpReset() {
		return (PrjService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>) services.get("ftp/reset");
	}

	/**
	 * Service: 'param/pull'
	 * Message type: mavros_msgs.ParamPull
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse> get_ParamPull() {
		return (PrjService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>) services.get("param/pull");
	}

	/**
	 * Service: 'param/push'
	 * Message type: mavros_msgs.ParamPush
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse> get_ParamPush() {
		return (PrjService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>) services.get("param/push");
	}

	/**
	 * Service: 'param/get'
	 * Message type: mavros_msgs.ParamGet
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse> get_ParamGet() {
		return (PrjService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>) services.get("param/get");
	}

	/**
	 * Service: 'param/set'
	 * Message type: mavros_msgs.ParamSet
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse> get_ParamSet() {
		return (PrjService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>) services.get("param/set");
	}

	/**
	 * Service: 'set_stream_rate'
	 * Message type: mavros_msgs.StreamRate
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse> get_SetStreamRate() {
		return (PrjService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>) services.get("set_stream_rate");
	}

	/**
	 * Service: 'set_mode'
	 * Message type: mavros_msgs.SetMode
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse> get_SetMode() {
		return (PrjService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>) services.get("set_mode");
	}

	/**
	 * Service: 'mission/pull'
	 * Message type: mavros_msgs.WaypointPull
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse> get_MissionPull() {
		return (PrjService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>) services.get("mission/pull");
	}

	/**
	 * Service: 'mission/push'
	 * Message type: mavros_msgs.WaypointPush
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse> get_MissionPush() {
		return (PrjService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>) services.get("mission/push");
	}

	/**
	 * Service: 'mission/clear'
	 * Message type: mavros_msgs.WaypointClear
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse> get_MissionClear() {
		return (PrjService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>) services.get("mission/clear");
	}

	/**
	 * Service: 'mission/set_current'
	 * Message type: mavros_msgs.WaypointSetCurrent
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse> get_MissionSetCurrent() {
		return (PrjService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>) services.get("mission/set_current");
	}

	/**
	 * Service: 'get_id'
	 * Message type: adhoc_communication.GetID
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.GetIDRequest, adhoc_communication.GetIDResponse> get_get_id() {
		return (PrjService<adhoc_communication.GetIDRequest, adhoc_communication.GetIDResponse>) services.get("get_id");
	}

	/**
	 * Service: 'send_string'
	 * Message type: adhoc_communication.SendString
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.SendStringRequest, adhoc_communication.SendStringResponse> get_send_string() {
		return (PrjService<adhoc_communication.SendStringRequest, adhoc_communication.SendStringResponse>) services.get("send_string");
	}

	/**
	 * Service: 'get_neighbors'
	 * Message type: adhoc_communication.GetNeighbors
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.GetNeighborsRequest, adhoc_communication.GetNeighborsResponse> get_get_neighbors() {
		return (PrjService<adhoc_communication.GetNeighborsRequest, adhoc_communication.GetNeighborsResponse>) services.get("get_neighbors");
	}

	/**
	 * Service: 'join_mc_group'
	 * Message type: adhoc_communication.ChangeMCMembership
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.ChangeMCMembershipRequest, adhoc_communication.ChangeMCMembershipResponse> get_join_mc_group() {
		return (PrjService<adhoc_communication.ChangeMCMembershipRequest, adhoc_communication.ChangeMCMembershipResponse>) services.get("join_mc_group");
	}

	/**
	 * Service: 'broadcast_string'
	 * Message type: adhoc_communication.BroadcastString
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.BroadcastStringRequest, adhoc_communication.BroadcastStringResponse> get_broadcast_string() {
		return (PrjService<adhoc_communication.BroadcastStringRequest, adhoc_communication.BroadcastStringResponse>) services.get("broadcast_string");
	}

	/**
	 * Service: 'shut_down'
	 * Message type: adhoc_communication.ShutDown
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.ShutDownRequest, adhoc_communication.ShutDownResponse> get_shut_down() {
		return (PrjService<adhoc_communication.ShutDownRequest, adhoc_communication.ShutDownResponse>) services.get("shut_down");
	}

	/**
	 * Service: 'get_group_state'
	 * Message type: adhoc_communication.GetGroupState
	 **/
	@SuppressWarnings("unchecked")
	public PrjService<adhoc_communication.GetGroupStateRequest, adhoc_communication.GetGroupStateResponse> get_get_group_state() {
		return (PrjService<adhoc_communication.GetGroupStateRequest, adhoc_communication.GetGroupStateResponse>) services.get("get_group_state");
	}
	
}