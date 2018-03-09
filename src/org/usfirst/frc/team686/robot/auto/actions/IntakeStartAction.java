package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Superstructure;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;

public class IntakeStartAction implements Action {
	Superstructure superstructure = Superstructure.getInstance();
	private boolean finished;

	public IntakeStartAction() {
		finished = false;
	}
	
	
	@Override
	public void start() 
	{
		boolean extended = true;
		superstructure.set(ElevatorArmBarStateEnum.GROUND, extended);
//		superstructure.grabberOut();
		superstructure.startIntake();
		finished = true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void update() {}

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
