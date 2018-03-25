package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ManualAutoStateEnum;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class ElevatorLoop implements Loop{
	
	private static ElevatorLoop instance = new ElevatorLoop();
	public static ElevatorLoop getInstance() { return instance; }

	private ElevatorState elevatorState = ElevatorState.getInstance();
	
	public enum ElevatorStateEnum { UNINITIALIZED, ARM_BAR_DELAY, ZEROING, RUNNING, ESTOPPED; }
	private ElevatorStateEnum state = ElevatorStateEnum.UNINITIALIZED;
	private ElevatorStateEnum nextState = ElevatorStateEnum.UNINITIALIZED;
	
	public boolean enabled = false;
	public boolean manualControls = false;
	
    public double position;
	public double target;

//	private static DigitalInput limitSwitch;
	private static TalonSRX talon;
	private int kSlotIdx= 0;
	
	private double startZeroingTime;
	private int printCnt = 0;
	private final double armBarDelay = 0.5;
	
	public ElevatorLoop()
	{
		System.out.println("ElevatorLoop constructor");
		
		// configure Talon
		talon = new TalonSRX(Constants.kElevatorTalonId);
		talon.set(ControlMode.PercentOutput, 0.0);
		talon.setNeutralMode(NeutralMode.Brake);
		
		// configure encoder
		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
		talon.setSensorPhase(true);
		talon.setInverted(false);
		
		// set relevant frame periods to be at least as fast as periodic rate
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0,  (int)(1000 * Constants.kLoopDt), Constants.kTalonTimeoutMs);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, (int)(1000 * Constants.kLoopDt), Constants.kTalonTimeoutMs);

        // set min and max outputs
        talon.configNominalOutputForward(+Constants.kMinElevatorOutput, Constants.kTalonTimeoutMs);
        talon.configNominalOutputReverse(-Constants.kMinElevatorOutput, Constants.kTalonTimeoutMs);
        talon.configPeakOutputForward(+Constants.kMaxElevatorOutput, Constants.kTalonTimeoutMs);
        talon.configPeakOutputReverse(-Constants.kMaxElevatorOutput, Constants.kTalonTimeoutMs);
		
		// configure position loop PID 
        talon.selectProfileSlot(kSlotIdx, Constants.kTalonPidIdx); 
        talon.config_kF(kSlotIdx, Constants.kElevatorKf, Constants.kTalonTimeoutMs); 
        talon.config_kP(kSlotIdx, Constants.kElevatorKp, Constants.kTalonTimeoutMs); 
        talon.config_kI(kSlotIdx, Constants.kElevatorKi, Constants.kTalonTimeoutMs); 
        talon.config_kD(kSlotIdx, Constants.kElevatorKd, Constants.kTalonTimeoutMs);
        
		// set acceleration and cruise velocity
		talon.configMotionCruiseVelocity((int)Constants.kElevatorCruiseVelocity, Constants.kTalonTimeoutMs);
		talon.configMotionAcceleration((int)Constants.kElevatorAccel, Constants.kTalonTimeoutMs);	

		//talon.configAllowableClosedloopError(kSlotIdx, Constants.kElevatorAllowableError, Constants.kTalonTimeoutMs);
  		
        // limit switch
		//limitSwitch = new DigitalInput(Constants.kElevatorLimitSwitchPwmId);
		
		disable();
	}
	

	public void enable() { enabled = true; }
	
	public void disable()
	{
		enabled = false;
		manualControls = false;
		state = ElevatorStateEnum.UNINITIALIZED; 
		nextState = ElevatorStateEnum.UNINITIALIZED;
	}
	
	public void setTarget(double _target)
    {
		//System.out.printf("Elevator setTarget(%.1f)\n", _target);
        target = Util.limit(_target, Constants.kElevatorMinHeightLimit, Constants.kElevatorMaxHeightLimit);
        manualControls = false;
 	}
	
	public double getTarget() { return target; }
	
	public ElevatorStateEnum getState() { return state; }
	
	public void stop()
	{
		disable();
		talon.set(ControlMode.PercentOutput, 0.0);
	}
	
	public void calibratePosition(double _positionInches) 
	{
		// calibrate encoder to this position
		talon.setSelectedSensorPosition( inchesToEncoderUnits(_positionInches), Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
	}
	
	double manualPercentOutput = 0.5;
	
	public void manualUp()
	{
        manualControls = true;
		talon.set(ControlMode.PercentOutput, +Constants.kElevatorManualOutput);
	}
	
	public void manualDown()
	{
        manualControls = true;
		talon.set(ControlMode.PercentOutput, -Constants.kElevatorManualOutput);
	}
	
	public void manualStop()
	{
        manualControls = true;
		talon.set(ControlMode.PercentOutput, 0.0);
	}
	
	public boolean getLimitSwitchDuringZeroing()
	{
		double elapsedZeroingTime = Timer.getFPGATimestamp() - startZeroingTime;
		double maxZeroingTime = Constants.kElevatorMaxHeightLimit / Constants.kElevatorZeroingVelocity + 2.0;
		
		//System.out.printf("limSwitch: %d, elevCurrent = %.1f, elapsedTime = %.1f\n",  elevatorState.is`Triggered() ? 1 : 0, elevatorState.getMotorCurrent(), elapsedZeroingTime);
		
		return (elevatorState.isLimitSwitchTriggered() || 
				(elevatorState.getMotorCurrent() > Constants.kElevatorMotorStallCurrentThreshold) ||
				elapsedZeroingTime > maxZeroingTime); 
	}
	
	
	public void disableSoftLimits()
	{
		// called when we use manual elevator controls
		talon.configReverseSoftLimitEnable(false, Constants.kTalonTimeoutMs);
		talon.configForwardSoftLimitEnable(false, Constants.kTalonTimeoutMs);
		talon.overrideLimitSwitchesEnable(false);	// disable soft limit switches
	}
	
	
	public void enableSoftLimits()
	{
		// called when we leave manual elevator controls
		
		// if current position is below limits, reset limits
		double positionInches = elevatorState.getPositionInches();
		if (positionInches < Constants.kElevatorMinHeightLimit)
		{
			calibratePosition(positionInches);
		}
		
		// re-enable soft limit switches
		talon.configReverseSoftLimitThreshold(inchesToEncoderUnits(Constants.kElevatorMinHeightLimit), Constants.kTalonTimeoutMs);
		talon.configForwardSoftLimitThreshold(inchesToEncoderUnits(Constants.kElevatorMaxHeightLimit), Constants.kTalonTimeoutMs);
		talon.configReverseSoftLimitEnable(true, Constants.kTalonTimeoutMs);
		talon.configForwardSoftLimitEnable(true, Constants.kTalonTimeoutMs);
		talon.overrideLimitSwitchesEnable(true);	
	}
	
	
	@Override
	public void onStart() 
	{
		state = ElevatorStateEnum.UNINITIALIZED;
		nextState = ElevatorStateEnum.UNINITIALIZED;
	}

	
	
	
	@Override
	public void onLoop()
	{
		// read status of elevator
		getStatus();
		position = elevatorState.getPositionInches();
		
		// transition states
		state = nextState;
		
		// start over if ever disabled
		if (!enabled)
			state = ElevatorStateEnum.UNINITIALIZED;

		if (manualControls)
		{
			// do not run remaining function when being manually controlled
			// we need to keep talon in PercentOutput mode, controlled by joystick buttons
			// automatic control resumes when a new target is set
			// will resume in same state (presumably RUNNING) when a new target is set
			return;	
		}
		
		switch (state)
		{
		case UNINITIALIZED:
			// initial state.  stay here until enabled
			target = position;			// initial goal is to stay in the same position
			talon.set(ControlMode.PercentOutput, 0.0);
			
			if (enabled)
			{
				talon.configReverseSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(false);	// disable soft limit switches during zeroing
				
				startZeroingTime = Timer.getFPGATimestamp();
				nextState = ElevatorStateEnum.ARM_BAR_DELAY;
			}
			break;
			
		case ARM_BAR_DELAY:
			double elapsedZeroingTime = Timer.getFPGATimestamp() - startZeroingTime;
			if (elapsedZeroingTime > armBarDelay)
			{
				startZeroingTime = Timer.getFPGATimestamp();
				nextState = ElevatorStateEnum.ZEROING;
			}
	
			
		case ZEROING:
			// update goal to be slightly lowered, limited in velocity
			target -= (Constants.kElevatorZeroingVelocity * Constants.kLoopDt);

			// simple P loop during zeroing (no motion magic)
			double Kp = 1.0;
			double error = target - position;
			double percentOutput = Kp * error;
			percentOutput = Util.limit(percentOutput, -1.0, +1.0);
			talon.set(ControlMode.PercentOutput, percentOutput);
			
			if (getLimitSwitchDuringZeroing())
			{
				System.out.println("Elevator Limit Switch Triggered");
				
				// ZEROING is done with limit switch is triggered
				percentOutput = 0.0;
				position = Constants.kGroundHeight;
				calibratePosition(position);
				target = position;
				
				// set soft limits
				talon.configReverseSoftLimitThreshold(inchesToEncoderUnits(Constants.kElevatorMinHeightLimit), Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitThreshold(inchesToEncoderUnits(Constants.kElevatorMaxHeightLimit), Constants.kTalonTimeoutMs);
				talon.configReverseSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(true);	// enable soft limit switches
				
				nextState = ElevatorStateEnum.RUNNING;	// start running state
			}
			
			
			System.out.printf("target: %.1f, position: %.1f, error: %.1f, percentOutput: %.1f\n", target, position, error, percentOutput);
			
			talon.set(ControlMode.PercentOutput, percentOutput);
			break;
			
		case RUNNING:
			// Talon motion magic will manage a trapezoidal motion profile to move to goal
			talon.set(ControlMode.MotionMagic, inchesToEncoderUnits(target));
			break;
			
		default:
			nextState = ElevatorStateEnum.UNINITIALIZED;
		}
		
//		printCnt++;
//		if (printCnt==5)
//		{
//			System.out.println(toString());
//			printCnt = 0;
//		}
	}

	public void getStatus()
	{
		// read status once per update
		elevatorState.setPositionInches( encoderUnitsToInches( talon.getSelectedSensorPosition(Constants.kTalonPidIdx) ) );
		elevatorState.setVelocityInchesPerSec( encoderVelocityToInchesPerSec(talon.getSelectedSensorVelocity(Constants.kTalonPidIdx)) );
		
		if (talon.getControlMode() == ControlMode.MotionMagic)
		{
			elevatorState.setTrajectoryTargetInches( encoderUnitsToInches(talon.getClosedLoopTarget(Constants.kTalonPidIdx)) );
			elevatorState.setTrajectoryPositionInches( encoderUnitsToInches(talon.getActiveTrajectoryPosition()) );
			elevatorState.setTrajectoryVelocityInchesPerSec( encoderVelocityToInchesPerSec(talon.getActiveTrajectoryVelocity()) );
			elevatorState.setPidError( talon.getClosedLoopError(Constants.kTalonPidIdx) );
		}
		
		elevatorState.setMotorPercentOutput( talon.getMotorOutputPercent() );
		elevatorState.setMotorCurrent( talon.getOutputCurrent() );

//		elevatorState.setLimitSwitchTriggered( !limitSwitch.get() );
		elevatorState.setLimitSwitchTriggered( false );
	}
	
	public static double encoderUnitsToInches(int _encoderUnits)
	{
		return _encoderUnits / Constants.kElevatorEncoderUnitsPerInch;
	}
	
	public static int inchesToEncoderUnits(double _inches)
	{
		return (int)(_inches * Constants.kElevatorEncoderUnitsPerInch);
	}
	
	public double encoderVelocityToInchesPerSec(int _encoderVelocity)
	{
		// extra factor of 10 because velocity is reported over 100ms periods 
		return _encoderVelocity * 10.0 / Constants.kElevatorEncoderUnitsPerInch;
	}
	
	@Override
	public void onStop() {
		stop();
	}
	
	public String toString() 
    {
		return String.format("%s, Target: %4.1f, TrajPos %4.1f, TrajVel: %5.1f, SensorPos: %4.1f, SensorVel: %5.1f, PIDError: %7.1f, Motor%%Output: %6.3f, MotorCurrent: %5.1f, LimitSwitch: %d",
				state.toString(), elevatorState.getTrajectoryTargetInches(), elevatorState.getTrajectoryPositionInches(), elevatorState.getTrajectoryVelocityInchesPerSec(),
				elevatorState.getPositionInches(), elevatorState.getVelocityInchesPerSec(), elevatorState.getPidError(), 
				elevatorState.getMotorPercentOutput(), elevatorState.getMotorCurrent(), elevatorState.isLimitSwitchTriggered() ? 1 : 0);
    }

	
}
