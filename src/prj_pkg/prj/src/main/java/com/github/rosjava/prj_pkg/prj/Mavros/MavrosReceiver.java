package com.github.rosjava.prj_pkg.prj.Mavros;

import java.util.ArrayList;
import java.util.List;

public class MavrosReceiver<T> {
	
	private final List<T> receivedMessages = new ArrayList<T>();
	
	protected void addReceivedMessage(T message) {
		if (hasReceivedMessage()) {
			// Override the previous message
			receivedMessages.set(0, message);
		}
		else {
			receivedMessages.add(message);
		}
	}
	
	public boolean hasReceivedMessage() {
		return !receivedMessages.isEmpty();
	}
	
	public T getReceivedMessage() {
		if (hasReceivedMessage()) {
			return receivedMessages.get(0);
		}
		else {
			return null;
		}
	}
	
	public T removeReceivedMessage() {
		if (hasReceivedMessage()) {
			return receivedMessages.remove(0);
		}
		else {
			return null;
		}
	}

}
