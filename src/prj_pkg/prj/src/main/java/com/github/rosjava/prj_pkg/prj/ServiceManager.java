package com.github.rosjava.prj_pkg.prj;

import java.util.HashMap;
import java.util.Map;

import org.ros.node.ConnectedNode;

public class ServiceManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,MavrosService<?,?>> services = new HashMap<>();

	public ServiceManager(PrjNode prjNode, ConnectedNode connectedNode) {
		this.prjNode = prjNode;
		this.connectedNode = connectedNode;
		// The complete service name is like: '/NAMESPACE/mavros/SERVICE_NAME'
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		this.mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
	}
	
	public void setupServices() {
		prjNode.printLog("Setup services.. ");
		services.put("cmd/command", new MavrosService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>(connectedNode, mavrosPrefix, "cmd/command", mavros_msgs.CommandLong._TYPE));
		services.put("cmd/command_int", new MavrosService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>(connectedNode, mavrosPrefix, "cmd/command_int", mavros_msgs.CommandInt._TYPE));
		services.put("cmd/arming", new MavrosService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>(connectedNode, mavrosPrefix, "cmd/arming", mavros_msgs.CommandBool._TYPE));
		services.put("cmd/set_home", new MavrosService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>(connectedNode, mavrosPrefix, "cmd/set_home", mavros_msgs.CommandHome._TYPE));
		services.put("cmd/takeoff", new MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(connectedNode, mavrosPrefix, "cmd/takeoff", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/land", new MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>(connectedNode, mavrosPrefix, "cmd/land", mavros_msgs.CommandTOL._TYPE));
		services.put("cmd/trigger_control", new MavrosService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>(connectedNode, mavrosPrefix, "cmd/trigger_control", mavros_msgs.CommandTriggerControl._TYPE));
		services.put("ftp/open", new MavrosService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>(connectedNode, mavrosPrefix, "ftp/open", mavros_msgs.FileOpen._TYPE));
		services.put("ftp/close", new MavrosService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>(connectedNode, mavrosPrefix, "ftp/close", mavros_msgs.FileClose._TYPE));
		services.put("ftp/read", new MavrosService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>(connectedNode, mavrosPrefix, "ftp/read", mavros_msgs.FileRead._TYPE));
		services.put("ftp/write", new MavrosService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>(connectedNode, mavrosPrefix, "ftp/write", mavros_msgs.FileWrite._TYPE));
		services.put("ftp/list", new MavrosService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>(connectedNode, mavrosPrefix, "ftp/list", mavros_msgs.FileList._TYPE));
		services.put("ftp/truncate", new MavrosService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>(connectedNode, mavrosPrefix, "ftp/truncate", mavros_msgs.FileTruncate._TYPE));
		services.put("ftp/remove", new MavrosService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>(connectedNode, mavrosPrefix, "ftp/remove", mavros_msgs.FileRemove._TYPE));
		services.put("ftp/rename", new MavrosService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>(connectedNode, mavrosPrefix, "ftp/rename", mavros_msgs.FileRename._TYPE));
		services.put("ftp/mkdir", new MavrosService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>(connectedNode, mavrosPrefix, "ftp/mkdir", mavros_msgs.FileMakeDir._TYPE));
		services.put("ftp/rmdir", new MavrosService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>(connectedNode, mavrosPrefix, "ftp/rmdir", mavros_msgs.FileRemoveDir._TYPE));
		services.put("ftp/checksum", new MavrosService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>(connectedNode, mavrosPrefix, "ftp/checksum", mavros_msgs.FileChecksum._TYPE));
		services.put("ftp/reset", new MavrosService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>(connectedNode, mavrosPrefix, "ftp/reset", std_srvs.Empty._TYPE));
		services.put("param/pull", new MavrosService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>(connectedNode, mavrosPrefix, "param/pull", mavros_msgs.ParamPull._TYPE));
		services.put("param/push", new MavrosService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>(connectedNode, mavrosPrefix, "param/push", mavros_msgs.ParamPush._TYPE));
		services.put("param/get", new MavrosService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>(connectedNode, mavrosPrefix, "param/get", mavros_msgs.ParamGet._TYPE));
		services.put("param/set", new MavrosService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>(connectedNode, mavrosPrefix, "param/set", mavros_msgs.ParamSet._TYPE));
		services.put("set_stream_rate", new MavrosService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>(connectedNode, mavrosPrefix, "set_stream_rate", mavros_msgs.StreamRate._TYPE));
		services.put("set_mode", new MavrosService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>(connectedNode, mavrosPrefix, "set_mode", mavros_msgs.SetMode._TYPE));
		services.put("mission/pull", new MavrosService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>(connectedNode, mavrosPrefix, "mission/pull", mavros_msgs.WaypointPull._TYPE));
		services.put("mission/push", new MavrosService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>(connectedNode, mavrosPrefix, "mission/push", mavros_msgs.WaypointPush._TYPE));
		services.put("mission/clear", new MavrosService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>(connectedNode, mavrosPrefix, "mission/clear", mavros_msgs.WaypointClear._TYPE));
		services.put("mission/set_current", new MavrosService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>(connectedNode, mavrosPrefix, "mission/set_current", mavros_msgs.WaypointSetCurrent._TYPE));

		/** Wait for all services to be connected **/
		waitServicesConnection();
	}
	
	private void waitServicesConnection() {
		prjNode.printLog("Wait for all services to be connected..");
		for (String key : services.keySet()) {
			prjNode.printLog("waiting service" + key);
			while (!services.get(key).isConnected()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					prjNode.printLog("Interruption command received.. wakeup from sleep");
				}
				
			}
		}
		prjNode.printLog("All services are connected");
	}
	
	/**
	 * Service: 'cmd/command'
	 **/

	public mavros_msgs.CommandLongRequest getNewMessage_CmdCommand() {
		return (mavros_msgs.CommandLongRequest) services.get("cmd/command").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdCommand(mavros_msgs.CommandLongRequest request) {
		((MavrosService<mavros_msgs.CommandLongRequest, mavros_msgs.CommandLongResponse>) services.get("cmd/command")).call(request);
	}

	public mavros_msgs.CommandLongResponse getServiceResponse_CmdCommand() {
		return (mavros_msgs.CommandLongResponse) services.get("cmd/command").getResponse();
	}

	public boolean hasServiceResponse_CmdCommand() {
		return services.get("cmd/command").hasResponse();
	}

	/**
	 * Service: 'cmd/command_int'
	 **/

	public mavros_msgs.CommandIntRequest getNewMessage_CmdCommandInt() {
		return (mavros_msgs.CommandIntRequest) services.get("cmd/command_int").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdCommandInt(mavros_msgs.CommandIntRequest request) {
		((MavrosService<mavros_msgs.CommandIntRequest, mavros_msgs.CommandIntResponse>) services.get("cmd/command_int")).call(request);
	}

	public mavros_msgs.CommandIntResponse getServiceResponse_CmdCommandInt() {
		return (mavros_msgs.CommandIntResponse) services.get("cmd/command_int").getResponse();
	}

	public boolean hasServiceResponse_CmdCommandInt() {
		return services.get("cmd/command_int").hasResponse();
	}

	/**
	 * Service: 'cmd/arming'
	 **/

	public mavros_msgs.CommandBoolRequest getNewMessage_CmdArming() {
		return (mavros_msgs.CommandBoolRequest) services.get("cmd/arming").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdArming(mavros_msgs.CommandBoolRequest request) {
		((MavrosService<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>) services.get("cmd/arming")).call(request);
	}

	public mavros_msgs.CommandBoolResponse getServiceResponse_CmdArming() {
		return (mavros_msgs.CommandBoolResponse) services.get("cmd/arming").getResponse();
	}

	public boolean hasServiceResponse_CmdArming() {
		return services.get("cmd/arming").hasResponse();
	}

	/**
	 * Service: 'cmd/set_home'
	 **/

	public mavros_msgs.CommandHomeRequest getNewMessage_CmdSetHome() {
		return (mavros_msgs.CommandHomeRequest) services.get("cmd/set_home").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdSetHome(mavros_msgs.CommandHomeRequest request) {
		((MavrosService<mavros_msgs.CommandHomeRequest, mavros_msgs.CommandHomeResponse>) services.get("cmd/set_home")).call(request);
	}

	public mavros_msgs.CommandHomeResponse getServiceResponse_CmdSetHome() {
		return (mavros_msgs.CommandHomeResponse) services.get("cmd/set_home").getResponse();
	}

	public boolean hasServiceResponse_CmdSetHome() {
		return services.get("cmd/set_home").hasResponse();
	}

	/**
	 * Service: 'cmd/takeoff'
	 **/

	public mavros_msgs.CommandTOLRequest getNewMessage_CmdTakeoff() {
		return (mavros_msgs.CommandTOLRequest) services.get("cmd/takeoff").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdTakeoff(mavros_msgs.CommandTOLRequest request) {
		((MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/takeoff")).call(request);
	}

	public mavros_msgs.CommandTOLResponse getServiceResponse_CmdTakeoff() {
		return (mavros_msgs.CommandTOLResponse) services.get("cmd/takeoff").getResponse();
	}

	public boolean hasServiceResponse_CmdTakeoff() {
		return services.get("cmd/takeoff").hasResponse();
	}

	/**
	 * Service: 'cmd/land'
	 **/

	public mavros_msgs.CommandTOLRequest getNewMessage_CmdLand() {
		return (mavros_msgs.CommandTOLRequest) services.get("cmd/land").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdLand(mavros_msgs.CommandTOLRequest request) {
		((MavrosService<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/land")).call(request);
	}

	public mavros_msgs.CommandTOLResponse getServiceResponse_CmdLand() {
		return (mavros_msgs.CommandTOLResponse) services.get("cmd/land").getResponse();
	}

	public boolean hasServiceResponse_CmdLand() {
		return services.get("cmd/land").hasResponse();
	}

	/**
	 * Service: 'cmd/trigger_control'
	 **/

	public mavros_msgs.CommandTriggerControlRequest getNewMessage_CmdTriggerControl() {
		return (mavros_msgs.CommandTriggerControlRequest) services.get("cmd/trigger_control").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_CmdTriggerControl(mavros_msgs.CommandTriggerControlRequest request) {
		((MavrosService<mavros_msgs.CommandTriggerControlRequest, mavros_msgs.CommandTriggerControlResponse>) services.get("cmd/trigger_control")).call(request);
	}

	public mavros_msgs.CommandTriggerControlResponse getServiceResponse_CmdTriggerControl() {
		return (mavros_msgs.CommandTriggerControlResponse) services.get("cmd/trigger_control").getResponse();
	}

	public boolean hasServiceResponse_CmdTriggerControl() {
		return services.get("cmd/trigger_control").hasResponse();
	}

	/**
	 * Service: 'ftp/open'
	 **/

	public mavros_msgs.FileOpenRequest getNewMessage_FtpOpen() {
		return (mavros_msgs.FileOpenRequest) services.get("ftp/open").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpOpen(mavros_msgs.FileOpenRequest request) {
		((MavrosService<mavros_msgs.FileOpenRequest, mavros_msgs.FileOpenResponse>) services.get("ftp/open")).call(request);
	}

	public mavros_msgs.FileOpenResponse getServiceResponse_FtpOpen() {
		return (mavros_msgs.FileOpenResponse) services.get("ftp/open").getResponse();
	}

	public boolean hasServiceResponse_FtpOpen() {
		return services.get("ftp/open").hasResponse();
	}

	/**
	 * Service: 'ftp/close'
	 **/

	public mavros_msgs.FileCloseRequest getNewMessage_FtpClose() {
		return (mavros_msgs.FileCloseRequest) services.get("ftp/close").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpClose(mavros_msgs.FileCloseRequest request) {
		((MavrosService<mavros_msgs.FileCloseRequest, mavros_msgs.FileCloseResponse>) services.get("ftp/close")).call(request);
	}

	public mavros_msgs.FileCloseResponse getServiceResponse_FtpClose() {
		return (mavros_msgs.FileCloseResponse) services.get("ftp/close").getResponse();
	}

	public boolean hasServiceResponse_FtpClose() {
		return services.get("ftp/close").hasResponse();
	}

	/**
	 * Service: 'ftp/read'
	 **/

	public mavros_msgs.FileReadRequest getNewMessage_FtpRead() {
		return (mavros_msgs.FileReadRequest) services.get("ftp/read").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpRead(mavros_msgs.FileReadRequest request) {
		((MavrosService<mavros_msgs.FileReadRequest, mavros_msgs.FileReadResponse>) services.get("ftp/read")).call(request);
	}

	public mavros_msgs.FileReadResponse getServiceResponse_FtpRead() {
		return (mavros_msgs.FileReadResponse) services.get("ftp/read").getResponse();
	}

	public boolean hasServiceResponse_FtpRead() {
		return services.get("ftp/read").hasResponse();
	}

	/**
	 * Service: 'ftp/write'
	 **/

	public mavros_msgs.FileWriteRequest getNewMessage_FtpWrite() {
		return (mavros_msgs.FileWriteRequest) services.get("ftp/write").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpWrite(mavros_msgs.FileWriteRequest request) {
		((MavrosService<mavros_msgs.FileWriteRequest, mavros_msgs.FileWriteResponse>) services.get("ftp/write")).call(request);
	}

	public mavros_msgs.FileWriteResponse getServiceResponse_FtpWrite() {
		return (mavros_msgs.FileWriteResponse) services.get("ftp/write").getResponse();
	}

	public boolean hasServiceResponse_FtpWrite() {
		return services.get("ftp/write").hasResponse();
	}

	/**
	 * Service: 'ftp/list'
	 **/

	public mavros_msgs.FileListRequest getNewMessage_FtpList() {
		return (mavros_msgs.FileListRequest) services.get("ftp/list").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpList(mavros_msgs.FileListRequest request) {
		((MavrosService<mavros_msgs.FileListRequest, mavros_msgs.FileListResponse>) services.get("ftp/list")).call(request);
	}

	public mavros_msgs.FileListResponse getServiceResponse_FtpList() {
		return (mavros_msgs.FileListResponse) services.get("ftp/list").getResponse();
	}

	public boolean hasServiceResponse_FtpList() {
		return services.get("ftp/list").hasResponse();
	}

	/**
	 * Service: 'ftp/truncate'
	 **/

	public mavros_msgs.FileTruncateRequest getNewMessage_FtpTruncate() {
		return (mavros_msgs.FileTruncateRequest) services.get("ftp/truncate").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpTruncate(mavros_msgs.FileTruncateRequest request) {
		((MavrosService<mavros_msgs.FileTruncateRequest, mavros_msgs.FileTruncateResponse>) services.get("ftp/truncate")).call(request);
	}

	public mavros_msgs.FileTruncateResponse getServiceResponse_FtpTruncate() {
		return (mavros_msgs.FileTruncateResponse) services.get("ftp/truncate").getResponse();
	}

	public boolean hasServiceResponse_FtpTruncate() {
		return services.get("ftp/truncate").hasResponse();
	}

	/**
	 * Service: 'ftp/remove'
	 **/

	public mavros_msgs.FileRemoveRequest getNewMessage_FtpRemove() {
		return (mavros_msgs.FileRemoveRequest) services.get("ftp/remove").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpRemove(mavros_msgs.FileRemoveRequest request) {
		((MavrosService<mavros_msgs.FileRemoveRequest, mavros_msgs.FileRemoveResponse>) services.get("ftp/remove")).call(request);
	}

	public mavros_msgs.FileRemoveResponse getServiceResponse_FtpRemove() {
		return (mavros_msgs.FileRemoveResponse) services.get("ftp/remove").getResponse();
	}

	public boolean hasServiceResponse_FtpRemove() {
		return services.get("ftp/remove").hasResponse();
	}

	/**
	 * Service: 'ftp/rename'
	 **/

	public mavros_msgs.FileRenameRequest getNewMessage_FtpRename() {
		return (mavros_msgs.FileRenameRequest) services.get("ftp/rename").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpRename(mavros_msgs.FileRenameRequest request) {
		((MavrosService<mavros_msgs.FileRenameRequest, mavros_msgs.FileRenameResponse>) services.get("ftp/rename")).call(request);
	}

	public mavros_msgs.FileRenameResponse getServiceResponse_FtpRename() {
		return (mavros_msgs.FileRenameResponse) services.get("ftp/rename").getResponse();
	}

	public boolean hasServiceResponse_FtpRename() {
		return services.get("ftp/rename").hasResponse();
	}

	/**
	 * Service: 'ftp/mkdir'
	 **/

	public mavros_msgs.FileMakeDirRequest getNewMessage_FtpMkdir() {
		return (mavros_msgs.FileMakeDirRequest) services.get("ftp/mkdir").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpMkdir(mavros_msgs.FileMakeDirRequest request) {
		((MavrosService<mavros_msgs.FileMakeDirRequest, mavros_msgs.FileMakeDirResponse>) services.get("ftp/mkdir")).call(request);
	}

	public mavros_msgs.FileMakeDirResponse getServiceResponse_FtpMkdir() {
		return (mavros_msgs.FileMakeDirResponse) services.get("ftp/mkdir").getResponse();
	}

	public boolean hasServiceResponse_FtpMkdir() {
		return services.get("ftp/mkdir").hasResponse();
	}

	/**
	 * Service: 'ftp/rmdir'
	 **/

	public mavros_msgs.FileRemoveDirRequest getNewMessage_FtpRmdir() {
		return (mavros_msgs.FileRemoveDirRequest) services.get("ftp/rmdir").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpRmdir(mavros_msgs.FileRemoveDirRequest request) {
		((MavrosService<mavros_msgs.FileRemoveDirRequest, mavros_msgs.FileRemoveDirResponse>) services.get("ftp/rmdir")).call(request);
	}

	public mavros_msgs.FileRemoveDirResponse getServiceResponse_FtpRmdir() {
		return (mavros_msgs.FileRemoveDirResponse) services.get("ftp/rmdir").getResponse();
	}

	public boolean hasServiceResponse_FtpRmdir() {
		return services.get("ftp/rmdir").hasResponse();
	}

	/**
	 * Service: 'ftp/checksum'
	 **/

	public mavros_msgs.FileChecksumRequest getNewMessage_FtpChecksum() {
		return (mavros_msgs.FileChecksumRequest) services.get("ftp/checksum").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpChecksum(mavros_msgs.FileChecksumRequest request) {
		((MavrosService<mavros_msgs.FileChecksumRequest, mavros_msgs.FileChecksumResponse>) services.get("ftp/checksum")).call(request);
	}

	public mavros_msgs.FileChecksumResponse getServiceResponse_FtpChecksum() {
		return (mavros_msgs.FileChecksumResponse) services.get("ftp/checksum").getResponse();
	}

	public boolean hasServiceResponse_FtpChecksum() {
		return services.get("ftp/checksum").hasResponse();
	}

	/**
	 * Service: 'ftp/reset'
	 **/

	public std_srvs.EmptyRequest getNewMessage_FtpReset() {
		return (std_srvs.EmptyRequest) services.get("ftp/reset").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_FtpReset(std_srvs.EmptyRequest request) {
		((MavrosService<std_srvs.EmptyRequest, std_srvs.EmptyResponse>) services.get("ftp/reset")).call(request);
	}

	public std_srvs.EmptyResponse getServiceResponse_FtpReset() {
		return (std_srvs.EmptyResponse) services.get("ftp/reset").getResponse();
	}

	public boolean hasServiceResponse_FtpReset() {
		return services.get("ftp/reset").hasResponse();
	}

	/**
	 * Service: 'param/pull'
	 **/

	public mavros_msgs.ParamPullRequest getNewMessage_ParamPull() {
		return (mavros_msgs.ParamPullRequest) services.get("param/pull").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_ParamPull(mavros_msgs.ParamPullRequest request) {
		((MavrosService<mavros_msgs.ParamPullRequest, mavros_msgs.ParamPullResponse>) services.get("param/pull")).call(request);
	}

	public mavros_msgs.ParamPullResponse getServiceResponse_ParamPull() {
		return (mavros_msgs.ParamPullResponse) services.get("param/pull").getResponse();
	}

	public boolean hasServiceResponse_ParamPull() {
		return services.get("param/pull").hasResponse();
	}

	/**
	 * Service: 'param/push'
	 **/

	public mavros_msgs.ParamPushRequest getNewMessage_ParamPush() {
		return (mavros_msgs.ParamPushRequest) services.get("param/push").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_ParamPush(mavros_msgs.ParamPushRequest request) {
		((MavrosService<mavros_msgs.ParamPushRequest, mavros_msgs.ParamPushResponse>) services.get("param/push")).call(request);
	}

	public mavros_msgs.ParamPushResponse getServiceResponse_ParamPush() {
		return (mavros_msgs.ParamPushResponse) services.get("param/push").getResponse();
	}

	public boolean hasServiceResponse_ParamPush() {
		return services.get("param/push").hasResponse();
	}

	/**
	 * Service: 'param/get'
	 **/

	public mavros_msgs.ParamGetRequest getNewMessage_ParamGet() {
		return (mavros_msgs.ParamGetRequest) services.get("param/get").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_ParamGet(mavros_msgs.ParamGetRequest request) {
		((MavrosService<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>) services.get("param/get")).call(request);
	}

	public mavros_msgs.ParamGetResponse getServiceResponse_ParamGet() {
		return (mavros_msgs.ParamGetResponse) services.get("param/get").getResponse();
	}

	public boolean hasServiceResponse_ParamGet() {
		return services.get("param/get").hasResponse();
	}

	/**
	 * Service: 'param/set'
	 **/

	public mavros_msgs.ParamSetRequest getNewMessage_ParamSet() {
		return (mavros_msgs.ParamSetRequest) services.get("param/set").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_ParamSet(mavros_msgs.ParamSetRequest request) {
		((MavrosService<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>) services.get("param/set")).call(request);
	}

	public mavros_msgs.ParamSetResponse getServiceResponse_ParamSet() {
		return (mavros_msgs.ParamSetResponse) services.get("param/set").getResponse();
	}

	public boolean hasServiceResponse_ParamSet() {
		return services.get("param/set").hasResponse();
	}

	/**
	 * Service: 'set_stream_rate'
	 **/

	public mavros_msgs.StreamRateRequest getNewMessage_SetStreamRate() {
		return (mavros_msgs.StreamRateRequest) services.get("set_stream_rate").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_SetStreamRate(mavros_msgs.StreamRateRequest request) {
		((MavrosService<mavros_msgs.StreamRateRequest, mavros_msgs.StreamRateResponse>) services.get("set_stream_rate")).call(request);
	}

	public mavros_msgs.StreamRateResponse getServiceResponse_SetStreamRate() {
		return (mavros_msgs.StreamRateResponse) services.get("set_stream_rate").getResponse();
	}

	public boolean hasServiceResponse_SetStreamRate() {
		return services.get("set_stream_rate").hasResponse();
	}

	/**
	 * Service: 'set_mode'
	 **/

	public mavros_msgs.SetModeRequest getNewMessage_SetMode() {
		return (mavros_msgs.SetModeRequest) services.get("set_mode").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_SetMode(mavros_msgs.SetModeRequest request) {
		((MavrosService<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>) services.get("set_mode")).call(request);
	}

	public mavros_msgs.SetModeResponse getServiceResponse_SetMode() {
		return (mavros_msgs.SetModeResponse) services.get("set_mode").getResponse();
	}

	public boolean hasServiceResponse_SetMode() {
		return services.get("set_mode").hasResponse();
	}

	/**
	 * Service: 'mission/pull'
	 **/

	public mavros_msgs.WaypointPullRequest getNewMessage_MissionPull() {
		return (mavros_msgs.WaypointPullRequest) services.get("mission/pull").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_MissionPull(mavros_msgs.WaypointPullRequest request) {
		((MavrosService<mavros_msgs.WaypointPullRequest, mavros_msgs.WaypointPullResponse>) services.get("mission/pull")).call(request);
	}

	public mavros_msgs.WaypointPullResponse getServiceResponse_MissionPull() {
		return (mavros_msgs.WaypointPullResponse) services.get("mission/pull").getResponse();
	}

	public boolean hasServiceResponse_MissionPull() {
		return services.get("mission/pull").hasResponse();
	}

	/**
	 * Service: 'mission/push'
	 **/

	public mavros_msgs.WaypointPushRequest getNewMessage_MissionPush() {
		return (mavros_msgs.WaypointPushRequest) services.get("mission/push").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_MissionPush(mavros_msgs.WaypointPushRequest request) {
		((MavrosService<mavros_msgs.WaypointPushRequest, mavros_msgs.WaypointPushResponse>) services.get("mission/push")).call(request);
	}

	public mavros_msgs.WaypointPushResponse getServiceResponse_MissionPush() {
		return (mavros_msgs.WaypointPushResponse) services.get("mission/push").getResponse();
	}

	public boolean hasServiceResponse_MissionPush() {
		return services.get("mission/push").hasResponse();
	}

	/**
	 * Service: 'mission/clear'
	 **/

	public mavros_msgs.WaypointClearRequest getNewMessage_MissionClear() {
		return (mavros_msgs.WaypointClearRequest) services.get("mission/clear").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_MissionClear(mavros_msgs.WaypointClearRequest request) {
		((MavrosService<mavros_msgs.WaypointClearRequest, mavros_msgs.WaypointClearResponse>) services.get("mission/clear")).call(request);
	}

	public mavros_msgs.WaypointClearResponse getServiceResponse_MissionClear() {
		return (mavros_msgs.WaypointClearResponse) services.get("mission/clear").getResponse();
	}

	public boolean hasServiceResponse_MissionClear() {
		return services.get("mission/clear").hasResponse();
	}

	/**
	 * Service: 'mission/set_current'
	 **/

	public mavros_msgs.WaypointSetCurrentRequest getNewMessage_MissionSetCurrent() {
		return (mavros_msgs.WaypointSetCurrentRequest) services.get("mission/set_current").newMessage();
	}

	@SuppressWarnings("unchecked")
	public void callService_MissionSetCurrent(mavros_msgs.WaypointSetCurrentRequest request) {
		((MavrosService<mavros_msgs.WaypointSetCurrentRequest, mavros_msgs.WaypointSetCurrentResponse>) services.get("mission/set_current")).call(request);
	}

	public mavros_msgs.WaypointSetCurrentResponse getServiceResponse_MissionSetCurrent() {
		return (mavros_msgs.WaypointSetCurrentResponse) services.get("mission/set_current").getResponse();
	}

	public boolean hasServiceResponse_MissionSetCurrent() {
		return services.get("mission/set_current").hasResponse();
	}
	
}
