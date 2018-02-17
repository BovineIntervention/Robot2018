package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.IntakeState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.IntakeLoop;

public class Intake extends Subsystem {
	
	private static Intake instance = new Intake();
	public static Intake getInstance() { return instance; }
	
	public IntakeLoop intakeLoop = IntakeLoop.getInstance();
	
	private Intake()
	{
		disable();
	}
	
	public void disable() { intakeLoop.disable(); }
	public void enable()  { intakeLoop.enable(); }
	
	public void intake()   { intakeLoop.intake(); }	
	public void outtake() { intakeLoop.outtake(); }	
	public void setGrabber() { intakeLoop.setGrabber(); }

	
	@Override
	public void stop() {
		intakeLoop.stop();
		
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
			try 
			{
				put("Intake/state", intakeLoop.state.toString() );
				
				IntakeState.getInstance().getLogger().log();
			} catch (NullPointerException e) {
				
			}
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
