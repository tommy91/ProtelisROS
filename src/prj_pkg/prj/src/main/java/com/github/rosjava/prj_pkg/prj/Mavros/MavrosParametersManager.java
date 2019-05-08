package com.github.rosjava.prj_pkg.prj;

import java.util.Collections;
import java.util.List;

import org.ros.node.ConnectedNode;

public class MavrosParametersManager extends ParametersManager {
	
	public MavrosParametersManager(PrjNode prjNode, ConnectedNode connectedNode) {
		super(prjNode, connectedNode);
	}
	
	/**
	 * Parameter: "~system_id"
	 * Type: int
	 * Default value: 1
	 **/

	public Integer getMavrosParam_SystemId() {
		return getIntegerParam("~system_id");
	}

	public void setMavrosParam_SystemId(int val) {
		setParam("~system_id", val);
	}

	public void resetMavrosParam_SystemId() {
		setParam("~system_id", 1);
	}

	/**
	 * Parameter: "~component_id"
	 * Type: int
	 * Default value: 240
	 **/

	public Integer getMavrosParam_ComponentId() {
		return getIntegerParam("~component_id");
	}

	public void setMavrosParam_ComponentId(int val) {
		setParam("~component_id", val);
	}

	public void resetMavrosParam_ComponentId() {
		setParam("~component_id", 240);
	}

	/**
	 * Parameter: "~target_system_id"
	 * Type: int
	 * Default value: 1
	 **/

	public Integer getMavrosParam_TargetSystemId() {
		return getIntegerParam("~target_system_id");
	}

	public void setMavrosParam_TargetSystemId(int val) {
		setParam("~target_system_id", val);
	}

	public void resetMavrosParam_TargetSystemId() {
		setParam("~target_system_id", 1);
	}

	/**
	 * Parameter: "~target_component_id"
	 * Type: int
	 * Default value: 1
	 **/

	public Integer getMavrosParam_TargetComponentId() {
		return getIntegerParam("~target_component_id");
	}

	public void setMavrosParam_TargetComponentId(int val) {
		setParam("~target_component_id", val);
	}

	public void resetMavrosParam_TargetComponentId() {
		setParam("~target_component_id", 1);
	}

	/**
	 * Parameter: "~startup_px4_usb_quirk"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_StartupPx4UsbQuirk() {
		return getBooleanParam("~startup_px4_usb_quirk");
	}

	public void setMavrosParam_StartupPx4UsbQuirk(boolean val) {
		setParam("~startup_px4_usb_quirk", val);
	}

	public void resetMavrosParam_StartupPx4UsbQuirk() {
		setParam("~startup_px4_usb_quirk", false);
	}

	/**
	 * Parameter: "~plugin_blacklist"
	 * Type: string[]
	 * Default value: []
	 **/

	public List<String> getMavrosParam_PluginBlacklist() {
		return getStringListParam("~plugin_blacklist");
	}

	public void setMavrosParam_PluginBlacklist(List<String> val) {
		setParam("~plugin_blacklist", val);
	}

	public void resetMavrosParam_PluginBlacklist() {
		setParam("~plugin_blacklist", Collections.<String>emptyList());
	}

	/**
	 * Parameter: "~plugin_whitelist"
	 * Type: string[]
	 * Default value: []
	 **/

	public List<String> getMavrosParam_PluginWhitelist() {
		return getStringListParam("~plugin_whitelist");
	}

	public void setMavrosParam_PluginWhitelist(List<String> val) {
		setParam("~plugin_whitelist", val);
	}

	public void resetMavrosParam_PluginWhitelist() {
		setParam("~plugin_whitelist", Collections.<String>emptyList());
	}

	/**
	 * Parameter: "~fcu_url"
	 * Type: string
	 * Default value: "/dev/ttyACM0:57600"
	 **/

	public String getMavrosParam_FcuUrl() {
		return getStringParam("~fcu_url");
	}

	public void setMavrosParam_FcuUrl(String val) {
		setParam("~fcu_url", val);
	}

	public void resetMavrosParam_FcuUrl() {
		setParam("~fcu_url", "/dev/ttyACM0:57600");
	}

