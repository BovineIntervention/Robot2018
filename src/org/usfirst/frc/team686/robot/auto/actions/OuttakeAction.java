package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.Timer;

public class OuttakeAction implements Action {


	private double mTimeToOuttake = 0.5;
	private double mStartTime;
	private boolean finished;

	Intake intake = Intake.getInstance();
	
	public OuttakeAction() {
		finished = false;
	}
	
	
	@Override
	public void start() {
		mStartTime = Timer.getFPGATimestamp();
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void update() {
		
		finished = (Timer.getFPGATimestamp() - mStartTime) >= mTimeToOuttake;
		intake.startOuttake();
		if (finished)
			intake.stopOuttake();
		
	}

	@Override
	public void done() {
	}

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
