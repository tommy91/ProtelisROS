package com.github.rosjava.prj_pkg.prj.Controllers;

import com.github.rosjava.prj_pkg.prj.PrjCancellableRunnable;
import com.github.rosjava.prj_pkg.prj.PrjNode;

import java8.util.Objects;

public class CopterController extends VehicleController implements CopterInterface {
	
	public CopterController(PrjNode prjNode) {
		super(prjNode);
	}
	
	public void takeoff(double altitude) {
		takeoff(null, null, null, null, altitude);
	}
	
	public void takeoff(final Double min_pitch, final Double yaw, final Double latitude, final Double longitude, final Double altitude) {
		logInfo("Initializing takeoff.. ");
		if (isFlying()) {
			logError("Takeoff error: the vehicle is already flying (altitude: " + getRelAltitude().toString() + ")");
		}
		else {
			setVehicleStatus("INITIALIZING_TAKEOFF");
			
			/** 
			 * Running the takeoff commands in a new thread 
			 * so the execution is not stucked here until the takeoff is completed 
			 */
			prjNode.getConnectedNode().getScheduledExecutorService().execute(new PrjCancellableRunnable(prjNode.getConnectedNode()) {
				@Override
				  public void execute() {
//					disablePreArmChecks();
					setMode("GUIDED");
					arming(true);
					setVehicleStatus("TAKING_OFF");
					logInfo(getTakeoffInfo(min_pitch, yaw, latitude, longitude, altitude));
					mavros_msgs.CommandTOLRequest request = setTakeoffRequest(min_pitch, yaw, latitude, longitude, altitude);
					if (servicesManager.get_CmdTakeoff().callSynch(request, sleepTimeMs).getSuccess()) {
						checkTargetReached(latitude, longitude, altitude, "ON_AIR");
					}
					else {
						logError("Takeoff failed.. retry takeoff.. ");
						execute();
					}
				}
			});
		}
	}
	
	private mavros_msgs.CommandTOLRequest setTakeoffRequest(Double min_pitch, Double yaw, Double latitude, Double longitude, Double altitude) {
		mavros_msgs.CommandTOLRequest request = servicesManager.get_CmdTakeoff().newRequest();
		if (altitude != null) {
			request.setAltitude(altitude.floatValue());
		}
		if (latitude != null) {
			request.setLatitude(latitude.floatValue());
		}
		if (longitude != null) {
			request.setLongitude(longitude.floatValue());
		}
		if (min_pitch != null) {
			request.setMinPitch(min_pitch.floatValue());
		}
		if (yaw != null) {
			request.setYaw(yaw.floatValue());
		}
		return request;
	}
	
	public boolean isFlying() {
		Double altitude = getRelAltitude();
		return (getVehicleStatus() == "ON_AIR") || ((altitude != null) && (altitude >= altitudeSkew));
	}
	
	private String getTakeoffInfo(Double min_pitch, Double yaw, Double latitude, Double longitude, Double altitude) {
		return getTakeoffLandInfo("takeoff", min_pitch, yaw, latitude, longitude, altitude);
	}
	
	private String getLandInfo(Double yaw, Double latitude, Double longitude, Double altitude) {
		return getTakeoffLandInfo("land", null, yaw, latitude, longitude, altitude);
	}
	
	private String getTakeoffLandInfo(String prefix, Double min_pitch, Double yaw, Double latitude, Double longitude, Double altitude) {
		String toPrint = prefix + ".. (";
		if (min_pitch != null)
			toPrint += Double.toString(min_pitch);
		else 
			toPrint += "null";
		if (yaw != null)
			toPrint += "," + Double.toString(yaw);
		else 
			toPrint += ",null";
		if (latitude != null)
			toPrint += "," + Double.toString(latitude);
		else 
			toPrint += ",null";
		if (longitude != null) 
			toPrint += "," + Double.toString(longitude);
		else 
			toPrint += ",null";
		if (altitude != null) 
			toPrint += "," + Double.toString(altitude) + ")";
		else 
			toPrint += ",null)";
		return toPrint;
	}
	
	public void land() {
		logInfo("Landing..");
		setVehicleStatus("LANDING");
		/** 
		 * Running the land commands in a new thread 
		 * so the execution is not stucked here until the landing is completed 
		 */
		prjNode.getConnectedNode().getScheduledExecutorService().execute(new PrjCancellableRunnable(prjNode.getConnectedNode()) {
			@Override
			  public void execute() {
				setMode("LAND");
				checkTargetReached(getLatitude(), getLongitude(), Double.valueOf(0), "ON_GROUND");
			}
		});
	}
	
	public void land(final Double yaw, final Double latitude, final Double longitude, final Double altitude) {
		if (!isFlying()) {
			logError("Landing error: the vehicle is not on air (altitude: " + getRelAltitude().toString() + ")");
		}
		else {
			/** 
			 * Running the land commands in a new thread 
			 * so the execution is not stucked here until the landing is completed 
			 */
			prjNode.getConnectedNode().getScheduledExecutorService().execute(new PrjCancellableRunnable(prjNode.getConnectedNode()) {
				@Override
				  public void execute() {
//					setMode("GUIDED");
					setVehicleStatus("LANDING");
					logInfo(getLandInfo(yaw, latitude, longitude, altitude));
					mavros_msgs.CommandTOLRequest request = setLandRequest(yaw, latitude, longitude, altitude);
					if (servicesManager.get_CmdLand().callSynch(request, sleepTimeMs).getSuccess()) {
						checkTargetReached(latitude, longitude, altitude, "ON_GROUND");
					}
					else {
						logError("Land failed.. retry land.. ");
						execute();
					}
				}
			});
		}
	}
	
	private mavros_msgs.CommandTOLRequest setLandRequest(Double yaw, Double latitude, Double longitude, Double altitude) {
		mavros_msgs.CommandTOLRequest request = servicesManager.get_CmdLand().newRequest();
		if (altitude != null) {
			request.setAltitude(altitude.floatValue());
		}
		if (latitude != null) {
			request.setLatitude(latitude.floatValue());
		}
		if (longitude != null) {
			request.setLongitude(longitude.floatValue());
		}
		if (yaw != null) {
			request.setYaw(yaw.floatValue());
		}
		return request;
	}
	
	public boolean isLanded() {
		return (getVehicleStatus() == "ON_GROUND") || (getRelAltitude() <= altitudeSkew);
	}
	
	@Override
	public boolean canMove() {
		return Objects.equals(getVehicleStatus(),"ON_AIR") || Objects.equals(getVehicleStatus(),"MOVING");
	}
	
}
