package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.DriveState;
import org.usfirst.frc.team686.robot.lib.sensors.NavX;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Superstructure;

import edu.wpi.first.wpilibj.DigitalInput;

public class CubeDetectionAction implements Action 
{
	Superstructure superstructure = Superstructure.getInstance();
	
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
       	if (!Constants.cubeCloseProximitySensor.get())
       	{
    		System.out.println("Cube Close -- Close Grabber!");
    		superstructure.grabberIn();
       	}
    }	
	
	
    @Override
    public boolean isFinished() 
    {
    	if (!Constants.cubeInProximitySensor.get())
    		System.out.println("Cube In!");

    	return !Constants.cubeInProximitySensor.get();
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
   			put("CubeDetectionAction/cubeCloseProximitySensor", Constants.cubeCloseProximitySensor.get() );
   			put("CubeDetectionAction/cubeInProximitySensor", Constants.cubeInProximitySensor.get() );
        }
    };
     
    public DataLogger getLogger() { return logger; }
}
