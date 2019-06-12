package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.internal.node.topic.SubscriberIdentifier;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.PublisherListener;

public class MavrosPublisher<T> {
	
	private class MavrosPublisherListener<T2> implements PublisherListener<T2> {

		@Override
		public void onMasterRegistrationFailure(Publisher<T2> publisher) {
			setMasterRegistration(false);
		}

		@Override
		public void onMasterRegistrationSuccess(Publisher<T2> publisher) {
			setMasterRegistration(true);
		}

		@Override
		public void onMasterUnregistrationFailure(Publisher<T2> publisher) {
		}

		@Override
		public void onMasterUnregistrationSuccess(Publisher<T2> publisher) {
		}

		@Override
		public void onNewSubscriber(Publisher<T2> arg0, SubscriberIdentifier publisher) {
		}

		@Override
		public void onShutdown(Publisher<T2> publisher) {
		}
		
	}
	
	private Publisher<T> publisher;
	
	private boolean isRegistered = false;
	private boolean hasFailedRegistration = false;

	public MavrosPublisher(final ConnectedNode connectedNode, String topicCompleteName, String topicType) {
		publisher = connectedNode.newPublisher(topicCompleteName, topicType);
		publisher.addListener(new MavrosPublisherListener<T>());
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
	
	public boolean hasSubscribers() {
		return publisher.hasSubscribers();
	}
	
	public int getNumberOfSubscribers() {
		return publisher.getNumberOfSubscribers();
	}
	
	public T newMessage() {
		return publisher.newMessage();
	}
	
	public void publish(T message) {
		publisher.publish(message);
	}
	
}