	/**
	 * Parameter: "~fcu_protocol"
	 * Type: string
	 * Default value: "v2.0"
	 **/

	public String getMavrosParam_FcuProtocol() {
		return getStringParam("~fcu_protocol");
	}

	public void setMavrosParam_FcuProtocol(String val) {
		setParam("~fcu_protocol", val);
	}

	public void resetMavrosParam_FcuProtocol() {
		setParam("~fcu_protocol", "v2.0");
	}

	/**
	 * Parameter: "~gcs_url"
	 * Type: string
	 * Default value: "udp://@"
	 **/

	public String getMavrosParam_GcsUrl() {
		return getStringParam("~gcs_url");
	}

	public void setMavrosParam_GcsUrl(String val) {
		setParam("~gcs_url", val);
	}

	public void resetMavrosParam_GcsUrl() {
		setParam("~gcs_url", "udp://@");
	}

	/**
	 * Parameter: "~frame_id"
	 * Type: string
	 * Default value: "map"
	 **/

	public String getMavrosParam_FrameId() {
		return getStringParam("~frame_id");
	}

	public void setMavrosParam_FrameId(String val) {
		setParam("~frame_id", val);
	}

	public void resetMavrosParam_FrameId() {
		setParam("~frame_id", "map");
	}

	/**
	 * Parameter: "~cmd/use_comp_id_system_control"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_CmdUseCompIdSystemControl() {
		return getBooleanParam("~cmd/use_comp_id_system_control");
	}

	public void setMavrosParam_CmdUseCompIdSystemControl(boolean val) {
		setParam("~cmd/use_comp_id_system_control", val);
	}

	public void resetMavrosParam_CmdUseCompIdSystemControl() {
		setParam("~cmd/use_comp_id_system_control", false);
	}

	/**
	 * Parameter: "~global_position/frame_id"
	 * Type: string
	 * Default value: "fcu"
	 **/

	public String getMavrosParam_GlobalPositionFrameId() {
		return getStringParam("~global_position/frame_id");
	}

	public void setMavrosParam_GlobalPositionFrameId(String val) {
		setParam("~global_position/frame_id", val);
	}

	public void resetMavrosParam_GlobalPositionFrameId() {
		setParam("~global_position/frame_id", "fcu");
	}

	/**
	 * Parameter: "~global_position/tf/send"
	 * Type: bool
	 * Default value: true
	 **/

	public Boolean getMavrosParam_GlobalPositionTfSend() {
		return getBooleanParam("~global_position/tf/send");
	}

	public void setMavrosParam_GlobalPositionTfSend(boolean val) {
		setParam("~global_position/tf/send", val);
	}

	public void resetMavrosParam_GlobalPositionTfSend() {
		setParam("~global_position/tf/send", true);
	}

	/**
	 * Parameter: "~global_position/tf/frame_id"
	 * Type: string
	 * Default value: "local_origin"
	 **/

	public String getMavrosParam_GlobalPositionTfFrameId() {
		return getStringParam("~global_position/tf/frame_id");
	}

	public void setMavrosParam_GlobalPositionTfFrameId(String val) {
		setParam("~global_position/tf/frame_id", val);
	}

	public void resetMavrosParam_GlobalPositionTfFrameId() {
		setParam("~global_position/tf/frame_id", "local_origin");
	}

	/**
	 * Parameter: "~global_position/tf/child_frame_id"
	 * Type: string
	 * Default value: "fcu_utmv"
	 **/

	public String getMavrosParam_GlobalPositionTfChildFrameId() {
		return getStringParam("~global_position/tf/child_frame_id");
	}

	public void setMavrosParam_GlobalPositionTfChildFrameId(String val) {
		setParam("~global_position/tf/child_frame_id", val);
	}

	public void resetMavrosParam_GlobalPositionTfChildFrameId() {
		setParam("~global_position/tf/child_frame_id", "fcu_utmv");
	}

	/**
	 * Parameter: "~imu/frame_id"
	 * Type: string
	 * Default value: "fcu"
	 **/

	public String getMavrosParam_ImuFrameId() {
		return getStringParam("~imu/frame_id");
	}

