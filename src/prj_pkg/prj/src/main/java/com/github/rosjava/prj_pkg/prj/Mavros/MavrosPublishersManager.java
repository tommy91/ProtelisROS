package com.github.rosjava.prj_pkg.prj.Mavros;

import java.util.HashMap;
import java.util.Map;

import org.ros.exception.RosRuntimeException;
import org.ros.node.ConnectedNode;

import com.github.rosjava.prj_pkg.prj.PrjNode;

public class MavrosPublishersManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private String mavrosPrefix;
	private Map<String,MavrosPublisher<?>> publishers = new HashMap<>();
	
	public MavrosPublishersManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		connectedNode = prjNode.getConnectedNode();
		String mavrosNamespace = prjNode.getMavrosNamespace();
		mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
	}
	
	public void setupPublishers() {
		prjNode.printLog("Setup publishers.. ");
		publishers.put("mavlink/to", new MavrosPublisher<mavros_msgs.Mavlink>(connectedNode, mavrosPrefix + "mavlink/to", mavros_msgs.Mavlink._TYPE));
		publishers.put("mavlink/from", new MavrosPublisher<mavros_msgs.Mavlink>(connectedNode, mavrosPrefix + "mavlink/from", mavros_msgs.Mavlink._TYPE));
		publishers.put("mavros/state", new MavrosPublisher<mavros_msgs.State>(connectedNode, mavrosPrefix + "mavros/state", mavros_msgs.State._TYPE));
		publishers.put("actuator_control", new MavrosPublisher<mavros_msgs.ActuatorControl>(connectedNode, mavrosPrefix + "actuator_control", mavros_msgs.ActuatorControl._TYPE));
		publishers.put("manual_control/send", new MavrosPublisher<mavros_msgs.ManualControl>(connectedNode, mavrosPrefix + "manual_control/send", mavros_msgs.ManualControl._TYPE));
		publishers.put("rc/override", new MavrosPublisher<mavros_msgs.OverrideRCIn>(connectedNode, mavrosPrefix + "rc/override", mavros_msgs.OverrideRCIn._TYPE));
		publishers.put("safety_area/set", new MavrosPublisher<geometry_msgs.PolygonStamped>(connectedNode, mavrosPrefix + "safety_area/set", geometry_msgs.PolygonStamped._TYPE));
		publishers.put("setpoint_accel/accel", new MavrosPublisher<geometry_msgs.Vector3Stamped>(connectedNode, mavrosPrefix + "setpoint_accel/accel", geometry_msgs.Vector3Stamped._TYPE));
		publishers.put("setpoint_attitude/cmd_vel", new MavrosPublisher<geometry_msgs.TwistStamped>(connectedNode, mavrosPrefix + "setpoint_attitude/cmd_vel", geometry_msgs.TwistStamped._TYPE));
		publishers.put("setpoint_attitude/attitude", new MavrosPublisher<geometry_msgs.PoseStamped>(connectedNode, mavrosPrefix + "setpoint_attitude/attitude", geometry_msgs.PoseStamped._TYPE));
		publishers.put("setpoint_attitude/thrust", new MavrosPublisher<mavros_msgs.Thrust>(connectedNode, mavrosPrefix + "setpoint_attitude/thrust", mavros_msgs.Thrust._TYPE));
		publishers.put("setpoint_position/global", new MavrosPublisher<mavros_msgs.GlobalPositionTarget>(connectedNode, mavrosPrefix + "setpoint_position/global", mavros_msgs.GlobalPositionTarget._TYPE));
		publishers.put("setpoint_position/local", new MavrosPublisher<geometry_msgs.PoseStamped>(connectedNode, mavrosPrefix + "setpoint_position/local", geometry_msgs.PoseStamped._TYPE));
		publishers.put("setpoint_raw/local", new MavrosPublisher<mavros_msgs.PositionTarget>(connectedNode, mavrosPrefix + "setpoint_raw/local", mavros_msgs.PositionTarget._TYPE));
		publishers.put("setpoint_raw/global", new MavrosPublisher<mavros_msgs.GlobalPositionTarget>(connectedNode, mavrosPrefix + "setpoint_raw/global", mavros_msgs.GlobalPositionTarget._TYPE));
		publishers.put("setpoint_raw/attitude", new MavrosPublisher<mavros_msgs.AttitudeTarget>(connectedNode, mavrosPrefix + "setpoint_raw/attitude", mavros_msgs.AttitudeTarget._TYPE));
		publishers.put("setpoint_velocity/cmd_vel_unstamped", new MavrosPublisher<geometry_msgs.Twist>(connectedNode, mavrosPrefix + "setpoint_velocity/cmd_vel_unstamped", geometry_msgs.Twist._TYPE));
		waitPublishersRegistration();
	}
	
	private void waitPublishersRegistration() {
		prjNode.printLog("Waiting for publishers registration with the master.. ");
		for (String topic : publishers.keySet()) {
			if (!publishers.get(topic).waitMasterRegistration()) {
				throw new RosRuntimeException("Publisher '" + topic + "' NOT registered with master");
			}
		}
		prjNode.printLog("All publishers have been registered with the master");
	}

	/**
	 * Publisher: "mavlink/to"
	 * Message type: mavros_msgs.Mavlink
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.Mavlink> getPublisher_MavlinkTo() {
		return (MavrosPublisher<mavros_msgs.Mavlink>) publishers.get("mavlink/to");
	}

	/**
	 * Publisher: "mavlink/from"
	 * Message type: mavros_msgs.Mavlink
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.Mavlink> getPublisher_MavlinkFrom() {
		return (MavrosPublisher<mavros_msgs.Mavlink>) publishers.get("mavlink/from");
	}

	/**
	 * Publisher: "mavros/state"
	 * Message type: mavros_msgs.State
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.State> getPublisher_MavrosState() {
		return (MavrosPublisher<mavros_msgs.State>) publishers.get("mavros/state");
	}

	/**
	 * Publisher: "actuator_control"
	 * Message type: mavros_msgs.ActuatorControl
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.ActuatorControl> getPublisher_ActuatorControl() {
		return (MavrosPublisher<mavros_msgs.ActuatorControl>) publishers.get("actuator_control");
	}

	/**
	 * Publisher: "manual_control/send"
	 * Message type: mavros_msgs.ManualControl
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.ManualControl> getPublisher_ManualControlSend() {
		return (MavrosPublisher<mavros_msgs.ManualControl>) publishers.get("manual_control/send");
	}

	/**
	 * Publisher: "rc/override"
	 * Message type: mavros_msgs.OverrideRCIn
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.OverrideRCIn> getPublisher_RcOverride() {
		return (MavrosPublisher<mavros_msgs.OverrideRCIn>) publishers.get("rc/override");
	}

	/**
	 * Publisher: "safety_area/set"
	 * Message type: geometry_msgs.PolygonStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.PolygonStamped> getPublisher_SafetyAreaSet() {
		return (MavrosPublisher<geometry_msgs.PolygonStamped>) publishers.get("safety_area/set");
	}

	/**
	 * Publisher: "setpoint_accel/accel"
	 * Message type: geometry_msgs.Vector3Stamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.Vector3Stamped> getPublisher_SetpointAccelAccel() {
		return (MavrosPublisher<geometry_msgs.Vector3Stamped>) publishers.get("setpoint_accel/accel");
	}

	/**
	 * Publisher: "setpoint_attitude/cmd_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.TwistStamped> getPublisher_SetpointAttitudeCmdVel() {
		return (MavrosPublisher<geometry_msgs.TwistStamped>) publishers.get("setpoint_attitude/cmd_vel");
	}

	/**
	 * Publisher: "setpoint_attitude/attitude"
	 * Message type: geometry_msgs.PoseStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.PoseStamped> getPublisher_SetpointAttitudeAttitude() {
		return (MavrosPublisher<geometry_msgs.PoseStamped>) publishers.get("setpoint_attitude/attitude");
	}

	/**
	 * Publisher: "setpoint_attitude/thrust"
	 * Message type: mavros_msgs.Thrust
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.Thrust> getPublisher_SetpointAttitudeThrust() {
		return (MavrosPublisher<mavros_msgs.Thrust>) publishers.get("setpoint_attitude/thrust");
	}

	/**
	 * Publisher: "setpoint_position/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointPositionGlobal() {
		return (MavrosPublisher<mavros_msgs.GlobalPositionTarget>) publishers.get("setpoint_position/global");
	}

	/**
	 * Publisher: "setpoint_position/local"
	 * Message type: geometry_msgs.PoseStamped
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.PoseStamped> getPublisher_SetpointPositionLocal() {
		return (MavrosPublisher<geometry_msgs.PoseStamped>) publishers.get("setpoint_position/local");
	}

	/**
	 * Publisher: "setpoint_raw/local"
	 * Message type: mavros_msgs.PositionTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.PositionTarget> getPublisher_SetpointRawLocal() {
		return (MavrosPublisher<mavros_msgs.PositionTarget>) publishers.get("setpoint_raw/local");
	}

	/**
	 * Publisher: "setpoint_raw/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointRawGlobal() {
		return (MavrosPublisher<mavros_msgs.GlobalPositionTarget>) publishers.get("setpoint_raw/global");
	}

	/**
	 * Publisher: "setpoint_raw/attitude"
	 * Message type: mavros_msgs.AttitudeTarget
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<mavros_msgs.AttitudeTarget> getPublisher_SetpointRawAttitude() {
		return (MavrosPublisher<mavros_msgs.AttitudeTarget>) publishers.get("setpoint_raw/attitude");
	}

	/**
	 * Publisher: "setpoint_velocity/cmd_vel_unstamped"
	 * Message type: geometry_msgs.Twist
	 **/
	@SuppressWarnings("unchecked")
	public MavrosPublisher<geometry_msgs.Twist> getPublisher_SetpointVelocityCmdVelUnstamped() {
		return (MavrosPublisher<geometry_msgs.Twist>) publishers.get("setpoint_velocity/cmd_vel_unstamped");
	}
}
