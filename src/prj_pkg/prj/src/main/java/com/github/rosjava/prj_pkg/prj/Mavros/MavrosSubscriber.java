package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.internal.node.topic.PublisherIdentifier;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.SubscriberListener;

public class MavrosSubscriber<T> extends MavrosReceiver<T>{
	
	private class MavrosSubscriberListener<T2> implements SubscriberListener<T2> {

		@Override
		public void onMasterRegistrationFailure(Subscriber<T2> subscriber) {
			setMasterRegistration(false);
		}

		@Override
		public void onMasterRegistrationSuccess(Subscriber<T2> subscriber) {
			setMasterRegistration(true);
		}

		@Override
		public void onMasterUnregistrationFailure(Subscriber<T2> subscriber) {
		}

		@Override
		public void onMasterUnregistrationSuccess(Subscriber<T2> subscriber) {
		}

		@Override
		public void onNewPublisher(Subscriber<T2> subscriber, PublisherIdentifier publisherID) {
		}

		@Override
		public void onShutdown(Subscriber<T2> subscriber) {
		}
		
	}
	
	private Subscriber<T> subscriber;
	private MessageListener<T> messageListener;
	private boolean isSubscribed = false;
	private boolean isRegistered = false;
	private boolean hasFailedRegistration = false;
	
	public MavrosSubscriber(ConnectedNode connectedNode, String topicCompleteName, String topicType) {
		subscriber = connectedNode.newSubscriber(topicCompleteName, topicType);
		subscriber.addSubscriberListener(new MavrosSubscriberListener<T>());
		messageListener = new MessageListener<T>() {
			@Override
			public void onNewMessage(T message) {
				addReceivedMessage(message);
			}
		};
		subscribe();
	}
	
	private void setMasterRegistration(boolean success) {
		isRegistered = success;
		hasFailedRegistration = !success;
	}
	
	public boolean waitMasterRegistration() {
		while ( (!isRegistered) && (!hasFailedRegistration) ) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		return isRegistered;
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
