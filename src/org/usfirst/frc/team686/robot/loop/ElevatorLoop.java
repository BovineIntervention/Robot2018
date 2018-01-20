package org.usfirst.frc.team686.robot.loop;

import org.usfirst.frc.team686.robot.Constants;

public class ElevatorLoop {
	
	public enum ElevatorMode { UNINITIALIZED, ZEROING, RUNNING, ESTOPPED; }
	private static double goal;
	private ElevatorMode mode = ElevatorMode.UNINITIALIZED;
	private static double offset = 0.0;
	public static double lastError = 0.0;
	private static double error_;
	private static double filteredGoal;
	
	
	public double Update(double encoder, boolean limitTriggered, boolean enabled){
		double position = encoder - offset;
		
		switch(mode)
		{
		case UNINITIALIZED:
			if(enabled){
				mode = ElevatorMode.ZEROING;
				filteredGoal = position;
			}
			break;
		case ZEROING:
			filteredGoal  = filteredGoal - (Constants.kElevatorDt * Constants.kElevatorZeroingVelocity);
			if(limitTriggered){
				mode = ElevatorMode.RUNNING;
				offset = -encoder;
				position = 0.0;
			}
			if(!enabled){
				mode = ElevatorMode.UNINITIALIZED;
			}
			break;
		case RUNNING:
			filteredGoal = goal;
			break;
		case ESTOPPED:
			break;
		default:
			mode = ElevatorMode.UNINITIALIZED;
		}
		
		double Kp = 100.0;
		double Kv = 0.1;
		double error = filteredGoal - position;
		double vel = (error - lastError) / Constants.kElevatorDt;
		lastError = error;
		error_ = error;
		double voltage = Kp * error + Kv * vel;
		return Math.min(Constants.kElevatorMaxVoltage, Math.max(-Constants.kElevatorMaxVoltage, voltage));
	}
	
	public void setGoal(double goal_){ goal = goal_; }
	public double getGoal () { return goal; } 
	
	public double getFilteredGoal() { return filteredGoal; }

}
