package org.usfirst.frc.team686.robot.command_status;

public class IntakeState {
	
	private static IntakeState instance = new IntakeState();
	public static IntakeState getInstance() { return instance; }
	
	private double velocityInchesPerSec;
	
	private double leftMotorCurrent;
	private double rightMotorCurrent;
	
	
	public IntakeState() {}
	

	public synchronized double getVelocityInchesPerSec(){
		return velocityInchesPerSec;
	}
	
	public synchronized void setVelocityInchesPerSec (double velocityInchesPerSec){
		this.velocityInchesPerSec = velocityInchesPerSec;
	}
	
	public synchronized double getLeftMotorCurrent() {
		return leftMotorCurrent;
	}

	public synchronized void setLeftMotorCurrent(double leftMotorCurrent) {
		this.leftMotorCurrent = leftMotorCurrent;
	}
	
	public synchronized double getRightMotorCurrent() {
		return rightMotorCurrent;
	}
	
	public synchronized void setRightMotorCurrent(double righ) {
		this.rightMotorCurrent = rig
	}
	
}
