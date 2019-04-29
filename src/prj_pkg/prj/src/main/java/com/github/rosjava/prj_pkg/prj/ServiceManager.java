package com.github.rosjava.prj_pkg.prj;

import java.util.HashMap;
import java.util.Map;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.message.MessageFactory;

public class ServiceManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private ExecutionEnvironment executionEnvironment;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,ServiceClient<?,?>> services = new HashMap<>();

	public ServiceManager(PrjNode prjNode, ConnectedNode connectedNode, ExecutionEnvironment executionEnvironment) {
		this.prjNode = prjNode;
		this.connectedNode = connectedNode;
		this.executionEnvironment = executionEnvironment;
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		// Mavros services prefix is like: '/dev0/mavros/'
		// The complete service name is prefix + service name 
		this.mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
	}
	
	public void setupServices() {
		prjNode.printLog("Setup services.. ");
		setupSetMode();
		setupArming();
		setupTakeoff();
		setupParamGet();
		setupParamSet();
	}
	
	/**
	 * All mavros services names are lowercase and with words separated by underscore
	 */
	
	private void setupSetMode() {
		prjNode.printLog("Setup set mode service to '" + mavrosPrefix + "set_mode" + "'");
		ServiceClient<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + "set_mode", mavros_msgs.SetMode._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		services.put("set_mode", serviceClient);
	}
	
	@SuppressWarnings("unchecked")
	public void setMode(String mode) {
		ServiceClient<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse> serviceClient = 
				(ServiceClient<mavros_msgs.SetModeRequest, mavros_msgs.SetModeResponse>) services.get("set_mode");
		mavros_msgs.SetModeRequest request = (mavros_msgs.SetModeRequest) serviceClient.newMessage();
		request.setCustomMode(mode);
		serviceClient.call(request, new ServiceResponseListener<mavros_msgs.SetModeResponse>() {
			@Override
			public void onSuccess(mavros_msgs.SetModeResponse response) {
				if (response.getModeSent()) {
					prjNode.printLog("Successfull call to setMode (modeSent: " + Boolean.toString(response.getModeSent()) + ")");
				}
				else {
					prjNode.printLog("Call to setMode NOT successfull (modeSent: " + Boolean.toString(response.getModeSent()) + ")");
				}
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}
	
	private void setupArming() {
		ServiceClient<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + "cmd/arming", mavros_msgs.CommandBool._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		services.put("cmd/arming", serviceClient);
	}
	
	@SuppressWarnings("unchecked")
	public void arming(boolean arm) {
		ServiceClient<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse> serviceClient = 
				(ServiceClient<mavros_msgs.CommandBoolRequest, mavros_msgs.CommandBoolResponse>) services.get("cmd/arming");
		mavros_msgs.CommandBoolRequest request = (mavros_msgs.CommandBoolRequest) serviceClient.newMessage();
		request.setValue(arm);
		serviceClient.call(request, new ServiceResponseListener<mavros_msgs.CommandBoolResponse>() {
			@Override
			public void onSuccess(mavros_msgs.CommandBoolResponse response) {
				if (response.getSuccess()) {
					prjNode.printLog("Successfull call to arming (Success: " + Boolean.toString(response.getSuccess()) + ", result:" 
										+ Byte.toString(response.getResult()) + ")");
				}
				else {
					prjNode.printLog("Call to arming NOT successfull (Success: " + Boolean.toString(response.getSuccess()) + ", result:" 
							+ Byte.toString(response.getResult()) + ")");
					put("arming_fail",true);
				}
				
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}
	
	private void setupTakeoff() {
		ServiceClient<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + "cmd/takeoff", mavros_msgs.CommandTOL._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		services.put("cmd/takeoff", serviceClient);
	}
	
	@SuppressWarnings("unchecked")
	public void takeoff(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		ServiceClient<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse> serviceClient = 
				(ServiceClient<mavros_msgs.CommandTOLRequest, mavros_msgs.CommandTOLResponse>) services.get("cmd/takeoff");
		mavros_msgs.CommandTOLRequest request = (mavros_msgs.CommandTOLRequest) serviceClient.newMessage();
		if (min_pitch != null)
			request.setMinPitch(min_pitch);
		if (yaw != null)
			request.setYaw(yaw);
		if (latitude != null)
			request.setLatitude(latitude);
		if (longitude != null) 
			request.setLongitude(longitude);
		if (altitude != null) 
			request.setAltitude(altitude);
		serviceClient.call(request, new ServiceResponseListener<mavros_msgs.CommandTOLResponse>() {
			@Override
			public void onSuccess(mavros_msgs.CommandTOLResponse response) {
				if (response.getSuccess()) {
					prjNode.printLog("Successfull call to takeoff (Success: " + Boolean.toString(response.getSuccess()) + ", result:" 
							+ Byte.toString(response.getResult()) + ")");
					put("waiting_takeoff", false);
					put("takeoff",true);
				}
				else {
					prjNode.printLog("Call to takeoff NOT successfull (Success: " + Boolean.toString(response.getSuccess()) + ", result:" 
							+ Byte.toString(response.getResult()) + ")");
					put("takeoff_fail",true);
				}
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}
	
	private void setupParamGet() {
		ServiceClient<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + "param/get", mavros_msgs.ParamGet._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		services.put("param/get", serviceClient);
	}
	
	@SuppressWarnings("unchecked")
	public void paramGet(String param_id) {
		ServiceClient<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse> serviceClient = 
				(ServiceClient<mavros_msgs.ParamGetRequest, mavros_msgs.ParamGetResponse>) services.get("param/get");
		mavros_msgs.ParamGetRequest request = (mavros_msgs.ParamGetRequest) serviceClient.newMessage();
		request.setParamId(param_id);
		serviceClient.call(request, new ServiceResponseListener<mavros_msgs.ParamGetResponse>() {
			@Override
			public void onSuccess(mavros_msgs.ParamGetResponse response) {
				if (response.getSuccess()) {
					mavros_msgs.ParamValue value = response.getValue();
					prjNode.printLog("Successfull call to param get: response = " + value.toString());
				}
				else {
					prjNode.printLog("Call to param get NOT successfull");
				}
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}
	
	private void setupParamSet() {
		ServiceClient<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient(mavrosPrefix + "param/set", mavros_msgs.ParamSet._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		services.put("param/set", serviceClient);
	}
	
	@SuppressWarnings("unchecked")
	public void paramSet(String param_id, mavros_msgs.ParamValue value) {
		ServiceClient<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse> serviceClient = 
				(ServiceClient<mavros_msgs.ParamSetRequest, mavros_msgs.ParamSetResponse>) services.get("param/set");
		mavros_msgs.ParamSetRequest request = (mavros_msgs.ParamSetRequest) serviceClient.newMessage();
		request.setParamId(param_id);
		request.setValue(value);
		serviceClient.call(request, new ServiceResponseListener<mavros_msgs.ParamSetResponse>() {
			@Override
			public void onSuccess(mavros_msgs.ParamSetResponse response) {
				if (response.getSuccess()) {
					prjNode.printLog("Successfull call to param set");
				}
				else {
					prjNode.printLog("Call to param set NOT successfull");
				}
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}
	
	@SuppressWarnings("null")
	public void paramSet(String param_id, int value) {
		MessageFactory messageFactory = connectedNode.getTopicMessageFactory();
		mavros_msgs.ParamValue pv = messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
		pv.setInteger(value);
		paramSet(param_id, pv);
	}
	
	@SuppressWarnings("null")
	public void paramSet(String param_id, float value) {
		MessageFactory messageFactory = connectedNode.getTopicMessageFactory();
		mavros_msgs.ParamValue pv = messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
		pv.setReal(value);
		paramSet(param_id, pv);
	}
	
	private void put(String key, Object value) {
		executionEnvironment.put(key, value);
	}
	
}
