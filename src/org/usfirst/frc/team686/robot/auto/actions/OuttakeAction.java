package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.Intake;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

import edu.wpi.first.wpilibj.Timer;

public class OuttakeAction implements Action {
	
	
	public enum ScoreCubeEnum {
		
		SWITCH(ElevatorArmBarStateEnum.SWITCH),
		SCALE(ElevatorArmBarStateEnum.SCALE_MED),
		GROUND(ElevatorArmBarStateEnum.GROUND);
		
		public ElevatorArmBarStateEnum target; //inches
		
		ScoreCubeEnum( ElevatorArmBarStateEnum _target )
		{
			this.target = _target;
		}
		
	}
	
	public ScoreCubeEnum state = ScoreCubeEnum.GROUND;

	private double mTimeToOuttake = 0.5;
	private double mStartTime;
	private boolean finished;
	
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	Intake intake = Intake.getInstance();
	
	public OuttakeAction(boolean isSwitch) {
		
		mStartTime = Timer.getFPGATimestamp();
		finished = false;
		
		if(isSwitch){ state = ScoreCubeEnum.SWITCH; }
		else { state = ScoreCubeEnum.SCALE; }
	}
	
	public OuttakeAction(){
		
		finished = false;
		state = ScoreCubeEnum.GROUND;
		
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
		
	}

	@Override
	public void done() {
		
		intake.stopOuttake();
		
	}

	@Override
	public void start() {
		
		elevatorArmBar.set(state.target, false);
		
	}

	@Override
	public DataLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
