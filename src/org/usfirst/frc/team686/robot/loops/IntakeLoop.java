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
	
	public enum IntakeStateEnum { STOP, INTAKE, OUTTAKE; }
	public IntakeStateEnum state = IntakeStateEnum.STOP;
	public IntakeStateEnum nextState = IntakeStateEnum.STOP;
	
	public boolean enabled = false;
	
	private static Spark leftSpark;
	private static Spark rightSpark;
	
	private static DoubleSolenoid grabber;
	
	public double leftVelocity;
	public double rightVelocity;
	public Value solenoidValue;
	
	public IntakeLoop()
	{
		System.out.println("ArmBarLoop constructor");
		
		// Configure Talon
		leftSpark = new Spark(Constants.kLeftIntakeSparkChannel);
		rightSpark = new Spark(Constants.kRightIntakeSparkChannel);

		grabber = new DoubleSolenoid(0, Constants.kIntakeSolenoidForwardChannel, Constants.kIntakeSolenoidReverseChannel);
		
		disable();
	}
	
	
	

	public void enable(){ enabled = true; }
	
	public void disable()
	{
		enabled = false; 
		state = IntakeStateEnum.STOP; 
		nextState = IntakeStateEnum.STOP;
	}
	

	public IntakeStateEnum getState() { return state; }
	
	public void intake(){ state = IntakeStateEnum.INTAKE; }
	
	public void outtake(){ state = IntakeStateEnum.OUTTAKE; }
	
	public void closeGrabber(){ 
		if( state != IntakeStateEnum.OUTTAKE )
			solenoidValue = DoubleSolenoid.Value.kForward; 
	}
	
	public void openGrabber(){ 
		if( state != IntakeStateEnum.OUTTAKE )
			solenoidValue = DoubleSolenoid.Value.kReverse; 
	}
	
	public void stop(){ state = IntakeStateEnum.STOP; }
	
	@Override
	public void onStart() {
		state = IntakeStateEnum.STOP;
	}

	@Override
	public void onLoop() 
	{
		
		//leftVelocity = intakeState.getLeftVelocityInchesPerSec();
		//rightVelocity = intakeState.getRightVelocityInchesPerSec();
		//solenoidValue = intakeState.getSolenoidValue();
		
		if (!enabled)
		{
			state = IntakeStateEnum.STOP;
		}
		
		switch (state)
		{
		case STOP:

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
			
			break;
			
		case OUTTAKE:

			leftVelocity = Constants.kOuttakeSpeed;
			rightVelocity = Constants.kOuttakeSpeed;
			
			solenoidValue = DoubleSolenoid.Value.kForward;
			
			break;
			
		default:
			nextState = IntakeStateEnum.STOP;
		}
		
		
		leftSpark.set(leftVelocity);
		rightSpark.set(rightVelocity);
		
		grabber.set(solenoidValue);
		
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
