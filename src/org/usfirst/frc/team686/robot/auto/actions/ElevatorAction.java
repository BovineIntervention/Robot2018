package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.Constants.RobotSelectionEnum;
import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.MyTimer;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class ElevatorAction implements Action {
	
	
	public ElevatorArmBarStateEnum targetState = ElevatorArmBarStateEnum.GROUND;

	private double targetOffset = 1.0;
	private double mStartTime;
	private double mTargetTime = 1.0;
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
		
		mStartTime = MyTimer.getTimestamp();
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
		boolean finished = false;
		
		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)
		{
			actualPosition = elevatorState.getPositionInches();
			finished = Math.abs(targetPosition - actualPosition) <= targetOffset;
		}
		else
		{
			// delay to simulate elevator movement
			if ((MyTimer.getTimestamp() - mStartTime) <= mTargetTime)
				finished = true;
		}
		
		return finished;
	}


	@Override
	public void done() {
		elevatorArmBar.set(ElevatorArmBarStateEnum.GROUND, extended);
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
