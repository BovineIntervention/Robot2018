package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.ArmBarState;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
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
	
	public double position;
	public double target;
	public double filteredTarget;

	public double error = 0.0;
	public double dError = 0.0;
	public double iError = 0.0;
	public double lastError = 0.0;
	
	public double voltage = 0.0;
	
	public TalonSRX talon;
	public DigitalInput limitSwitch;
	
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
				
		// Configure Encoder
		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);	// configure for closed-loop PID
		talon.setSensorPhase(true);
		talon.setInverted(true);

		// Configure Limit Switch
		talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.kTalonTimeoutMs);
		talon.overrideLimitSwitchesEnable(true); 	// enable limit switch
		
		disable();
	}
	

	public void enable(){ enable = true; }
	
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
	
	public void calibrateAngleDeg(double _angleDeg) 
	{
		talon.setSelectedSensorPosition( angleDegToEncoderUnits(_angleDeg), Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
	}
	
	public boolean getLimitSwitchDuringZeroing()
	{
		double elapsedZeroingTime = Timer.getFPGATimestamp() - startZeroingTime;
		double maxZeroingTime = Constants.kElevatorMaxHeightLimit / Constants.kElevatorZeroingVelocity + 2.0;
		
		return (armBarState.isLimitSwitchTriggered() || 
				(armBarState.getMotorCurrent() > Constants.kArmBarMotorStallCurrentThreshold) ||
				elapsedZeroingTime > maxZeroingTime); 
	}
	
	
	
	@Override
	public void onStart() {
		state = ArmBarStateEnum.UNINITIALIZED;
		nextState = ArmBarStateEnum.UNINITIALIZED;
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
				
		//System.out.println(toString());
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
			filteredTarget = position;				// hold position
			if (enabled)
			{
				nextState = ArmBarStateEnum.CALIBRATING;	// when enabled, state ZEROING
				talon.configReverseSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(false);	// disable soft limit switches during zeroing
			}
			break;
			
		case CALIBRATING:
			// slowly move up towards limit switch
			filteredTarget += (Constants.kArmBarZeroingVelocity * Constants.kLoopDt);
			
			if (limitSwitchTriggered)
			{
				// CALIBRATING is done when limit switch is hit
				calibrateAngleDeg(Constants.kArmBarUpAngleDeg);	// write new position to Talon
				position = Constants.kArmBarUpAngleDeg;		// override position before limit switch was hit
				setTarget(position);							// initial goal is to stay in the same position
				filteredTarget = position;					// initial goal is to stay in the same position
				
				// set soft limits
				talon.configReverseSoftLimitThreshold( angleDegToEncoderUnits(Constants.kArmBarDownAngleDeg), Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitThreshold( angleDegToEncoderUnits(Constants.kArmBarUpAngleDeg),   Constants.kTalonTimeoutMs);
				talon.configReverseSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(true);	// enable soft limit switches
								
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
		voltage = Math.min(Constants.kMaxArmBarVoltage, Math.max(-Constants.kMaxArmBarVoltage, voltage));
		
		if (limitSwitchTriggered)
			voltage = Math.min(voltage, 0.0);	// do not let elevator continue up when at limit switch

		return voltage;
	}	
	
	public double encoderUnitsToAngleDeg(int _encoderUnits)
	{
		return _encoderUnits / Constants.kArmBarEncoderUnitsPerDeg;
	}
	
	public int angleDegToEncoderUnits(double _deg)
	{
		return (int)(_deg * Constants.kArmBarEncoderUnitsPerDeg);
	}
	
	
	
	public void getStatus()
	{
		// read status once per update
		armBarState.setAngleDeg( encoderUnitsToAngleDeg( talon.getSelectedSensorPosition(Constants.kTalonPidIdx) ) );		

		armBarState.setMotorPercentOutput( talon.getMotorOutputPercent() );
		armBarState.setMotorCurrent( talon.getOutputCurrent() );

		armBarState.setLimitSwitchTriggered( talon.getSensorCollection().isFwdLimitSwitchClosed() );
		
//		System.out.println(talon.getSelectedSensorPosition(Constants.kTalonPidIdx) + ",  " + talon.getMotorOutputPercent()  + ",  " + talon.getOutputCurrent() + ",  " + talon.getSensorCollection().isFwdLimitSwitchClosed());;
		
	}

	
    public String toString() 
    {
		return String.format("%s, Target: %4.1f, FilteredTarget %4.1f, Angle: %4.1f, PIDError: %7.1f, Motor%%Output: %6.3f, MotorCurrent: %5.1f, LimitSwitch: %d",
				state.toString(), armBarState.getTargetAngleDeg(), armBarState.getFilteredTargetAngleDeg(), armBarState.getAngleDeg(),
				armBarState.getPidError(), armBarState.getMotorPercentOutput(), armBarState.getMotorCurrent(), armBarState.isLimitSwitchTriggered() ? 1 : 0);
    }
}
