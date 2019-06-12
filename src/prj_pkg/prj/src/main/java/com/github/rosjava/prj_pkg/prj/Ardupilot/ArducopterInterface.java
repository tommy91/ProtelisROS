package com.github.rosjava.prj_pkg.prj.Ardupilot;

public interface ArducopterInterface extends ArdupilotInterface {
	
	public void takeoff(double altitude);
	
	public void takeoff(Float min_pitch, Float yaw, Float latitude, Float longitude, Float altitude);
	
	public boolean isFlying();
	
	public void land();
	
	public void land(Float yaw, Float latitude, Float longitude, Float altitude);
	
	public boolean isLanded();
	
	@Override
	public boolean canMove();

}
