package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class PickUpCubeAction implements Action {
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	Intake intake = Intake.getInstance();
	private boolean finished;
	double startTime = 0.0;
	double cubeCollectionTime = 0.5;
	
	public PickUpCubeAction() {
		finished = false;
	}
	
	
	@Override
	public void start() 
	{
		intake.grabberIn();
		intake.startIntake();
		startTime = Timer.getFPGATimestamp();
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void update() 
	{
		// allow a little time for cube to settle
		// TODO: use proximity sensors instead
		double elapsedTime = Timer.getFPGATimestamp() - startTime;
		if (elapsedTime >= cubeCollectionTime)
		{
			intake.startHold();
			boolean extended = false;
			elevatorArmBar.set(ElevatorArmBarStateEnum.GROUND, extended);	// return to retracted position
			finished = true;
		}
	}

	@Override
	public void done() {}

	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
	    }
    };
	
	@Override
	public DataLogger getLogger() { return logger; }

}
