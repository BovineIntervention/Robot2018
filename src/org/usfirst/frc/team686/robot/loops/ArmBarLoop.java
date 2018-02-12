package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop.ElevatorState;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class ArmBarLoop implements Loop {
	
	private static ArmBarLoop instance = new ArmBarLoop();
	public static ArmBarLoop getInstance() { return instance; }
	
	public enum ArmBarState { UNINITIALIZED, ZEROING, RUNNING; }
	
	private ArmBarState state = ArmBarState.UNINITIALIZED;
	private ArmBarState nextState = ArmBarState.UNINITIALIZED;
	
	private static boolean enabled = false;
	
	TalonSRX armBarTalon;
	ControlMode armBarControlMode;
	DigitalInput forwardLimitSwitch;
	DigitalInput reverseLimitSwitch;
	Counter forwardCounter;
	Counter reverseCounter;
	
	public ArmBarLoop(){
		
		
		armBarTalon = new TalonSRX(Constants.kArmBarTalonId);
		armBarControlMode = ControlMode.Velocity;
		
		armBarTalon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		armBarTalon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
	
		armBarTalon.overrideLimitSwitchesEnable(true);
		
		armBarTalon.getSensorCollection().isFwdLimitSwitchClosed();
		armBarTalon.getSensorCollection().isRevLimitSwitchClosed();
		
		armBarTalon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0, 10);
		
		forwardLimitSwitch = new DigitalInput(1);
		reverseLimitSwitch = new DigitalInput(2);
		
		forwardCounter = new Counter(forwardLimitSwitch);
		reverseCounter = new Counter(reverseLimitSwitch);
	}
	
	
	public double getVelocity(boolean reverseLimitTriggered, boolean forwardLimitTriggered, boolean enabled)
	{
		// transition states
		state = nextState;
		double velocity = 0.0;
		// start over if ever disabled
		if (!enabled)
		{
			state = ArmBarState.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:
			// initial state.  stay here until enabled
			velocity = 0.0;
			if (enabled)
			{
				nextState = ArmBarState.ZEROING;	// when enabled, state ZEROING
			}
			break;
			
		case ZEROING:

			velocity = -Constants.kArmBarVelocity;
			if (reverseLimitTriggered)
			{
				// ZEROING is done with limit switch is done.  
				nextState = ArmBarState.RUNNING;	// start running state
			}
			break;
			
		case RUNNING:
			velocity = Constants.kArmBarVelocity;
			if(forwardLimitTriggered){
				nextState = ArmBarState.UNINITIALIZED;
			}
			break;
			
		default:
			nextState = ArmBarState.UNINITIALIZED;
		}

		
		return velocity;
	}
	
	

	public boolean isForwardSwitchSet(){
		return forwardCounter.get() > 0;
	}
	
	public boolean isReverseSwitchSet(){
		return reverseCounter.get() > 0;
	}
	
	public void initializeForwardCounter(){
		forwardCounter.reset();
	}
	
	public void initializeReverseCounter(){
		reverseCounter.reset();
	}
	
	public void enable(){
		enabled = true;
		initializeForwardCounter();
		initializeReverseCounter();
	}


	@Override
	public void onStart() {
		
		
	}


	@Override
	public void onLoop() {
		// TODO Auto-generated method stub
		double velocity = getVelocity(isReverseSwitchSet(), isForwardSwitchSet(), enabled);
		
		armBarTalon.set(armBarControlMode, velocity);
	}


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}
	


	
}
