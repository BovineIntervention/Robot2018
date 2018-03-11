package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;


public class CrossXAction implements Action 
{
	public RobotState robotState = RobotState.getInstance();
	double threshold;
	double diff;
	double prevDiff;
	
    public CrossXAction(double _threshold) 
    {
    	threshold = _threshold;
    	diff = 0;
    	prevDiff = 0;
    }

    @Override
    public void start() 
    {
    	diff = (robotState.getLatestFieldToVehicle().getX() - threshold);
    	prevDiff = diff;
    }


    @Override
    public void update() 
    {
    	// do nothing -- just waiting for a collision
    }	
	
	
    @Override
    public boolean isFinished() 
    {
    	diff = (robotState.getLatestFieldToVehicle().getX() - threshold);
    	
    	// we are done when we cross the threshold line
    	// (so the sign of the difference will change)
    	boolean finished = (Math.signum(diff) != Math.signum(prevDiff));
    	
//    	if (finished)
//    		System.out.println("Crossed X Threshold: " + threshold);

    	prevDiff = diff;
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
