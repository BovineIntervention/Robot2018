package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Intake;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class IntakeStartAction implements Action {
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	Intake intake = Intake.getInstance();
	private boolean finished;

	public IntakeStartAction() {
		finished = false;
	}
	
	
	@Override
	public void start() 
	{
		boolean extended = true;
		elevatorArmBar.set(ElevatorArmBarStateEnum.GROUND, extended);
		intake.grabberOut();
		intake.startIntake();
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
