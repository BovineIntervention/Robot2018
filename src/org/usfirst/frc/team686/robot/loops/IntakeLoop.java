package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.IntakeState;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop.ElevatorState;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Spark;

public class IntakeLoop implements Loop
{
	private static IntakeLoop instance = new IntakeLoop();
	public static IntakeLoop getInstance() { return instance; }

	private IntakeState intakeState = IntakeState.getInstance();
	
	public enum IntakeStateEnum { UNINITIALIZED, INTAKE, OUTTAKE; }
	public IntakeStateEnum state = IntakeStateEnum.UNINITIALIZED;
	public IntakeStateEnum nextState = IntakeStateEnum.UNINITIALIZED;
	
	public boolean enable = false;
	
	private static Spark leftSpark;
	private static Spark rightSpark;
	
	private static DoubleSolenoid solenoid;
	
	public double velocity;
	public Value solenoidValue;
	
	public IntakeLoop()
	{
		System.out.println("ArmBarLoop constructor");
		
		// Configure Talon
		leftSpark = new Spark(Constants.kLeftIntakeSparkChannel);
		rightSpark = new Spark(Constants.kRightIntakeSparkChannel);

		solenoid = new DoubleSolenoid(0, Constants.kIntakeSolenoidForwardChannel, Constants.kIntakeSolenoidReverseChannel);
		
		disable();
	}
	
	
	

	public void enable(){ enable = true; }
	
	public void disable()
	{
		enable = false; 
		state = IntakeStateEnum.UNINITIALIZED; 
		nextState = IntakeStateEnum.UNINITIALIZED;
	}
	

	public IntakeStateEnum getState() { return state; }
	
	public void intake(){ state = IntakeStateEnum.INTAKE; }
	
	public void outtake(){ state = IntakeStateEnum.OUTTAKE; }
	
	public void stop(){ state = IntakeStateEnum.UNINITIALIZED; }
	
	@Override
	public void onStart() {
		state = IntakeStateEnum.UNINITIALIZED;
	}

	@Override
	public void onLoop() 
	{
		
		
		double velocity = getVelocity(enable);
		leftSpark.set(velocity);
		rightSpark.set(velocity);
		
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	}	
	
	
	
	public double getVelocity(boolean enabled)
	{
		// start over if ever disabled
		if (!enabled)
		{
			state = IntakeStateEnum.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:

			if (enabled)
			{
				velocity = 0.0;
			}
			break;
			
		case INTAKE:

			velocity = Constants.kIntakeSpeed;
			break;
			
		case OUTTAKE:

			velocity = Constants.kOuttakeSpeed;
			break;
			
		default:
			nextState = IntakeStateEnum.UNINITIALIZED;
		}
		
		return velocity;
	}	
	
	public Value getValue(boolean enabled){
		if (!enabled)
		{
			state = IntakeStateEnum.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:

			if (enabled)
			{
				solenoidValue = DoubleSolenoid.Value.kOff;
			}
			break;
			
		case INTAKE:

			solenoidValue = DoubleSolenoid.Value.kForward;
			break;
			
		case OUTTAKE:

			solenoidValue = DoubleSolenoid.Value.kReverse;
			break;
			
		default:
			nextState = IntakeStateEnum.UNINITIALIZED;
		}
		
		return solenoidValue;
	}
	
    public String toString() 
    {
    	return null;
    	//return String.format("%s, Enc: %d, Pos: %.1f, LimSwitch: %d, Goal: %.1f, FiltGoal = %.1f, e = %.1f, de = %.1f, ie = %.1f, voltage = %.1f", state.toString(), getEncoder(), getPosition(), getLimitSwitch() ? 1 : 0, goal, filteredGoal, error, dError, iError, voltage);
    }
}
