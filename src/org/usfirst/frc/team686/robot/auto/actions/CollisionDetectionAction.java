package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.sensors.NavX;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathFollowerWithVisionDriveController;
import org.usfirst.frc.team686.robot.lib.util.PathFollowerWithVisionDriveController.PathVisionState;

import edu.wpi.first.wpilibj.Timer;

/**
 * Action for following a path defined by a Path object.
 * 
 * Serially configures a PathFollower object to follow each path 
 */
public class CollisionDetectionAction implements Action 
{
	public static NavX gyro;
	private double lastWorldLinearAccelerationX;
	private double lastWorldLinearAccelerationY;
	double currentJerkX = 0.0;
	double currentJerkY = 0.0;
	
    public CollisionDetectionAction() 
    {
    	gyro = NavX.getInstance();
    }

    @Override
    public void start() 
    {
		System.out.println("Starting CollisionDetectionAction");
    	lastWorldLinearAccelerationX = gyro.getWorldLinearAccelerationX();
    	lastWorldLinearAccelerationY = gyro.getWorldLinearAccelerationY();
    }


    @Override
    public void update() 
    {
    	// do nothing -- just waiting for a collision
    }	
	
	
    @Override
    public boolean isFinished() 
    {
        double currWorldLinearAccelerationX = gyro.getWorldLinearAccelerationX();
        double currentJerkX = currWorldLinearAccelerationX - lastWorldLinearAccelerationX;
        lastWorldLinearAccelerationX = currWorldLinearAccelerationX;
        double currWorldLinearAccelerationY = gyro.getWorldLinearAccelerationY();
        double currentJerkY = currWorldLinearAccelerationY - lastWorldLinearAccelerationY;
        lastWorldLinearAccelerationY = currWorldLinearAccelerationY;

        //System.out.println("jerkX: " + currentJerkX + ", jerkY: " + currentJerkY + ", threshold: " + Constants.kCollisionThreshold);  
        
        boolean collisionDetected = false;
        if ( ( Math.abs(currentJerkX) > Constants.kCollisionThreshold ) ||
             ( Math.abs(currentJerkY) > Constants.kCollisionThreshold) ) {
        	System.out.println("MAXIMUM JERK X: " + currentJerkX);
        	System.out.println("MAXIMUM JERK Y: " + currentJerkY);
        	System.out.println("COLLISION DETECTED");
            collisionDetected = true;
        }
    	return collisionDetected;
    }

    @Override
    public void done() 
    {
		System.out.println("Finished CollisionDetectionAction");
        System.out.println("jerkX: " + currentJerkX + ", jerkY: " + currentJerkY + ", threshold: " + Constants.kCollisionThreshold);  
		
		// cleanup code, if any
    }

 
    
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
    		put("AutoAction", "CollisionDetectionAction" );
   			put("CollisionDetectionAction/JerkX", currentJerkX );
   			put("CollisionDetectionAction/JerkY", currentJerkY );
   			put("CollisionDetectionAction/JerkThresh", Constants.kCollisionThreshold );
        }
    };
     
    public DataLogger getLogger() { return logger; }
}
