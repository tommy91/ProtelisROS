package com.github.rosjava.prj_pkg.prj.Ardupilot;

import java.util.List;

public interface ArdupilotInterface {
	
	public void waitArdupilotReady();
	
	public String getVehicleStatus();
	
	public void disablePreArmChecks();
	
	public List<Double> getCoordinates();
	
	public Double getLatitude();
	
	public Double getLongitude();
	
	public Double getAltitude();
	
	public Double getRelAltitude();
	
	public geometry_msgs.PoseStamped getLocalPosition();
	
	public sensor_msgs.NavSatFix getGlobalPosition();
	
	public mavros_msgs.State getState();
	
	public void setMode(String mode);
	
	public String getMode();
	
	public void arming(boolean arm);
	
	public boolean isArmed();
	
	public void returnToLaunch();
	
	public boolean isRTL();
	
	public boolean canMove();
	
	public void moveLocal(Double forward, Double right, Double down);
	
	public void moveGlobal(Double latitude, Double longitude, Double altitude);
	
	public void waypointClear();
	
	public int waypointPush(Double latitude, Double longitude, Double relAltitude);
	
	public void waypointSetCurrent(int current);
	
	public boolean waypointHasReached();
	
	public Integer waypointGetLastReached();
	
	public boolean waypointHasCurrent();
	
	public Integer waypointGetCurrent();
	
	public Integer waypointGetLast();
	
	public boolean waypointReachedCurrent();

}
