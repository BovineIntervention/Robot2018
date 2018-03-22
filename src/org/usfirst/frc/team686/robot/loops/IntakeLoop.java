package org.usfirst.frc.team686.robot.loops;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.IntakeState;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Spark;

public class IntakeLoop implements Loop
{
	private static IntakeLoop instance = new IntakeLoop();
	public static IntakeLoop getInstance() { return instance; }

	private IntakeState intakeState = IntakeState.getInstance();
	
	private static Spark lMotor;
	private static Spark rMotor;
	
	private static DoubleSolenoid grabber;
	
	public double lVelocity;
	public double rVelocity;
	public Value solenoidValue = DoubleSolenoid.Value.kOff;
	
	public enum IntakeModeEnum { STOP, INTAKE, OUTTAKE, HOLD; }
	IntakeModeEnum intakeMode = IntakeModeEnum.STOP; 
	
	public boolean grabberInFlag = false;
	
	public IntakeLoop()
	{
		System.out.println("IntakeLoop constructor");
		
		// Configure Motor Controllers
		lMotor = new Spark(Constants.kLeftIntakePwmChannel);
		rMotor = new Spark(Constants.kRightIntakePwmChannel);
		lMotor.setInverted(Constants.kIntakeLeftMotorInverted);
		rMotor.setInverted(Constants.kIntakeRightMotorInverted);
		
		solenoidValue = DoubleSolenoid.Value.kOff;
		grabber = new DoubleSolenoid(0, Constants.kIntakeSolenoidForwardChannel, Constants.kIntakeSolenoidReverseChannel);
		
		stop();
	}
	
	
	public void startIntake() { intakeMode = IntakeModeEnum.INTAKE; }
	public void stopIntake() { intakeMode = IntakeModeEnum.STOP; }
	
	public void startHold() {  intakeMode = IntakeModeEnum.HOLD; }
	
	public void startOuttake() {intakeMode = IntakeModeEnum.OUTTAKE; }
	public void stopOuttake() { intakeMode = IntakeModeEnum.STOP; }
	
	public void grabberIn() { grabberInFlag = true;	}
	public void grabberOut() { grabberInFlag = false; }
	public void grabberToggle() { grabberInFlag = !grabberInFlag; }
	

	public void stop()
	{
		intakeMode = IntakeModeEnum.STOP;
		lMotor.set(0);
		rMotor.set(0);
		grabber.set(DoubleSolenoid.Value.kOff);
	}
	
	@Override
	public void onStart() 
	{
		stopIntake();
		stopOuttake();
		grabberIn();
	}

	@Override
	public void onLoop() 
	{
		//System.out.println("IntakeLoop onLoop");
		
		switch (intakeMode)
		{
		case INTAKE:
			lVelocity = Constants.kIntakeSpeed;
			rVelocity = Constants.kIntakeSpeed;
			break;

		case HOLD:
			lVelocity = Constants.kIntakeHoldSpeed;
			rVelocity = Constants.kIntakeHoldSpeed;
			break;
			
		case OUTTAKE:
			lVelocity = Constants.kOuttakeSpeed;
			rVelocity = Constants.kOuttakeSpeed;
			break;
			
		case STOP:
			lVelocity = 0;
			rVelocity = 0;
			break;
		}
		

		
		if (!grabberInFlag)
			solenoidValue = DoubleSolenoid.Value.kReverse;
		else
			solenoidValue = DoubleSolenoid.Value.kForward;
			
		
		
		lMotor.set(lVelocity);
		rMotor.set(rVelocity);
		grabber.set(solenoidValue);
		
		System.out.println(toString());
	}

	@Override
	public void onStop() {
		stop();
	}	
	
	
	public void getStatus()
	{
		intakeState.setLeftVelocity(lVelocity);
		intakeState.setRightVelocity(rVelocity);
		
		intakeState.setSolenoidValue(solenoidValue);
	}
	
	
    public String toString() 
    {
    	return String.format("intakeMode: %s, lMotor: %5.2f, rMotor: %5.2f, Grabber: %s", intakeMode, lMotor.get(), rMotor.get(), grabber.get().name());
    }
}
