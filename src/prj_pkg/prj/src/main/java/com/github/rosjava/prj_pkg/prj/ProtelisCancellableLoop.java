package com.github.rosjava.prj_pkg.prj;

import org.ros.concurrent.CancellableLoop;

public class ProtelisCancellableLoop extends CancellableLoop {
	protected PrjExecutionContext executionContext;
	protected final int sleepTime;
	
	public ProtelisCancellableLoop(PrjExecutionContext executionContext, final int sleepTime) {
		super();
		this.executionContext = executionContext;
		this.sleepTime = sleepTime;
	}

	@Override
	protected void loop() throws InterruptedException {
		executionContext.getVM().runCycle();
		executionContext.sendToNeighbors();
		Thread.sleep(sleepTime);
	}
	
}
