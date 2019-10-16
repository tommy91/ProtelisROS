package com.github.rosjava.prj_pkg.prj;

import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeListener;

public abstract class PrjCancellableRunnable implements Runnable {

	private Thread thread;

	public PrjCancellableRunnable(ConnectedNode connectedNode) {
		connectedNode.addListener(new NodeListener() {
			@Override
			public void onStart(ConnectedNode connectedNode) {
			}

			@Override
			public void onShutdown(Node node) {
				cancel();
			}

			@Override
			public void onShutdownComplete(Node node) {
			}

			@Override
			public void onError(Node node, Throwable throwable) {
				cancel();
			}
		});
	}


	@Override
	public void run() {
		thread = Thread.currentThread();
		try {
			execute();
		} catch (InterruptedException e) {
			handleInterruptedException(e);
		} finally {
			thread = null;
		}
	}

	/**
	 * Override this method to execute your code in a new cancellable thread
	 */
	protected abstract void execute() throws InterruptedException;

	/**
	 * An {@link InterruptedException} was thrown.
	 */
	protected void handleInterruptedException(InterruptedException e) {
	}

	/**
	 * Interrupts the loop.
	 */
	public void cancel() {
		if (thread != null) {
			thread.interrupt();
		}
	}

	/**
	 * @return {@code true} if the loop is running
	 */
	public boolean isRunning() {
		return thread != null && !thread.isInterrupted();
	}
}
