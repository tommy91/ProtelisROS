package com.github.rosjava.prj_pkg.prj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.node.ConnectedNode;

public class StatusManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private ExecutionEnvironment executionEnvironment;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,MavrosSubscriber<?>> subscribers = new HashMap<>();
	private List<String> interestedTopics = Arrays.asList(
			"state",
			"global_position/global",
			"global_position/rel_alt"
			); 
	
	public StatusManager(PrjNode prjNode, ConnectedNode connectedNode, ExecutionEnvironment executionEnvironment) {
		this.prjNode = prjNode;
		this.connectedNode = connectedNode;
		this.executionEnvironment = executionEnvironment;
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
		setupMavrosSubscribers();
	}
	
	public List<String> getInterestedTopics() {
		return interestedTopics;
	}
	
	public void subscribeInterestedTopics() {
		for (String topic : interestedTopics) {
			prjNode.printLog("Subscribing to " + mavrosPrefix + topic + ".. ");
			subscribers.get(topic).subscribe();
		}
	}
	
	private void setupMavrosSubscribers() {
		subscribers.put("mavlink/from", new MavrosSubscriber<mavros_msgs.Mavlink>(connectedNode, executionEnvironment, mavrosPrefix, "mavlink/from", mavros_msgs.Mavlink._TYPE));
		subscribers.put("diagnostics", new MavrosSubscriber<diagnostic_msgs.DiagnosticStatus>(connectedNode, executionEnvironment, mavrosPrefix, "diagnostics", diagnostic_msgs.DiagnosticStatus._TYPE));
		subscribers.put("radio_status", new MavrosSubscriber<mavros_msgs.RadioStatus>(connectedNode, executionEnvironment, mavrosPrefix, "radio_status", mavros_msgs.RadioStatus._TYPE));
		subscribers.put("hil_controls/hil_controls", new MavrosSubscriber<mavros_msgs.HilControls>(connectedNode, executionEnvironment, mavrosPrefix, "hil_controls/hil_controls", mavros_msgs.HilControls._TYPE));
		subscribers.put("global_position/global", new MavrosSubscriber<sensor_msgs.NavSatFix>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/global", sensor_msgs.NavSatFix._TYPE));
		subscribers.put("global_position/local", new MavrosSubscriber<geometry_msgs.PoseWithCovarianceStamped>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/local", geometry_msgs.PoseWithCovarianceStamped._TYPE));
		subscribers.put("global_position/gp_vel", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/gp_vel", geometry_msgs.TwistStamped._TYPE));
		subscribers.put("global_position/rel_alt", new MavrosSubscriber<std_msgs.Float64>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/rel_alt", std_msgs.Float64._TYPE));
		subscribers.put("global_position/compass_hdg", new MavrosSubscriber<std_msgs.Float64>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/compass_hdg", std_msgs.Float64._TYPE));
		subscribers.put("global_position/raw/fix", new MavrosSubscriber<sensor_msgs.NavSatFix>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/raw/fix", sensor_msgs.NavSatFix._TYPE));
		subscribers.put("global_position/raw/gps_vel", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, executionEnvironment, mavrosPrefix, "global_position/raw/gps_vel", geometry_msgs.TwistStamped._TYPE));
		subscribers.put("imu/data", new MavrosSubscriber<sensor_msgs.Imu>(connectedNode, executionEnvironment, mavrosPrefix, "imu/data", sensor_msgs.Imu._TYPE));
		subscribers.put("imu/data_raw", new MavrosSubscriber<sensor_msgs.Imu>(connectedNode, executionEnvironment, mavrosPrefix, "imu/data_raw", sensor_msgs.Imu._TYPE));
		subscribers.put("imu/mag", new MavrosSubscriber<sensor_msgs.MagneticField>(connectedNode, executionEnvironment, mavrosPrefix, "imu/mag", sensor_msgs.MagneticField._TYPE));
		subscribers.put("imu/temperature", new MavrosSubscriber<sensor_msgs.Temperature>(connectedNode, executionEnvironment, mavrosPrefix, "imu/temperature", sensor_msgs.Temperature._TYPE));
		subscribers.put("imu/atm_pressure", new MavrosSubscriber<sensor_msgs.FluidPressure>(connectedNode, executionEnvironment, mavrosPrefix, "imu/atm_pressure", sensor_msgs.FluidPressure._TYPE));
		subscribers.put("local_position/pose", new MavrosSubscriber<geometry_msgs.PoseStamped>(connectedNode, executionEnvironment, mavrosPrefix, "local_position/pose", geometry_msgs.PoseStamped._TYPE));
		subscribers.put("local_position/velocity", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, executionEnvironment, mavrosPrefix, "local_position/velocity", geometry_msgs.TwistStamped._TYPE));
		subscribers.put("manual_control/control", new MavrosSubscriber<mavros_msgs.ManualControl>(connectedNode, executionEnvironment, mavrosPrefix, "manual_control/control", mavros_msgs.ManualControl._TYPE));
		subscribers.put("rc/in", new MavrosSubscriber<mavros_msgs.RCIn>(connectedNode, executionEnvironment, mavrosPrefix, "rc/in", mavros_msgs.RCIn._TYPE));
		subscribers.put("rc/out", new MavrosSubscriber<mavros_msgs.RCOut>(connectedNode, executionEnvironment, mavrosPrefix, "rc/out", mavros_msgs.RCOut._TYPE));
		subscribers.put("setpoint_raw/target_local", new MavrosSubscriber<mavros_msgs.PositionTarget>(connectedNode, executionEnvironment, mavrosPrefix, "setpoint_raw/target_local", mavros_msgs.PositionTarget._TYPE));
		subscribers.put("setpoint_raw/target_global", new MavrosSubscriber<mavros_msgs.GlobalPositionTarget>(connectedNode, executionEnvironment, mavrosPrefix, "setpoint_raw/target_global", mavros_msgs.GlobalPositionTarget._TYPE));
		subscribers.put("setpoint_raw/target_attitude", new MavrosSubscriber<mavros_msgs.AttitudeTarget>(connectedNode, executionEnvironment, mavrosPrefix, "setpoint_raw/target_attitude", mavros_msgs.AttitudeTarget._TYPE));
		subscribers.put("state", new MavrosSubscriber<mavros_msgs.State>(connectedNode, executionEnvironment, mavrosPrefix, "state", mavros_msgs.State._TYPE));
		subscribers.put("battery", new MavrosSubscriber<mavros_msgs.BatteryStatus>(connectedNode, executionEnvironment, mavrosPrefix, "battery", mavros_msgs.BatteryStatus._TYPE));
		subscribers.put("battery", new MavrosSubscriber<sensor_msgs.BatteryState>(connectedNode, executionEnvironment, mavrosPrefix, "battery", sensor_msgs.BatteryState._TYPE));
		subscribers.put("extended_state", new MavrosSubscriber<mavros_msgs.ExtendedState>(connectedNode, executionEnvironment, mavrosPrefix, "extended_state", mavros_msgs.ExtendedState._TYPE));
		subscribers.put("time_reference", new MavrosSubscriber<sensor_msgs.TimeReference>(connectedNode, executionEnvironment, mavrosPrefix, "time_reference", sensor_msgs.TimeReference._TYPE));
		subscribers.put("vfr_hud", new MavrosSubscriber<mavros_msgs.VFR_HUD>(connectedNode, executionEnvironment, mavrosPrefix, "vfr_hud", mavros_msgs.VFR_HUD._TYPE));
		subscribers.put("wind_estimation", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, executionEnvironment, mavrosPrefix, "wind_estimation", geometry_msgs.TwistStamped._TYPE));
		subscribers.put("mission/reached", new MavrosSubscriber<mavros_msgs.WaypointReached>(connectedNode, executionEnvironment, mavrosPrefix, "mission/reached", mavros_msgs.WaypointReached._TYPE));
		subscribers.put("mission/waypoints", new MavrosSubscriber<mavros_msgs.WaypointList>(connectedNode, executionEnvironment, mavrosPrefix, "mission/waypoints", mavros_msgs.WaypointList._TYPE));
	}
	
	/**
	 * Subscribing topic: "mavlink/from"
	 * Message type: mavros_msgs.Mavlink
	 **/

	public void subscribe_MavlinkFrom() {
		subscribers.get("mavlink/from").subscribe();
	}

	public void unsubscribe_MavlinkFrom() {
		subscribers.get("mavlink/from").unsubscribe();
	}

	public boolean hasMavlinkFromValues() {
		return executionEnvironment.has("mavlink/from");
	}

	public mavros_msgs.Mavlink getMavlinkFromValues() {
		return (mavros_msgs.Mavlink) executionEnvironment.get("mavlink/from");
	}

	/**
	 * Subscribing topic: "diagnostics"
	 * Message type: diagnostic_msgs.DiagnosticStatus
	 **/

	public void subscribe_Diagnostics() {
		subscribers.get("diagnostics").subscribe();
	}

	public void unsubscribe_Diagnostics() {
		subscribers.get("diagnostics").unsubscribe();
	}

	public boolean hasDiagnosticsValues() {
		return executionEnvironment.has("diagnostics");
	}

	public diagnostic_msgs.DiagnosticStatus getDiagnosticsValues() {
		return (diagnostic_msgs.DiagnosticStatus) executionEnvironment.get("diagnostics");
	}

	/**
	 * Subscribing topic: "radio_status"
	 * Message type: mavros_msgs.RadioStatus
	 **/

	public void subscribe_RadioStatus() {
		subscribers.get("radio_status").subscribe();
	}

	public void unsubscribe_RadioStatus() {
		subscribers.get("radio_status").unsubscribe();
	}

	public boolean hasRadioStatusValues() {
		return executionEnvironment.has("radio_status");
	}

	public mavros_msgs.RadioStatus getRadioStatusValues() {
		return (mavros_msgs.RadioStatus) executionEnvironment.get("radio_status");
	}

	/**
	 * Subscribing topic: "hil_controls/hil_controls"
	 * Message type: mavros_msgs.HilControls
	 **/

	public void subscribe_HilControlsHilControls() {
		subscribers.get("hil_controls/hil_controls").subscribe();
	}

	public void unsubscribe_HilControlsHilControls() {
		subscribers.get("hil_controls/hil_controls").unsubscribe();
	}

	public boolean hasHilControlsHilControlsValues() {
		return executionEnvironment.has("hil_controls/hil_controls");
	}

	public mavros_msgs.HilControls getHilControlsHilControlsValues() {
		return (mavros_msgs.HilControls) executionEnvironment.get("hil_controls/hil_controls");
	}

	/**
	 * Subscribing topic: "global_position/global"
	 * Message type: sensor_msgs.NavSatFix
	 **/

	public void subscribe_GlobalPositionGlobal() {
		subscribers.get("global_position/global").subscribe();
	}

	public void unsubscribe_GlobalPositionGlobal() {
		subscribers.get("global_position/global").unsubscribe();
	}

	public boolean hasGlobalPositionGlobalValues() {
		return executionEnvironment.has("global_position/global");
	}

	public sensor_msgs.NavSatFix getGlobalPositionGlobalValues() {
		return (sensor_msgs.NavSatFix) executionEnvironment.get("global_position/global");
	}

	/**
	 * Subscribing topic: "global_position/local"
	 * Message type: geometry_msgs.PoseWithCovarianceStamped
	 **/

	public void subscribe_GlobalPositionLocal() {
		subscribers.get("global_position/local").subscribe();
	}

	public void unsubscribe_GlobalPositionLocal() {
		subscribers.get("global_position/local").unsubscribe();
	}

	public boolean hasGlobalPositionLocalValues() {
		return executionEnvironment.has("global_position/local");
	}

	public geometry_msgs.PoseWithCovarianceStamped getGlobalPositionLocalValues() {
		return (geometry_msgs.PoseWithCovarianceStamped) executionEnvironment.get("global_position/local");
	}

	/**
	 * Subscribing topic: "global_position/gp_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/

	public void subscribe_GlobalPositionGpVel() {
		subscribers.get("global_position/gp_vel").subscribe();
	}

	public void unsubscribe_GlobalPositionGpVel() {
		subscribers.get("global_position/gp_vel").unsubscribe();
	}

	public boolean hasGlobalPositionGpVelValues() {
		return executionEnvironment.has("global_position/gp_vel");
	}

	public geometry_msgs.TwistStamped getGlobalPositionGpVelValues() {
		return (geometry_msgs.TwistStamped) executionEnvironment.get("global_position/gp_vel");
	}

	/**
	 * Subscribing topic: "global_position/rel_alt"
	 * Message type: std_msgs.Float64
	 **/

	public void subscribe_GlobalPositionRelAlt() {
		subscribers.get("global_position/rel_alt").subscribe();
	}

	public void unsubscribe_GlobalPositionRelAlt() {
		subscribers.get("global_position/rel_alt").unsubscribe();
	}

	public boolean hasGlobalPositionRelAltValues() {
		return executionEnvironment.has("global_position/rel_alt");
	}

	public std_msgs.Float64 getGlobalPositionRelAltValues() {
		return (std_msgs.Float64) executionEnvironment.get("global_position/rel_alt");
	}

	/**
	 * Subscribing topic: "global_position/compass_hdg"
	 * Message type: std_msgs.Float64
	 **/

	public void subscribe_GlobalPositionCompassHdg() {
		subscribers.get("global_position/compass_hdg").subscribe();
	}

	public void unsubscribe_GlobalPositionCompassHdg() {
		subscribers.get("global_position/compass_hdg").unsubscribe();
	}

	public boolean hasGlobalPositionCompassHdgValues() {
		return executionEnvironment.has("global_position/compass_hdg");
	}

	public std_msgs.Float64 getGlobalPositionCompassHdgValues() {
		return (std_msgs.Float64) executionEnvironment.get("global_position/compass_hdg");
	}

	/**
	 * Subscribing topic: "global_position/raw/fix"
	 * Message type: sensor_msgs.NavSatFix
	 **/

	public void subscribe_GlobalPositionRawFix() {
		subscribers.get("global_position/raw/fix").subscribe();
	}

	public void unsubscribe_GlobalPositionRawFix() {
		subscribers.get("global_position/raw/fix").unsubscribe();
	}

	public boolean hasGlobalPositionRawFixValues() {
		return executionEnvironment.has("global_position/raw/fix");
	}

	public sensor_msgs.NavSatFix getGlobalPositionRawFixValues() {
		return (sensor_msgs.NavSatFix) executionEnvironment.get("global_position/raw/fix");
	}

	/**
	 * Subscribing topic: "global_position/raw/gps_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/

	public void subscribe_GlobalPositionRawGpsVel() {
		subscribers.get("global_position/raw/gps_vel").subscribe();
	}

	public void unsubscribe_GlobalPositionRawGpsVel() {
		subscribers.get("global_position/raw/gps_vel").unsubscribe();
	}

	public boolean hasGlobalPositionRawGpsVelValues() {
		return executionEnvironment.has("global_position/raw/gps_vel");
	}

	public geometry_msgs.TwistStamped getGlobalPositionRawGpsVelValues() {
		return (geometry_msgs.TwistStamped) executionEnvironment.get("global_position/raw/gps_vel");
	}

	/**
	 * Subscribing topic: "imu/data"
	 * Message type: sensor_msgs.Imu
	 **/

	public void subscribe_ImuData() {
		subscribers.get("imu/data").subscribe();
	}

	public void unsubscribe_ImuData() {
		subscribers.get("imu/data").unsubscribe();
	}

	public boolean hasImuDataValues() {
		return executionEnvironment.has("imu/data");
	}

	public sensor_msgs.Imu getImuDataValues() {
		return (sensor_msgs.Imu) executionEnvironment.get("imu/data");
	}

	/**
	 * Subscribing topic: "imu/data_raw"
	 * Message type: sensor_msgs.Imu
	 **/

	public void subscribe_ImuDataRaw() {
		subscribers.get("imu/data_raw").subscribe();
	}

	public void unsubscribe_ImuDataRaw() {
		subscribers.get("imu/data_raw").unsubscribe();
	}

	public boolean hasImuDataRawValues() {
		return executionEnvironment.has("imu/data_raw");
	}

	public sensor_msgs.Imu getImuDataRawValues() {
		return (sensor_msgs.Imu) executionEnvironment.get("imu/data_raw");
	}

	/**
	 * Subscribing topic: "imu/mag"
	 * Message type: sensor_msgs.MagneticField
	 **/

	public void subscribe_ImuMag() {
		subscribers.get("imu/mag").subscribe();
	}

	public void unsubscribe_ImuMag() {
		subscribers.get("imu/mag").unsubscribe();
	}

	public boolean hasImuMagValues() {
		return executionEnvironment.has("imu/mag");
	}

	public sensor_msgs.MagneticField getImuMagValues() {
		return (sensor_msgs.MagneticField) executionEnvironment.get("imu/mag");
	}

	/**
	 * Subscribing topic: "imu/temperature"
	 * Message type: sensor_msgs.Temperature
	 **/

	public void subscribe_ImuTemperature() {
		subscribers.get("imu/temperature").subscribe();
	}

	public void unsubscribe_ImuTemperature() {
		subscribers.get("imu/temperature").unsubscribe();
	}

	public boolean hasImuTemperatureValues() {
		return executionEnvironment.has("imu/temperature");
	}

	public sensor_msgs.Temperature getImuTemperatureValues() {
		return (sensor_msgs.Temperature) executionEnvironment.get("imu/temperature");
	}

	/**
	 * Subscribing topic: "imu/atm_pressure"
	 * Message type: sensor_msgs.FluidPressure
	 **/

	public void subscribe_ImuAtmPressure() {
		subscribers.get("imu/atm_pressure").subscribe();
	}

	public void unsubscribe_ImuAtmPressure() {
		subscribers.get("imu/atm_pressure").unsubscribe();
	}

	public boolean hasImuAtmPressureValues() {
		return executionEnvironment.has("imu/atm_pressure");
	}

	public sensor_msgs.FluidPressure getImuAtmPressureValues() {
		return (sensor_msgs.FluidPressure) executionEnvironment.get("imu/atm_pressure");
	}

	/**
	 * Subscribing topic: "local_position/pose"
	 * Message type: geometry_msgs.PoseStamped
	 **/

	public void subscribe_LocalPositionPose() {
		subscribers.get("local_position/pose").subscribe();
	}

	public void unsubscribe_LocalPositionPose() {
		subscribers.get("local_position/pose").unsubscribe();
	}

	public boolean hasLocalPositionPoseValues() {
		return executionEnvironment.has("local_position/pose");
	}

	public geometry_msgs.PoseStamped getLocalPositionPoseValues() {
		return (geometry_msgs.PoseStamped) executionEnvironment.get("local_position/pose");
	}

	/**
	 * Subscribing topic: "local_position/velocity"
	 * Message type: geometry_msgs.TwistStamped
	 **/

	public void subscribe_LocalPositionVelocity() {
		subscribers.get("local_position/velocity").subscribe();
	}

	public void unsubscribe_LocalPositionVelocity() {
		subscribers.get("local_position/velocity").unsubscribe();
	}

	public boolean hasLocalPositionVelocityValues() {
		return executionEnvironment.has("local_position/velocity");
	}

	public geometry_msgs.TwistStamped getLocalPositionVelocityValues() {
		return (geometry_msgs.TwistStamped) executionEnvironment.get("local_position/velocity");
	}

	/**
	 * Subscribing topic: "manual_control/control"
	 * Message type: mavros_msgs.ManualControl
	 **/

	public void subscribe_ManualControlControl() {
		subscribers.get("manual_control/control").subscribe();
	}

	public void unsubscribe_ManualControlControl() {
		subscribers.get("manual_control/control").unsubscribe();
	}

	public boolean hasManualControlControlValues() {
		return executionEnvironment.has("manual_control/control");
	}

	public mavros_msgs.ManualControl getManualControlControlValues() {
		return (mavros_msgs.ManualControl) executionEnvironment.get("manual_control/control");
	}

	/**
	 * Subscribing topic: "rc/in"
	 * Message type: mavros_msgs.RCIn
	 **/

	public void subscribe_RcIn() {
		subscribers.get("rc/in").subscribe();
	}

	public void unsubscribe_RcIn() {
		subscribers.get("rc/in").unsubscribe();
	}

	public boolean hasRcInValues() {
		return executionEnvironment.has("rc/in");
	}

	public mavros_msgs.RCIn getRcInValues() {
		return (mavros_msgs.RCIn) executionEnvironment.get("rc/in");
	}

	/**
	 * Subscribing topic: "rc/out"
	 * Message type: mavros_msgs.RCOut
	 **/

	public void subscribe_RcOut() {
		subscribers.get("rc/out").subscribe();
	}

	public void unsubscribe_RcOut() {
		subscribers.get("rc/out").unsubscribe();
	}

	public boolean hasRcOutValues() {
		return executionEnvironment.has("rc/out");
	}

	public mavros_msgs.RCOut getRcOutValues() {
		return (mavros_msgs.RCOut) executionEnvironment.get("rc/out");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_local"
	 * Message type: mavros_msgs.PositionTarget
	 **/

	public void subscribe_SetpointRawTargetLocal() {
		subscribers.get("setpoint_raw/target_local").subscribe();
	}

	public void unsubscribe_SetpointRawTargetLocal() {
		subscribers.get("setpoint_raw/target_local").unsubscribe();
	}

	public boolean hasSetpointRawTargetLocalValues() {
		return executionEnvironment.has("setpoint_raw/target_local");
	}

	public mavros_msgs.PositionTarget getSetpointRawTargetLocalValues() {
		return (mavros_msgs.PositionTarget) executionEnvironment.get("setpoint_raw/target_local");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/

	public void subscribe_SetpointRawTargetGlobal() {
		subscribers.get("setpoint_raw/target_global").subscribe();
	}

	public void unsubscribe_SetpointRawTargetGlobal() {
		subscribers.get("setpoint_raw/target_global").unsubscribe();
	}

	public boolean hasSetpointRawTargetGlobalValues() {
		return executionEnvironment.has("setpoint_raw/target_global");
	}

	public mavros_msgs.GlobalPositionTarget getSetpointRawTargetGlobalValues() {
		return (mavros_msgs.GlobalPositionTarget) executionEnvironment.get("setpoint_raw/target_global");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_attitude"
	 * Message type: mavros_msgs.AttitudeTarget
	 **/

	public void subscribe_SetpointRawTargetAttitude() {
		subscribers.get("setpoint_raw/target_attitude").subscribe();
	}

	public void unsubscribe_SetpointRawTargetAttitude() {
		subscribers.get("setpoint_raw/target_attitude").unsubscribe();
	}

	public boolean hasSetpointRawTargetAttitudeValues() {
		return executionEnvironment.has("setpoint_raw/target_attitude");
	}

	public mavros_msgs.AttitudeTarget getSetpointRawTargetAttitudeValues() {
		return (mavros_msgs.AttitudeTarget) executionEnvironment.get("setpoint_raw/target_attitude");
	}

	/**
	 * Subscribing topic: "state"
	 * Message type: mavros_msgs.State
	 **/

	public void subscribe_State() {
		subscribers.get("state").subscribe();
	}

	public void unsubscribe_State() {
		subscribers.get("state").unsubscribe();
	}

	public boolean hasStateValues() {
		return executionEnvironment.has("state");
	}

	public mavros_msgs.State getStateValues() {
		return (mavros_msgs.State) executionEnvironment.get("state");
	}

	/**
	 * Subscribing topic: "battery"
	 * Message type: mavros_msgs.BatteryStatus
	 **/

	public void subscribe_Battery() {
		subscribers.get("battery").subscribe();
	}

	public void unsubscribe_Battery() {
		subscribers.get("battery").unsubscribe();
	}

	public boolean hasBatteryValues() {
		return executionEnvironment.has("battery");
	}

	public mavros_msgs.BatteryStatus getBatteryValues() {
		return (mavros_msgs.BatteryStatus) executionEnvironment.get("battery");
	}

	/**
	 * Subscribing topic: "battery"
	 * Message type: sensor_msgs.BatteryState
	 **/

//	public sensor_msgs.BatteryState getBatteryValues() {
//		return (sensor_msgs.BatteryState) executionEnvironment.get("battery");
//	}

	/**
	 * Subscribing topic: "extended_state"
	 * Message type: mavros_msgs.ExtendedState
	 **/

	public void subscribe_ExtendedState() {
		subscribers.get("extended_state").subscribe();
	}

	public void unsubscribe_ExtendedState() {
		subscribers.get("extended_state").unsubscribe();
	}

	public boolean hasExtendedStateValues() {
		return executionEnvironment.has("extended_state");
	}

	public mavros_msgs.ExtendedState getExtendedStateValues() {
		return (mavros_msgs.ExtendedState) executionEnvironment.get("extended_state");
	}

	/**
	 * Subscribing topic: "time_reference"
	 * Message type: sensor_msgs.TimeReference
	 **/

	public void subscribe_TimeReference() {
		subscribers.get("time_reference").subscribe();
	}

	public void unsubscribe_TimeReference() {
		subscribers.get("time_reference").unsubscribe();
	}

	public boolean hasTimeReferenceValues() {
		return executionEnvironment.has("time_reference");
	}

	public sensor_msgs.TimeReference getTimeReferenceValues() {
		return (sensor_msgs.TimeReference) executionEnvironment.get("time_reference");
	}

	/**
	 * Subscribing topic: "vfr_hud"
	 * Message type: mavros_msgs.VFR_HUD
	 **/

	public void subscribe_VfrHud() {
		subscribers.get("vfr_hud").subscribe();
	}

	public void unsubscribe_VfrHud() {
		subscribers.get("vfr_hud").unsubscribe();
	}

	public boolean hasVfrHudValues() {
		return executionEnvironment.has("vfr_hud");
	}

	public mavros_msgs.VFR_HUD getVfrHudValues() {
		return (mavros_msgs.VFR_HUD) executionEnvironment.get("vfr_hud");
	}

	/**
	 * Subscribing topic: "wind_estimation"
	 * Message type: geometry_msgs.TwistStamped
	 **/

	public void subscribe_WindEstimation() {
		subscribers.get("wind_estimation").subscribe();
	}

	public void unsubscribe_WindEstimation() {
		subscribers.get("wind_estimation").unsubscribe();
	}

	public boolean hasWindEstimationValues() {
		return executionEnvironment.has("wind_estimation");
	}

	public geometry_msgs.TwistStamped getWindEstimationValues() {
		return (geometry_msgs.TwistStamped) executionEnvironment.get("wind_estimation");
	}

	/**
	 * Subscribing topic: "mission/reached"
	 * Message type: mavros_msgs.WaypointReached
	 **/

	public void subscribe_MissionReached() {
		subscribers.get("mission/reached").subscribe();
	}

	public void unsubscribe_MissionReached() {
		subscribers.get("mission/reached").unsubscribe();
	}

	public boolean hasMissionReachedValues() {
		return executionEnvironment.has("mission/reached");
	}

	public mavros_msgs.WaypointReached getMissionReachedValues() {
		return (mavros_msgs.WaypointReached) executionEnvironment.get("mission/reached");
	}

	/**
	 * Subscribing topic: "mission/waypoints"
	 * Message type: mavros_msgs.WaypointList
	 **/

	public void subscribe_MissionWaypoints() {
		subscribers.get("mission/waypoints").subscribe();
	}

	public void unsubscribe_MissionWaypoints() {
		subscribers.get("mission/waypoints").unsubscribe();
	}

	public boolean hasMissionWaypointsValues() {
		return executionEnvironment.has("mission/waypoints");
	}

	public mavros_msgs.WaypointList getMissionWaypointsValues() {
		return (mavros_msgs.WaypointList) executionEnvironment.get("mission/waypoints");
	}

}
