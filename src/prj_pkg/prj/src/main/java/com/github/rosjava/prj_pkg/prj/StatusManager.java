package com.github.rosjava.prj_pkg.prj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class StatusManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private ExecutionEnvironment executionEnvironment;
	private final String mavrosNamespace;
	private final String mavrosPrefix;
	private Map<String,Subscriber<?>> subscribers = new HashMap<>();
	private final String mavrosDiagnosticsTopic = "/diagnostics";
	private List<String> interestedTopics = Arrays.asList(
			"state",
			"global_position/global",
			"global_position/rel_alt"
			); 
	
	public StatusManager(PrjNode prjNode, ConnectedNode connectedNode, ExecutionEnvironment executionEnvironment) {
		this.prjNode = prjNode;
		this.connectedNode = connectedNode;
		this.executionEnvironment = executionEnvironment;
		this.mavrosNamespace = prjNode.getMavrosNamespace();
		// Mavros topics prefix is like: '/dev0/mavros/'
		// The complete topic name is prefix + topic name 
		this.mavrosPrefix = "/" + mavrosNamespace + "/mavros/";
		// addDiagnosticsListener();
	}
	
	private void addDiagnosticsListener() {
		printLog("Add diagnostics listener.. ");
		Subscriber<diagnostic_msgs.DiagnosticArray> subscriber = connectedNode.newSubscriber(mavrosDiagnosticsTopic,
				diagnostic_msgs.DiagnosticArray._TYPE);
		subscriber.addMessageListener(new MessageListener<diagnostic_msgs.DiagnosticArray>() {
			@Override
			public void onNewMessage(diagnostic_msgs.DiagnosticArray msg) {
				List<diagnostic_msgs.DiagnosticStatus> statusList = msg.getStatus();
				for (diagnostic_msgs.DiagnosticStatus status : statusList) {
					// Status name is like 'mavrosNamespace/mavros: FCU connection'
					String statusName = status.getName().trim();
					// Check if the namenspace is corresponding with this one
					String msgNamespace = statusName.substring(0, mavrosNamespace.length());
					if (Objects.equals(msgNamespace, mavrosNamespace)) {
						// => get the system name without 'mavrosNamespace/mavros: '
						// => just 'FCU connection'
						// (9 is the length of '/mavros: ') 
						String systemName = statusName.substring(mavrosNamespace.length() + 9);
						String statusMsg = status.getMessage().trim();
						updateStatusWithMessage(systemName, systemName, statusMsg);
//						// Add also systems checks present in System
//						if (Objects.equals(systemName, "System")) {
//							List<diagnostic_msgs.KeyValue> values = status.getValues();
//							for (diagnostic_msgs.KeyValue value : values) {
//								String key = value.getKey();
//								String val = value.getValue();
//								updateStatusWithMessage("System: " + key, "System " + key, val);
//							}
//						}
					}
				}
			}
		});
	}
	
	public List<String> getInterestedTopics() {
		return interestedTopics;
	}
	
	public void addInterestedTopicsListeners() {
		for (String topic : interestedTopics) {
			printLog("Add " + mavrosPrefix + topic + " listener.. ");
			switch(topic) {
				case "state":
					addStateListener();
					break;
				case "global_position/global":
					addGlobalPositionGlobalListener();
					break;
				case "global_position/rel_alt":
					addGlobalPositionRelAltListener();
					break;
				default:
					printLog("Unknown topic '" + topic + "'");
			}
		}
	}
	
	private void addStateListener() {
		Subscriber<mavros_msgs.State> subscriber = connectedNode.newSubscriber(mavrosPrefix + "state", mavros_msgs.State._TYPE);
		subscriber.addMessageListener(new MessageListener<mavros_msgs.State>() {
			@Override
			public void onNewMessage(mavros_msgs.State msg) {
				updateStatusWithMessage("Connection status changed", "connected", msg.getConnected());
				updateStatus("armed", msg.getArmed());
				updateStatus("guided", msg.getGuided());
				updateStatus("mode", msg.getMode());
				updateStatusWithMessage("System status changed", "system_status", msg.getSystemStatus());
			}
		});
		subscribers.put("state",subscriber);
	}
	
	private void addGlobalPositionGlobalListener() {
		final String topic = "global_position/global";
		Subscriber<sensor_msgs.NavSatFix> subscriber = connectedNode.newSubscriber(mavrosPrefix + topic, sensor_msgs.NavSatFix._TYPE);
		subscriber.addMessageListener(new MessageListener<sensor_msgs.NavSatFix>() {
			@Override
			public void onNewMessage(sensor_msgs.NavSatFix msg) {
				updateStatus(topic + "/status", msg.getStatus());
				updateStatus(topic + "/latitude", msg.getLatitude());
				updateStatus(topic + "/longitude", msg.getLongitude());
				updateStatus(topic + "/altitude", msg.getAltitude());
				updateStatus(topic + "/position_covariance", msg.getPositionCovariance());
				updateStatus(topic + "/position_covariance_type", msg.getPositionCovarianceType());
			}
		});
		subscribers.put(topic,subscriber);
	}
	
	private void addGlobalPositionRelAltListener() {
		Subscriber<std_msgs.Float64> subscriber = connectedNode.newSubscriber(mavrosPrefix + "global_position/rel_alt", std_msgs.Float64._TYPE);
		subscriber.addMessageListener(new MessageListener<std_msgs.Float64>() {
			@Override
			public void onNewMessage(std_msgs.Float64 msg) {
				updateStatus("altezza", msg.getData());
			}
		});
		subscribers.put("global_position/rel_alt",subscriber);
	}
	
	private void updateStatusWithMessage(String messagePrefix, String key, Object value) {
		if ( (!executionEnvironment.has(key)) || (!Objects.equals(executionEnvironment.get(key),value)) ) {
			printLog(messagePrefix + ": " + Objects.toString(value));
			executionEnvironment.put(key, value);
		}
	}
	
	private void updateStatus(String key, Object value) {
		executionEnvironment.put(key, value);
	}
	
	private void printLog(String message) {
		prjNode.printLog(message);
	}
	
	

}
