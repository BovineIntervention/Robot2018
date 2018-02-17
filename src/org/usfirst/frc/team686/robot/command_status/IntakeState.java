package org.usfirst.frc.team686.robot.command_status;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class IntakeState {
	
	private static IntakeState instance = new IntakeState();
	public static IntakeState getInstance() { return instance; }
	
	private double leftVelocityInchesPerSec;
	private double rightVelocityInchesPerSec;
	
	private double leftMotorCurrent;
	private double rightMotorCurrent;
	
	private Value solenoidValue;
	
	
	public IntakeState() {}
	

	public synchronized double getLeftVelocityInchesPerSec(){
		return leftVelocityInchesPerSec;
	}
	
	public synchronized void setLeftVelocityInchesPerSec (double leftVelocityInchesPerSec){
		this.leftVelocityInchesPerSec = leftVelocityInchesPerSec;
	}
	
	public synchronized double getRightVelocityInchesPerSec(){
		return rightVelocityInchesPerSec;
	}
	
	public synchronized void setRightVelocityInchesPerSec(double rightVelocityInchesPerSec){
		this.rightVelocityInchesPerSec = rightVelocityInchesPerSec;
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
	
	public synchronized void setRightMotorCurrent(double rightMotorCurrent) {
		this.rightMotorCurrent = rightMotorCurrent;
	}
	
	public synchronized Value getSolenoidValue() {
		return solenoidValue;
	}
	
	public synchronized void setSolenoidValue(Value solenoidValue) {
		this.solenoidValue = solenoidValue;
	}
	
	
	private final DataLogger logger = new DataLogger()
	{
		@Override
		public void log()
		{
			synchronized (IntakeState.this)
			{
				put("IntakeState/leftVelocityInchesPerSec", leftVelocityInchesPerSec);
				put("IntakeState/rightVelocityInchesPerSec", rightVelocityInchesPerSec);
				put("IntakeState/leftMotorCurrent", leftMotorCurrent);
				put("IntakeState/rightMotorCurrent", rightMotorCurrent);
			}
			
		}
	};
	
	public DataLogger getLogger() { return logger; }
	
}
