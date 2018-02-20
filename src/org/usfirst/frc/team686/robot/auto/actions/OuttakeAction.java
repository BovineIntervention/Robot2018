package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.Intake;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

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
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void update() {
		
		System.out.println("scoring power cube");
		
		finished = (Timer.getFPGATimestamp() - mStartTime) >= mTimeToOuttake;
		intake.startOuttake();
		if(finished)
			intake.stopOuttake();
		
	}

	@Override
	public void done() {
		
		
	}

	@Override
	public void start() {
		mStartTime = Timer.getFPGATimestamp();
	}

	@Override
	public DataLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
