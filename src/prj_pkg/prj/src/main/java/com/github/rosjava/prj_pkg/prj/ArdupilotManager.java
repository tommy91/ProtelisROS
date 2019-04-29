package com.github.rosjava.prj_pkg.prj;

import java.lang.Byte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.protelis.vm.ExecutionEnvironment;

public class ArdupilotManager {
	
	private final PrjNode prjNode;
	private ExecutionEnvironment executionEnvironment;
	ServiceManager serviceManager;

	
	public ArdupilotManager(PrjNode prjNode, ExecutionEnvironment executionEnvironment, ServiceManager serviceManager) {
		this.prjNode = prjNode;
		this.executionEnvironment = executionEnvironment;
		this.serviceManager = serviceManager;
		
	}
	
	public void waitArdupilotSystemsOnline() {
		printLog("Waiting for the ardupilot systems to be online.. ");
		
		// Map of < System name, Online status message >
		Map<String,String> systemsStatus = new HashMap<String,String>();
		systemsStatus.put("GCS bridge", "connected");
		systemsStatus.put("System", "Normal");
		systemsStatus.put("Heartbeat", "Normal");
		systemsStatus.put("GPS", "3D fix");
		systemsStatus.put("FCU connection", "connected");
		// TODO systemsStatus.put("Battery", "???");
		
		List<String> systemSystemsNeeded = new ArrayList<String>();
		systemSystemsNeeded.add("System 3D gyro");
		systemSystemsNeeded.add("System 3D accelerometer");
		systemSystemsNeeded.add("System 3D magnetometer");
		systemSystemsNeeded.add("System absolute pressure");
		systemSystemsNeeded.add("System GPS");
		systemSystemsNeeded.add("System 3D angular rate control");
		systemSystemsNeeded.add("System attitude stabilization");
		systemSystemsNeeded.add("System yaw position");
		systemSystemsNeeded.add("System motor outputs / control");
		systemSystemsNeeded.add("System rc receiver");
		systemSystemsNeeded.add("System AHRS subsystem health");
		systemSystemsNeeded.add("System Terrain subsystem health");
		systemSystemsNeeded.add("System Battery");
		int numSystemSystemsNeeded = systemSystemsNeeded.size();
		
		// Setting all the systems as offline before starting to check
		List<String> offlineSystems = new ArrayList<String>();
		for (String systemName : systemsStatus.keySet()) {
			offlineSystems.add(systemName);
		}
		
		while (!offlineSystems.isEmpty()) {
			List<String> remainingSystems = new ArrayList<String>(offlineSystems);
			for (int i = 0; i < offlineSystems.size(); i++) {
				String systemName = offlineSystems.get(i);
				if (has(systemName) && Objects.equals(get(systemName), systemsStatus.get(systemName))) {
					// Wait for all checks in System to be "ok"
//					if (Objects.equals(systemName,"System")) {
//						systemSystemsNeeded = checkSystem(systemSystemsNeeded);
//						if (systemSystemsNeeded.size() > 0) {
//							if (numSystemSystemsNeeded > systemSystemsNeeded.size()) {
//								// Not all checks in System are good, but someone is now ok
//								numSystemSystemsNeeded = systemSystemsNeeded.size();
//								String missing = list2str(systemSystemsNeeded);
//								printLog(systemName + ": missing [" + missing + "]");
//							}
//							continue;
//						}
//					}
					offlineSystems.remove(i);
					i--;
					remainingSystems.remove(systemName);
					if (remainingSystems.size() > 0) {
						// Collecting remaining offline systems for printing purposes
						String missing = list2str(remainingSystems);
						printLog(systemName + " is online (" + systemsStatus.get(systemName) + ") missing [" + missing + "]");
					}
					else {
						printLog(systemName + " is online (" + systemsStatus.get(systemName) + ")");
					}
				}
			}
		}
		printLog("All the systems are online");
	}
	
	private String list2str(List<String> l) {
		String s = "";
		for (int i = 0; i < l.size(); i++) {
			s += l.get(i);
			if (i + 1 < l.size()) { 
				s += ","; 
			}
		}
		return s;
	}
	
	private List<String> checkSystem(List<String> systemSystemsNeeded) {
		for (int i = 0; i < systemSystemsNeeded.size(); i++) {
			if (has(systemSystemsNeeded.get(i)) && Objects.equals(get(systemSystemsNeeded.get(i)), "Ok")) {
				systemSystemsNeeded.remove(i);
				i--;
			}
		}
		return systemSystemsNeeded;
	}
	
