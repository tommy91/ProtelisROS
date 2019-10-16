package com.github.rosjava.prj_pkg.prj.RosCommunicationManagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

public class MavrosDiagnosticsManager {
	
	private SubscribersManager subscribersManager;
	private String namespace;
	
	private String GCS_BRIDGE_OK_STATUS = "connected";
	private String SYSTEM_OK_STATUS = "Normal";
	private String HEARTBEAT_OK_STATUS = "Normal";
	private String GPS_OK_STATUS = "3D fix";
	private String FCU_CONNECTION_OK_STATUS = "connected";
	private String BATTERY_OK_STATUS = "???";	// TODO
	
	private diagnostic_msgs.DiagnosticStatus Time_Sync_Status;
	private diagnostic_msgs.DiagnosticStatus GCS_bridge_Status;
	private diagnostic_msgs.DiagnosticStatus Battery_Status;
	private diagnostic_msgs.DiagnosticStatus System_Status;
	private diagnostic_msgs.DiagnosticStatus Heartbeat_Status;
	private diagnostic_msgs.DiagnosticStatus GPS_Status;
	private diagnostic_msgs.DiagnosticStatus FCU_connection_Status;
	
	public MavrosDiagnosticsManager(SubscribersManager subscribersManager, String namespace) {
		this.subscribersManager = subscribersManager;
		this.namespace = namespace;
	}
	
	public boolean hasDiagnosticStatusMessage() {
		return subscribersManager.get_Diagnostics().hasReceivedMessage();
	}
	
	public diagnostic_msgs.DiagnosticStatus getDiagnosticStatusMessage() {
		return subscribersManager.get_Diagnostics().getReceivedMessage();
	}
	
	public void updateStatus() {
		if (hasDiagnosticStatusMessage()) {
			diagnostic_msgs.DiagnosticStatus diagnosticMsg = getDiagnosticStatusMessage();
			
			for (diagnostic_msgs.KeyValue kv : getDiagnosticStatusMessage().getValues()) {
				
				// Status message key is like 'mavrosNamespace/mavros: FCU connection'
				String key = kv.getKey().trim();
				// Check if the namespace is refering to this device
				String msgNamespace = key.substring(0, namespace.length());
				if (Objects.equals(msgNamespace, namespace)) {
					// => get the system name without 'mavrosNamespace/mavros: ' => just 'FCU connection' (9 is the length of '/mavros: ') 
					String systemName = key.substring(namespace.length() + 9);
					
					String val = kv.getValue().trim();
//					updateStatusWithMessage(systemName, systemName, statusMsg);
////					// Add also systems checks present in System
////					if (Objects.equals(systemName, "System")) {
////						List<diagnostic_msgs.KeyValue> values = status.getValues();
////						for (diagnostic_msgs.KeyValue value : values) {
////							String key = value.getKey();
////							String val = value.getValue();
////							updateStatusWithMessage("System: " + key, "System " + key, val);
////						}
////					}
				}
				
			}
		}
	}
	
	
	
//	public void waitArdupilotSystemsOnline() {
//		printLog("Waiting for the ardupilot systems to be online.. ");
//		
//		
//		List<String> systemSystemsNeeded = new ArrayList<String>();
//		systemSystemsNeeded.add("System 3D gyro");
//		systemSystemsNeeded.add("System 3D accelerometer");
//		systemSystemsNeeded.add("System 3D magnetometer");
//		systemSystemsNeeded.add("System absolute pressure");
//		systemSystemsNeeded.add("System GPS");
//		systemSystemsNeeded.add("System 3D angular rate control");
//		systemSystemsNeeded.add("System attitude stabilization");
//		systemSystemsNeeded.add("System yaw position");
//		systemSystemsNeeded.add("System motor outputs / control");
//		systemSystemsNeeded.add("System rc receiver");
//		systemSystemsNeeded.add("System AHRS subsystem health");
//		systemSystemsNeeded.add("System Terrain subsystem health");
//		systemSystemsNeeded.add("System Battery");
//		int numSystemSystemsNeeded = systemSystemsNeeded.size();
//		
//		// Setting all the systems as offline before starting to check
//		List<String> offlineSystems = new ArrayList<String>();
//		for (String systemName : systemsStatus.keySet()) {
//			offlineSystems.add(systemName);
//		}
//		
//		while (!offlineSystems.isEmpty()) {
//			List<String> remainingSystems = new ArrayList<String>(offlineSystems);
//			for (int i = 0; i < offlineSystems.size(); i++) {
//				String systemName = offlineSystems.get(i);
//				if (executionEnvironment.has(systemName) && 
//						Objects.equals(executionEnvironment.get(systemName), systemsStatus.get(systemName))) {
//					// Wait for all checks in System to be "ok"
////					if (Objects.equals(systemName,"System")) {
////						systemSystemsNeeded = checkSystem(systemSystemsNeeded);
////						if (systemSystemsNeeded.size() > 0) {
////							if (numSystemSystemsNeeded > systemSystemsNeeded.size()) {
////								// Not all checks in System are good, but someone is now ok
////								numSystemSystemsNeeded = systemSystemsNeeded.size();
////								String missing = list2str(systemSystemsNeeded);
////								printLog(systemName + ": missing [" + missing + "]");
////							}
////							continue;
////						}
////					}
//					offlineSystems.remove(i);
//					i--;
//					remainingSystems.remove(systemName);
//					if (remainingSystems.size() > 0) {
//						// Collecting remaining offline systems for printing purposes
//						String missing = list2str(remainingSystems);
//						printLog(systemName + " is online (" + systemsStatus.get(systemName) + ") missing [" + missing + "]");
//					}
//					else {
//						printLog(systemName + " is online (" + systemsStatus.get(systemName) + ")");
//					}
//				}
//			}
//		}
//		printLog("All the systems are online");
//	}
//	
//	private String list2str(List<String> l) {
//		String s = "";
//		for (int i = 0; i < l.size(); i++) {
//			s += l.get(i);
//			if (i + 1 < l.size()) { 
//				s += ","; 
//			}
//		}
//		return s;
//	}
//	
//	private List<String> checkSystem(List<String> systemSystemsNeeded) {
//		for (int i = 0; i < systemSystemsNeeded.size(); i++) {
//			if (executionEnvironment.has(systemSystemsNeeded.get(i)) && 
//					Objects.equals(executionEnvironment.get(systemSystemsNeeded.get(i)), "Ok")) {
//				systemSystemsNeeded.remove(i);
//				i--;
//			}
//		}
//		return systemSystemsNeeded;
//	}

}
