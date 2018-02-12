package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;

public class ArmBar extends Subsystem {
	
	private static ArmBar instance = new ArmBar();
	public static ArmBar getInstance() { return instance; }
	
	public ArmBarLoop armBarLoop = ArmBarLoop.getInstance();
	
	private ArmBar()
	{
		disable();
	}
	
	public void disable() { armBarLoop.disable(); }
	public void enable()  { armBarLoop.enable(); }
	
	public void up()   { armBarLoop.setGoal(Constants.kArmBarUpAngleDeg); }	
	public void down() {	armBarLoop.setGoal(Constants.kArmBarDownAngleDeg); 	}	

	@Override
	public void stop() {
		armBarLoop.stop();
		
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
				put("ArmBar/State", armBarLoop.state.toString() );
				put("ArmBar/Position", armBarLoop.getPosition() );
				put("ArmBar/Goal", armBarLoop.getGoal() );
				put("ArmBar/LimitSwitch", armBarLoop.getLimitSwitch() );
			} catch (NullPointerException e) {
				// skip logging pathFollowingController when it doesn't exist
			}
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
