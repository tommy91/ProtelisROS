package com.github.rosjava.prj_pkg.prj.Mavros;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ros.node.ConnectedNode;

import com.github.rosjava.prj_pkg.prj.PrjNode;

public class MavrosSubscribersManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,MavrosSubscriber<?>> subscribers = new HashMap<>();
	private List<String> interestedTopics = Arrays.asList(
			"state",
			"global_position/global",
			"global_position/rel_alt"
			); 
	
	public MavrosSubscribersManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		this.connectedNode = prjNode.getConnectedNode();
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
//		subscribers.put("mavlink/from", new MavrosSubscriber<mavros_msgs.Mavlink>(connectedNode, mavrosPrefix + "mavlink/from", mavros_msgs.Mavlink._TYPE));
//		subscribers.put("diagnostics", new MavrosSubscriber<diagnostic_msgs.DiagnosticStatus>(connectedNode, mavrosPrefix + "diagnostics", diagnostic_msgs.DiagnosticStatus._TYPE));
//		subscribers.put("radio_status", new MavrosSubscriber<mavros_msgs.RadioStatus>(connectedNode, mavrosPrefix + "radio_status", mavros_msgs.RadioStatus._TYPE));
//		subscribers.put("hil_controls/hil_controls", new MavrosSubscriber<mavros_msgs.HilControls>(connectedNode, mavrosPrefix + "hil_controls/hil_controls", mavros_msgs.HilControls._TYPE));
		subscribers.put("global_position/global", new MavrosSubscriber<sensor_msgs.NavSatFix>(connectedNode, mavrosPrefix + "global_position/global", sensor_msgs.NavSatFix._TYPE));
//		subscribers.put("global_position/local", new MavrosSubscriber<geometry_msgs.PoseWithCovarianceStamped>(connectedNode, mavrosPrefix + "global_position/local", geometry_msgs.PoseWithCovarianceStamped._TYPE));
//		subscribers.put("global_position/gp_vel", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, mavrosPrefix + "global_position/gp_vel", geometry_msgs.TwistStamped._TYPE));
		subscribers.put("global_position/rel_alt", new MavrosSubscriber<std_msgs.Float64>(connectedNode, mavrosPrefix + "global_position/rel_alt", std_msgs.Float64._TYPE));
//		subscribers.put("global_position/compass_hdg", new MavrosSubscriber<std_msgs.Float64>(connectedNode, mavrosPrefix + "global_position/compass_hdg", std_msgs.Float64._TYPE));
//		subscribers.put("global_position/raw/fix", new MavrosSubscriber<sensor_msgs.NavSatFix>(connectedNode, mavrosPrefix + "global_position/raw/fix", sensor_msgs.NavSatFix._TYPE));
//		subscribers.put("global_position/raw/gps_vel", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, mavrosPrefix + "global_position/raw/gps_vel", geometry_msgs.TwistStamped._TYPE));
//		subscribers.put("imu/data", new MavrosSubscriber<sensor_msgs.Imu>(connectedNode, mavrosPrefix + "imu/data", sensor_msgs.Imu._TYPE));
//		subscribers.put("imu/data_raw", new MavrosSubscriber<sensor_msgs.Imu>(connectedNode, mavrosPrefix + "imu/data_raw", sensor_msgs.Imu._TYPE));
//		subscribers.put("imu/mag", new MavrosSubscriber<sensor_msgs.MagneticField>(connectedNode, mavrosPrefix + "imu/mag", sensor_msgs.MagneticField._TYPE));
//		subscribers.put("imu/temperature", new MavrosSubscriber<sensor_msgs.Temperature>(connectedNode, mavrosPrefix + "imu/temperature", sensor_msgs.Temperature._TYPE));
//		subscribers.put("imu/atm_pressure", new MavrosSubscriber<sensor_msgs.FluidPressure>(connectedNode, mavrosPrefix + "imu/atm_pressure", sensor_msgs.FluidPressure._TYPE));
//		subscribers.put("local_position/pose", new MavrosSubscriber<geometry_msgs.PoseStamped>(connectedNode, mavrosPrefix + "local_position/pose", geometry_msgs.PoseStamped._TYPE));
//		subscribers.put("local_position/velocity", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, mavrosPrefix + "local_position/velocity", geometry_msgs.TwistStamped._TYPE));
//		subscribers.put("manual_control/control", new MavrosSubscriber<mavros_msgs.ManualControl>(connectedNode, mavrosPrefix + "manual_control/control", mavros_msgs.ManualControl._TYPE));
//		subscribers.put("rc/in", new MavrosSubscriber<mavros_msgs.RCIn>(connectedNode, mavrosPrefix + "rc/in", mavros_msgs.RCIn._TYPE));
//		subscribers.put("rc/out", new MavrosSubscriber<mavros_msgs.RCOut>(connectedNode, mavrosPrefix + "rc/out", mavros_msgs.RCOut._TYPE));
//		subscribers.put("setpoint_raw/target_local", new MavrosSubscriber<mavros_msgs.PositionTarget>(connectedNode, mavrosPrefix + "setpoint_raw/target_local", mavros_msgs.PositionTarget._TYPE));
//		subscribers.put("setpoint_raw/target_global", new MavrosSubscriber<mavros_msgs.GlobalPositionTarget>(connectedNode, mavrosPrefix + "setpoint_raw/target_global", mavros_msgs.GlobalPositionTarget._TYPE));
//		subscribers.put("setpoint_raw/target_attitude", new MavrosSubscriber<mavros_msgs.AttitudeTarget>(connectedNode, mavrosPrefix + "setpoint_raw/target_attitude", mavros_msgs.AttitudeTarget._TYPE));
		subscribers.put("state", new MavrosSubscriber<mavros_msgs.State>(connectedNode, mavrosPrefix + "state", mavros_msgs.State._TYPE));
