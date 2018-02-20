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
public class PathFollowerWithVisionAndCollisionDetectionAction implements Action 
{
	PathFollowerWithVisionDriveController driveCtrl;
	public static NavX gyro;
	private double lastWorldLinearAccelerationX;
	private double lastWorldLinearAccelerationY;
	private boolean collisionDetected;

    public PathFollowerWithVisionAndCollisionDetectionAction(Path _path) 
    {
    	
    	driveCtrl = new PathFollowerWithVisionDriveController(_path, PathVisionState.PATH_FOLLOWING);
    	gyro = NavX.getInstance();
    	lastWorldLinearAccelerationX = gyro.getWorldLinearAccelerationX();
    	lastWorldLinearAccelerationY = gyro.getWorldLinearAccelerationY();
    	collisionDetected = false;

    }

    public PathFollowerWithVisionDriveController getDriveController() { return driveCtrl; }

    @Override
    public void start() 
    {
		System.out.println("Starting PathFollowerWithVisionAction");
		driveCtrl.start();
    }


    @Override
    public void update() 
    {
    	driveCtrl.update();
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
        
        if ( ( Math.abs(currentJerkX) > Constants.kCollisionThreshold ) ||
             ( Math.abs(currentJerkY) > Constants.kCollisionThreshold) ) {
            collisionDetected = true;
        }
        
    	
    	return driveCtrl.isFinished() || collisionDetected ;
    }

    @Override
    public void done() 
    {
		System.out.println("Finished PathFollowerWithVisionAction");
		// cleanup code, if any
		driveCtrl.done();
    }

 
    
    
    public DataLogger getLogger() { return driveCtrl.getLogger(); }
}