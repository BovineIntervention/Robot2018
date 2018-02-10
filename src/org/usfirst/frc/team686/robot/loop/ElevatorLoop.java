package org.usfirst.frc.team686.robot.loop;

import org.usfirst.frc.team686.robot.Constants;

public class ElevatorLoop {
	
	public enum ElevatorState { UNINITIALIZED, ZEROING, RUNNING, ESTOPPED; }
	private static double goal;
	private ElevatorState state = ElevatorState.UNINITIALIZED;
	private ElevatorState nextState = ElevatorState.UNINITIALIZED;
	private static double offset = 0.0;
	public static double lastError = 0.0;
	private static double error_;
	private static double filteredGoal;
	

	public void setGoal(double goal_){ goal = goal_; }
	public double getGoal () { return goal; } 
	
	public double getFilteredGoal() { return filteredGoal; }

	public ElevatorState getState() { return state; }

	
	
	public double Update(double encoder, boolean limitTriggered, boolean enabled)
	{
		// transition states
		state = nextState;
		// start over if ever disabled
		if (!enabled)
		{
			state = ElevatorState.UNINITIALIZED;
		}
		
		switch (state)
		{
		case UNINITIALIZED:
			// initial state.  stay here until enabled
			if (enabled)
			{
				nextState = ElevatorState.ZEROING;	// when enabled, state ZEROING
				filteredGoal = encoder;			// initial goal is to stay in the same position
			}
			break;
			
		case ZEROING:
			// update goal to be slightly lowered, limited in velocity
			filteredGoal  = filteredGoal - (Constants.kLoopDt * Constants.kZeroingVelocity);
			
			if (limitTriggered)
			{
				// ZEROING is done with limit switch is done.  
				offset = encoder;					// set offset so that future position is zerored properly
				nextState = ElevatorState.RUNNING;	// start running state
				filteredGoal = 0;					// stay at zero until new goal is given
			}
			break;
			
		case RUNNING:
			filteredGoal = goal;
			break;
			
		case ESTOPPED:
			break;

		default:
			nextState = ElevatorState.UNINITIALIZED;
		}

		
		double Kp = 100.0;
		double Kv = 0.1;

		double position = encoder - offset;		// get true position, after calibrating out any encoder error
		double error = filteredGoal - position;
		double vel = (error - lastError) / Constants.kLoopDt;

		lastError = error;
		error_ = error;
		
		double voltage = Kp * error + Kv * vel;
		
		return Math.min(Constants.kMaxBatteryVoltage, Math.max(-Constants.kMaxBatteryVoltage, voltage));
	}
	
}
