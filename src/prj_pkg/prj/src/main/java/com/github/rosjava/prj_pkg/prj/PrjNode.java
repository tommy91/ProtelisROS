/*
 * Copyright (C) 2014 %(author)s.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.rosjava.prj_pkg.prj;

import java.util.Set;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;

import org.protelis.lang.ProtelisLoader;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.ProtelisProgram;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.Node;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import org.ros.exception.ParameterNotFoundException;
import org.ros.internal.node.parameter.DefaultParameterTree;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 */
public class PrjNode extends AbstractNodeMain {
	
	/** Edit this variables **/
	
	private final String protelisModuleName = "maxAltitude";
	
	private final String rosIP = "127.0.0.1";
	
	private final String baseLogDirectory = "/home/tommy/prj_ws/src/prj_pkg/prj/logs/";
	private final String baseNamespace = "dev";
	private final String mavrosConnectionProtocol = "udp";
	private final String mavrosAddress = "127.0.0.1";		// should be equal to rosIP
	private final String mavrosBashFile = "/home/tommy/prj_ws/src/prj_pkg/prj/src/main/java/com/github/rosjava/prj_pkg/prj/mavrosLaunch.sh";
	private int mavrosPortLocal = 14551;
	private int mavrosPortRemote = 14555;
	private final String mavrosDiagnosticsTopic = "/diagnostics";
	
	private final String sitlBashFile = "/home/tommy/prj_ws/src/prj_pkg/prj/src/main/java/com/github/rosjava/prj_pkg/prj/sitlLaunch.sh";

	private final int sleepTime = 1000;
	
	private final String listenerTopicPrefix = "prj_node_";
	
	/** ------------------- **/
	
	// Diagnostics messages from mavros
	private final String mavrosStartingUpMessage = "Node starting up";
	private final String mavrosNotConnectedMessage = "not connected";
	private final String mavrosConnectedMessage = "connected";
	
	// Where we will print
	private Log log;
	
	// My neighbors
	private Set<DeviceUID> neighbors = new HashSet<>();
	
	// My Protelis device variables
	private Map<String, Object> protelisEnvVars = new LinkedHashMap<>();
	private PrjExecutionContext executionContext;
	private DeviceUID deviceUID;
	private int deviceIntID;
	private String deviceStringID;
	private String deviceGsonID;
	private String listenerChannel;

	private Gson gson = new Gson();
	private int deviceID;

	// The corresponding publisher for each neighbor
	private Map<DeviceUID, Publisher<rosjava_custom_msg.StateWithID>> publishers = new HashMap<>();
	
	// Mavros variables
	private String bashCmd;
	private Process mavrosProcess;
	private File mavrosLogFile;
	private String mavrosNamespace;
	private String mavrosStatus = "";
	private boolean isMavrosConnected = false;
	private StatusManager statusManager;
	
	// SITL variables
	private Process sitlProcess;

