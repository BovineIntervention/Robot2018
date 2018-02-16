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
	
	public enum ElevatorState { UNINITIALIZED, ZEROING, RUNNING, ESTOPPED; }
	private ElevatorState state = ElevatorState.UNINITIALIZED;
	private ElevatorState nextState = ElevatorState.UNINITIALIZED;
	
	public boolean enabled = false;

    public double position;
	public double goal;
	public double filteredGoal;

	private static DigitalInput hallEffect;
	private static TalonSRX talon;
	private int kSlotIdx= 0;
	
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
		
		// configure position loop PID 
        talon.config_kF(kSlotIdx, Constants.kElevatorKf, Constants.kTalonTimeoutMs); 
        talon.config_kP(kSlotIdx, Constants.kElevatorKp, Constants.kTalonTimeoutMs); 
        talon.config_kI(kSlotIdx, Constants.kElevatorKi, Constants.kTalonTimeoutMs); 
        talon.config_kD(kSlotIdx, Constants.kElevatorKd, Constants.kTalonTimeoutMs); 
        talon.config_IntegralZone(kSlotIdx, Constants.kElevatorIZone, Constants.kTalonTimeoutMs); 
        talon.configAllowableClosedloopError(kSlotIdx, Constants.kElevatorAllowableError, Constants.kTalonTimeoutMs); 
        talon.selectProfileSlot(kSlotIdx, Constants.kTalonPidIdx); 
 		
        talon.configNominalOutputForward(+Constants.kMinElevatorVoltage/Constants.kNominalBatteryVoltage, Constants.kTalonTimeoutMs);
        talon.configNominalOutputReverse(-Constants.kMinElevatorVoltage/Constants.kNominalBatteryVoltage, Constants.kTalonTimeoutMs);
        talon.configPeakOutputForward(+Constants.kMaxElevatorVoltage/Constants.kNominalBatteryVoltage, Constants.kTalonTimeoutMs);
        talon.configPeakOutputReverse(-Constants.kMaxElevatorVoltage/Constants.kNominalBatteryVoltage, Constants.kTalonTimeoutMs);
        
		hallEffect = new DigitalInput(Constants.kHallEffectSensorId);
		
		disable();
	}
	

	public void enable(){ enabled = true; }
	
	public void disable()
	{
		enabled = false; 
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
			filteredGoal -= (Constants.kElevatorZeroingVelocity * Constants.kLoopDt);
			
			if (limitSwitch)
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
			filteredGoal = goal;
			break;
			
		default:
			nextState = ElevatorState.UNINITIALIZED;
		}

		talon.set(ControlMode.Position, filteredGoal * Constants.kElevatorEncoderUnitsPerInch);
		
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() 
    {
    	return String.format("%s, Goal: %.1f, FiltGoal = %.1f, Pos: %.1f, LimSwitch: %d", state.toString(), goal, filteredGoal, getPosition(), getLimitSwitch() ? 1 : 0);
    }

	
}