	public void setMavrosParam_ImuFrameId(String val) {
		setParam("~imu/frame_id", val);
	}

	public void resetMavrosParam_ImuFrameId() {
		setParam("~imu/frame_id", "fcu");
	}

	/**
	 * Parameter: "~imu/linear_acceleration_stdev"
	 * Type: double
	 * Default value: 0.0003
	 **/

	public Double getMavrosParam_ImuLinearAccelerationStdev() {
		return getDoubleParam("~imu/linear_acceleration_stdev");
	}

	public void setMavrosParam_ImuLinearAccelerationStdev(double val) {
		setParam("~imu/linear_acceleration_stdev", val);
	}

	public void resetMavrosParam_ImuLinearAccelerationStdev() {
		setParam("~imu/linear_acceleration_stdev", 0.0003);
	}

	/**
	 * Parameter: "~imu/angular_velocity_stdev"
	 * Type: double
	 * Default value: 0.02
	 **/

	public Double getMavrosParam_ImuAngularVelocityStdev() {
		return getDoubleParam("~imu/angular_velocity_stdev");
	}

	public void setMavrosParam_ImuAngularVelocityStdev(double val) {
		setParam("~imu/angular_velocity_stdev", val);
	}

	public void resetMavrosParam_ImuAngularVelocityStdev() {
		setParam("~imu/angular_velocity_stdev", 0.02);
	}

	/**
	 * Parameter: "~imu/orientation_stdev"
	 * Type: double
	 * Default value: 1.0
	 **/

	public Double getMavrosParam_ImuOrientationStdev() {
		return getDoubleParam("~imu/orientation_stdev");
	}

	public void setMavrosParam_ImuOrientationStdev(double val) {
		setParam("~imu/orientation_stdev", val);
	}

	public void resetMavrosParam_ImuOrientationStdev() {
		setParam("~imu/orientation_stdev", 1.0);
	}

	/**
	 * Parameter: "~imu/magnetic_stdev"
	 * Type: double
	 * Default value: 0.0
	 **/

	public Double getMavrosParam_ImuMagneticStdev() {
		return getDoubleParam("~imu/magnetic_stdev");
	}

	public void setMavrosParam_ImuMagneticStdev(double val) {
		setParam("~imu/magnetic_stdev", val);
	}

	public void resetMavrosParam_ImuMagneticStdev() {
		setParam("~imu/magnetic_stdev", 0.0);
	}

	/**
	 * Parameter: "~local_position/frame_id"
	 * Type: string
	 * Default value: "fcu"
	 **/

	public String getMavrosParam_LocalPositionFrameId() {
		return getStringParam("~local_position/frame_id");
	}

	public void setMavrosParam_LocalPositionFrameId(String val) {
		setParam("~local_position/frame_id", val);
	}

	public void resetMavrosParam_LocalPositionFrameId() {
		setParam("~local_position/frame_id", "fcu");
	}

	/**
	 * Parameter: "~local_position/tf/send"
	 * Type: bool
	 * Default value: true
	 **/

	public Boolean getMavrosParam_LocalPositionTfSend() {
		return getBooleanParam("~local_position/tf/send");
	}

	public void setMavrosParam_LocalPositionTfSend(boolean val) {
		setParam("~local_position/tf/send", val);
	}

	public void resetMavrosParam_LocalPositionTfSend() {
		setParam("~local_position/tf/send", true);
	}

	/**
	 * Parameter: "~local_position/tf/frame_id"
	 * Type: string
	 * Default value: "local_origin"
	 **/

	public String getMavrosParam_LocalPositionTfFrameId() {
		return getStringParam("~local_position/tf/frame_id");
	}

	public void setMavrosParam_LocalPositionTfFrameId(String val) {
		setParam("~local_position/tf/frame_id", val);
	}

	public void resetMavrosParam_LocalPositionTfFrameId() {
		setParam("~local_position/tf/frame_id", "local_origin");
	}

	/**
	 * Parameter: "~local_position/tf/child_frame_id"
	 * Type: string
	 * Default value: "fcu"
	 **/

	public String getMavrosParam_LocalPositionTfChildFrameId() {
		return getStringParam("~local_position/tf/child_frame_id");
	}

