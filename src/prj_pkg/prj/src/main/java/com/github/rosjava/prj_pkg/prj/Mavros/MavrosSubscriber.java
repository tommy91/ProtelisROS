package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class MavrosSubscriber<T> extends MavrosReceiver<T>{
	
	private Subscriber<T> subscriber;
	private MessageListener<T> messageListener;
	private boolean isSubscribed = false;
	
	public MavrosSubscriber(ConnectedNode connectedNode, String topicCompleteName, String topicType) {
		subscriber = connectedNode.newSubscriber(topicCompleteName, topicType);
		messageListener = new MessageListener<T>() {
			@Override
			public void onNewMessage(T message) {
				addReceivedMessage(message);
			}
		};
		subscribe();
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
