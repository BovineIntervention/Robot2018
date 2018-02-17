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
	
	public boolean enabled = false;
	
	private static Spark leftSpark;
	private static Spark rightSpark;
	
	private static DoubleSolenoid solenoid;
	
	public double leftVelocity;
	public double rightVelocity;
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
	
	
	

	public void enable(){ enabled = true; }
	
	public void disable()
	{
		enabled = false; 
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
		
		//leftVelocity = intakeState.getLeftVelocityInchesPerSec();
		//rightVelocity = intakeState.getRightVelocityInchesPerSec();
		//solenoidValue = intakeState.getSolenoidValue();
		
		if (!enabled)
		{
			state = IntakeStateEnum.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:

			if (enabled)
			{
				leftVelocity = 0.0;
				rightVelocity = 0.0;
				
				solenoidValue = DoubleSolenoid.Value.kOff;
			}
			break;
			
		case INTAKE:

			leftVelocity = Constants.kIntakeSpeed;
			rightVelocity = Constants.kIntakeSpeed;
			
			solenoidValue = DoubleSolenoid.Value.kForward;
			break;
			
		case OUTTAKE:

			leftVelocity = Constants.kOuttakeSpeed;
			rightVelocity = Constants.kOuttakeSpeed;
			
			solenoidValue = DoubleSolenoid.Value.kReverse;
			
			break;
			
		default:
			nextState = IntakeStateEnum.UNINITIALIZED;
		}
		
		
		leftSpark.set(leftVelocity);
		rightSpark.set(rightVelocity);
		
		solenoid.set(solenoidValue);
		
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	}	
	
	
	public void getStatus(){
		
		intakeState.setLeftVelocityInchesPerSec(leftVelocity);
		intakeState.setRightVelocityInchesPerSec(rightVelocity);
		
		//intakeState.setLeftMotorCurrent(leftSpark.); not sure how to get current from Sparks
		
		intakeState.setSolenoidValue(solenoidValue);
		
	}
	
	
    public String toString() 
    {
    	return null;
    	//return String.format("%s, Enc: %d, Pos: %.1f, LimSwitch: %d, Goal: %.1f, FiltGoal = %.1f, e = %.1f, de = %.1f, ie = %.1f, voltage = %.1f", state.toString(), getEncoder(), getPosition(), getLimitSwitch() ? 1 : 0, goal, filteredGoal, error, dError, iError, voltage);
    }
}
