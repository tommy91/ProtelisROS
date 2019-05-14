package com.github.rosjava.prj_pkg.prj.Mavros;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.Time;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import com.github.rosjava.prj_pkg.prj.PrjNode;

public class MavrosService<T_Req,T_Res> extends MavrosReceiver<T_Res>{
	
	private ServiceClient<T_Req, T_Res> serviceClient;
	private ServiceResponseListener<T_Res> serviceResponseListener;
	private boolean canBeFound = true;	// default value set to true
	private String completeServiceName;
	private Time lastResponseTime;
	
	
	public MavrosService(PrjNode prjNode, String completeServiceName, String serviceType) {
		this.completeServiceName = completeServiceName;
		try {
			serviceClient = prjNode.getConnectedNode().newServiceClient(completeServiceName, serviceType);
			serviceResponseListener = new ServiceResponseListener<T_Res>() {
				@Override
				public void onFailure(RemoteException e) {
					throw new RosRuntimeException(e);
				}

				@Override
				public void onSuccess(T_Res response) {
					lastResponseTime = Time.fromMillis(System.currentTimeMillis());
					addReceivedMessage(response);
				}
			};
		} catch (ServiceNotFoundException e) {
			prjNode.printLog("Error on setup service '" + completeServiceName + "': service not found exception");
			canBeFound = false;
		}
	}
	
	public boolean isNotFound() {
		return !canBeFound;
	}
	
	private void checkServiceFounded() {
		if (!canBeFound) {
			throw new RosRuntimeException("Cannot call the service '" + completeServiceName + "': service not found exception");
		}
	}
	
	public boolean isConnected() {
		checkServiceFounded();
		return serviceClient.isConnected();
	}
	
	public T_Req newRequest() {
		checkServiceFounded();
		return serviceClient.newMessage();
	}
	
	public boolean hasResponse() {
		return hasReceivedMessage();
	}
	
	public T_Res getResponse() {
		return removeReceivedMessage();
	}
	
	public Time getResponseTime() {
		return lastResponseTime;
	}
	
	public void call(final T_Req request) {
		checkServiceFounded();
		serviceClient.call(request, serviceResponseListener);
	}
	
	public void callSynch(final T_Req request, int sleepTimeMs) {
		call(request);
		while (!hasReceivedMessage()) {
			try {
				Thread.sleep(sleepTimeMs);
			} catch (InterruptedException e) {
			}
		}
	}
	
}