	public void setMavrosParam_LocalPositionTfChildFrameId(String val) {
		setParam("~local_position/tf/child_frame_id", val);
	}

	public void resetMavrosParam_LocalPositionTfChildFrameId() {
		setParam("~local_position/tf/child_frame_id", "fcu");
	}

	/**
	 * Parameter: "~safety_area/p1/x"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP1X() {
		return getDoubleParam("~safety_area/p1/x");
	}

	public void setMavrosParam_SafetyAreaP1X(double val) {
		setParam("~safety_area/p1/x", val);
	}

	/**
	 * Parameter: "~safety_area/p1/y"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP1Y() {
		return getDoubleParam("~safety_area/p1/y");
	}

	public void setMavrosParam_SafetyAreaP1Y(double val) {
		setParam("~safety_area/p1/y", val);
	}

	/**
	 * Parameter: "~safety_area/p1/z"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP1Z() {
		return getDoubleParam("~safety_area/p1/z");
	}

	public void setMavrosParam_SafetyAreaP1Z(double val) {
		setParam("~safety_area/p1/z", val);
	}

	/**
	 * Parameter: "~safety_area/p2/x"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP2X() {
		return getDoubleParam("~safety_area/p2/x");
	}

	public void setMavrosParam_SafetyAreaP2X(double val) {
		setParam("~safety_area/p2/x", val);
	}

	/**
	 * Parameter: "~safety_area/p2/y"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP2Y() {
		return getDoubleParam("~safety_area/p2/y");
	}

	public void setMavrosParam_SafetyAreaP2Y(double val) {
		setParam("~safety_area/p2/y", val);
	}

	/**
	 * Parameter: "~safety_area/p2/z"
	 * Type: double
	 * Default value: 
	 **/

	public Double getMavrosParam_SafetyAreaP2Z() {
		return getDoubleParam("~safety_area/p2/z");
	}

	public void setMavrosParam_SafetyAreaP2Z(double val) {
		setParam("~safety_area/p2/z", val);
	}

	/**
	 * Parameter: "~setpoint_accel/send_force"
	 * Type: bool
	 * Default value: 
	 **/

	public Boolean getMavrosParam_SetpointAccelSendForce() {
		return getBooleanParam("~setpoint_accel/send_force");
	}

	public void setMavrosParam_SetpointAccelSendForce(boolean val) {
		setParam("~setpoint_accel/send_force", val);
	}

	/**
	 * Parameter: "~setpoint_attitude/reverse_throttle"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_SetpointAttitudeReverseThrottle() {
		return getBooleanParam("~setpoint_attitude/reverse_throttle");
	}

	public void setMavrosParam_SetpointAttitudeReverseThrottle(boolean val) {
		setParam("~setpoint_attitude/reverse_throttle", val);
	}

	public void resetMavrosParam_SetpointAttitudeReverseThrottle() {
		setParam("~setpoint_attitude/reverse_throttle", false);
	}

	/**
	 * Parameter: "~setpoint_attitude/use_quaternion"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_SetpointAttitudeUseQuaternion() {
		return getBooleanParam("~setpoint_attitude/use_quaternion");
	}

	public void setMavrosParam_SetpointAttitudeUseQuaternion(boolean val) {
		setParam("~setpoint_attitude/use_quaternion", val);
	}

	public void resetMavrosParam_SetpointAttitudeUseQuaternion() {
		setParam("~setpoint_attitude/use_quaternion", false);
	}

	/**
	 * Parameter: "~setpoint_attitude/tf/listen"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_SetpointAttitudeTfListen() {
		return getBooleanParam("~setpoint_attitude/tf/listen");
	}

	public void setMavrosParam_SetpointAttitudeTfListen(boolean val) {
		setParam("~setpoint_attitude/tf/listen", val);
	}

	public void resetMavrosParam_SetpointAttitudeTfListen() {
		setParam("~setpoint_attitude/tf/listen", false);
	}

	/**
	 * Parameter: "~setpoint_attutude/tf/frame_id"
	 * Type: string
	 * Default value: "local_origin"
	 **/

	public String getMavrosParam_SetpointAttutudeTfFrameId() {
		return getStringParam("~setpoint_attutude/tf/frame_id");
	}

