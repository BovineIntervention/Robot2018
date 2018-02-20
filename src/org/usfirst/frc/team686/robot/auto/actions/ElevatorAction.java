package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.Intake;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

import edu.wpi.first.wpilibj.Timer;

public class ElevatorAction implements Action {
	
	
	public enum ElevatorTarget {
		
		SWITCH(ElevatorArmBarStateEnum.SWITCH),
		SCALE(ElevatorArmBarStateEnum.SCALE_MED),
		GROUND(ElevatorArmBarStateEnum.GROUND);
		
		public ElevatorArmBarStateEnum target; //inches
		
		ElevatorTarget( ElevatorArmBarStateEnum _target )
		{
			this.target = _target;
		}
		
	}
	
	public ElevatorTarget state = ElevatorTarget.GROUND;

	private double targetOffset = 1.0;
	private double mStartTime;
	private double mTargetTime = 5;
	private double position;
	private double target;
	private boolean extended;
	private boolean finished;
	
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	ElevatorState elevatorState = ElevatorState.getInstance();
	
	public ElevatorAction(boolean isSwitch) {
		
		position = elevatorState.getPositionInches();
		finished = false;
		
		if(isSwitch){ state = ElevatorTarget.SWITCH; }
		else { state = ElevatorTarget.SCALE; }
	}
	
	public ElevatorAction(){
		
		finished = false;
		state = ElevatorTarget.GROUND;
		
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void update() {
		
		System.out.println("scoring power cube");
		
		position = elevatorState.getPositionInches();
		
		finished = Math.abs(target - position) <= targetOffset;// || (Timer.getFPGATimestamp() - mStartTime) <= mTargetTime;
				
		if(finished){
			state = ElevatorTarget.GROUND;
			elevatorArmBar.set(state.target, false);
		}
		
	}

	@Override
	public void done() {
		
		
	}

	@Override
	public void start() {
		
		mStartTime = Timer.getFPGATimestamp();
		elevatorArmBar.set(state.target, false);
		position = elevatorState.getPositionInches();
		target = elevatorState.getTrajectoryTargetInches();
		
	}

	@Override
	public DataLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
