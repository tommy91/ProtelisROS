package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class MavrosPublishersManager {
	
	private final ConnectedNode connectedNode;
	
	public MavrosPublishersManager(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
	}
	
	/**
	 * Publisher: "mavlink/to"
	 * Message type: mavros_msgs.Mavlink
	 **/
	public Publisher<mavros_msgs.Mavlink> getPublisher_MavlinkTo() {
		return connectedNode.newPublisher("mavlink/to", mavros_msgs.Mavlink._TYPE);
	}

	/**
	 * Publisher: "mavlink/from"
	 * Message type: mavros_msgs.Mavlink
	 **/
	public Publisher<mavros_msgs.Mavlink> getPublisher_MavlinkFrom() {
		return connectedNode.newPublisher("mavlink/from", mavros_msgs.Mavlink._TYPE);
	}

	/**
	 * Publisher: "mavros/state"
	 * Message type: mavros_msgs.State
	 **/
	public Publisher<mavros_msgs.State> getPublisher_MavrosState() {
		return connectedNode.newPublisher("mavros/state", mavros_msgs.State._TYPE);
	}

	/**
	 * Publisher: "actuator_control"
	 * Message type: mavros_msgs.ActuatorControl
	 **/
	public Publisher<mavros_msgs.ActuatorControl> getPublisher_ActuatorControl() {
		return connectedNode.newPublisher("actuator_control", mavros_msgs.ActuatorControl._TYPE);
	}

	/**
	 * Publisher: "manual_control/send"
	 * Message type: mavros_msgs.ManualControl
	 **/
	public Publisher<mavros_msgs.ManualControl> getPublisher_ManualControlSend() {
		return connectedNode.newPublisher("manual_control/send", mavros_msgs.ManualControl._TYPE);
	}

	/**
	 * Publisher: "rc/override"
	 * Message type: mavros_msgs.OverrideRCIn
	 **/
	public Publisher<mavros_msgs.OverrideRCIn> getPublisher_RcOverride() {
		return connectedNode.newPublisher("rc/override", mavros_msgs.OverrideRCIn._TYPE);
	}

	/**
	 * Publisher: "safety_area/set"
	 * Message type: geometry_msgs.PolygonStamped
	 **/
	public Publisher<geometry_msgs.PolygonStamped> getPublisher_SafetyAreaSet() {
		return connectedNode.newPublisher("safety_area/set", geometry_msgs.PolygonStamped._TYPE);
	}

	/**
	 * Publisher: "setpoint_accel/accel"
	 * Message type: geometry_msgs.Vector3Stamped
	 **/
	public Publisher<geometry_msgs.Vector3Stamped> getPublisher_SetpointAccelAccel() {
		return connectedNode.newPublisher("setpoint_accel/accel", geometry_msgs.Vector3Stamped._TYPE);
	}

	/**
	 * Publisher: "setpoint_attitude/cmd_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/
	public Publisher<geometry_msgs.TwistStamped> getPublisher_SetpointAttitudeCmdVel() {
		return connectedNode.newPublisher("setpoint_attitude/cmd_vel", geometry_msgs.TwistStamped._TYPE);
	}

	/**
	 * Publisher: "setpoint_attitude/attitude"
	 * Message type: geometry_msgs.PoseStamped
	 **/
	public Publisher<geometry_msgs.PoseStamped> getPublisher_SetpointAttitudeAttitude() {
		return connectedNode.newPublisher("setpoint_attitude/attitude", geometry_msgs.PoseStamped._TYPE);
	}

	/**
	 * Publisher: "setpoint_attitude/thrust"
	 * Message type: mavros_msgs.Thrust
	 **/
	public Publisher<mavros_msgs.Thrust> getPublisher_SetpointAttitudeThrust() {
		return connectedNode.newPublisher("setpoint_attitude/thrust", mavros_msgs.Thrust._TYPE);
	}

	/**
	 * Publisher: "setpoint_position/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/
	public Publisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointPositionGlobal() {
		return connectedNode.newPublisher("setpoint_position/global", mavros_msgs.GlobalPositionTarget._TYPE);
	}

	/**
	 * Publisher: "setpoint_position/local"
	 * Message type: geometry_msgs.PoseStamped
	 **/
	public Publisher<geometry_msgs.PoseStamped> getPublisher_SetpointPositionLocal() {
		return connectedNode.newPublisher("setpoint_position/local", geometry_msgs.PoseStamped._TYPE);
	}

	/**
	 * Publisher: "setpoint_raw/local"
	 * Message type: mavros_msgs.PositionTarget
	 **/
	public Publisher<mavros_msgs.PositionTarget> getPublisher_SetpointRawLocal() {
		return connectedNode.newPublisher("setpoint_raw/local", mavros_msgs.PositionTarget._TYPE);
	}

	/**
	 * Publisher: "setpoint_raw/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/
	public Publisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointRawGlobal() {
		return connectedNode.newPublisher("setpoint_raw/global", mavros_msgs.GlobalPositionTarget._TYPE);
	}

	/**
	 * Publisher: "setpoint_raw/attitude"
	 * Message type: mavros_msgs.AttitudeTarget
	 **/
	public Publisher<mavros_msgs.AttitudeTarget> getPublisher_SetpointRawAttitude() {
		return connectedNode.newPublisher("setpoint_raw/attitude", mavros_msgs.AttitudeTarget._TYPE);
	}

	/**
	 * Publisher: "setpoint_velocity/cmd_vel_unstamped"
	 * Message type: geometry_msgs.Twist
	 **/
	public Publisher<geometry_msgs.Twist> getPublisher_SetpointVelocityCmdVelUnstamped() {
		return connectedNode.newPublisher("setpoint_velocity/cmd_vel_unstamped", geometry_msgs.Twist._TYPE);
	}

}
