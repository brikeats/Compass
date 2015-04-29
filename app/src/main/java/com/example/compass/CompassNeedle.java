package com.example.compass;


public class CompassNeedle{
	
	private final String TAG = "CompassNeedle";
	
	public static enum Dynamics{
		NONE, SMOOTH, SPRINGY, WOBBLY, CUSTOM
	}
	
	private double angle=0, speed=0;
	private double mass=100, drag = 1.2;
	private double dt = 3;
	private Dynamics dynamics;
	
	public CompassNeedle() {}
	public CompassNeedle(Dynamics dynamics) {this.dynamics = dynamics;}
	public CompassNeedle(double mass, double drag, double dt) {this.mass=mass; this.drag=drag; this.dt=dt;}
	
	public void setDynamics(Dynamics dynamics){
		this.dynamics = dynamics;
		switch(dynamics){
		case SMOOTH:
			mass = 10; drag = 1.3; dt = 0.5; break;
		case SPRINGY:
			mass = 17; drag = 1.2; dt = 2.3; break;
		case WOBBLY:
			mass = 60; drag = 1.05; dt = 1.5; break;
		}		
	}
	
	public void setDynamics(String dynamicsStr){
		switch(dynamicsStr){
		case "None":
			setDynamics(Dynamics.NONE); break;
		case "Smooth":
			setDynamics(Dynamics.SMOOTH); break;
		case "Springy":
			setDynamics(Dynamics.SPRINGY); break;
		case "Wobbly":
			setDynamics(Dynamics.WOBBLY); break;
		case "Custom":
			setDynamics(Dynamics.CUSTOM); break;
		}
	}
	 
	
	public void setMass(double newMass){mass = newMass;}
	public double getMass(){return mass;}
	public void setDrag(double newDrag){drag = newDrag;}
	public double getDrag(){return drag;}
	public void setTimestep(double newTimestep){dt = newTimestep;}
	public double getTimestep(){return dt;}
	
	public double update(double target){
		
		if (dynamics == Dynamics.NONE) return target;
		
		double err = target - angle;
		if (err > Math.PI) err -= 2*Math.PI;
		if (err < -Math.PI) err += 2*Math.PI;
		
		double torque = err;
		speed += dt*torque/mass;
		speed *= 1/drag;
		angle += dt*speed;

		if (angle > Math.PI) angle -= 2*Math.PI;
		if (angle < -Math.PI) angle += 2*Math.PI;
		return angle;
	}
		
}