//		subscribers.put("battery", new MavrosSubscriber<mavros_msgs.BatteryStatus>(connectedNode, mavrosPrefix + "battery", mavros_msgs.BatteryStatus._TYPE));
//		subscribers.put("battery", new MavrosSubscriber<sensor_msgs.BatteryState>(connectedNode, mavrosPrefix + "battery", sensor_msgs.BatteryState._TYPE));
//		subscribers.put("extended_state", new MavrosSubscriber<mavros_msgs.ExtendedState>(connectedNode, mavrosPrefix + "extended_state", mavros_msgs.ExtendedState._TYPE));
//		subscribers.put("time_reference", new MavrosSubscriber<sensor_msgs.TimeReference>(connectedNode, mavrosPrefix + "time_reference", sensor_msgs.TimeReference._TYPE));
//		subscribers.put("vfr_hud", new MavrosSubscriber<mavros_msgs.VFR_HUD>(connectedNode, mavrosPrefix + "vfr_hud", mavros_msgs.VFR_HUD._TYPE));
//		subscribers.put("wind_estimation", new MavrosSubscriber<geometry_msgs.TwistStamped>(connectedNode, mavrosPrefix + "wind_estimation", geometry_msgs.TwistStamped._TYPE));
//		subscribers.put("mission/reached", new MavrosSubscriber<mavros_msgs.WaypointReached>(connectedNode, mavrosPrefix + "mission/reached", mavros_msgs.WaypointReached._TYPE));
//		subscribers.put("mission/waypoints", new MavrosSubscriber<mavros_msgs.WaypointList>(connectedNode, mavrosPrefix + "mission/waypoints", mavros_msgs.WaypointList._TYPE));
	}
	
	/**
	 * Subscribing topic: "mavlink/from"
	 * Message type: mavros_msgs.Mavlink
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.Mavlink> get_MavlinkFrom() {
		return (MavrosSubscriber<mavros_msgs.Mavlink>) subscribers.get("mavlink/from");
	}

	/**
	 * Subscribing topic: "diagnostics"
	 * Message type: diagnostic_msgs.DiagnosticStatus
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<diagnostic_msgs.DiagnosticStatus> get_Diagnostics() {
		return (MavrosSubscriber<diagnostic_msgs.DiagnosticStatus>) subscribers.get("diagnostics");
	}

	/**
	 * Subscribing topic: "radio_status"
	 * Message type: mavros_msgs.RadioStatus
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.RadioStatus> get_RadioStatus() {
		return (MavrosSubscriber<mavros_msgs.RadioStatus>) subscribers.get("radio_status");
	}

	/**
	 * Subscribing topic: "hil_controls/hil_controls"
	 * Message type: mavros_msgs.HilControls
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.HilControls> get_HilControlsHilControls() {
		return (MavrosSubscriber<mavros_msgs.HilControls>) subscribers.get("hil_controls/hil_controls");
	}

	/**
	 * Subscribing topic: "global_position/global"
	 * Message type: sensor_msgs.NavSatFix
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.NavSatFix> get_GlobalPositionGlobal() {
		return (MavrosSubscriber<sensor_msgs.NavSatFix>) subscribers.get("global_position/global");
	}

	/**
	 * Subscribing topic: "global_position/local"
	 * Message type: geometry_msgs.PoseWithCovarianceStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.PoseWithCovarianceStamped> get_GlobalPositionLocal() {
		return (MavrosSubscriber<geometry_msgs.PoseWithCovarianceStamped>) subscribers.get("global_position/local");
	}

	/**
	 * Subscribing topic: "global_position/gp_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.TwistStamped> get_GlobalPositionGpVel() {
		return (MavrosSubscriber<geometry_msgs.TwistStamped>) subscribers.get("global_position/gp_vel");
	}

	/**
	 * Subscribing topic: "global_position/rel_alt"
	 * Message type: std_msgs.Float64
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<std_msgs.Float64> get_GlobalPositionRelAlt() {
		return (MavrosSubscriber<std_msgs.Float64>) subscribers.get("global_position/rel_alt");
	}

	/**
	 * Subscribing topic: "global_position/compass_hdg"
	 * Message type: std_msgs.Float64
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<std_msgs.Float64> get_GlobalPositionCompassHdg() {
		return (MavrosSubscriber<std_msgs.Float64>) subscribers.get("global_position/compass_hdg");
	}

	/**
	 * Subscribing topic: "global_position/raw/fix"
	 * Message type: sensor_msgs.NavSatFix
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.NavSatFix> get_GlobalPositionRawFix() {
		return (MavrosSubscriber<sensor_msgs.NavSatFix>) subscribers.get("global_position/raw/fix");
	}

	/**
	 * Subscribing topic: "global_position/raw/gps_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.TwistStamped> get_GlobalPositionRawGpsVel() {
		return (MavrosSubscriber<geometry_msgs.TwistStamped>) subscribers.get("global_position/raw/gps_vel");
	}

	/**
	 * Subscribing topic: "imu/data"
	 * Message type: sensor_msgs.Imu
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.Imu> get_ImuData() {
		return (MavrosSubscriber<sensor_msgs.Imu>) subscribers.get("imu/data");
	}

	/**
	 * Subscribing topic: "imu/data_raw"
	 * Message type: sensor_msgs.Imu
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.Imu> get_ImuDataRaw() {
		return (MavrosSubscriber<sensor_msgs.Imu>) subscribers.get("imu/data_raw");
	}

	/**
	 * Subscribing topic: "imu/mag"
	 * Message type: sensor_msgs.MagneticField
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.MagneticField> get_ImuMag() {
		return (MavrosSubscriber<sensor_msgs.MagneticField>) subscribers.get("imu/mag");
	}

	/**
	 * Subscribing topic: "imu/temperature"
	 * Message type: sensor_msgs.Temperature
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.Temperature> get_ImuTemperature() {
		return (MavrosSubscriber<sensor_msgs.Temperature>) subscribers.get("imu/temperature");
	}

	/**
	 * Subscribing topic: "imu/atm_pressure"
	 * Message type: sensor_msgs.FluidPressure
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.FluidPressure> get_ImuAtmPressure() {
		return (MavrosSubscriber<sensor_msgs.FluidPressure>) subscribers.get("imu/atm_pressure");
	}

	/**
	 * Subscribing topic: "local_position/pose"
	 * Message type: geometry_msgs.PoseStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.PoseStamped> get_LocalPositionPose() {
		return (MavrosSubscriber<geometry_msgs.PoseStamped>) subscribers.get("local_position/pose");
	}

	/**
	 * Subscribing topic: "local_position/velocity"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.TwistStamped> get_LocalPositionVelocity() {
		return (MavrosSubscriber<geometry_msgs.TwistStamped>) subscribers.get("local_position/velocity");
	}

	/**
	 * Subscribing topic: "manual_control/control"
	 * Message type: mavros_msgs.ManualControl
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.ManualControl> get_ManualControlControl() {
		return (MavrosSubscriber<mavros_msgs.ManualControl>) subscribers.get("manual_control/control");
	}

	/**
	 * Subscribing topic: "rc/in"
	 * Message type: mavros_msgs.RCIn
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.RCIn> get_RcIn() {
		return (MavrosSubscriber<mavros_msgs.RCIn>) subscribers.get("rc/in");
	}

	/**
	 * Subscribing topic: "rc/out"
	 * Message type: mavros_msgs.RCOut
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.RCOut> get_RcOut() {
		return (MavrosSubscriber<mavros_msgs.RCOut>) subscribers.get("rc/out");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_local"
	 * Message type: mavros_msgs.PositionTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.PositionTarget> get_SetpointRawTargetLocal() {
		return (MavrosSubscriber<mavros_msgs.PositionTarget>) subscribers.get("setpoint_raw/target_local");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.GlobalPositionTarget> get_SetpointRawTargetGlobal() {
		return (MavrosSubscriber<mavros_msgs.GlobalPositionTarget>) subscribers.get("setpoint_raw/target_global");
	}

	/**
	 * Subscribing topic: "setpoint_raw/target_attitude"
	 * Message type: mavros_msgs.AttitudeTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.AttitudeTarget> get_SetpointRawTargetAttitude() {
		return (MavrosSubscriber<mavros_msgs.AttitudeTarget>) subscribers.get("setpoint_raw/target_attitude");
	}

	/**
	 * Subscribing topic: "state"
	 * Message type: mavros_msgs.State
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.State> get_State() {
		return (MavrosSubscriber<mavros_msgs.State>) subscribers.get("state");
	}

//	/**
//	 * Subscribing topic: "battery"
//	 * Message type: mavros_msgs.BatteryStatus
//	 **/
//	@SuppressWarnings("unchecked")
//	public MavrosSubscriber<mavros_msgs.BatteryStatus> get_Battery() {
//		return (MavrosSubscriber<mavros_msgs.BatteryStatus>) subscribers.get("battery");
//	}

	/**
	 * Subscribing topic: "battery"
	 * Message type: sensor_msgs.BatteryState
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.BatteryState> get_Battery() {
		return (MavrosSubscriber<sensor_msgs.BatteryState>) subscribers.get("battery");
	}

	/**
	 * Subscribing topic: "extended_state"
	 * Message type: mavros_msgs.ExtendedState
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.ExtendedState> get_ExtendedState() {
		return (MavrosSubscriber<mavros_msgs.ExtendedState>) subscribers.get("extended_state");
	}

	/**
	 * Subscribing topic: "time_reference"
	 * Message type: sensor_msgs.TimeReference
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<sensor_msgs.TimeReference> get_TimeReference() {
		return (MavrosSubscriber<sensor_msgs.TimeReference>) subscribers.get("time_reference");
	}

	/**
	 * Subscribing topic: "vfr_hud"
	 * Message type: mavros_msgs.VFR_HUD
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.VFR_HUD> get_VfrHud() {
		return (MavrosSubscriber<mavros_msgs.VFR_HUD>) subscribers.get("vfr_hud");
	}

	/**
	 * Subscribing topic: "wind_estimation"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<geometry_msgs.TwistStamped> get_WindEstimation() {
		return (MavrosSubscriber<geometry_msgs.TwistStamped>) subscribers.get("wind_estimation");
	}

	/**
	 * Subscribing topic: "mission/reached"
	 * Message type: mavros_msgs.WaypointReached
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.WaypointReached> get_MissionReached() {
		return (MavrosSubscriber<mavros_msgs.WaypointReached>) subscribers.get("mission/reached");
	}

	/**
	 * Subscribing topic: "mission/waypoints"
	 * Message type: mavros_msgs.WaypointList
	 **/
	@SuppressWarnings("unchecked")
	public MavrosSubscriber<mavros_msgs.WaypointList> get_MissionWaypoints() {
		return (MavrosSubscriber<mavros_msgs.WaypointList>) subscribers.get("mission/waypoints");
	}


}
