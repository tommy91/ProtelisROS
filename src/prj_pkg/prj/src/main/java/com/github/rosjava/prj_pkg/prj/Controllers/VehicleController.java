package com.github.rosjava.prj_pkg.prj.Controllers;

import java.lang.Byte;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.protelis.vm.ExecutionEnvironment;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageFactory;
import org.ros.message.Time;

import com.github.rosjava.prj_pkg.prj.PrjNode;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.MavrosDiagnosticsManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ParametersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.PublishersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ServicesManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.SubscribersManager;

public class VehicleController implements VehicleInterface {
	
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
			prjNode.logInfo(message);
			int num_errors = 0;
			while (!checkCondition()) {
				if ( check_num_errors && (num_errors++ > max_num_errors) ) {
					return false;
				}
				try {
					Thread.sleep(sleepTimeMs);
				} catch (InterruptedException e) {
					logError("Interruption command received.. wakeup from sleep");
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
	protected ServicesManager servicesManager;
	protected SubscribersManager subscribersManager;
	protected PublishersManager publishersManager;
	protected MavrosDiagnosticsManager mavrosDiagnosticsManager;
	
	protected List<Double> homePosition;
	
	/* Mavros publishes with a frequency of 1Hz */
	protected Integer sleepTimeMs;
	
	protected String vehicleStatus = "BOOTING_UP";
	protected Double altitudeSkew;
	protected Double latitudeSkew;
	protected Double longitudeSkew;
	protected Double xSkew;
	protected Double ySkew;
	protected Double zSkew;
	
	/**
	 * The following thread will be used to check if the target position is reached 
	 * so the program won't be stucked waiting for the target position to be reached
	 */
	protected CancellableLoop checkerTargetReached;
	
	public VehicleController(PrjNode prjNode) {
		this.prjNode = prjNode;
		
		/* Read the input parameters from the Parameter Server */
		readInputParameters();
		
		executionEnvironment = prjNode.getExecutionEnvironment();
		servicesManager = prjNode.getServicesManager();
		subscribersManager = prjNode.getSubscribersManager();
		publishersManager = prjNode.getPublishersManager();
		mavrosDiagnosticsManager = new MavrosDiagnosticsManager(subscribersManager, prjNode.getNamespace().toString());
		messageFactory = prjNode.getConnectedNode().getTopicMessageFactory();
	}
	
	private void readInputParameters() {
		logInfo("Reading the parameters:");
		ParametersManager mpm = prjNode.getParametersManager();
		sleepTimeMs = mpm.getIntegerParam("~ardupilot/sleepTimeMs");
		if (sleepTimeMs == null) {
			prjNode.exitWithError("Parameter sleepTimeMs is null.. review the prj_configuration file.");
		}
		logInfo("sleepTimeMs: " + sleepTimeMs.toString());
		altitudeSkew = mpm.getDoubleParam("~ardupilot/altitudeSkew");
		if (altitudeSkew == null) {
			prjNode.exitWithError("Parameter altitudeSkew is null.. review the prj_configuration file.");
		}
		logInfo("altitudeSkew: " + altitudeSkew.toString());
		latitudeSkew = mpm.getDoubleParam("~ardupilot/latitudeSkew");
		if (latitudeSkew == null) {
			prjNode.exitWithError("Parameter latitudeSkew is null.. review the prj_configuration file.");
		}
		logInfo("latitudeSkew: " + latitudeSkew.toString());
		longitudeSkew = mpm.getDoubleParam("~ardupilot/longitudeSkew");
		if (longitudeSkew == null) {
			prjNode.exitWithError("Parameter longitudeSkew is null.. review the prj_configuration file.");
		}
		logInfo("longitudeSkew: " + longitudeSkew.toString());
		xSkew = mpm.getDoubleParam("~ardupilot/xSkew");
		if (xSkew == null) {
			prjNode.exitWithError("Parameter xSkew is null.. review the prj_configuration file.");
		}
		logInfo("xSkew: " + xSkew.toString());
		ySkew = mpm.getDoubleParam("~ardupilot/ySkew");
		if (ySkew == null) {
			prjNode.exitWithError("Parameter ySkew is null.. review the prj_configuration file.");
		}
		logInfo("ySkew: " + ySkew.toString());
		zSkew = mpm.getDoubleParam("~ardupilot/zSkew");
		if (zSkew == null) {
			prjNode.exitWithError("Parameter zSkew is null.. review the prj_configuration file.");
		}
		logInfo("zSkew: " + zSkew.toString());
	}
	
	public void waitVehicleReady() {
		new Waiter() {
			@Override
			protected boolean checkCondition() {
				if (!subscribersManager.get_State().hasReceivedMessage()) {
					return false;
				}
				mavros_msgs.State current_state = getState();
				return (current_state.getConnected() && (byte2int(current_state.getSystemStatus()) == 3));
			}
		}.waitCondition("Waiting for the ardupilot device to be connected..");
		logInfo("Ardupilot device connected");
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
		logInfo("Comparing " + lowerTime.toString() + " with " + greaterTime.toString());
		if (greaterTime.compareTo(lowerTime) == 1) {
			logInfo("True");
			return true;
		}
		try {
			Thread.sleep(sleepTimeMs);
		} catch (InterruptedException e) {
			logError("Interruption command received.. wakeup from sleep");
		}
		logInfo("False");
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
		logInfo("New status: " + vehicleStatus);
	}
	
	private void checkNeedToUpdateStatus() {
		if (Objects.equals(vehicleStatus, "MOVING") && waypointReachedCurrent()) {
			setVehicleStatus("ON_AIR");
		}
	}
	
	public void disablePreArmChecks() {
		logInfo("Disabling pre-arm checks.. ");
		mavros_msgs.ParamSetRequest request = servicesManager.get_ParamSet().newRequest();
		mavros_msgs.ParamValue pv = messageFactory.newFromType(mavros_msgs.ParamValue._TYPE);
		pv.setInteger(0);
		request.setParamId("ARMING_CHECK");
		request.setValue(pv);
		if (servicesManager.get_ParamSet().callSynch(request, sleepTimeMs).getSuccess()) {
			logInfo("Pre-arm checks disabled");
		}
		else {
			logError("Error on disabling pre-arm checks");
		}
	}
	
	public List<Double> getCoordinates() {
		Double latitude = null;
		Double longitude = null;
		Double relAltitude = null;
		if (subscribersManager.get_GlobalPositionGlobal().hasReceivedMessage()) {
			sensor_msgs.NavSatFix gpg = subscribersManager.get_GlobalPositionGlobal().getReceivedMessage();
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
		if (subscribersManager.get_GlobalPositionRelAlt().hasReceivedMessage()) {
			return subscribersManager.get_GlobalPositionRelAlt().getReceivedMessage().getData();
		}
		else {
			return null;
		}
	}
	
	public geometry_msgs.PoseStamped getLocalPosition() {
		return subscribersManager.get_LocalPositionPose().getReceivedMessage();
	}
	
	public sensor_msgs.NavSatFix getGlobalPosition() {
		return subscribersManager.get_GlobalPositionGlobal().getReceivedMessage();
	}
	
	public mavros_msgs.State getState() {
		return subscribersManager.get_State().getReceivedMessage();
	}
	
	protected mavros_msgs.State getStateAfterTime(Time afterTime, int sleetTimeMillis) {
		mavros_msgs.State current_state = getState();
		while (!checkMessagesTimes(afterTime, current_state.getHeader().getStamp(), sleetTimeMillis)) {
			current_state = getState();
		}
		return current_state;
	}
	
	public void setMode(String mode) {
		logInfo("Setting mode to '" + mode + "'.. ");
		if (checkMode(mode)) {
			logInfo("The vehicle mode is already set to '" + mode + "'");
		}
		else {
			mavros_msgs.SetModeRequest request = servicesManager.get_SetMode().newRequest();
			request.setCustomMode(mode);
			request.setBaseMode((byte)0);	// Set base mode to 0 because using only custom mode
			if (servicesManager.get_SetMode().callSynch(request, sleepTimeMs).getModeSent()) {
				if (waitMode(mode)) {
					logInfo("Mode set to '" + mode + "'");
				}
				else {
					logError("Set mode failed (not set after successful call).. retry setting mode to '" + mode + "'");
					setMode(mode);
				}
			}
			else {
				logError("Set mode failed (unsuccessful call).. retry setting mode to '" + mode + "'");
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
		logInfo(arm ? "Arming.. " : "Disarming.. ");
		if (checkArmed(arm)) {
			logInfo("The vehicle is already " + (arm ? "armed" : "disarmed"));
		}
		else {
			mavros_msgs.CommandBoolRequest request = servicesManager.get_CmdArming().newRequest();
			request.setValue(arm);
			if (servicesManager.get_CmdArming().callSynch(request, sleepTimeMs).getSuccess()) {
				if (waitArmed(arm)) {
					logInfo("The vehicle is now " + (arm ? "armed" : "disarmed"));
					if (arm) {
						homePosition = getCoordinates();
					}
				}
				else {
					logError((arm ? "Arming " : "Disarming ") + "failed (not set after successful call).. retry " + (arm ? "arming " : "disarming "));
					arming(arm);
				}
			}
			else {
				logError((arm ? "Arming " : "Disarming ") + "failed (unsuccessful call).. retry " + (arm ? "arming " : "disarming "));
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
		logInfo("Return to launch position..");
		setMode("RTL");
		setVehicleStatus("RTL");
		checkRTLCompleted();
	}
	
	public boolean isRTL() {
		return isOnTarget(homePosition.get(0), homePosition.get(1), homePosition.get(2));
	}
	
	private void checkRTLCompleted() {
		CancellableLoop synchronizer = new CancellableLoop() {
			
			int sleepTimeMillis = sleepTimeMs;
			
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
			logError("Move error: the vehicle cannot be moved (status: " + getVehicleStatus() + ")");
		}
		else {
			// Set the target position updating the current position with the non null target values
			geometry_msgs.PoseStamped currentLocalPosition = getLocalPosition();
			logInfo("Current position (x,y,z): " + localPositionToString(currentLocalPosition));
			if (forward != null) {
				currentLocalPosition.getPose().getPosition().setY(forward);
			}
			if (right != null) {
				currentLocalPosition.getPose().getPosition().setX(right);
			}
			if (down != null) {
				currentLocalPosition.getPose().getPosition().setZ(down);
			}
			logInfo("Moving to (x,y,z): " + localPositionToString(currentLocalPosition));
			
			// Publish the command and run the target reached checker
			setVehicleStatus("MOVING");
			publishersManager.getPublisher_SetpointPositionLocal().publish(currentLocalPosition);
			checkLocalTargetReached(currentLocalPosition);
		}
	}
	
	private void checkLocalTargetReached(final geometry_msgs.PoseStamped position) {
		if (checkerTargetReached != null) {
			checkerTargetReached.cancel();
		}
		checkerTargetReached = new CancellableLoop() {
			
			int sleepTimeMillis = sleepTimeMs;
			
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
		logInfo("Current position (x,y,z): " + localPositionToString(current));
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
	
	protected void checkTargetReached(final Double latitude, final Double longitude, final Double relAltitude, final String toStatus) {
		if (checkerTargetReached != null) {
			checkerTargetReached.cancel();
		}
		checkerTargetReached = new CancellableLoop() {
			
			int sleepTimeMillis = sleepTimeMs;
			
			@Override
			protected void loop() throws InterruptedException {
				
				if (isOnTarget(latitude, longitude, relAltitude)) {
					setVehicleStatus(toStatus);
					cancel();
				}
				else {
					Thread.sleep(sleepTimeMillis);
				}
			}
		};
		
		prjNode.getConnectedNode().executeCancellableLoop(checkerTargetReached);
	}
	
	private boolean isOnTarget(Double latitude, Double longitude, Double relAltitude) {
		List<Double> currentPos = getCoordinates();
		boolean checkLatitude = (latitude == null) || (Math.abs(currentPos.get(0) - latitude) <= latitudeSkew);
		boolean checkLongitude = (longitude == null) || (Math.abs(currentPos.get(1) - longitude) <= longitudeSkew);
		boolean checkAltitude = (relAltitude == null) || (Math.abs(currentPos.get(2) - relAltitude) <= altitudeSkew);
		return  checkLatitude && checkLongitude && checkAltitude;
	}
	
	public void moveGlobal(Double latitude, Double longitude, Double relAltitude) {
		if (!canMove()) {
			logError("Move error: the vehicle cannot be moved (status: " + getVehicleStatus() + ")");
		}
		else {
			// Normalize the target position
			List<Double> currentPos = getCoordinates();
			logInfo("Current position (Lat,Long,RelAlt): (" + currentPos.get(0).toString() + "," + 
					currentPos.get(1).toString() + "," + currentPos.get(2).toString() + ")");
			latitude = (latitude == null) ? currentPos.get(0) : latitude;
			longitude = (longitude == null) ? currentPos.get(1) : longitude;
			relAltitude = (relAltitude == null) ? currentPos.get(2) : relAltitude;
			logInfo("Moving to (Lat,Long,RelAlt): (" + latitude.toString() + "," + longitude.toString() + "," + relAltitude.toString() + ")");
			// Set the request
			mavros_msgs.GlobalPositionTarget request = publishersManager.getPublisher_SetpointPositionGlobal().newMessage();
			request.setLatitude(latitude);
			request.setLongitude(longitude);
			request.setAltitude(relAltitude.floatValue());
			// Run the command and the target reached checker
			setVehicleStatus("MOVING");
			publishersManager.getPublisher_SetpointPositionGlobal().publish(request);
			checkTargetReached(latitude, longitude, relAltitude, "ON_AIR");
		}
	}
	
	public void waypointClear() {
		logInfo("Clear waypoint..");
		mavros_msgs.WaypointClearRequest request = servicesManager.get_MissionClear().newRequest();
		if (servicesManager.get_MissionClear().callSynch(request, sleepTimeMs).getSuccess()) {
			logInfo("Clear waypoint completed");
		}
		else {
			logError("Clear waypoint unsuccessful.. retrying");
			waypointClear();
		}
	}
	
	public int waypointPush(Double latitude, Double longitude, Double relAltitude) {
		logInfo("Push waypoint: " +
				(latitude != null ? latitude.toString() + " Lat " : "") +
				(longitude != null ? longitude.toString() + " Long " : "") +
				(relAltitude != null ? relAltitude.toString() + " Alt " : "") 
				);
		mavros_msgs.WaypointPushRequest request = servicesManager.get_MissionPush().newRequest();
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
		mavros_msgs.WaypointPushResponse response = servicesManager.get_MissionPush().callSynch(request, sleepTimeMs);
		if (response.getSuccess()) {
			logInfo("Waypoint correctly pushed");
			return response.getWpTransfered();
		}
		else {
			logError("Push waypoint unsuccessful (" + Integer.toString(response.getWpTransfered()) + ").. retrying");
			return waypointPush(latitude, longitude, relAltitude);
		}
	}
	
	public void waypointSetCurrent(int current) {
		logInfo("Set current waypoint to " + Integer.toString(current) + "..");
		mavros_msgs.WaypointSetCurrentRequest request = servicesManager.get_MissionSetCurrent().newRequest();
		request.setWpSeq((short) current);
		if (servicesManager.get_MissionSetCurrent().callSynch(request, sleepTimeMs).getSuccess()) {
			logInfo("Set current waypoint completed");
		}
		else {
			logError("Set current waypoint unsuccessful.. retrying");
			waypointSetCurrent(current);
		}
	}
	
	public boolean waypointHasReached() {
		return subscribersManager.get_MissionReached().hasReceivedMessage();
	}
	
	public Integer waypointGetLastReached() {
		if (waypointHasReached()) {
			return (int) subscribersManager.get_MissionReached().getReceivedMessage().getWpSeq();
		}
		else {
			return null;
		}
	}
	
	public boolean waypointHasCurrent() {
		return subscribersManager.get_MissionWaypoints().hasReceivedMessage();
	}
	
	public Integer waypointGetCurrent() {
		if (waypointHasCurrent()) {
			return (int) subscribersManager.get_MissionWaypoints().getReceivedMessage().getCurrentSeq();
		}
		return null;
	}
	
	public Integer waypointGetLast() {
		if (waypointHasCurrent()) {
			return (int) subscribersManager.get_MissionWaypoints().getReceivedMessage().getWaypoints().size() - 1;
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
	
	protected void logInfo(String message) {
		prjNode.logInfo(message);
	}
	
	protected void logError(String message) {
		prjNode.logError(message);
	}

}
