package org.usfirst.frc.team686.robot.auto.actions;


import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.MyTimer;

/**
 * Action to wait for a given amount of time To use this Action, call
 * runAction(new WaitAction(your_time))
 */
public class WaitAction implements Action {

    private double mTimeToWait;
    private double mStartTime;

    public WaitAction(double timeToWait) {
        mTimeToWait = timeToWait;
    }


    @Override
    public void start() {
        mStartTime = MyTimer.getTimestamp();
    }

    @Override
    public void update() {

    }
    
    @Override
    public boolean isFinished() {
        return MyTimer.getTimestamp() - mStartTime >= mTimeToWait;
    }

    @Override
    public void done() {

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
