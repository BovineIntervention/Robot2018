package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.DriveCommand;
import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathFollower;
import org.usfirst.frc.team686.robot.lib.util.PathFollower.PathVisionState;
import org.usfirst.frc.team686.robot.subsystems.Drive;
import org.usfirst.frc.team686.robot.subsystems.Superstructure;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

/**
 * Action for following a path defined by a Path object.
 * 
 * Serially configures a PathFollower object to follow each path 
 */
public class DrivingCubeIntakeAction implements Action  
{
	Drive drive = Drive.getInstance();
	PathFollower driveCtrl;
	Path path;
	Superstructure superstructure = Superstructure.getInstance();

	public DigitalInput proximitySensor = new DigitalInput(Constants.kProximitySensor1Port);
	boolean cubeDetected = false;

	double startTime = 0.0;
	double elapsedTime = 0.0;
	final double cubeCollectionTime = 2.0;
	final double backupTime = 0.5;
	final double backupSpeed = -0.25;
	
	enum CubeDetectionStateEnum { DRIVING_FORWARD, BACKING_UP, DONE }
	CubeDetectionStateEnum cubeDetectionState;
	
	
    public DrivingCubeIntakeAction(Path _path) 
    {
    	driveCtrl = new PathFollower(_path, PathVisionState.PATH_FOLLOWING);
    	path = _path;
    }

    public PathFollower getDriveController() { return driveCtrl; }

    @Override
    public void start() 
    {
		System.out.println("PathFollowerAction.start(), pose = " + RobotState.getInstance().getLatestFieldToVehicle().toString());

		// make sure intake is extended and rotating, if it wasn't already
		boolean extended = true;
		superstructure.set(ElevatorArmBarStateEnum.GROUND, extended);
		superstructure.startIntake();
		
		startTime = Timer.getFPGATimestamp();		

		driveCtrl.start();
		
    	cubeDetectionState = CubeDetectionStateEnum.DRIVING_FORWARD;
    	cubeDetected = false;
    }


    @Override
    public void update() 
    {
    	cubeDetected = !proximitySensor.get();
    	
    	switch (cubeDetectionState)
    	{
    	case DRIVING_FORWARD:
        	driveCtrl.update();

        	elapsedTime = Timer.getFPGATimestamp() - startTime;
        	
        	
           	if (driveCtrl.isFinished() || cubeDetected || elapsedTime > cubeCollectionTime)
           	{
           		if (cubeDetected)
           			System.out.println("Proximity Sensor Detected Cube!");
           		else
           			System.out.println("Cube Detection Timeout");
           		
            	superstructure.startHold();
        		boolean extended = false;
        		superstructure.set(ElevatorArmBarStateEnum.GROUND, extended);	// return to retracted position
        		
       			cubeDetectionState = CubeDetectionStateEnum.BACKING_UP;

       			startTime = Timer.getFPGATimestamp();		
       			driveCtrl.done();
           	}
           	break;
           	
    	case BACKING_UP:
    		// back up a tiny bit so arm bar can retract
    		drive.setOpenLoop(new DriveCommand(backupSpeed, backupSpeed));
        	elapsedTime = Timer.getFPGATimestamp() - startTime;
        	if (elapsedTime >= backupTime)
        		cubeDetectionState = CubeDetectionStateEnum.DONE;
    		break;
    		
    	case DONE:
    		break;
           	
    	}

    	
	}	
	
	
    @Override
    public boolean isFinished() 
    {
    	//System.out.println(toString());
    	boolean finished = (cubeDetectionState == CubeDetectionStateEnum.DONE);
    	return finished;
    }

    @Override
    public void done() 
    {
		System.out.println("DrivingCubeIntakeAction.done(),  pose = " + RobotState.getInstance().getLatestFieldToVehicle().toString());
		// cleanup code, if any
		drive.stop();
    }

 
    public String toString()
    {
    	return String.format("cubePickupState = %s, driveCtrl.isFinished = %b, cubeDetected = %b, elapsedTime = %.1f\n", cubeDetectionState, driveCtrl.isFinished(), cubeDetected, elapsedTime);
    }
    
    
    
    public DataLogger getLogger() { return driveCtrl.getLogger(); }
}
