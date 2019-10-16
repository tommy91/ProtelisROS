package com.github.rosjava.prj_pkg.prj.RosCommunicationManagers;

import java.util.ArrayList;
import java.util.List;

public class PrjReceiver<T> {
	
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
	
	protected T getForcedReceivedMessage() {
		return receivedMessages.get(0);
	}
	
	public T getReceivedMessage() {
		if (hasReceivedMessage()) {
			return receivedMessages.get(0);
		}
		else {
			return null;
		}
	}
	
	protected T removeForcedReceivedMessage() {
		return receivedMessages.remove(0);
	}
	
	public T removeReceivedMessage() {
		if (hasReceivedMessage()) {
			return receivedMessages.remove(0);
		}
		else {
			return null;
		}
	}
	
	public void waitReceivedMessage(int sleepTimeMillis) {
		while (!hasReceivedMessage()) {
			try {
				Thread.sleep(sleepTimeMillis);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public T waitAndGetReceivedMessage(int sleepTimeMillis) {
		waitReceivedMessage(sleepTimeMillis);
		return receivedMessages.get(0);
	}
	
	public T waitAndRemoveReceivedMessage(int sleepTimeMillis) {
		waitReceivedMessage(sleepTimeMillis);
		return receivedMessages.remove(0);
	}

}