	public void setMavrosParam_SetpointAttutudeTfFrameId(String val) {
		setParam("~setpoint_attutude/tf/frame_id", val);
	}

	public void resetMavrosParam_SetpointAttutudeTfFrameId() {
		setParam("~setpoint_attutude/tf/frame_id", "local_origin");
	}

	/**
	 * Parameter: "~setpoint_attitude/tf/child_frame_id"
	 * Type: string
	 * Default value: "attitude"
	 **/

	public String getMavrosParam_SetpointAttitudeTfChildFrameId() {
		return getStringParam("~setpoint_attitude/tf/child_frame_id");
	}

	public void setMavrosParam_SetpointAttitudeTfChildFrameId(String val) {
		setParam("~setpoint_attitude/tf/child_frame_id", val);
	}

	public void resetMavrosParam_SetpointAttitudeTfChildFrameId() {
		setParam("~setpoint_attitude/tf/child_frame_id", "attitude");
	}

	/**
	 * Parameter: "~setpoint_attitude/tf/rate_limit"
	 * Type: double
	 * Default value: 10.0
	 **/

	public Double getMavrosParam_SetpointAttitudeTfRateLimit() {
		return getDoubleParam("~setpoint_attitude/tf/rate_limit");
	}

	public void setMavrosParam_SetpointAttitudeTfRateLimit(double val) {
		setParam("~setpoint_attitude/tf/rate_limit", val);
	}

	public void resetMavrosParam_SetpointAttitudeTfRateLimit() {
		setParam("~setpoint_attitude/tf/rate_limit", 10.0);
	}

	/**
	 * Parameter: "~setpoint_position/tf/listen"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_SetpointPositionTfListen() {
		return getBooleanParam("~setpoint_position/tf/listen");
	}

	public void setMavrosParam_SetpointPositionTfListen(boolean val) {
		setParam("~setpoint_position/tf/listen", val);
	}

	public void resetMavrosParam_SetpointPositionTfListen() {
		setParam("~setpoint_position/tf/listen", false);
	}

	/**
	 * Parameter: "~setpoint_position/tf/frame_id"
	 * Type: string
	 * Default value: "local_origin"
	 **/

	public String getMavrosParam_SetpointPositionTfFrameId() {
		return getStringParam("~setpoint_position/tf/frame_id");
	}

	public void setMavrosParam_SetpointPositionTfFrameId(String val) {
		setParam("~setpoint_position/tf/frame_id", val);
	}

	public void resetMavrosParam_SetpointPositionTfFrameId() {
		setParam("~setpoint_position/tf/frame_id", "local_origin");
	}

	/**
	 * Parameter: "~setpoint_position/tf/child_frame_id"
	 * Type: string
	 * Default value: "setpoint"
	 **/

	public String getMavrosParam_SetpointPositionTfChildFrameId() {
		return getStringParam("~setpoint_position/tf/child_frame_id");
	}

	public void setMavrosParam_SetpointPositionTfChildFrameId(String val) {
		setParam("~setpoint_position/tf/child_frame_id", val);
	}

	public void resetMavrosParam_SetpointPositionTfChildFrameId() {
		setParam("~setpoint_position/tf/child_frame_id", "setpoint");
	}

	/**
	 * Parameter: "~setpoint_position/tf/rate_limit"
	 * Type: double
	 * Default value: 50.0
	 **/

	public Double getMavrosParam_SetpointPositionTfRateLimit() {
		return getDoubleParam("~setpoint_position/tf/rate_limit");
	}

	public void setMavrosParam_SetpointPositionTfRateLimit(double val) {
		setParam("~setpoint_position/tf/rate_limit", val);
	}

	public void resetMavrosParam_SetpointPositionTfRateLimit() {
		setParam("~setpoint_position/tf/rate_limit", 50.0);
	}

	/**
	 * Parameter: "~conn/timeout"
	 * Type: double
	 * Default value: 30.0
	 **/

	public Double getMavrosParam_ConnTimeout() {
		return getDoubleParam("~conn/timeout");
	}

