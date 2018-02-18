package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.command_status.IntakeState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.IntakeLoop;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class Intake extends Subsystem {
	
	private static Intake instance = new Intake();
	public static Intake getInstance() { return instance; }
	
	public IntakeLoop intakeLoop = IntakeLoop.getInstance();
	
	private Intake()
	{
		stop();
	}
	
	public boolean intakeButton = false;
	public boolean outtakeButton = false;
	public boolean grabberButton = false;
	
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	
	

	public void processInputs(boolean _newIntakeButton, boolean _newOuttakeButon, boolean _newGrabberButton)
	{
		if (_newOuttakeButon != outtakeButton)
		{
			outtakeButton = _newOuttakeButon;
			if (outtakeButton)
				intakeLoop.startOuttake();
			else
				intakeLoop.stopOuttake();
		}

		// intake has higher priority than outtake
		if (_newIntakeButton != intakeButton)
		{
			intakeButton = _newIntakeButton;
			if (intakeButton && (elevatorArmBar.state == ElevatorArmBarStateEnum.GROUND))
			{
				intakeLoop.grabberOut();
				intakeLoop.startIntake();
			}
			else
				intakeLoop.stopIntake();
		}

		if (_newGrabberButton != grabberButton)
		{
			grabberButton = _newGrabberButton;
			if (grabberButton && (elevatorArmBar.state == ElevatorArmBarStateEnum.GROUND))
				intakeLoop.grabberToggle();
		}

		
		// grabber is always in and intake stopped when off the ground
		if (elevatorArmBar.state != ElevatorArmBarStateEnum.GROUND)
		{
			intakeLoop.grabberIn();
			intakeLoop.stopIntake();
		}

	}
	
	
	public void startOuttake(){ intakeLoop.startOuttake(); }
	public void stopOuttake(){ intakeLoop.stopOuttake(); }
	
	@Override
	public void stop() {
		intakeLoop.stop();
		
	}

	@Override
	public void zeroSensors() {
		// TODO Auto-generated method stub
	}

	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			try 
			{
				IntakeState.getInstance().getLogger().log();
			} catch (NullPointerException e) {
				
			}
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
