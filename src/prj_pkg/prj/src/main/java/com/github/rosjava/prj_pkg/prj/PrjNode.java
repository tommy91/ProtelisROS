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

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import java8.util.function.Function;

import org.apache.commons.logging.Log;

import org.protelis.lang.ProtelisLoader;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.lang.datatype.Field;
import org.protelis.lang.datatype.Tuple;
import org.protelis.lang.datatype.impl.ArrayTupleImpl;
import org.protelis.vm.LocalizedDevice;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.SpatiallyEmbeddedDevice;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

import com.github.rosjava.prj_pkg.prj.Mavros.MavrosMessagesManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosParametersManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosPublishersManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosServicesManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosSubscribersManager;

import org.ros.concurrent.CancellableLoop;

/**
 * A ROS node implementation which extends a Protelis AbstractExecutionContext
 */
public class PrjNode extends AbstractExecutionContext implements NodeMain, SpatiallyEmbeddedDevice, LocalizedDevice {
	
	/** Edit this variables **/
	
	private final String protelisProgramFilename = "arducopter";
	
	private final String baseLogDirectory = "/home/tommy/prj_ws/src/prj_pkg/launch/logs/";
	private final String baseNamespace = "dev";

	private final int sleepTime = 1000;
	
	/** ------------------- **/
	
	private ConnectedNode connectedNode;
	private Integer system_id;
	
	// Where we will print
	private Log log;
	
	// My Protelis device variables
	private ProtelisVM vm;
	private DeviceUID deviceUID;
	private int deviceIntID;
	private String deviceStringID;
	
	// Mavros variables
	private Process mavrosProcess;
	private File mavrosLogFile;
	
	private MavrosMessagesManager mavrosMessagesManager;
	private MavrosParametersManager mavrosParametersManager;
	private NeighborManager neighborManager;
	private MavrosSubscribersManager mavrosSubscribersManager;
	private MavrosPublishersManager mavrosPublishersManager;
	private MavrosServicesManager mavrosServicesManager;
	private ArdupilotManager ardupilotManager;
	
	// SITL variables
	private Process sitlProcess;

	
	public PrjNode() {
		// Calling AbstractExecutionContext constructor
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		
	}
	
	private void setupProtelis() {
		ProtelisProgram program = ProtelisLoader.parse(protelisProgramFilename);
		vm = new ProtelisVM(program, this);
	}
	
	private void setupUIDs() {
		deviceUID = new IntegerUID(system_id);
		IntegerUID integerUID = (IntegerUID) deviceUID;
		deviceIntID = integerUID.getUID();
		deviceStringID = integerUID.toString();
	}
	 
