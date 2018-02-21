package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

import edu.wpi.first.wpilibj.Timer;

public class ElevatorAction implements Action {
	
	
	public ElevatorArmBarStateEnum targetState = ElevatorArmBarStateEnum.GROUND;

	private double targetOffset = 1.0;
	private double mStartTime;
	private double mTargetTime = 5;
	private double actualPosition;
	private double targetPosition;
	private boolean extended = false;
	
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	ElevatorState elevatorState = ElevatorState.getInstance();
	
	public ElevatorAction(ElevatorArmBarStateEnum _targetState) {
		
		actualPosition = elevatorState.getPositionInches();
		targetState = _targetState;
	}
	
	public ElevatorAction(){
		targetState = ElevatorArmBarStateEnum.GROUND;
	}

	@Override
	public void start() {
		
		mStartTime = Timer.getFPGATimestamp();
		elevatorArmBar.set(targetState, extended);
		targetPosition = elevatorState.getTrajectoryTargetInches();
		actualPosition = elevatorState.getPositionInches();
	}
	
	@Override
	public void update() {
		// do nothing -- just waiting for elevator to reach target
	}

	@Override
	public boolean isFinished() {
		actualPosition = elevatorState.getPositionInches();
		
		boolean finished = Math.abs(targetPosition - actualPosition) <= targetOffset;// || (Timer.getFPGATimestamp() - mStartTime) <= mTargetTime;
		
		return finished;
	}


	@Override
	public void done() {
		elevatorArmBar.set(ElevatorArmBarStateEnum.GROUND, extended);
	}

	@Override
	public DataLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
