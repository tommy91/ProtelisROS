package com.github.rosjava.prj_pkg.prj;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class MavrosSubscriber<T> {
	
	private Subscriber<T> subscriber;
	private MessageListener<T> messageListener;
	private final ExecutionEnvironment executionEnv;
	private final String objTopicName;
	private boolean isSubscribed = false;
	
	public MavrosSubscriber(ConnectedNode connectedNode, ExecutionEnvironment executionEnvironment, 
			String mavrosPrefix, String topicName, String topicType) {
		objTopicName = topicName;
		executionEnv = executionEnvironment;
		String topicCompleteName = mavrosPrefix + topicName;
		subscriber = connectedNode.newSubscriber(topicCompleteName, topicType);
		messageListener = new MessageListener<T>() {
			@Override
			public void onNewMessage(T message) {
				executionEnv.put(objTopicName, message);
			}
		};
	}
	
	public void subscribe() {
		if (!isSubscribed) {
			subscriber.addMessageListener(messageListener);
			isSubscribed = true;
		}
	}
	
	public void unsubscribe() {
		if (isSubscribed) {
			subscriber.removeMessageListener(messageListener);
			isSubscribed = false;
		}
	}
	
	public boolean isSubscribed() {
		return isSubscribed;
	}
	
}
