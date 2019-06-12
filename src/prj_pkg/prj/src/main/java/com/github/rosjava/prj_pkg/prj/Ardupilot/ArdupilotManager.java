package com.github.rosjava.prj_pkg.prj.Ardupilot;

import java.lang.Byte;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageFactory;
import org.ros.message.Time;

import com.github.rosjava.prj_pkg.prj.PrjNode;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosDiagnosticsManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosPublishersManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosServicesManager;
import com.github.rosjava.prj_pkg.prj.Mavros.MavrosSubscribersManager;

public class ArdupilotManager implements ArdupilotInterface {
	
	protected class Waiter {
		
		private boolean check_num_errors = false;
		private int max_num_errors;
		
		public Waiter(int max_num_errors) {
			check_num_errors = true;
			this.max_num_errors = max_num_errors;
		}
		
		public Waiter() {
		}
		
		public boolean waitCondition(String message) {
			prjNode.printLog(message);
			int num_errors = 0;
			while (!checkCondition()) {
				if ( check_num_errors && (num_errors++ > max_num_errors) ) {
					return false;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					printLog("Interruption command received.. wakeup from sleep");
				}
			}
			return true;
		}
		
		/**
		 * Method to be overridden to specify the condition to be verified in waitCondition
		 * Until the condition is NOT verified sleep and loop in waitCondition
		 */
		protected boolean checkCondition() {
			return true;
		}

	}
	
	protected final PrjNode prjNode;
	protected MessageFactory messageFactory;
	protected ExecutionEnvironment executionEnvironment;
	protected MavrosServicesManager mavrosServicesManager;
	protected MavrosSubscribersManager mavrosSubscribersManager;
	protected MavrosPublishersManager mavrosPublishersManager;
	protected MavrosDiagnosticsManager mavrosDiagnosticsManager;
	
	protected List<Double> homePosition;
	
	protected String vehicleStatus = "BOOTING_UP";
	protected double altitudeSkew = 0.1;
	protected double latitudeSkew = 0.1;
	protected double longitudeSkew = 0.1;
	protected double xSkew = 0.1;
	protected double ySkew = 0.1;
	protected double zSkew = 0.1;
	
	protected CancellableLoop checkerTargetReached;
	