	public void setMavrosParam_ConnTimeout(double val) {
		setParam("~conn/timeout", val);
	}

	public void resetMavrosParam_ConnTimeout() {
		setParam("~conn/timeout", 30.0);
	}

	/**
	 * Parameter: "~conn/heartbeat_rate"
	 * Type: double
	 * Default value: 0.0
	 **/

	public Double getMavrosParam_ConnHeartbeatRate() {
		return getDoubleParam("~conn/heartbeat_rate");
	}

	public void setMavrosParam_ConnHeartbeatRate(double val) {
		setParam("~conn/heartbeat_rate", val);
	}

	public void resetMavrosParam_ConnHeartbeatRate() {
		setParam("~conn/heartbeat_rate", 0.0);
	}

	/**
	 * Parameter: "~sys/min_voltage"
	 * Type: double
	 * Default value: 6.0
	 **/

	public Double getMavrosParam_SysMinVoltage() {
		return getDoubleParam("~sys/min_voltage");
	}

	public void setMavrosParam_SysMinVoltage(double val) {
		setParam("~sys/min_voltage", val);
	}

	public void resetMavrosParam_SysMinVoltage() {
		setParam("~sys/min_voltage", 6.0);
	}

	/**
	 * Parameter: "~sys/disable_diag"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_SysDisableDiag() {
		return getBooleanParam("~sys/disable_diag");
	}

	public void setMavrosParam_SysDisableDiag(boolean val) {
		setParam("~sys/disable_diag", val);
	}

	public void resetMavrosParam_SysDisableDiag() {
		setParam("~sys/disable_diag", false);
	}

	/**
	 * Parameter: "~conn/system_time_rate"
	 * Type: double
	 * Default value: 0.0
	 **/

	public Double getMavrosParam_ConnSystemTimeRate() {
		return getDoubleParam("~conn/system_time_rate");
	}

	public void setMavrosParam_ConnSystemTimeRate(double val) {
		setParam("~conn/system_time_rate", val);
	}

	public void resetMavrosParam_ConnSystemTimeRate() {
		setParam("~conn/system_time_rate", 0.0);
	}

	/**
	 * Parameter: "~conn/timesync_rate"
	 * Type: double
	 * Default value: 0.0
	 **/

	public Double getMavrosParam_ConnTimesyncRate() {
		return getDoubleParam("~conn/timesync_rate");
	}

	public void setMavrosParam_ConnTimesyncRate(double val) {
		setParam("~conn/timesync_rate", val);
	}

	public void resetMavrosParam_ConnTimesyncRate() {
		setParam("~conn/timesync_rate", 0.0);
	}

	/**
	 * Parameter: "~time/time_ref_source"
	 * Type: string
	 * Default value: "fcu"
	 **/

	public String getMavrosParam_TimeTimeRefSource() {
		return getStringParam("~time/time_ref_source");
	}

	public void setMavrosParam_TimeTimeRefSource(String val) {
		setParam("~time/time_ref_source", val);
	}

	public void resetMavrosParam_TimeTimeRefSource() {
		setParam("~time/time_ref_source", "fcu");
	}

	/**
	 * Parameter: "~time/timesync_avg_alpha"
	 * Type: double
	 * Default value: 0.6
	 **/

	public Double getMavrosParam_TimeTimesyncAvgAlpha() {
		return getDoubleParam("~time/timesync_avg_alpha");
	}

	public void setMavrosParam_TimeTimesyncAvgAlpha(double val) {
		setParam("~time/timesync_avg_alpha", val);
	}

	public void resetMavrosParam_TimeTimesyncAvgAlpha() {
		setParam("~time/timesync_avg_alpha", 0.6);
	}

	/**
	 * Parameter: "~mission/pull_after_gcs"
	 * Type: bool
	 * Default value: false
	 **/

	public Boolean getMavrosParam_MissionPullAfterGcs() {
		return getBooleanParam("~mission/pull_after_gcs");
	}

	public void setMavrosParam_MissionPullAfterGcs(boolean val) {
		setParam("~mission/pull_after_gcs", val);
	}

	public void resetMavrosParam_MissionPullAfterGcs() {
		setParam("~mission/pull_after_gcs", false);
	}

}
