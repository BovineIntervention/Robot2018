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
	public double offset;

	public double error = 0.0;
	public double dError = 0.0;
	public double iError = 0.0;
	public static double lastError = 0.0;
	
	public double voltage = 0.0;
	
	private static DigitalInput hallEffect;
	private static TalonSRX elevatorTalon;
	
	public ElevatorLoop(){
		
		System.out.println("ElevatorLoop constructor");
		
		//configure Talon
		elevatorTalon = new TalonSRX(Constants.kElevatorTalonId);
		elevatorTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, Constants.kTalonTimeoutMs);
		elevatorTalon.set(ControlMode.PercentOutput, 0.0);
		elevatorTalon.setNeutralMode(NeutralMode.Brake);
		
		//configure encoder
		elevatorTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);	// configure for closed-loop PID
		elevatorTalon.setSensorPhase(true);
		elevatorTalon.setInverted(true);
		
		hallEffect = new DigitalInput(Constants.kHallEffectSensorId);
		
		enable = false;
	}
	

	public void enable(){ enable = true; }
	public void disable() { enable = false; }
	
	public void setGoal(double goal_){ goal = goal_; }
	public double getGoal () { return goal; } 
	public double getFilteredGoal() { return filteredGoal; }

	public ElevatorState getState() { return state; }
	
	public void stop() { goal = getPosition(); }
	
	public double getPosition() { return position; }
	
	public void setPosition(int inches)  //?? not sure if need to convert to pulses
	{
		elevatorTalon.setSelectedSensorPosition(inches, Constants.kTalonPidIdx, Constants.kTalonTimeoutMs);
	}
	
	public int getPositionFromEncoder(){
		int encoderPosition = elevatorTalon.getSelectedSensorPosition(Constants.kTalonPidIdx);
		return encoderPosition;
	}
	
	public static boolean getLimitSwitch(){ return !hallEffect.get(); }
	
	
	@Override
	public void onStart() {
		state = ElevatorState.UNINITIALIZED;
		nextState = ElevatorState.UNINITIALIZED;
	}

	@Override
	public void onLoop() {
		
		position = getPositionFromEncoder();
		boolean limitSwitch = getLimitSwitch();
		
		double voltage = getVoltage(position, limitSwitch, enable);
		double percentOutput = voltage / Constants.kMaxBatteryVoltage;
		elevatorTalon.set(ControlMode.PercentOutput, percentOutput);
		
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}
	
	public double getVoltage(double encoder, boolean limitTriggered, boolean enabled)
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
			if (enabled)
			{
				nextState = ElevatorState.ZEROING;	// when enabled, state ZEROING
				filteredGoal = encoder;			// initial goal is to stay in the same position
			}
			break;
			
		case ZEROING:
			// update goal to be slightly lowered, limited in velocity
			filteredGoal  -= (Constants.kLoopDt * Constants.kZeroingVelocity);
			
			if (limitTriggered)
			{
				// ZEROING is done with limit switch is done. 
				offset = encoder;					// set offset so that future position is zerored properly
				setPosition(0);
				position = 0.0;
				filteredGoal = 0.0;
				nextState = ElevatorState.RUNNING;	// start running state
			}
			break;
			
		case RUNNING:
			filteredGoal = goal;
			break;
			
		default:
			nextState = ElevatorState.UNINITIALIZED;
		}

		
		position = encoder - offset;		// get true position, after calibrating out any encoder error
		
		error = filteredGoal - position;
		dError = (error - lastError) / Constants.kLoopDt;
		iError += (error * Constants.kLoopDt);
		
		lastError = error;
		
		voltage = Kp * error + Kd * dError + Ki * iError;
		voltage = Math.min(Constants.kMaxBatteryVoltage, Math.max(-Constants.kMaxBatteryVoltage, voltage));
		
		if(limitTriggered)
			voltage = Math.min(voltage, 0.0);
		return voltage;
	}

	

	
}
