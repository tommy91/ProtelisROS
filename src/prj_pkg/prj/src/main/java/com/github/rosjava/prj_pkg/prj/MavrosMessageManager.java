package com.github.rosjava.prj_pkg.prj;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;

public class MavrosMessageManager {
	
	ConnectedNode connectedNode;
	MessageFactory messageFactory;
	
	public MavrosMessageManager(ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
		messageFactory = connectedNode.getTopicMessageFactory();
	}
	
	public MessageFactory getTopicMessageFactory() {
		return messageFactory;
	}
	
	public mavros_msgs.ParamValue getParamValueMessage() {
		return messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
	}

}