	public PrjNode(int deviceID, Map<String, Object> protelisEnvVars) {
		this.protelisEnvVars = protelisEnvVars;
		this.deviceID = deviceID;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava/prjnode");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		// Where to print
		log = connectedNode.getLog();
		
		// Setup Protelis
		executionContext = new PrjExecutionContext(this, protelisEnvVars, protelisModuleName, deviceID);
		setupDeviceData();
		
		// Printing neighbors here because printLog is available just from here on
		printNeighbors();
		
		// Launch mavros
		launchMavrosNode(connectedNode);
		
		// Launch sitl
//		launchSITL(connectedNode);
		
		printLog("Waiting to establish mavros connection..");
		while (!isMavrosConnected) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				printLog("Interruction command received.. wakeup from sleep");
			}
		}
		
		// Setup mavros listeners to collect status informations
		statusManager = new StatusManager(this, connectedNode, executionContext);
		statusManager.addInterestedTopicsListeners();
		
		// Setup publisher mapping for all neighbors
		setupNeighborsPublishers(connectedNode);
		
		// Setup listener to get data from neighbors
		setupNeighborsListener(connectedNode);

		// Get parameters from parameter server
		// getParams(connectedNode);
		
		runSynchronizer(connectedNode);

	}
	
	@Override
	public void onShutdown(Node node) {
		if (mavrosProcess != null) {
			printLog("Killing mavros node..");
			mavrosProcess.destroy();
			printLog("Killed mavros");
		}
		
		if (sitlProcess != null) {
			printLog("Killing SITL..");
			sitlProcess.destroy();
			printLog("Killed SITL");
		}
	}
	
	@Override
	public void onError(Node node, Throwable throwable) {
	}
	
	private void launchSITL(ConnectedNode connectedNode) {
		// SITL device name (it can be whatever)
		String deviceName = baseNamespace + Integer.toString(deviceID);
		
		// For -In -> n = deviceID (starting from 0)
		List<String> bashCmd = Arrays.asList("bash", "-c", sitlBashFile + " " + deviceName + " " + deviceID + " " + rosIP);
		ProcessBuilder sitlBuilder = new ProcessBuilder().command(bashCmd).inheritIO();
		
		printLog("Launching SITL.. ");	
		
		try {
			sitlProcess = sitlBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void launchMavrosNode(ConnectedNode connectedNode) {
		// The command to launch mavros should be like
		// 'rosrun mavros mavros_node __ns:=dev1 _fcu_url:=udp://127.0.0.1:14551@14555 __ip:=127.0.0.1'
		// __ip (the IP of the machine) is needed to prevent the use of hostnames instead of addresses
		
		// Namespace like 'dev1'
		mavrosNamespace = baseNamespace + Integer.toString(deviceID);
		
		// Ports like '14551@14555' increasing by 10 for every device
		int skipPorts = 10 * deviceID;
		mavrosPortLocal += skipPorts;
		mavrosPortRemote += skipPorts;
		String ports = Integer.toString(mavrosPortLocal) + "@" + Integer.toString(mavrosPortRemote);
		
		// Final address like 'udp://127.0.0.1:14551@14555'
		String completeAddress = mavrosConnectionProtocol + "://" + mavrosAddress + ":" + ports; 
		
//		// Arguments to pass to mavros
//		String nsArg = "__ns:=" + mavrosNamespace;
//		String fcuArg = "_fcu_url:=" + completeAddress;
//		String ipAddr = "__ip:=" + mavrosAddress;
		
//		// Setup log file:
//		mavrosLogFile = new File(baseLogDirectory + mavrosNamespace + "_mavros.log");
//		try {
//			mavrosLogFile.createNewFile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		List<String> bashCmd = Arrays.asList("bash", "-c", mavrosBashFile + " " + mavrosNamespace + " " + completeAddress + " " + rosIP);
		
//		List<String> mavrosCmd = Arrays.asList("rosrun", "mavros", "mavros_node", nsArg, fcuArg, ipAddr);
		ProcessBuilder mavrosBuilder = new ProcessBuilder().command(bashCmd).inheritIO();
		// mavrosBuilder.redirectError(mavrosLogFile);
//		mavrosBuilder.redirectOutput(mavrosLogFile);
		
		printLog("Launching mavros.. ");
//		printLog("Executing: rosrun mavros mavros_node " + nsArg + " " + fcuArg + " " + ipAddr);
		
		try {
			mavrosProcess = mavrosBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addDiagnosticsListener(connectedNode);
		
	}
	
	private void addDiagnosticsListener(ConnectedNode connectedNode) {
		printLog("Add diagnostics listener.. ");
		Subscriber<diagnostic_msgs.DiagnosticArray> subscriber = connectedNode.newSubscriber(mavrosDiagnosticsTopic,
				diagnostic_msgs.DiagnosticArray._TYPE);
		subscriber.addMessageListener(new MessageListener<diagnostic_msgs.DiagnosticArray>() {
			@Override
			public void onNewMessage(diagnostic_msgs.DiagnosticArray msg) {
				List<diagnostic_msgs.DiagnosticStatus> statusList = msg.getStatus();
				for (diagnostic_msgs.DiagnosticStatus status : statusList) {
					// Checking FCU connection (starting up, not connected, connected)
					if (Objects.equals(status.getName().trim(), mavrosNamespace + "/mavros: FCU connection")) {
						String statusMsg = status.getMessage().trim();
						if (!Objects.equals(statusMsg, mavrosStatus)) {
							if (Objects.equals(statusMsg, mavrosStartingUpMessage)) {
								printLog(mavrosNamespace + "/mavros node is starting up..");
							} 
							else if (Objects.equals(statusMsg, mavrosConnectedMessage)) {
								printLog(mavrosNamespace + "/mavros node is connected!");
								isMavrosConnected = true;
							}
							else if (Objects.equals(statusMsg, mavrosNotConnectedMessage)) {
								if (Objects.equals(mavrosStatus, mavrosConnectedMessage)) {
									printLog(mavrosNamespace + "/mavros node has lost connection!");
								}
								else {
									printLog(mavrosNamespace + "/mavros node is NOT connected!");
								}
								isMavrosConnected = false;
							}
							else {
								printLog(mavrosNamespace + "/mavros node status unknown: '" + statusMsg + "'");
							}
							mavrosStatus = statusMsg;
							break;
						}
					}
				}
			}
		});
	}
	
	public String getMavrosNamespace() {
		return mavrosNamespace;
	}

	private void getParams(ConnectedNode connectedNode) {
		DefaultParameterTree params = (DefaultParameterTree) connectedNode.getParameterTree();
		try {
			int myID = params.getInteger(connectedNode.resolveName("~myID").toString());
			printLog("My new ID: " + Integer.toString(myID));
		} catch (ParameterNotFoundException e) {
			printLog("ParameterNotFoundException: " + e);
			List<GraphName> paramsNames = params.getNames();
			for (int i = 0; i < paramsNames.size(); i++) {
				printLog("parametro " + Integer.toString(i) + ": " + paramsNames.get(i));
			}
		}
	}

	public void printLog(String message) {
		log.info(message);
	}

	private void setupDeviceData() {
		deviceUID = executionContext.getDeviceUID();
		IntegerUID integerUID = (IntegerUID) deviceUID;
		deviceIntID = integerUID.getUID();
		deviceStringID = integerUID.toString();
		deviceGsonID = gson.toJson(deviceUID);
		listenerChannel = listenerTopicPrefix + deviceStringID;
	}
	
	private String getNeighborTopic(DeviceUID neighborUID) {
		return listenerTopicPrefix + ((IntegerUID) neighborUID).toString();
	}
	
	public PrjExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	private void runSynchronizer(ConnectedNode connectedNode) {
		connectedNode.executeCancellableLoop(new ProtelisCancellableLoop(executionContext, sleepTime));
	}

	public void sendToNeighbors(String state) {
		// Deliver shared-state updates over the network
//		printLog("Start sending state to neighbors..");
		for (DeviceUID neighborID : publishers.keySet()) {
//			printLog("Sending state to neighbor " + neighborID.toString());
			Publisher<rosjava_custom_msg.StateWithID> pub = publishers.get(neighborID);
			rosjava_custom_msg.StateWithID msg = pub.newMessage();
			msg.setId(deviceGsonID);
			msg.setState(state);
			pub.publish(msg);
		}
	}

	private void setupNeighborsListener(ConnectedNode connectedNode) {
		Subscriber<rosjava_custom_msg.StateWithID> subscriber = connectedNode.newSubscriber(listenerChannel,
				rosjava_custom_msg.StateWithID._TYPE);
		subscriber.addMessageListener(new MessageListener<rosjava_custom_msg.StateWithID>() {
			@Override
			public void onNewMessage(rosjava_custom_msg.StateWithID msg) {
//				printLog("Receiving state from neighbor");
				String neighborID = msg.getId();
				String neighborState = msg.getState();
				executionContext.accessNetworkManager().receiveFromNeighbor(neighborID, neighborState);
			}
		});
	}
	
	public void addNeighbor(DeviceUID neighborUID) {
		neighbors.add(neighborUID);
		if (log != null) {
			printLog("Add new neighbor: " + neighborUID.toString());
		}
	}
	
	private void printNeighbors() {
		String neighborsUIDs = "";
		for (DeviceUID neighbor : neighbors) {
			neighborsUIDs += neighbor.toString() + "  ";
		}
		printLog("Neighbors: " + neighborsUIDs);
	}
	
	private void setupNeighborsPublishers(ConnectedNode connectedNode) {
		for (DeviceUID neighborUID : neighbors) {
			String neighborTopic = getNeighborTopic(neighborUID);
			Publisher<rosjava_custom_msg.StateWithID> neighborPublisher = connectedNode.newPublisher(neighborTopic, rosjava_custom_msg.StateWithID._TYPE);
			publishers.put(neighborUID, neighborPublisher);
		}
	}
	
	public DeviceUID getDeviceUID() {
		return deviceUID;
	}

}
