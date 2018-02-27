package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.ArmBarState;
import org.usfirst.frc.team686.robot.lib.util.Util;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class ArmBarLoop implements Loop
{
	private static ArmBarLoop instance = new ArmBarLoop();
	public static ArmBarLoop getInstance() { return instance; }
	
	public double Kf = Constants.kArmBarKf;
	public double Kp = Constants.kArmBarKp;
	public double Kd = Constants.kArmBarKd;
	public double Ki = Constants.kArmBarKi;
	
	public enum ArmBarStateEnum { UNINITIALIZED, CALIBRATING, RUNNING, ESTOPPED; }
	public ArmBarStateEnum state = ArmBarStateEnum.UNINITIALIZED;
	public ArmBarStateEnum nextState = ArmBarStateEnum.UNINITIALIZED;

	private ArmBarState armBarState = ArmBarState.getInstance();
	
	public boolean enable = false;
	public boolean forceDisable = false;
	
	public double position;
	public double target;
	public double filteredTarget;

	public double error = 0.0;
	public double dError = 0.0;
	public double iError = 0.0;
	public double lastError = 0.0;
	
	public double voltage = 0.0;
	
	public TalonSRX talon;
	
	private double startZeroingTime;
	
	
	public ArmBarLoop()
	{
		System.out.println("ArmBarLoop constructor");

		// Configure Talon
		talon = new TalonSRX(Constants.kArmBarTalonId);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General,   (int)(1000 * Constants.kLoopDt), Constants.kTalonTimeoutMs);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, (int)(1000 * Constants.kLoopDt), Constants.kTalonTimeoutMs);
		talon.set(ControlMode.PercentOutput, 0.0);
		talon.setNeutralMode(NeutralMode.Brake);
				
		// current limit to stop breaking motor mount
		talon.configPeakCurrentLimit(Constants.kArmBarPeakCurrentLimit, Constants.kTalonTimeoutMs);
		talon.configPeakCurrentDuration(Constants.kArmBarPeakCurrentDuration, Constants.kTalonTimeoutMs);
		talon.configContinuousCurrentLimit(Constants.kArmBarContinuousCurrentLimit, Constants.kTalonTimeoutMs);
		talon.enableCurrentLimit(true);
		
		
		// Configure Encoder
		// first, find the current absolute encoder values
		talon.setSensorPhase(true);
		talon.setInverted(true);

		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);	
		int absoluteEncoderValue = talon.getSensorCollection().getPulseWidthPosition();

		// set relative position to absolute position
		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);	// configure for closed-loop PID
		talon.setSelectedSensorPosition( absoluteEncoderValue, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
		
		// set soft limits
		talon.configForwardSoftLimitThreshold( Constants.kArmBarEncoderLimitUp,   Constants.kTalonTimeoutMs);
		talon.configReverseSoftLimitThreshold( Constants.kArmBarEncoderLimitDown, Constants.kTalonTimeoutMs);
		talon.configForwardSoftLimitEnable(true, Constants.kTalonTimeoutMs);
		talon.configReverseSoftLimitEnable(true, Constants.kTalonTimeoutMs);
		talon.overrideLimitSwitchesEnable(true);	// enable soft limit switches
		
		int relativeEncoderValue = talon.getSelectedSensorPosition(Constants.kTalonPidIdx);
		if ((relativeEncoderValue > Constants.kArmBarEncoderLimitUp) || (relativeEncoderValue < Constants.kArmBarEncoderLimitDown))
		{
			forceDisable = true;
			String errorString = String.format("Four Bar relative encoder reading of %d not within absolute encoder limits (%d, %d).  Forcing arm bar to disabled", relativeEncoderValue, Constants.kArmBarEncoderLimitDown, Constants.kArmBarEncoderLimitUp);
			System.out.println(errorString);
			DriverStation.reportError(errorString, false);
		}
		
		disable();
	}
	

	public void enable()
	{
		if (forceDisable)
		{
			// if relativePosition is not within absolute position limits, do not trust it
			// disable four bar to avoid damage
			enable = false;
		}
		else
		{
			enable = true; 
			System.out.println("ArmBarLoop enable");
		}
	}
	
	public void disable()
	{
		enable = false; 
		state = ArmBarStateEnum.UNINITIALIZED; 
		nextState = ArmBarStateEnum.UNINITIALIZED;
	}
	
	public void setTarget(double _target) { target = _target; }
	public double getTarget () { return target; } 
	
	public double getFilteredTarget() { return filteredTarget; }

	public ArmBarStateEnum getState() { return state; }
	
	public void stop() { target = getPosition(); }

	public double getPosition() { return position; }
	
	public boolean getLimitSwitchDuringZeroing()
	{
		double elapsedZeroingTime = Timer.getFPGATimestamp() - startZeroingTime;
		double maxZeroingTime = Constants.kElevatorMaxHeightLimit / Constants.kElevatorZeroingVelocity + 2.0;
		
		int encoderPosition = talon.getSelectedSensorPosition(Constants.kTalonPidIdx);
		return ((encoderPosition >= Constants.kArmBarEncoderLimitUp) || 
				(armBarState.getMotorCurrent() > Constants.kArmBarMotorStallCurrentThreshold) ||
				elapsedZeroingTime > maxZeroingTime);
	}
	
	
	double startTime;
	
	@Override
	public void onStart() {
		state = ArmBarStateEnum.UNINITIALIZED;
		nextState = ArmBarStateEnum.UNINITIALIZED;
		
		startTime = Timer.getFPGATimestamp();
		forceDisable = false;		
	}

	@Override
	public void onLoop() 
	{
		getStatus();

		double voltage = calcVoltage(armBarState.getAngleDeg(), armBarState.isLimitSwitchTriggered(), enable);			// output in [-12, +12] volts	
		double percentOutput = voltage / Constants.kNominalBatteryVoltage;			// normalize output to [-1,. +1]
		talon.set(ControlMode.PercentOutput, percentOutput);				// send to motor control

		// set PID status before we leave
		armBarState.setTargetAngleDeg(target);		
		armBarState.setFilteredTargetAngleDeg(filteredTarget);
		armBarState.setPidError(voltage);
			
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		disable();
		talon.set(ControlMode.PercentOutput, 0.0);
	}	
	
	
	
	public double calcVoltage(double position, boolean limitSwitchTriggered, boolean enabled)
	{
		// transition states
		state = nextState;
		
		// start over if ever disabled
		if (!enabled)
		{
			state = ArmBarStateEnum.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:
			// initial state.  stay here until enabled
			setTarget(position);						// initial goal is to stay in the same position
			filteredTarget = position;				// hold position
			if (enabled)
			{
				nextState = ArmBarStateEnum.CALIBRATING;	// when enabled, state ZEROING
			}
			break;
			
		case CALIBRATING:
			// slowly move up towards limit switch
			filteredTarget += (Constants.kArmBarZeroingVelocity * Constants.kLoopDt);
			
			if (limitSwitchTriggered)
			{
				// CALIBRATING is done when limit switch is hit
				setTarget(position);						// initial goal is to stay in the same position
				filteredTarget = position;					// initial goal is to stay in the same position
				
				nextState = ArmBarStateEnum.RUNNING;		// start running state
			}
			break;
			
		case RUNNING:
			// velocity control -- move filtered goal a little more towards the ultimate goal
			
			if (target > filteredTarget)
			{
				// moving up
				filteredTarget += Constants.kArmBarVelocity * Constants.kLoopDt;
				filteredTarget = Math.min(filteredTarget, target);
			}
			else 
			{
				// moving down
				filteredTarget -= Constants.kArmBarVelocity * Constants.kLoopDt;
				filteredTarget = Math.max(filteredTarget, target);
			}

			break;
			
		default:
			nextState = ArmBarStateEnum.UNINITIALIZED;
		}
		
		error = filteredTarget - position;
		dError = (error - lastError) / Constants.kLoopDt;
		iError += (error * Constants.kLoopDt);

		lastError = error;
		
		voltage = Kp * error + Kd * dError + Ki * iError;
		voltage = Util.limit(voltage,  Constants.kMaxArmBarVoltage);
		
		if (limitSwitchTriggered)
			voltage = Math.min(voltage, 0.0);	// do not let elevator continue up when at limit switch		
		
		return voltage;
	}	
	
	public static double encoderUnitsToAngleDeg(int _encoderUnits)
	{
		return _encoderUnits / Constants.kArmBarEncoderUnitsPerDeg;
	}
	
	public static int angleDegToEncoderUnits(double _deg)
	{
		return (int)(_deg * Constants.kArmBarEncoderUnitsPerDeg);
	}
	
	
	
	public void getStatus()
	{
		// read status once per update
		armBarState.setAngleDeg( encoderUnitsToAngleDeg( talon.getSelectedSensorPosition(Constants.kTalonPidIdx) ) );		

		armBarState.setMotorPercentOutput( talon.getMotorOutputPercent() );
		armBarState.setMotorCurrent( talon.getOutputCurrent() );

		armBarState.setLimitSwitchTriggered( talon.getSelectedSensorPosition(Constants.kTalonPidIdx) >= Constants.kArmBarEncoderLimitUp);
	}

	
    public String toString() 
    {
		return String.format("%s, Target: %4.1f, FilteredTarget %4.1f, Angle: %4.1f, PIDError: %7.1f, Motor%%Output: %6.3f, MotorCurrent: %5.1f, LimitSwitch: %d",
				state.toString(), armBarState.getTargetAngleDeg(), armBarState.getFilteredTargetAngleDeg(), armBarState.getAngleDeg(),
				armBarState.getPidError(), armBarState.getMotorPercentOutput(), armBarState.getMotorCurrent(), armBarState.isLimitSwitchTriggered() ? 1 : 0);
    }
}
