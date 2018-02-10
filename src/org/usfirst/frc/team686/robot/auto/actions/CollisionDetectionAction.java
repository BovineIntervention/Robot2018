package org.usfirst.frc.team686.robot.auto.actions;


import org.usfirst.frc.team686.robot.lib.util.DataLogger;

import edu.wpi.first.wpilibj.Timer;

/**
 * Action to wait for a given amount of time To use this Action, call
 * runAction(new WaitAction(your_time))
 */
public class CollisionDetectionAction implements Action {

    private double mTimeToWait;
    private double mStartTime;

    public CollisionDetectionAction(double timeToWait) {
        mTimeToWait = timeToWait;
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - mStartTime >= mTimeToWait;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        mStartTime = Timer.getFPGATimestamp();
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
