package com.github.rosjava.prj_pkg.prj.Controllers;

public interface CopterInterface extends VehicleInterface {
	
	public void takeoff(double altitude);
	
	public void takeoff(Double min_pitch, Double yaw, Double latitude, Double longitude, Double altitude);
	
	public boolean isFlying();
	
	public void land();
	
	public void land(Double yaw, Double latitude, Double longitude, Double altitude);
	
	public boolean isLanded();
	
	@Override
	public boolean canMove();

}