	public void waitArdupilotReady() {
		printLog("Waiting for the ardupilot device to be connected..");
		while (true) {
			if (has("connected") && has("system_status")) {
				if (getBool("connected") && (getByte("system_status")==3)) {
					printLog("Ardupilot device connected");
					break;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				printLog("Interruption command received.. wakeup from sleep");
			}
		}
	}
	
	public String getStatus() {
		if (has("waiting_takeoff") && getBool("waiting_takeoff")){
			return "WAITING_TAKEOFF";
		}
		else if (has("takeoff") && getBool("takeoff")){
			return "FLYING";
		}
		else if (has("landed") && getBool("landed")) {
			return "LANDED";
		}
		else if (has("connected") && has("system_status") && getBool("connected") && (getByte("system_status")==3)) {
			return "READY";
		}
		else {
			return "UNKNOWN";
		}
	}
	
	public void disablePreArmChecks() {
		printLog("Disabling pre-arm checks.. ");
		serviceManager.paramSet("ARMING_CHECK", 0);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			printLog("Interruption command received.. wakeup from sleep");
		}
	}
	
	public Double[] getCoordinates() {
		Double latitude = null;
		Double longitude = null;
		Double altitude = null;
		String fromTopic = "global_position/global/";
		if (has(fromTopic + "latitude")) {
			latitude = getDouble(fromTopic + "latitude");
		}
		if (has(fromTopic + "longitude")) {
			longitude = getDouble(fromTopic + "longitude");
		}
		if (has(fromTopic + "altitude")) {
			altitude = getDouble(fromTopic + "altitude");
		}
		return new Double[]{latitude, longitude, altitude};
	}
	
	public void takeoff(double altitude) {
		takeoff(null, null, null, null, (float)altitude);
	}
	
	public void takeoff(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		printLog("Initializing takeoff.. ");
		put("waiting_takeoff", true);
//		disablePreArmChecks();
		setMode("GUIDED");
		arming(true);
		String toPrint = "Takeoff.. (";
		if (min_pitch != null)
			toPrint += Float.toString(min_pitch);
		else 
			toPrint += "null";
		if (yaw != null)
			toPrint += "," + Float.toString(yaw);
		else 
			toPrint += ",null";
		if (latitude != null)
			toPrint += "," + Float.toString(latitude);
		else 
			toPrint += ",null";
		if (longitude != null) 
			toPrint += "," + Float.toString(longitude);
		else 
			toPrint += ",null";
		if (altitude != null) 
			toPrint += "," + Float.toString(altitude) + ")";
		else 
			toPrint += ",null)";
		printLog(toPrint);
		put("takeoff_fail",false);
		serviceManager.takeoff(min_pitch, yaw, latitude, longitude, altitude);
		while ((!getBool("takeoff")) && getDouble("altezza") <= 0.1) {
			if(has("takeoff_fail") && getBool("takeoff_fail")) {
				printLog("Takeoff failed.. retry takeoff.. ");
				put("takeoff_fail",false);
				serviceManager.takeoff(min_pitch, yaw, latitude, longitude, altitude);
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					printLog("Interruption command received.. wakeup from sleep");
				}
			}
		}
		put("waiting_takeoff", false);
		put("takeoff",true);
	}
	
	public void setMode(String mode) {
		printLog("Set mode " + mode + ".. ");
		serviceManager.setMode(mode);
		while (!Objects.equals(getString("mode"),mode)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				printLog("Interruption command received.. wakeup from sleep");
			}
		}
		printLog("Mode GUIDED");
	}
	
	public void arming(boolean arm) {
		if (arm) {
			printLog("Arming.. ");
		}
		else {
			printLog("Disarming.. ");
		}
		put("arming_fail",false);
		serviceManager.arming(arm);
		while (!getBool("armed") == arm) {
			if(has("arming_fail") && getBool("arming_fail")) {
				if (arm) {
					printLog("Arming failed.. retry arming.. ");
				}
				else {
					printLog("Disarming failed.. retry disarming.. ");
				}
				put("arming_fail",false);
				serviceManager.arming(arm);
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					printLog("Interruption command received.. wakeup from sleep");
				}
			}
		}
		if (arm) {
			printLog("Armed");
		}
		else {
			printLog("Disarmed");
		}
	}
	
	private String getString(String key) {
		return (String) get(key);
	}
	
	private boolean getBool(String key) {
		return (boolean) get(key);
	}
	
	private int getInt(String key) {
		return (int) get(key);
	}
	
	private double getDouble(String key) {
		return (double) get(key);
	}
	
	private int getByte(String key) {
		return (new Byte((byte) get(key))).intValue();
	}
	
	private Object get(String key) {
		return executionEnvironment.get(key);
	}
	
	private boolean has(String key) {
		return executionEnvironment.has(key);
	}
	
	private void put(String key, Object value) {
		executionEnvironment.put(key, value);
	}
	
	private void printLog(String message) {
		prjNode.printLog(message);
	}

}
