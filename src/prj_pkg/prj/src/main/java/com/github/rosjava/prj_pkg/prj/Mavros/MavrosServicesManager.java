package com.github.rosjava.prj_pkg.prj.Mavros;

import java.util.HashMap;
import java.util.Map;

import org.ros.exception.RosRuntimeException;

import com.github.rosjava.prj_pkg.prj.PrjNode;

public class MavrosServicesManager {
	
	private final PrjNode prjNode;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,MavrosService<?,?>> services = new HashMap<>();

	public MavrosServicesManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		// The complete service name is like: '/NAMESPACE/mavros/SERVICE_NAME'
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		this.mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
	}
	
	public void setupServices() {
		prjNode.printLog("Setup services.. ");
		services.put("cmd/command", new MavrosService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>(prjNode, mavrosPrefix + "cmd/command", mavros_msgs.CommandLong._TYPE));
		services.put("cmd/command_int", new MavrosService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>(prjNode, mavrosPrefix + "cmd/command_int", mavros_msgs.CommandInt._TYPE));
		services.put("cmd/arming", new MavrosService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>(prjNode, mavrosPrefix + "cmd/arming", mavros_msgs.CommandBool._TYPE));
		services.put("cmd/set_home", new MavrosService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>(prjNode, mavrosPrefix + "cmd/set_home", mavros_msgs.CommandHome._TYPE));
		services.put("cmd/takeoff", new MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(prjNode, mavrosPrefix + "cmd/takeoff", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/land", new MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(prjNode, mavrosPrefix + "cmd/land", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/trigger_control", new MavrosService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>(prjNode, mavrosPrefix + "cmd/trigger_control", mavros_msgs.CommandTriggerControl._TYPE));
		services.put("param/pull", new MavrosService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>(prjNode, mavrosPrefix + "param/pull", mavros_msgs.ParamPull._TYPE));
		services.put("param/push", new MavrosService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>(prjNode, mavrosPrefix + "param/push", mavros_msgs.ParamPush._TYPE));
		services.put("param/get", new MavrosService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>(prjNode, mavrosPrefix + "param/get", mavros_msgs.ParamGet._TYPE));
		services.put("param/set", new MavrosService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>(prjNode, mavrosPrefix + "param/set", mavros_msgs.ParamSet._TYPE));
		services.put("set_stream_rate", new MavrosService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>(prjNode, mavrosPrefix + "set_stream_rate", mavros_msgs.StreamRate._TYPE));
		services.put("set_mode", new MavrosService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>(prjNode, mavrosPrefix + "set_mode", mavros_msgs.SetMode._TYPE));
		services.put("mission/pull", new MavrosService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>(prjNode, mavrosPrefix + "mission/pull", mavros_msgs.WaypointPull._TYPE));
		services.put("mission/push", new MavrosService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>(prjNode, mavrosPrefix + "mission/push", mavros_msgs.WaypointPush._TYPE));
		services.put("mission/clear", new MavrosService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>(prjNode, mavrosPrefix + "mission/clear", mavros_msgs.WaypointClear._TYPE));
		services.put("mission/set_current", new MavrosService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>(prjNode, mavrosPrefix + "mission/set_current", mavros_msgs.WaypointSetCurrent._TYPE));

//		// MISSING:
//		services.put("ftp/open", new MavrosService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>(prjNode, mavrosPrefix + "ftp/open", mavros_msgs.FileOpen._TYPE));
//		services.put("ftp/close", new MavrosService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>(prjNode, mavrosPrefix + "ftp/close", mavros_msgs.FileClose._TYPE));
//		services.put("ftp/read", new MavrosService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>(prjNode, mavrosPrefix + "ftp/read", mavros_msgs.FileRead._TYPE));
//		services.put("ftp/write", new MavrosService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>(prjNode, mavrosPrefix + "ftp/write", mavros_msgs.FileWrite._TYPE));
//		services.put("ftp/list", new MavrosService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>(prjNode, mavrosPrefix + "ftp/list", mavros_msgs.FileList._TYPE));
//		services.put("ftp/truncate", new MavrosService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>(prjNode, mavrosPrefix + "ftp/truncate", mavros_msgs.FileTruncate._TYPE));
//		services.put("ftp/remove", new MavrosService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>(prjNode, mavrosPrefix + "ftp/remove", mavros_msgs.FileRemove._TYPE));
//		services.put("ftp/rename", new MavrosService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>(prjNode, mavrosPrefix + "ftp/rename", mavros_msgs.FileRename._TYPE));
//		services.put("ftp/mkdir", new MavrosService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>(prjNode, mavrosPrefix + "ftp/mkdir", mavros_msgs.FileMakeDir._TYPE));
//		services.put("ftp/rmdir", new MavrosService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>(prjNode, mavrosPrefix + "ftp/rmdir", mavros_msgs.FileRemoveDir._TYPE));
//		services.put("ftp/checksum", new MavrosService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>(prjNode, mavrosPrefix + "ftp/checksum", mavros_msgs.FileChecksum._TYPE));
//		services.put("ftp/reset", new MavrosService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>(prjNode, mavrosPrefix + "ftp/reset", std_srvs.Empty._TYPE));
		
		/** Wait for all services to be connected **/
		waitServicesConnection();
	}
	
	private void waitServicesConnection() {
		prjNode.printLog("Wait for all services to be connected..");
		for (String key : services.keySet()) {
			if (!services.get(key).waitServiceConnection()) {
				throw new RosRuntimeException("Service '" + key + "' NOT found");
			}
		}
		prjNode.printLog("All services have been connected");
	}
	
	/**
	 * Service: 'cmd/command'
	 * Message type: mavros_msgs.CommandLong
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse> get_CmdCommand() {
		return (MavrosService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>) services.get("cmd/command");
	}

	/**
	 * Service: 'cmd/command_int'
	 * Message type: mavros_msgs.CommandInt
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse> get_CmdCommandInt() {
		return (MavrosService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>) services.get("cmd/command_int");
	}

	/**
	 * Service: 'cmd/arming'
	 * Message type: mavros_msgs.CommandBool
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse> get_CmdArming() {
		return (MavrosService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>) services.get("cmd/arming");
	}

	/**
	 * Service: 'cmd/set_home'
	 * Message type: mavros_msgs.CommandHome
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse> get_CmdSetHome() {
		return (MavrosService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>) services.get("cmd/set_home");
	}

	/**
	 * Service: 'cmd/takeoff'
	 * Message type: mavros_msgs.CommandTOL
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> get_CmdTakeoff() {
		return (MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/takeoff");
	}

	/**
	 * Service: 'cmd/land'
	 * Message type: mavros_msgs.CommandTOL
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> get_CmdLand() {
		return (MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/land");
	}

	/**
	 * Service: 'cmd/trigger_control'
	 * Message type: mavros_msgs.CommandTriggerControl
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse> get_CmdTriggerControl() {
		return (MavrosService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>) services.get("cmd/trigger_control");
	}

	/**
	 * Service: 'ftp/open'
	 * Message type: mavros_msgs.FileOpen
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse> get_FtpOpen() {
		return (MavrosService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>) services.get("ftp/open");
	}

	/**
	 * Service: 'ftp/close'
	 * Message type: mavros_msgs.FileClose
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse> get_FtpClose() {
		return (MavrosService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>) services.get("ftp/close");
	}

	/**
	 * Service: 'ftp/read'
	 * Message type: mavros_msgs.FileRead
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse> get_FtpRead() {
		return (MavrosService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>) services.get("ftp/read");
	}

	/**
	 * Service: 'ftp/write'
	 * Message type: mavros_msgs.FileWrite
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse> get_FtpWrite() {
		return (MavrosService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>) services.get("ftp/write");
	}

	/**
	 * Service: 'ftp/list'
	 * Message type: mavros_msgs.FileList
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse> get_FtpList() {
		return (MavrosService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>) services.get("ftp/list");
	}

	/**
	 * Service: 'ftp/truncate'
	 * Message type: mavros_msgs.FileTruncate
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse> get_FtpTruncate() {
		return (MavrosService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>) services.get("ftp/truncate");
	}

	/**
	 * Service: 'ftp/remove'
	 * Message type: mavros_msgs.FileRemove
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse> get_FtpRemove() {
		return (MavrosService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>) services.get("ftp/remove");
	}

	/**
	 * Service: 'ftp/rename'
	 * Message type: mavros_msgs.FileRename
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse> get_FtpRename() {
		return (MavrosService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>) services.get("ftp/rename");
	}

	/**
	 * Service: 'ftp/mkdir'
	 * Message type: mavros_msgs.FileMakeDir
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse> get_FtpMkdir() {
		return (MavrosService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>) services.get("ftp/mkdir");
	}

	/**
	 * Service: 'ftp/rmdir'
	 * Message type: mavros_msgs.FileRemoveDir
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse> get_FtpRmdir() {
		return (MavrosService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>) services.get("ftp/rmdir");
	}

	/**
	 * Service: 'ftp/checksum'
	 * Message type: mavros_msgs.FileChecksum
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse> get_FtpChecksum() {
		return (MavrosService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>) services.get("ftp/checksum");
	}

	/**
	 * Service: 'ftp/reset'
	 * Message type: std_srvs.Empty
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<std_srvs.EmptyRequest, std_srvs.EmptyResponse> get_FtpReset() {
		return (MavrosService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>) services.get("ftp/reset");
	}

	/**
	 * Service: 'param/pull'
	 * Message type: mavros_msgs.ParamPull
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse> get_ParamPull() {
		return (MavrosService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>) services.get("param/pull");
	}

	/**
	 * Service: 'param/push'
	 * Message type: mavros_msgs.ParamPush
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse> get_ParamPush() {
		return (MavrosService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>) services.get("param/push");
	}

	/**
	 * Service: 'param/get'
	 * Message type: mavros_msgs.ParamGet
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse> get_ParamGet() {
		return (MavrosService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>) services.get("param/get");
	}

	/**
	 * Service: 'param/set'
	 * Message type: mavros_msgs.ParamSet
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse> get_ParamSet() {
		return (MavrosService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>) services.get("param/set");
	}

	/**
	 * Service: 'set_stream_rate'
	 * Message type: mavros_msgs.StreamRate
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse> get_SetStreamRate() {
		return (MavrosService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>) services.get("set_stream_rate");
	}

	/**
	 * Service: 'set_mode'
	 * Message type: mavros_msgs.SetMode
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse> get_SetMode() {
		return (MavrosService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>) services.get("set_mode");
	}

	/**
	 * Service: 'mission/pull'
	 * Message type: mavros_msgs.WaypointPull
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse> get_MissionPull() {
		return (MavrosService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>) services.get("mission/pull");
	}

	/**
	 * Service: 'mission/push'
	 * Message type: mavros_msgs.WaypointPush
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse> get_MissionPush() {
		return (MavrosService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>) services.get("mission/push");
	}

	/**
	 * Service: 'mission/clear'
	 * Message type: mavros_msgs.WaypointClear
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse> get_MissionClear() {
		return (MavrosService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>) services.get("mission/clear");
	}

	/**
	 * Service: 'mission/set_current'
	 * Message type: mavros_msgs.WaypointSetCurrent
	 **/
	@SuppressWarnings("unchecked")
	public MavrosService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse> get_MissionSetCurrent() {
		return (MavrosService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>) services.get("mission/set_current");
	}
	
}