	public ArdupilotManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		executionEnvironment = prjNode.getExecutionEnvironment();
		mavrosServicesManager = prjNode.getMavrosServicesManager();
		mavrosSubscribersManager = prjNode.getMavrosSubscribersManager();
		mavrosPublishersManager = prjNode.getMavrosPublishersManager();
		mavrosDiagnosticsManager = new MavrosDiagnosticsManager(mavrosSubscribersManager, prjNode.getMavrosNamespace());
		messageFactory = prjNode.getConnectedNode().getTopicMessageFactory();
	}
	
	public void waitArdupilotReady() {
		new Waiter() {
			@Override
			protected boolean checkCondition() {
				if (!mavrosSubscribersManager.get_State().hasReceivedMessage()) {
					return false;
				}
				mavros_msgs.State current_state = getState();
				return (current_state.getConnected() && (byte2int(current_state.getSystemStatus()) == 3));
			}
		}.waitCondition("Waiting for the ardupilot device to be connected..");
		printLog("Ardupilot device connected");
		setVehicleStatus("READY");
	}
	
	/** 
	 * Checking messages times here for two reasons:
	 * 1) need to cast to the corresponding message type to access the object methods
	 * 2) need to compare the time in the header of the message of the subscripted topic 
	 *    (which can be before in time respect to the receiving time)
	 *    with the receiving time of the response
	 */
	protected boolean checkMessagesTimes(Time lowerTime, Time greaterTime, int sleetTimeMillis) {
		printLog("Comparing " + lowerTime.toString() + " with " + greaterTime.toString());
		if (greaterTime.compareTo(lowerTime) == 1) {
			printLog("True");
			return true;
		}
		try {
			printLog("Sleep 1000");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			printLog("Interruption command received.. wakeup from sleep");
		}
		printLog("False");
		return false;
	}
	
	public String getVehicleStatus() {
		checkNeedToUpdateStatus();
		return vehicleStatus;
	}
	
	protected void setVehicleStatus(String status) {
//		// If currently in MOVING status stop the checker before changing status
//		if ( Objects.equals(vehicleStatus, "MOVING") && Objects.equals(status, "MOVING") && (checkerTargetReached != null) ) {
//			checkerTargetReached.cancel();
//		}
		vehicleStatus = status;
	}
	
	private void checkNeedToUpdateStatus() {
		if (Objects.equals(vehicleStatus, "MOVING") && waypointReachedCurrent()) {
			setVehicleStatus("ON_AIR");
		}
	}
	
	public void disablePreArmChecks() {
		printLog("Disabling pre-arm checks.. ");
		mavros_msgs.ParamSetRequest request = mavrosServicesManager.get_ParamSet().newRequest();
		mavros_msgs.ParamValue pv = messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
		pv.setInteger(0);
		request.setParamId("ARMING_CHECK");
		request.setValue(pv);
		if (mavrosServicesManager.get_ParamSet().callSynch(request, 1000).getSuccess()) {
			printLog("Pre-arm checks disabled");
		}
		else {
			printLog("Error on disabling pre-arm checks");
		}
	}
	
	public List<Double> getCoordinates() {
		Double latitude = null;
		Double longitude = null;
		Double relAltitude = null;
		if (mavrosSubscribersManager.get_GlobalPositionGlobal().hasReceivedMessage()) {
			sensor_msgs.NavSatFix gpg = mavrosSubscribersManager.get_GlobalPositionGlobal().getReceivedMessage();
			latitude = gpg.getLatitude();
			longitude = gpg.getLongitude();
			relAltitude = getRelAltitude();
		}
//		return new Double[]{latitude, longitude, altitude};
		return Arrays.asList(latitude, longitude, relAltitude);
	}
	
	public Double getLatitude() {
		return getCoordinates().get(0);
	}
	
	public Double getLongitude() {
		return getCoordinates().get(1);
	}
	
	public Double getAltitude() {
		return getCoordinates().get(2);
	}
	
	public Double getRelAltitude() {
		if (mavrosSubscribersManager.get_GlobalPositionRelAlt().hasReceivedMessage()) {
			return mavrosSubscribersManager.get_GlobalPositionRelAlt().getReceivedMessage().getData();
		}
		else {
			return null;
		}
	}
	
	public geometry_msgs.PoseStamped getLocalPosition() {
		return mavrosSubscribersManager.get_LocalPositionPose().getReceivedMessage();
	}
	
	public sensor_msgs.NavSatFix getGlobalPosition() {
		return mavrosSubscribersManager.get_GlobalPositionGlobal().getReceivedMessage();
	}
	
	public mavros_msgs.State getState() {
		return mavrosSubscribersManager.get_State().getReceivedMessage();
	}
	
	protected mavros_msgs.State getStateAfterTime(Time afterTime, int sleetTimeMillis) {
		mavros_msgs.State current_state = getState();
		while (!checkMessagesTimes(afterTime, current_state.getHeader().getStamp(), sleetTimeMillis)) {
			current_state = getState();
		}
		return current_state;
	}
	
	public void setMode(String mode) {
		printLog("Setting mode to '" + mode + "'.. ");
		if (checkMode(mode)) {
			printLog("The vehicle mode is already set to '" + mode + "'");
		}
		else {
			mavros_msgs.SetModeRequest request = mavrosServicesManager.get_SetMode().newRequest();
			request.setCustomMode(mode);
			request.setBaseMode((byte)0);	// Set base mode to 0 because using only custom mode
			if (mavrosServicesManager.get_SetMode().callSynch(request, 1000).getModeSent()) {
				if (waitMode(mode)) {
					printLog("Mode set to '" + mode + "'");
				}
				else {
					printLog("Set mode failed (not set after successful call).. retry setting mode to '" + mode + "'");
					setMode(mode);
				}
			}
			else {
				printLog("Set mode failed (unsuccessful call).. retry setting mode to '" + mode + "'");
				setMode(mode);
			}
		}
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
	
	protected boolean checkMode(String mode) {
		return Objects.equals(getMode(),mode);
	}
	
	private boolean waitMode(final String mode) {
		return (new Waiter(5) {
			@Override
			protected boolean checkCondition() {
				return checkMode(mode);
			}
		}).waitCondition("Waiting for mode to change to " + mode + ".. ");
	}
	
	public void arming(boolean arm) {
		printLog(arm ? "Arming.. " : "Disarming.. ");
		if (checkArmed(arm)) {
			printLog("The vehicle is already " + (arm ? "armed" : "disarmed"));
		}
		else {
			mavros_msgs.CommandBoolRequest request = mavrosServicesManager.get_CmdArming().newRequest();
			request.setValue(arm);
			if (mavrosServicesManager.get_CmdArming().callSynch(request, 1000).getSuccess()) {
				if (waitArmed(arm)) {
					printLog("The vehicle is now " + (arm ? "armed" : "disarmed"));
					if (arm) {
						homePosition = getCoordinates();
					}
				}
				else {
					printLog((arm ? "Arming " : "Disarming ") + "failed (not set after successful call).. retry " + (arm ? "arming " : "disarming "));
					arming(arm);
				}
			}
			else {
				printLog((arm ? "Arming " : "Disarming ") + "failed (unsuccessful call).. retry " + (arm ? "arming " : "disarming "));
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
	
	protected boolean checkArmed(boolean arm) {
		return isArmed() == arm;
	}
	
	private boolean waitArmed(final boolean arm) {
		return (new Waiter() {
			@Override
			protected boolean checkCondition() {
				return checkArmed(arm);
			}
		}).waitCondition("Waiting for armed status to change to " + Boolean.toString(arm) + ".. ");
	}
	
	public void returnToLaunch() {
		printLog("Return to launch position..");
		setMode("RTL");
		setVehicleStatus("RTL");
		checkRTLCompleted();
	}
	
	public boolean isRTL() {
		return isOnTarget(homePosition.get(0), homePosition.get(1), homePosition.get(2));
	}
	
	private void checkRTLCompleted() {
		CancellableLoop synchronizer = new CancellableLoop() {
			
			int sleepTimeMillis = 100;
			
			@Override
			protected void loop() throws InterruptedException {
				if (isRTL()) {
					setVehicleStatus("ON_GROUND");
					arming(false);
					cancel();
				}
				else {
					Thread.sleep(sleepTimeMillis);
				}
			}
		};
		
		prjNode.getConnectedNode().executeCancellableLoop(synchronizer);
	}
	
	public boolean canMove() {
		return true;
	}
	
	public void moveLocal(Double forward, Double right, Double down) {
		if (!canMove()) {
			printLog("Move error: the vehicle cannot be moved (status: " + getVehicleStatus() + ")");
		}
		else {
			// Set the target position updating the current position with the non null target values
			geometry_msgs.PoseStamped currentLocalPosition = getLocalPosition();
			printLog("Current position (x,y,z): " + localPositionToString(currentLocalPosition));
			if (forward != null) {
				currentLocalPosition.getPose().getPosition().setY(forward);
			}
			if (right != null) {
				currentLocalPosition.getPose().getPosition().setX(right);
			}
			if (down != null) {
				currentLocalPosition.getPose().getPosition().setZ(down);
			}
			printLog("Moving to (x,y,z): " + localPositionToString(currentLocalPosition));
			
			// Publish the command and run the target reached checker
			setVehicleStatus("MOVING");
			mavrosPublishersManager.getPublisher_SetpointPositionLocal().publish(currentLocalPosition);
			checkLocalTargetReached(currentLocalPosition);
		}
	}
	
	private void checkLocalTargetReached(final geometry_msgs.PoseStamped position) {
		if (checkerTargetReached != null) {
			checkerTargetReached.cancel();
		}
		checkerTargetReached = new CancellableLoop() {
			
			int sleepTimeMillis = 100;
			
			@Override
			protected void loop() throws InterruptedException {
				
				if (isOnLocalTarget(position)) {
					setVehicleStatus("ON_AIR");
					cancel();
				}
				else {
					Thread.sleep(sleepTimeMillis);
				}
			}
		};
		
		prjNode.getConnectedNode().executeCancellableLoop(checkerTargetReached);
	}
	
	private boolean isOnLocalTarget(geometry_msgs.PoseStamped target) {
		geometry_msgs.PoseStamped current = getLocalPosition();
		printLog("Current position (x,y,z): " + localPositionToString(current));
		return (Math.abs(current.getPose().getPosition().getX() - target.getPose().getPosition().getX()) <= xSkew) &&
				(Math.abs(current.getPose().getPosition().getY() - target.getPose().getPosition().getY()) <= ySkew) &&
				(Math.abs(current.getPose().getPosition().getZ() - target.getPose().getPosition().getZ()) <= zSkew);
	}
	
	private String localPositionToString(geometry_msgs.PoseStamped position) {
		Double x = position.getPose().getPosition().getX();
		Double y = position.getPose().getPosition().getY();
		Double z = position.getPose().getPosition().getZ();
		return "(" + x.toString() + "," + y.toString() + "," + z.toString() + ")";
	}
	
	private void checkTargetReached(final double latitude, final double longitude, final double relAltitude) {
		if (checkerTargetReached != null) {
			checkerTargetReached.cancel();
		}
		checkerTargetReached = new CancellableLoop() {
			
			int sleepTimeMillis = 100;
			
			@Override
			protected void loop() throws InterruptedException {
				
				if (isOnTarget(latitude, longitude, relAltitude)) {
					setVehicleStatus("ON_AIR");
					cancel();
				}
				else {
					Thread.sleep(sleepTimeMillis);
				}
			}
		};
		
		prjNode.getConnectedNode().executeCancellableLoop(checkerTargetReached);
	}
	
	private boolean isOnTarget(double latitude, double longitude, double relAltitude) {
		List<Double> currentPos = getCoordinates();
		return (Math.abs(currentPos.get(0) - latitude) <= latitudeSkew) &&
				(Math.abs(currentPos.get(1) - longitude) <= longitudeSkew) &&
				(Math.abs(currentPos.get(2) - relAltitude) <= altitudeSkew);
	}
	
	public void moveGlobal(Double latitude, Double longitude, Double relAltitude) {
		if (!canMove()) {
			printLog("Move error: the vehicle cannot be moved (status: " + getVehicleStatus() + ")");
		}
		else {
			// Normalize the target position
			List<Double> currentPos = getCoordinates();
			printLog("Current position (Lat,Long,RelAlt): (" + currentPos.get(0).toString() + "," + 
					currentPos.get(1).toString() + "," + currentPos.get(2).toString() + ")");
			latitude = (latitude == null) ? currentPos.get(0) : latitude;
			longitude = (longitude == null) ? currentPos.get(1) : longitude;
			relAltitude = (relAltitude == null) ? currentPos.get(2) : relAltitude;
			printLog("Moving to (Lat,Long,RelAlt): (" + latitude.toString() + "," + longitude.toString() + "," + relAltitude.toString() + ")");
			// Set the request
			mavros_msgs.GlobalPositionTarget request = mavrosPublishersManager.getPublisher_SetpointPositionGlobal().newMessage();
			request.setLatitude(latitude);
			request.setLongitude(longitude);
			request.setAltitude(relAltitude.floatValue());
			// Run the command and the target reached checker
			setVehicleStatus("MOVING");
			mavrosPublishersManager.getPublisher_SetpointPositionGlobal().publish(request);
			checkTargetReached(latitude, longitude, relAltitude);
		}
	}
	
	public void waypointClear() {
		printLog("Clear waypoint..");
		mavros_msgs.WaypointClearRequest request = mavrosServicesManager.get_MissionClear().newRequest();
		if (mavrosServicesManager.get_MissionClear().callSynch(request, 1000).getSuccess()) {
			printLog("Clear waypoint completed");
		}
		else {
			printLog("Clear waypoint unsuccessful.. retrying");
			waypointClear();
		}
	}
	
	public int waypointPush(Double latitude, Double longitude, Double relAltitude) {
		printLog("Push waypoint: " +
				(latitude != null ? latitude.toString() + " Lat " : "") +
				(longitude != null ? longitude.toString() + " Long " : "") +
				(relAltitude != null ? relAltitude.toString() + " Alt " : "") 
				);
		mavros_msgs.WaypointPushRequest request = mavrosServicesManager.get_MissionPush().newRequest();
		mavros_msgs.Waypoint waypoint = messageFactory.newFromType(mavros_msgs.Waypoint._TYPE);
		if (latitude != null) {
			waypoint.setXLat(latitude);
		}
		if (longitude != null) {
			waypoint.setYLong(longitude);
		}
		if (relAltitude != null) {
			waypoint.setZAlt(relAltitude);
		}
		waypoint.setCommand((short)16);
		waypoint.setAutocontinue(true);
//		printLog("Created waypoint: " + waypoint.toString());
		request.setStartIndex((short)waypointGetNext());
		request.setWaypoints(Arrays.asList(waypoint));
		mavros_msgs.WaypointPushResponse response = mavrosServicesManager.get_MissionPush().callSynch(request, 1000);
		if (response.getSuccess()) {
			printLog("Waypoint correctly pushed");
			return response.getWpTransfered();
		}
		else {
			printLog("Push waypoint unsuccessful (" + Integer.toString(response.getWpTransfered()) + ").. retrying");
			return waypointPush(latitude, longitude, relAltitude);
		}
	}
	
	public void waypointSetCurrent(int current) {
		printLog("Set current waypoint to " + Integer.toString(current) + "..");
		mavros_msgs.WaypointSetCurrentRequest request = mavrosServicesManager.get_MissionSetCurrent().newRequest();
		request.setWpSeq((short) current);
		if (mavrosServicesManager.get_MissionSetCurrent().callSynch(request, 1000).getSuccess()) {
			printLog("Set current waypoint completed");
		}
		else {
			printLog("Set current waypoint unsuccessful.. retrying");
			waypointSetCurrent(current);
		}
	}
	
	public boolean waypointHasReached() {
		return mavrosSubscribersManager.get_MissionReached().hasReceivedMessage();
	}
	
	public Integer waypointGetLastReached() {
		if (waypointHasReached()) {
			return (int) mavrosSubscribersManager.get_MissionReached().getReceivedMessage().getWpSeq();
		}
		else {
			return null;
		}
	}
	
	public boolean waypointHasCurrent() {
		return mavrosSubscribersManager.get_MissionWaypoints().hasReceivedMessage();
	}
	
	public Integer waypointGetCurrent() {
		if (waypointHasCurrent()) {
			return (int) mavrosSubscribersManager.get_MissionWaypoints().getReceivedMessage().getCurrentSeq();
		}
		return null;
	}
	
	public Integer waypointGetLast() {
		if (waypointHasCurrent()) {
			return (int) mavrosSubscribersManager.get_MissionWaypoints().getReceivedMessage().getWaypoints().size() - 1;
		}
		return null;
	}
	
	public boolean waypointReachedCurrent() {
		return waypointHasCurrent() && waypointHasReached() && (waypointGetLast() == waypointGetLastReached());
	}
	
	public int waypointGetNext() {
		Integer current = waypointGetCurrent();
		return (current == null) ? 0 : current;
	}
	
	protected int byte2int(Byte b) {
		return b.intValue();
	}
	
	protected void printLog(String message) {
		prjNode.printLog(message);
	}

}
