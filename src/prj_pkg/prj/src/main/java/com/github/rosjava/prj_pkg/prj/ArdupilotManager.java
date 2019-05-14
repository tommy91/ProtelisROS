package com.github.rosjava.prj_pkg.prj;

import java.lang.Byte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.message.Time;

import com.github.rosjava.prj_pkg.prj.Mavros.MavrosMessagesManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosServicesManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosSubscribersManager;

public class ArdupilotManager {
	
	private final PrjNode prjNode;
	private ExecutionEnvironment executionEnvironment;
	private MavrosServicesManager mavrosServicesManager;
	private MavrosSubscribersManager mavrosSubscribersManager;
	private MavrosMessagesManager mavrosMessagesManager;
	
	private double takeoffMinimumAltitude = 0.1;
	private String vehicleStatus = "BOOTING_UP";

	
	public ArdupilotManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		executionEnvironment = prjNode.getExecutionEnvironment();
		mavrosServicesManager = prjNode.getMavrosServicesManager();
		mavrosSubscribersManager = prjNode.getMavrosSubscribersManager();
		mavrosMessagesManager = prjNode.getMavrosMessagesManager();
	}
	
//	public void waitArdupilotSystemsOnline() {
//		printLog("Waiting for the ardupilot systems to be online.. ");
//		
//		// Map of < System name, Online status message >
//		Map<String,String> systemsStatus = new HashMap<String,String>();
//		systemsStatus.put("GCS bridge", "connected");
//		systemsStatus.put("System", "Normal");
//		systemsStatus.put("Heartbeat", "Normal");
//		systemsStatus.put("GPS", "3D fix");
//		systemsStatus.put("FCU connection", "connected");
//		// TODO systemsStatus.put("Battery", "???");
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
	
	public void waitArdupilotReady() {
		printLog("Waiting for the ardupilot device to be connected..");
		while (true) {
			if (mavrosSubscribersManager.get_State().hasReceivedMessage()) {
				mavros_msgs.State current_state = getState();
				if (current_state.getConnected() && (byte2int(current_state.getSystemStatus()) == 3)) {
					printLog("Ardupilot device connected");
					setVehicleStatus("READY");
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
	
	private boolean checkMessagesTimes(Time lowerTime, Time greaterTime, int sleetTimeMillis) {
		if (greaterTime.compareTo(lowerTime) == 1) {
			return true;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			printLog("Interruption command received.. wakeup from sleep");
		}
		return false;
	}
	
	public String getVehicleStatus() {
		return vehicleStatus;
	}
	
	private void setVehicleStatus(String status) {
		vehicleStatus = status;
	}
	
	public void disablePreArmChecks() {
		printLog("Disabling pre-arm checks.. ");
		mavros_msgs.ParamSetRequest request = mavrosServicesManager.get_ParamSet().newRequest();
		mavros_msgs.ParamValue pv = mavrosMessagesManager.getParamValueMessage(0);
		request.setParamId("ARMING_CHECK");
		request.setValue(pv);
		mavrosServicesManager.get_ParamSet().callSynch(request, 1000);
		if (mavrosServicesManager.get_ParamSet().getResponse().getSuccess()) {
			printLog("Pre-arm checks disabled");
		}
		else {
			printLog("Error on disabling pre-arm checks");
		}
	}
	
	public Double[] getCoordinates() {
		Double latitude = null;
		Double longitude = null;
		Double altitude = null;
		if (mavrosSubscribersManager.get_GlobalPositionGlobal().hasReceivedMessage()) {
			sensor_msgs.NavSatFix gpg = mavrosSubscribersManager.get_GlobalPositionGlobal().getReceivedMessage();
			latitude = gpg.getLatitude();
			longitude = gpg.getLongitude();
			altitude = getRelAltitude();
		}
		return new Double[]{latitude, longitude, altitude};
	}
	
	public Double getLatitude() {
		return getCoordinates()[0];
	}
	
	public Double getLongitude() {
		return getCoordinates()[1];
	}
	
	public Double getAltitude() {
		return getCoordinates()[2];
	}
	
	public Double getRelAltitude() {
		if (mavrosSubscribersManager.get_GlobalPositionRelAlt().hasReceivedMessage()) {
			return mavrosSubscribersManager.get_GlobalPositionRelAlt().getReceivedMessage().getData();
		}
		else {
			return null;
		}
	}
	
	public void takeoff(double altitude) {
		takeoff(null, null, null, null, (float)altitude);
	}
	
	public void takeoff(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		printLog("Initializing takeoff.. ");
		if (isFlying()) {
			printLog("Takeoff error: the vehicle is already flying (altitude: " + getAltitude().toString() + ")");
		}
		else {
			setVehicleStatus("WAITING_TAKEOFF");
//			disablePreArmChecks();
			setMode("GUIDED");
			arming(true);
			printLog(getTakeoffInfo(min_pitch, yaw, latitude, longitude, altitude));
			mavros_msgs.CommandTOLRequest request = setTakeoffRequest(min_pitch, yaw, latitude, longitude, altitude);
			mavrosServicesManager.get_CmdTakeoff().callSynch(request, 1000);
			if ( (!mavrosServicesManager.get_CmdTakeoff().getResponse().getSuccess()) && (!isFlying()) ) {
				printLog("Takeoff failed.. retry takeoff.. ");
				takeoff(min_pitch, yaw, latitude, longitude, altitude);
			}
			else {
				setVehicleStatus("FLYING");
			}
		}
	}
	
	private mavros_msgs.CommandTOLRequest setTakeoffRequest(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		mavros_msgs.CommandTOLRequest request = mavrosServicesManager.get_CmdTakeoff().newRequest();
		if (altitude != null) {
			request.setAltitude(altitude);
		}
		if (latitude != null) {
			request.setLatitude(latitude);
		}
		if (longitude != null) {
			request.setLongitude(longitude);
		}
		if (min_pitch != null) {
			request.setMinPitch(min_pitch);
		}
		if (yaw != null) {
			request.setYaw(yaw);
		}
		return request;
	}
	
	public boolean isFlying() {
		Double altitude = getRelAltitude();
		return (altitude != null) && (altitude > takeoffMinimumAltitude);
	}
	
	private String getTakeoffInfo(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
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
		return toPrint;
	}
	
	public void setMode(String mode) {
		printLog("Setting mode to '" + mode + "'.. ");
		if (checkMode(mode)) {
			printLog("The vehicle mode is already set to '" + mode + "'");
		}
		else {
			mavros_msgs.SetModeRequest request = mavrosServicesManager.get_SetMode().newRequest();
			request.setCustomMode(mode);
			mavrosServicesManager.get_SetMode().callSynch(request, 1000);
			Time responseTime = mavrosServicesManager.get_SetMode().getResponseTime();
			if ( mavrosServicesManager.get_SetMode().getResponse().getModeSent() && checkMode(mode, responseTime, 1000) ) {
				printLog("Mode set to '" + mode + "'");
			}
			else {
				printLog("Set mode failed.. retry setting mode to '" + mode + "'");
				setMode(mode);
			}
		}
	}
	
	public mavros_msgs.State getState() {
		return mavrosSubscribersManager.get_State().getReceivedMessage();
	}
	
	public mavros_msgs.State getStateAfterTime(Time afterTime, int sleetTimeMillis) {
		mavros_msgs.State current_state = getState();
		while (!checkMessagesTimes(afterTime, current_state.getHeader().getStamp(), sleetTimeMillis)) {
			current_state = getState();
		}
		return current_state;
	}
	
	public String getMode() {
		mavros_msgs.State state = getState();
		if (state == null) {
			return null;
		}
		else {
			return state.getMode();
		}
	}
	
	public boolean checkMode(String mode) {
		return Objects.equals(getMode(),mode);
	}
	
	public boolean checkMode(String mode, Time afterTime, int sleetTimeMillis) {
		return Objects.equals(getStateAfterTime(afterTime, sleetTimeMillis).getMode(), mode);
	}
	
	public void arming(boolean arm) {
		printLog(arm ? "Arming.. " : "Disarming.. ");
		if (checkArmed(arm)) {
			printLog("The vehicle is already " + (arm ? "armed" : "disarmed"));
		}
		else {
			mavros_msgs.CommandBoolRequest request = mavrosServicesManager.get_CmdArming().newRequest();
			request.setValue(arm);
			mavrosServicesManager.get_CmdArming().callSynch(request, 1000);
			Time responseTime = mavrosServicesManager.get_CmdArming().getResponseTime();
			if ( mavrosServicesManager.get_CmdArming().getResponse().getSuccess() && checkArmed(arm, responseTime, 1000) ) {
				printLog("The vehicle is now " + (arm? "armed" : "disarmed"));
			}
			else {
				printLog((arm ? "Arming " : "Disarming ") + "failed.. retry " + (arm ? "arming " : "disarming "));
				arming(arm);
			}
		}
	}
	
	public boolean isArmed() {
		mavros_msgs.State state = getState();
		if (state == null) {
			return false;
		}
		else {
			return state.getArmed();
		}
	}
	
	public boolean checkArmed(boolean arm) {
		return isArmed() == arm;
	}
	
	public boolean checkArmed(boolean arm, Time afterTime, int sleetTimeMillis) {
		return getStateAfterTime(afterTime, sleetTimeMillis).getArmed() == arm;
	}
	
	private int byte2int(Byte b) {
		return b.intValue();
	}
	
	private void printLog(String message) {
		prjNode.printLog(message);
	}

}
