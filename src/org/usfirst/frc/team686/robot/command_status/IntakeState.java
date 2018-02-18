package org.usfirst.frc.team686.robot.command_status;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class IntakeState {
	
	private static IntakeState instance = new IntakeState();
	public static IntakeState getInstance() { return instance; }
	
	private double lVelocity;
	private double rVelocity;
	
	private double lMotorCurrent;
	private double rMotorCurrent;
	
	private Value solenoidValue;
	
	
	public IntakeState() {}
	

	public synchronized double getLeftVelocity(){
		return lVelocity;
	}
	
	public synchronized void setLeftVelocity (double leftVelocity){
		this.lVelocity = leftVelocity;
	}
	
	public synchronized double getRightVelocity(){
		return rVelocity;
	}
	
	public synchronized void setRightVelocity(double rightVelocity){
		this.rVelocity = rightVelocity;
	}
	
	public synchronized double getLeftMotorCurrent() {
		return lMotorCurrent;
	}

	public synchronized void setLeftMotorCurrent(double leftMotorCurrent) {
		this.lMotorCurrent = leftMotorCurrent;
	}
	
	public synchronized double getRightMotorCurrent() {
		return rMotorCurrent;
	}
	
	public synchronized void setRightMotorCurrent(double rightMotorCurrent) {
		this.rMotorCurrent = rightMotorCurrent;
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
				put("IntakeState/leftVelocity", lVelocity);
				put("IntakeState/rightVelocity", rVelocity);
				put("IntakeState/solenoidValue", solenoidValue.name());
				//put("IntakeState/leftMotorCurrent", lMotorCurrent);
				//put("IntakeState/rightMotorCurrent", rMotorCurrent);
			}
			
		}
	};
	
	public DataLogger getLogger() { return logger; }
	
}
