package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;


public class CrossXAction implements Action 
{
	public RobotState robotState = RobotState.getInstance();
	double threshold;
	
    public CrossXAction(double _threshold) 
    {
    	threshold = _threshold;
    }

    @Override
    public void start() 
    {}


    @Override
    public void update() 
    {
    	// do nothing -- just waiting for a collision
    }	
	
	
    @Override
    public boolean isFinished() 
    {
    	boolean finished = (robotState.getLatestFieldToVehicle().getX() > threshold);
//    	if (finished)
//    		System.out.println("Crossed X Threshold: " + threshold);
    	return finished;
    }

    @Override
    public void done() 
    {
		// cleanup code, if any
    }

	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
        }
    };
     
    public DataLogger getLogger() { return logger; }
}
