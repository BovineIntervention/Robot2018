package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;

import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team686.robot.subsystems.Superstructure;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;

public class PickUpCubeAction implements Action {
	Superstructure superstructure = Superstructure.getInstance();
	private boolean finished;
	double startTime = 0.0;
	double cubeCollectionTime = 0.5;
	
	public PickUpCubeAction() {
		finished = false;
	}
	
	
	@Override
	public void start() 
	{
		superstructure.grabberIn();
		superstructure.startIntake();
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
			superstructure.startHold();
			boolean extended = false;
			superstructure.set(ElevatorArmBarStateEnum.GROUND, extended);	// return to retracted position
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
