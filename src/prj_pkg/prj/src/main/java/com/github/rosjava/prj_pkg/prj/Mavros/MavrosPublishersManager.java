package com.github.rosjava.prj_pkg.prj;

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

	private Publisher<mavros_msgs.Mavlink> getPublisher_MavlinkTo() {
		return connectedNode.newPublisher("mavlink/to", mavros_msgs.Mavlink._TYPE);
	}

	public mavros_msgs.Mavlink newMessage_MavlinkTo() {
		return getPublisher_MavlinkTo().newMessage();
	}

	public void publish_MavlinkTo(mavros_msgs.Mavlink msg) {
		getPublisher_MavlinkTo().publish(msg);
	}

	/**
	 * Publisher: "mavlink/from"
	 * Message type: mavros_msgs.Mavlink
	 **/

	private Publisher<mavros_msgs.Mavlink> getPublisher_MavlinkFrom() {
		return connectedNode.newPublisher("mavlink/from", mavros_msgs.Mavlink._TYPE);
	}

	public mavros_msgs.Mavlink newMessage_MavlinkFrom() {
		return getPublisher_MavlinkFrom().newMessage();
	}

	public void publish_MavlinkFrom(mavros_msgs.Mavlink msg) {
		getPublisher_MavlinkFrom().publish(msg);
	}

	/**
	 * Publisher: "mavros/state"
	 * Message type: mavros_msgs.State
	 **/

	private Publisher<mavros_msgs.State> getPublisher_MavrosState() {
		return connectedNode.newPublisher("mavros/state", mavros_msgs.State._TYPE);
	}

	public mavros_msgs.State newMessage_MavrosState() {
		return getPublisher_MavrosState().newMessage();
	}

	public void publish_MavrosState(mavros_msgs.State msg) {
		getPublisher_MavrosState().publish(msg);
	}

	/**
	 * Publisher: "actuator_control"
	 * Message type: mavros_msgs.ActuatorControl
	 **/

	private Publisher<mavros_msgs.ActuatorControl> getPublisher_ActuatorControl() {
		return connectedNode.newPublisher("actuator_control", mavros_msgs.ActuatorControl._TYPE);
	}

	public mavros_msgs.ActuatorControl newMessage_ActuatorControl() {
		return getPublisher_ActuatorControl().newMessage();
	}

	public void publish_ActuatorControl(mavros_msgs.ActuatorControl msg) {
		getPublisher_ActuatorControl().publish(msg);
	}

	/**
	 * Publisher: "manual_control/send"
	 * Message type: mavros_msgs.ManualControl
	 **/

	private Publisher<mavros_msgs.ManualControl> getPublisher_ManualControlSend() {
		return connectedNode.newPublisher("manual_control/send", mavros_msgs.ManualControl._TYPE);
	}

	public mavros_msgs.ManualControl newMessage_ManualControlSend() {
		return getPublisher_ManualControlSend().newMessage();
	}

	public void publish_ManualControlSend(mavros_msgs.ManualControl msg) {
		getPublisher_ManualControlSend().publish(msg);
	}

	/**
	 * Publisher: "rc/override"
	 * Message type: mavros_msgs.OverrideRCIn
	 **/

	private Publisher<mavros_msgs.OverrideRCIn> getPublisher_RcOverride() {
		return connectedNode.newPublisher("rc/override", mavros_msgs.OverrideRCIn._TYPE);
	}

	public mavros_msgs.OverrideRCIn newMessage_RcOverride() {
		return getPublisher_RcOverride().newMessage();
	}

	public void publish_RcOverride(mavros_msgs.OverrideRCIn msg) {
		getPublisher_RcOverride().publish(msg);
	}

	/**
	 * Publisher: "safety_area/set"
	 * Message type: geometry_msgs.PolygonStamped
	 **/

	private Publisher<geometry_msgs.PolygonStamped> getPublisher_SafetyAreaSet() {
		return connectedNode.newPublisher("safety_area/set", geometry_msgs.PolygonStamped._TYPE);
	}

	public geometry_msgs.PolygonStamped newMessage_SafetyAreaSet() {
		return getPublisher_SafetyAreaSet().newMessage();
	}

	public void publish_SafetyAreaSet(geometry_msgs.PolygonStamped msg) {
		getPublisher_SafetyAreaSet().publish(msg);
	}

	/**
	 * Publisher: "setpoint_accel/accel"
	 * Message type: geometry_msgs.Vector3Stamped
	 **/

	private Publisher<geometry_msgs.Vector3Stamped> getPublisher_SetpointAccelAccel() {
		return connectedNode.newPublisher("setpoint_accel/accel", geometry_msgs.Vector3Stamped._TYPE);
	}

	public geometry_msgs.Vector3Stamped newMessage_SetpointAccelAccel() {
		return getPublisher_SetpointAccelAccel().newMessage();
	}

	public void publish_SetpointAccelAccel(geometry_msgs.Vector3Stamped msg) {
		getPublisher_SetpointAccelAccel().publish(msg);
	}

	/**
	 * Publisher: "setpoint_attitude/cmd_vel"
	 * Message type: geometry_msgs.TwistStamped
	 **/

	private Publisher<geometry_msgs.TwistStamped> getPublisher_SetpointAttitudeCmdVel() {
		return connectedNode.newPublisher("setpoint_attitude/cmd_vel", geometry_msgs.TwistStamped._TYPE);
	}

	public geometry_msgs.TwistStamped newMessage_SetpointAttitudeCmdVel() {
		return getPublisher_SetpointAttitudeCmdVel().newMessage();
	}

	public void publish_SetpointAttitudeCmdVel(geometry_msgs.TwistStamped msg) {
		getPublisher_SetpointAttitudeCmdVel().publish(msg);
	}

	/**
	 * Publisher: "setpoint_attitude/attitude"
	 * Message type: geometry_msgs.PoseStamped
	 **/

	private Publisher<geometry_msgs.PoseStamped> getPublisher_SetpointAttitudeAttitude() {
		return connectedNode.newPublisher("setpoint_attitude/attitude", geometry_msgs.PoseStamped._TYPE);
	}

	public geometry_msgs.PoseStamped newMessage_SetpointAttitudeAttitude() {
		return getPublisher_SetpointAttitudeAttitude().newMessage();
	}

	public void publish_SetpointAttitudeAttitude(geometry_msgs.PoseStamped msg) {
		getPublisher_SetpointAttitudeAttitude().publish(msg);
	}

	/**
	 * Publisher: "setpoint_attitude/thrust"
	 * Message type: mavros_msgs.Thrust
	 **/

	private Publisher<mavros_msgs.Thrust> getPublisher_SetpointAttitudeThrust() {
		return connectedNode.newPublisher("setpoint_attitude/thrust", mavros_msgs.Thrust._TYPE);
	}

	public mavros_msgs.Thrust newMessage_SetpointAttitudeThrust() {
		return getPublisher_SetpointAttitudeThrust().newMessage();
	}

	public void publish_SetpointAttitudeThrust(mavros_msgs.Thrust msg) {
		getPublisher_SetpointAttitudeThrust().publish(msg);
	}

	/**
	 * Publisher: "setpoint_position/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/

	private Publisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointPositionGlobal() {
		return connectedNode.newPublisher("setpoint_position/global", mavros_msgs.GlobalPositionTarget._TYPE);
	}

	public mavros_msgs.GlobalPositionTarget newMessage_SetpointPositionGlobal() {
		return getPublisher_SetpointPositionGlobal().newMessage();
	}

	public void publish_SetpointPositionGlobal(mavros_msgs.GlobalPositionTarget msg) {
		getPublisher_SetpointPositionGlobal().publish(msg);
	}

	/**
	 * Publisher: "setpoint_position/local"
	 * Message type: geometry_msgs.PoseStamped
	 **/

	private Publisher<geometry_msgs.PoseStamped> getPublisher_SetpointPositionLocal() {
		return connectedNode.newPublisher("setpoint_position/local", geometry_msgs.PoseStamped._TYPE);
	}

	public geometry_msgs.PoseStamped newMessage_SetpointPositionLocal() {
		return getPublisher_SetpointPositionLocal().newMessage();
	}

	public void publish_SetpointPositionLocal(geometry_msgs.PoseStamped msg) {
		getPublisher_SetpointPositionLocal().publish(msg);
	}

	/**
	 * Publisher: "setpoint_raw/local"
	 * Message type: mavros_msgs.PositionTarget
	 **/

	private Publisher<mavros_msgs.PositionTarget> getPublisher_SetpointRawLocal() {
		return connectedNode.newPublisher("setpoint_raw/local", mavros_msgs.PositionTarget._TYPE);
	}

	public mavros_msgs.PositionTarget newMessage_SetpointRawLocal() {
		return getPublisher_SetpointRawLocal().newMessage();
	}

	public void publish_SetpointRawLocal(mavros_msgs.PositionTarget msg) {
		getPublisher_SetpointRawLocal().publish(msg);
	}

	/**
	 * Publisher: "setpoint_raw/global"
	 * Message type: mavros_msgs.GlobalPositionTarget
	 **/

	private Publisher<mavros_msgs.GlobalPositionTarget> getPublisher_SetpointRawGlobal() {
		return connectedNode.newPublisher("setpoint_raw/global", mavros_msgs.GlobalPositionTarget._TYPE);
	}

	public mavros_msgs.GlobalPositionTarget newMessage_SetpointRawGlobal() {
		return getPublisher_SetpointRawGlobal().newMessage();
	}

	public void publish_SetpointRawGlobal(mavros_msgs.GlobalPositionTarget msg) {
		getPublisher_SetpointRawGlobal().publish(msg);
	}

	/**
	 * Publisher: "setpoint_raw/attitude"
	 * Message type: mavros_msgs.AttitudeTarget
	 **/

	private Publisher<mavros_msgs.AttitudeTarget> getPublisher_SetpointRawAttitude() {
		return connectedNode.newPublisher("setpoint_raw/attitude", mavros_msgs.AttitudeTarget._TYPE);
	}

	public mavros_msgs.AttitudeTarget newMessage_SetpointRawAttitude() {
		return getPublisher_SetpointRawAttitude().newMessage();
	}

	public void publish_SetpointRawAttitude(mavros_msgs.AttitudeTarget msg) {
		getPublisher_SetpointRawAttitude().publish(msg);
	}

	/**
	 * Publisher: "setpoint_velocity/cmd_vel_unstamped"
	 * Message type: geometry_msgs.Twist
	 **/

	private Publisher<geometry_msgs.Twist> getPublisher_SetpointVelocityCmdVelUnstamped() {
		return connectedNode.newPublisher("setpoint_velocity/cmd_vel_unstamped", geometry_msgs.Twist._TYPE);
	}

	public geometry_msgs.Twist newMessage_SetpointVelocityCmdVelUnstamped() {
		return getPublisher_SetpointVelocityCmdVelUnstamped().newMessage();
	}

	public void publish_SetpointVelocityCmdVelUnstamped(geometry_msgs.Twist msg) {
		getPublisher_SetpointVelocityCmdVelUnstamped().publish(msg);
	}

}
