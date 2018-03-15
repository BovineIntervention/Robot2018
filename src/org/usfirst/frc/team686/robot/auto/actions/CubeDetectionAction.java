package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.DriveState;
import org.usfirst.frc.team686.robot.lib.sensors.NavX;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;

import edu.wpi.first.wpilibj.DigitalInput;

public class CubeDetectionAction implements Action 
{
	
    public CubeDetectionAction() 
    {
    }

    @Override
    public void start() 
    {
		System.out.println("Starting CubeDetectionAction");
     }


    @Override
    public void update() 
    {
    	// do nothing -- just waiting for a collision
    }	
	
	
    @Override
    public boolean isFinished() 
    {
    	if (!Constants.proximitySensor.get())
    		System.out.println("Detected Cube!");

    	return !Constants.proximitySensor.get();
    }

    @Override
    public void done() 
    {
		System.out.println("Finished CubeDetectionAction");
		
		// cleanup code, if any
    }

	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
   			put("CubeDetectionAction/proximitySensor", Constants.proximitySensor.get() );
        }
    };
     
    public DataLogger getLogger() { return logger; }
}
