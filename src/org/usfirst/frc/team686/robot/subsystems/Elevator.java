package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop;

public class Elevator extends Subsystem {
	
	private static Elevator instance = new Elevator();
	public static Elevator getInstance() { return instance; }
	
	public ElevatorLoop elevatorLoop = ElevatorLoop.getInstance();
	
	private Elevator()
	{
		disable();
	}
	
	public void disable() { elevatorLoop.disable(); }
	public void enable()  { elevatorLoop.enable(); }
	
	public void goToSwitch()   { elevatorLoop.setGoal(Constants.kSwitchHeight); }
	public void goToScale() { elevatorLoop.setGoal(Constants.kScaleHeight); }
	public void goToGround() { elevatorLoop.setGoal(Constants.kElevatorDownHeight); }

	@Override
	public void stop() {
		elevatorLoop.stop();
		
	}

	@Override
	public void zeroSensors() {
		// TODO Auto-generated method stub
	}

	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			try // pathFollowingController doesn't exist until started
			{
				put("Elevator/State", elevatorLoop.toString() );
				put("Elevator/Position", elevatorLoop.getPosition() );
				put("Elevator/Goal", elevatorLoop.getGoal() );
				put("Elevator/LimitSwitch", elevatorLoop.getLimitSwitch() );
			} catch (NullPointerException e) {
				// skip logging pathFollowingController when it doesn't exist
			}
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
