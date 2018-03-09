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
	
	

	public void processInputs(boolean _newIntakeButton, boolean _newOuttakeButon, boolean _newGrabberButton)
	{
		//if (_newOuttakeButon != outtakeButton)
		{
			outtakeButton = _newOuttakeButon;
			if (outtakeButton)
				intakeLoop.startOuttake();
			else
			{
				if (!_newIntakeButton)
					intakeLoop.stopOuttake();
			}
		}

		if (_newGrabberButton != grabberButton)
		{
			grabberButton = _newGrabberButton;
			if (grabberButton)
				intakeLoop.grabberToggle();
		}

		// intake start/stop at ground state taken care of by ElevatorArmBar
		
	}
	
	
	public void startIntake(){ intakeLoop.startIntake(); }
	public void stopIntake(){ intakeLoop.stopOuttake(); }

	public void startHold(){ intakeLoop.startHold(); }
	
	public void startOuttake(){ intakeLoop.startOuttake(); }
	public void stopOuttake(){ intakeLoop.stopOuttake(); }
	
	public void grabberIn() { intakeLoop.grabberIn();	}
	public void grabberOut() { intakeLoop.grabberOut(); }
	public void grabberToggle() { intakeLoop.grabberToggle(); }
	
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
