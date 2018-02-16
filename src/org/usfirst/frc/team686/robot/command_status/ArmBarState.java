package org.usfirst.frc.team686.robot.command_status;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;

/**
 * ElevatorArmBar status structure, filled by ElevatorLoop.java
 */
public class ArmBarState
{
	private static ArmBarState instance = new ArmBarState();
	public static ArmBarState getInstance() { return instance; }	
	
	// all member variables should be private to force other object to use the set/get access methods
	// which are synchronized to allow multi-thread synchronization

	private double angleDeg;

	private double targetAngleDeg;
	private double filteredTargetAngleDeg;
	
	private double pidError;
	private double motorPercentOutput;
	private double motorCurrent;

	private boolean limitSwitchTriggered;
	
	
	public ArmBarState() {}
	
	public synchronized double getAngleDeg() {
		return angleDeg;
	}

	public synchronized void setAngleDeg(double angleDeg) {
		this.angleDeg = angleDeg;
	}

	public synchronized double getTargetAngleDeg() {
		return targetAngleDeg;
	}

	public synchronized void setTargetAngleDeg(double targetAngleDeg) {
		this.targetAngleDeg = targetAngleDeg;
	}

	public synchronized double getFilteredTargetAngleDeg() {
		return filteredTargetAngleDeg;
	}

	public synchronized void setFilteredTargetAngleDeg(double filteredTargetAngleDeg) {
		this.filteredTargetAngleDeg = filteredTargetAngleDeg;
	}

	public synchronized double getPidError() {
		return pidError;
	}

	public synchronized void setPidError(double pidError) {
		this.pidError = pidError;
	}

	public synchronized double getMotorPercentOutput() {
		return motorPercentOutput;
	}

	public synchronized void setMotorPercentOutput(double motorPercentOutput) {
		this.motorPercentOutput = motorPercentOutput;
	}

	public synchronized double getMotorCurrent() {
		return motorCurrent;
	}

	public synchronized void setMotorCurrent(double motorCurrent) {
		this.motorCurrent = motorCurrent;
	}
	
	public synchronized boolean isLimitSwitchTriggered() {
		return limitSwitchTriggered;
	}

	public synchronized void setLimitSwitchTriggered(boolean limitSwitchTriggered) {
		this.limitSwitchTriggered = limitSwitchTriggered;
	}

	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
        	synchronized (ArmBarState.this)
        	{
	    		put("ArmBarState/positionInches", angleDeg );
	    		put("ArmBarState/trajectoryTargetInches", targetAngleDeg );
	    		put("ArmBarState/pidError", pidError );
	    		put("ArmBarState/motorPercentOutput", motorPercentOutput );
	    		put("ArmBarState/motorCurrent",  motorCurrent );
	    		put("ArmBarState/limitSwitch",  limitSwitchTriggered );
        	}
        }
    };
    
    public DataLogger getLogger() { return logger; }


    
	
}
