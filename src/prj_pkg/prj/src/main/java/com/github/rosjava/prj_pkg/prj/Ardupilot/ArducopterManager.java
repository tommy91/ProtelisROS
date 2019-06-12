package com.github.rosjava.prj_pkg.prj.Ardupilot;

import org.ros.concurrent.CancellableLoop;

import com.github.rosjava.prj_pkg.prj.PrjNode;

import java8.util.Objects;

public class ArducopterManager extends ArdupilotManager implements ArducopterInterface {
	
	public ArducopterManager(PrjNode prjNode) {
		super(prjNode);
	}
	
	public void takeoff(double altitude) {
		takeoff(null, null, null, null, (float)altitude);
	}
	
	public void takeoff(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		printLog("Initializing takeoff.. ");
		if (isFlying()) {
			printLog("Takeoff error: the vehicle is already flying (altitude: " + getRelAltitude().toString() + ")");
		}
		else {
			setVehicleStatus("INITIALIZING_TAKEOFF");
//			disablePreArmChecks();
			setMode("GUIDED");
			arming(true);
			setVehicleStatus("TAKING_OFF");
			printLog(getTakeoffInfo(min_pitch, yaw, latitude, longitude, altitude));
			mavros_msgs.CommandTOLRequest request = setTakeoffRequest(min_pitch, yaw, latitude, longitude, altitude);
			if (mavrosServicesManager.get_CmdTakeoff().callSynch(request, 1000).getSuccess()) {
				checkTakeoffCompleted(altitude);
			}
			else {
				printLog("Takeoff failed.. retry takeoff.. ");
				takeoff(min_pitch, yaw, latitude, longitude, altitude);
			}
		}
	}
	
	private void checkTakeoffCompleted(final float altitude) {
		CancellableLoop synchronizer = new CancellableLoop() {
			
			int sleepTimeMillis = 100;
			
			@Override
			protected void loop() throws InterruptedException {
				if (Math.abs(getRelAltitude().floatValue() - altitude) <= altitudeSkew) {
					setVehicleStatus("ON_AIR");
					cancel();
				}
				else {
					Thread.sleep(sleepTimeMillis);
				}
			}
		};
		
		prjNode.getConnectedNode().executeCancellableLoop(synchronizer);
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
		return (altitude != null) && (altitude >= altitudeSkew);
	}
	
	private String getTakeoffInfo(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		return getTakeoffLandInfo("takeoff", min_pitch, yaw, latitude, longitude, altitude);
	}
	
	private String getLandInfo(Float yaw, Float latitude, Float longitude, Float altitude) {
		return getTakeoffLandInfo("land", null, yaw, latitude, longitude, altitude);
	}
	
	private String getTakeoffLandInfo(String prefix, Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude) {
		String toPrint = prefix + ".. (";
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
	
	public void land() {
		printLog("Landing..");
		setVehicleStatus("LANDING");
		setMode("LAND");
		checkLandCompleted();
	}
	
	public void land(Float yaw, Float latitude, Float longitude, Float altitude) {
		if (!isFlying()) {
			printLog("Landing error: the vehicle is not on air (altitude: " + getRelAltitude().toString() + ")");
		}
		else {
//			setMode("GUIDED");
			setVehicleStatus("LANDING");
			printLog(getLandInfo(yaw, latitude, longitude, altitude));
			mavros_msgs.CommandTOLRequest request = setLandRequest(yaw, latitude, longitude, altitude);
			if (!mavrosServicesManager.get_CmdLand().callSynch(request, 1000).getSuccess()) {
				printLog("Land failed.. retry land.. ");
				land(yaw, latitude, longitude, altitude);
			}
			else {
				checkLandCompleted();
			}
		}
	}
	
	private mavros_msgs.CommandTOLRequest setLandRequest(Float yaw, Float latitude, Float longitude, Float altitude) {
		mavros_msgs.CommandTOLRequest request = mavrosServicesManager.get_CmdLand().newRequest();
		if (altitude != null) {
			request.setAltitude(altitude);
		}
		if (latitude != null) {
			request.setLatitude(latitude);
		}
		if (longitude != null) {
			request.setLongitude(longitude);
		}
		if (yaw != null) {
			request.setYaw(yaw);
		}
		return request;
	}
	
	private void checkLandCompleted() {
		CancellableLoop synchronizer = new CancellableLoop() {
			
			int sleepTimeMillis = 100;
			
			@Override
			protected void loop() throws InterruptedException {
				if (isLanded()) {
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
	
	public boolean isLanded() {
		return getRelAltitude() <= altitudeSkew;
	}
	
	@Override
	public boolean canMove() {
		return Objects.equals(getVehicleStatus(),"ON_AIR") || Objects.equals(getVehicleStatus(),"MOVING");
	}
	
}
