package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorLoop implements Loop{
	
	private static ElevatorLoop instance = new ElevatorLoop();
	public static ElevatorLoop getInstance() { return instance; }
	
	public double Kf = Constants.kElevatorKf;
	public double Kp = Constants.kElevatorKp;
	public double Kd = Constants.KElevatorKd;
	public double Ki = Constants.KElevatorKi;
	
	public enum ElevatorState { UNINITIALIZED, ZEROING, RUNNING, ESTOPPED; }
	private ElevatorState state = ElevatorState.UNINITIALIZED;
	private ElevatorState nextState = ElevatorState.UNINITIALIZED;
	
	public boolean enable = false;

    public double position;
	public double goal;
	public double filteredGoal;

	public double error = 0.0;
	public double dError = 0.0;
	public double iError = 0.0;
	public static double lastError = 0.0;
	
	public double voltage = 0.0;
	
	private static DigitalInput hallEffect;
	private static TalonSRX talon;
	
	public ElevatorLoop(){
		
		System.out.println("ElevatorLoop constructor");
		
		//configure Talon
		talon = new TalonSRX(Constants.kElevatorTalonId);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, Constants.kTalonTimeoutMs);
		talon.set(ControlMode.PercentOutput, 0.0);
		talon.setNeutralMode(NeutralMode.Brake);
		
		//configure encoder
		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);	// configure for closed-loop PID
		talon.setSensorPhase(true);
		talon.setInverted(false);
		
		hallEffect = new DigitalInput(Constants.kHallEffectSensorId);
		
		disable();
	}
	

	public void enable(){ enable = true; }
	
	public void disable()
	{
		enable = false; 
		state = ElevatorState.UNINITIALIZED; 
		nextState = ElevatorState.UNINITIALIZED;
	}
	
	public void setGoal(double _goal)
    {
        // limit goal
        goal = Math.min(Constants.kElevatorMaxHeightLimit, Math.max(Constants.kElevatorMinHeightLimit, _goal));
	}
	public double getGoal () { return goal; } 
	public double getFilteredGoal() { return filteredGoal; }

	public ElevatorState getState() { return state; }
	
	public void stop() { goal = getPosition(); }
	
	public double getPosition() { return position; }
	
	public void setPosition(double inches) 
	{
		int encoderEdges = (int)(inches * Constants.kElevatorEncoderUnitsPerInch);
		talon.setSelectedSensorPosition(encoderEdges, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
	}
	
	public int getEncoder()
	{
		int encoderEdges = talon.getSelectedSensorPosition(Constants.kTalonPidIdx);
		return encoderEdges;
	}
	
	public double getPositionFromEncoder()
	{
		double encoderPosition = getEncoder() / Constants.kElevatorEncoderUnitsPerInch;
		return encoderPosition;
	}
	
	public boolean getLimitSwitch(){ return !hallEffect.get(); }
	
	
	@Override
	public void onStart() 
	{
		state = ElevatorState.UNINITIALIZED;
		nextState = ElevatorState.UNINITIALIZED;
	}

	@Override
	public void onLoop()
	{
		position = getPositionFromEncoder();
		boolean limitSwitch = getLimitSwitch();
		
		double voltage = getVoltage(position, limitSwitch, enable);
		double percentOutput = voltage / Constants.kNominalBatteryVoltage;
		talon.set(ControlMode.PercentOutput, percentOutput);
		
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}
	
	public double getVoltage(double position, boolean limitTriggered, boolean enabled)
	{
	
		// transition states
		state = nextState;
		// start over if ever disabled
		if (!enabled)
		{
			state = ElevatorState.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:
			// initial state.  stay here until enabled
			filteredGoal = position;			// initial goal is to stay in the same position
			if (enabled)
			{
				nextState = ElevatorState.ZEROING;	// when enabled, state ZEROING
				talon.configReverseSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(false, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(false);	// disable soft limit switches during zeroing
			}
			break;
			
		case ZEROING:
			// update goal to be slightly lowered, limited in velocity
			Kd = 0.0;		// stop jittering during calibration			
			filteredGoal  -= (Constants.kElevatorZeroingVelocity * Constants.kLoopDt);
			
			if (limitTriggered)
			{
				System.out.println("Elevator Limit Switch Triggered");
				
				// ZEROING is done with limit switch is triggered 
				setPosition(Constants.kGroundHeight);
				position = Constants.kGroundHeight;
				setGoal(position);
				filteredGoal = position;
				
				// set soft limits
				talon.configReverseSoftLimitThreshold((int)(Constants.kElevatorMinHeightLimit * Constants.kElevatorEncoderUnitsPerInch), Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitThreshold((int)(Constants.kElevatorMaxHeightLimit * Constants.kElevatorEncoderUnitsPerInch), Constants.kTalonTimeoutMs);
				talon.configReverseSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.configForwardSoftLimitEnable(true, Constants.kTalonTimeoutMs);
				talon.overrideLimitSwitchesEnable(true);	// enable soft limit switches
				
				nextState = ElevatorState.RUNNING;	// start running state
			}
			break;
			
		case RUNNING:
			Kd = Constants.KElevatorKd;		// use Kd during running state
			filteredGoal = goal;
			break;
			
		default:
			nextState = ElevatorState.UNINITIALIZED;
		}

		// PID Loop
		error = filteredGoal - position;
		dError = (error - lastError) / Constants.kLoopDt;
		iError += (error * Constants.kLoopDt);

		lastError = error;
		
		voltage = Kp * error + Kd * dError + Ki * iError;
		voltage = Math.min(Constants.kMaxElevatorVoltage, Math.max(-Constants.kMaxElevatorVoltage, voltage));
		
		if(limitTriggered)
			voltage = Math.max(voltage, 0.0);
		
		return voltage;
	}

	public String toString() 
    {
    	return String.format("%s, Enc: %d, Pos: %.1f, LimSwitch: %d, Goal: %.1f, FiltGoal = %.1f, e = %.1f, de = %.1f, ie = %.1f, voltage = %.1f", state.toString(), getEncoder(), getPosition(), getLimitSwitch() ? 1 : 0, goal, filteredGoal, error, dError, iError, voltage);
    }

	
}
