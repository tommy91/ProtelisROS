package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;

public class MavrosMessagesManager {
	
	ConnectedNode connectedNode;
	MessageFactory messageFactory;
	
	public MavrosMessagesManager(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
		messageFactory = connectedNode.getTopicMessageFactory();
	}
	
	public MessageFactory getTopicMessageFactory() {
		return messageFactory;
	}
	
	public mavros_msgs.ParamValue getParamValueMessage() {
		return messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
	}
	
	public mavros_msgs.ParamValue getParamValueMessage(int val) {
		mavros_msgs.ParamValue pv = getParamValueMessage();
		pv.setInteger(val);
		return pv;
	}
	
	public mavros_msgs.ParamValue getParamValueMessage(double val) {
		mavros_msgs.ParamValue pv = getParamValueMessage();
		pv.setReal(val);
		return pv;
	}

}
