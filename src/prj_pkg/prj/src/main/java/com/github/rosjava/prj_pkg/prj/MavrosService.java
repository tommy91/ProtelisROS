package com.github.rosjava.prj_pkg.prj;

import java.util.ArrayList;
import java.util.List;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

public class MavrosService<T_Req,T_Res> {
	
	private ServiceClient<T_Req, T_Res> serviceClient;
	private ServiceResponseListener<T_Res> serviceResponseListener;
	private final List<T_Res> responses = new ArrayList<T_Res>();
	
	public MavrosService(ConnectedNode connectedNode, String mavrosPrefix, String serviceName, String serviceType) {
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + serviceName, serviceType);
			serviceResponseListener = new ServiceResponseListener<T_Res>() {
				@Override
				public void onFailure(RemoteException e) {
					throw new RosRuntimeException(e);
				}

				@Override
				public void onSuccess(T_Res response) {
					responses.add(response);
				}
			};
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
	}
	
	public boolean isConnected() {
		return serviceClient.isConnected();
	}
	
	public T_Req newMessage() {
		return serviceClient.newMessage();
	}
	
	public void call(final T_Req request) {
		serviceClient.call(request, serviceResponseListener);
	}
	
	public boolean hasResponse() {
		return responses.isEmpty();
	}
	
	public T_Res getResponse() {
		if (hasResponse()) {
			return responses.remove(0);
		}
		else {
			return null;
		}
	}
	
}
