package com.github.rosjava.prj_pkg.prj;

import java.util.Arrays;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class StatusManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private PrjExecutionContext executionContext;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private List<String> interestedTopics = Arrays.asList(
			"global_position/rel_alt"
			); 
	
	public StatusManager(PrjNode prjNode, ConnectedNode connectedNode, PrjExecutionContext executionContext) {
		this.prjNode = prjNode;
		this.connectedNode = connectedNode;
		this.executionContext = executionContext;
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		this.mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
	}
	
	
	public List<String> getInterestedTopics() {
		return interestedTopics;
	}
	
	public void addInterestedTopicsListeners() {
		if (interestedTopics.contains("global_position/rel_alt")) {
			prjNode.printLog("Add " + mavrosPrefix + "global_position/rel_alt listener.. ");
			addGlobalPositionRelAltListener();
		}
	}
	
	private void addGlobalPositionRelAltListener() {
		Subscriber<std_msgs.Float64> subscriber = connectedNode.newSubscriber(mavrosPrefix + "global_position/rel_alt", std_msgs.Float64._TYPE);
		subscriber.addMessageListener(new MessageListener<std_msgs.Float64>() {
			@Override
			public void onNewMessage(std_msgs.Float64 msg) {
				updateStatus("altezza", msg.getData());
			}
		});
	}
	
	private void updateStatus(String key, Object value) {
		executionContext.getExecutionEnvironment().put(key, value);
	}
	
	

}