	/** 
	 * Needed by Protelis AbstractExecutionContext: Internal-only lightweight constructor to support "instance" 
	 */
	private PrjNode(DeviceUID deviceUID) {
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		this.deviceUID = deviceUID;
		vm = null;
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	protected AbstractExecutionContext instance() {
		return new PrjNode(deviceUID);
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	public DeviceUID getDeviceUID() {
		return deviceUID;
	}
	
	public String getDeviceStringID() {
		return deviceStringID;
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	public Number getCurrentTime() {
		return System.currentTimeMillis();
	}

	/** 
	 * Requested by Protelis AbstractExecutionContext
	 * Note: this should be going away in the future, to be replaced by standard Java random
	 * @deprecated
	 */
	@Override
	public double nextRandomDouble() {
		return Math.random();
	}
	
	/** 
	 * Requested by Protelis SpatiallyEmbeddedDevice interface
	 * Note: Get the distance between the current device and its neighbors.
     *       Distance must be positive.
     */
	@Override
	public Field nbrRange() {
		return buildField(new Function<Object,Double>() {
			public Double apply(final Object otherNode) {
				return getDistance((PrjNode)otherNode);
			}
		}, this);
	}
	
	/** 
	 * Requested by Protelis LocalizedDevice interface
	 * @return field of directions to other devices
	 */
	@Override
	public Field nbrVector() {
		return buildField(new Function<Object,Tuple>() {
			@Override
			public Tuple apply(final Object otherNode) {
				return getVectorDistance((PrjNode)otherNode);
			}
		}, this);
	}
	
	public double getDistance(final PrjNode otherNode) {
        final Tuple c1 = getCoordinates();
        final Tuple c2 = otherNode.getCoordinates();
        double latComp = Math.pow((Double) c2.get(0) - (Double) c1.get(0), 2);
        double lonComp = Math.pow((Double) c2.get(1) - (Double) c1.get(1), 2);
        double altComp = Math.pow((Double) c2.get(2) - (Double) c1.get(2), 2);
        return Math.sqrt(latComp + lonComp + altComp);
    }
	
	private Tuple getVectorDistance(final PrjNode otherNode) {
		final Tuple c1 = getCoordinates();
        final Tuple c2 = otherNode.getCoordinates();
        Tuple tmp = otherNode.getCoordinates();
        for (int i = 0; i < c1.size(); i++) {
            tmp = tmp.set(i, (Double) c2.get(i) - (Double) c1.get(i));
        }
        return tmp;
    }
	
	/** 
	 * Requested by Protelis LocalizedDevice interface
	 * @return coordinates of the current device
	 */
	@Override
	public Tuple getCoordinates() {
		final Double[] cd = ardupilotManager.getCoordinates();
		Tuple c = new ArrayTupleImpl(0, cd.length);
		for (int i = 0; i < cd.length; i++) {
			c = c.set(i, cd[i]);
	    }
	    return c;
	}
	
	/** 
	 * Accessor for virtual machine, to allow external execution triggering 
	 */
	public ProtelisVM getVM() {
		return vm;
	}
	
	/** 
	 * Called by Protelis to dump a string message to the output
	 */
	public void announce(String message) {
		printLog(message);
	}

	/** 
	 * Expose the network manager, to allow external simulation of network
	 * For real devices, the NetworkManager usually runs autonomously in its own thread(s)
     */
	public PrjNetworkManager accessNetworkManager() {
		return (PrjNetworkManager)super.getNetworkManager();
	}
	
	/**
	 * Requested by ROS NodeMain interface 
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava/prjnode");
	}
	
	/**
	 * Requested by ROS NodeMain interface 
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		this.connectedNode = connectedNode;
		
		// Get ROS default logger
		log = connectedNode.getLog();
		
		mavrosParametersManager = new MavrosParametersManager(this);
		
		// Get the system id from parameters
		getSystemID();
		
		setupProtelis();
		setupUIDs();
		
		launchMavrosNode();
		
		neighborManager = new NeighborManager(this);
		
		mavrosSubscribersManager = new MavrosSubscribersManager(this);
		mavrosPublishersManager = new MavrosPublishersManager(connectedNode);
		mavrosMessagesManager = new MavrosMessagesManager(connectedNode);
		mavrosServicesManager = new MavrosServicesManager(this);
		ardupilotManager = new ArdupilotManager(this);
		
		// Blocking here until the ardupilot device state is connected
		// ardupilotManager.waitArdupilotSystemsOnline();
				
		// Setup mavros listeners and service callers
//		mavrosSubscribersManager.subscribeInterestedTopics();
		ardupilotManager.waitArdupilotReady();
		mavrosServicesManager.setupServices();
		
		// Execute in loop the protelis program
		runSynchronizer();
	}
	
	/** 
	 * Requested by ROS NodeMain interface 
	 */
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
	
	/** 
	 * Requested by ROS NodeMain interface
	 */
	@Override
	public void onShutdownComplete(Node node) {
	}
	
	/** 
	 * Requested by ROS NodeMain interface
	 */
	@Override
	public void onError(Node node, Throwable throwable) {
	}
	
	private void getSystemID() {
		system_id = mavrosParametersManager.getIntegerParam("~system_id");
		if (system_id != null) {
			printLog("System ID: " + Integer.toString(system_id));
		}
		else {
			system_id = 1;
			printLog("Default System ID: " + Integer.toString(system_id));
		}
	}
	
	private void launchMavrosNode() {
		// Setup log file:
		mavrosLogFile = new File(baseLogDirectory + getMavrosNamespace() + "_mavros.log");
		try {
			mavrosLogFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String launchfilePath = "/home/tommy/prj_ws/src/prj_pkg/launch/mavros.launch";
		
//		List<String> bashCmd = Arrays.asList("bash", "-c", mavrosBashFile + " " + deviceStringID);
		List<String> bashCmd = Arrays.asList("roslaunch", launchfilePath, "system_id:=" + deviceStringID);
		
//		ProcessBuilder mavrosBuilder = new ProcessBuilder().command(bashCmd).inheritIO();
		ProcessBuilder mavrosBuilder = new ProcessBuilder().command(bashCmd);
//		mavrosBuilder.redirectError(mavrosLogFile);
		mavrosBuilder.redirectOutput(mavrosLogFile);
		
		printLog("Launching mavros.. ");
		
		try {
			mavrosProcess = mavrosBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * NOTE: The method getExecutionEnvironment() is implemented in AbstractExecutionContext
	 */
	
	public MavrosMessagesManager getMavrosMessagesManager() {
		return mavrosMessagesManager;
	}
	
	public MavrosParametersManager getMavrosParametersManager() {
		return mavrosParametersManager;
	}
	
	public MavrosServicesManager getMavrosServicesManager() {
		return mavrosServicesManager;
	}
	
	public MavrosSubscribersManager getMavrosSubscribersManager() {
		return mavrosSubscribersManager;
	}
	
	public MavrosPublishersManager getMavrosPublishersManager() {
		return mavrosPublishersManager;
	}
	
	public ConnectedNode getConnectedNode() {
		return connectedNode;
	}
	
	public ArdupilotManager getArdupilotManager() {
		return ardupilotManager;
	}
	
	public String getMavrosNamespace() {
		// Namespace like 'dev0', 'dev1', ... 
		return baseNamespace + deviceStringID;
	}

	public void printLog(String message) {
		log.info(message);
	}
	
	private void runSynchronizer() {
		CancellableLoop synchronizer = new CancellableLoop() {
			@Override
			protected void loop() throws InterruptedException {
				getVM().runCycle();
				String state = accessNetworkManager().getSendCache();
				neighborManager.sendToNeighbors(state);
				Thread.sleep(sleepTime);
			}
		};
		connectedNode.executeCancellableLoop(synchronizer);
	}

}
