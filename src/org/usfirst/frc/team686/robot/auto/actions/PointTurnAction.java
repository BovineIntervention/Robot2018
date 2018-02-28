package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Kinematics.WheelSpeed;
import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.DriveCommand;
import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.command_status.DriveCommand.DriveControlMode;
import org.usfirst.frc.team686.robot.subsystems.Drive;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Timer;

public class PointTurnAction implements Action
{
    private double targetHeading;
    double heading;
    double error;
    double output;
 
    double Kp = Constants.kPointTurnKp;
    double Kd = Constants.kPointTurnKd;
    double Ki = Constants.kPointTurnKi;
    double Kf = Constants.kPointTurnKf;
    
    boolean firstUpdate = true;
    double lastError = 0.0;
    double dError = 0.0;
    double iError = 0.0;
    double dErrorFilt = 0.0;
    
    double startTime;
    
    private Drive drive = Drive.getInstance();
    private RobotState robotState = RobotState.getInstance();
    
    
    public PointTurnAction(double _targetHeadingDeg)
    {
    	setHeading(_targetHeadingDeg);
    }

    public void setHeading(double _targetHeadingDeg)
    {
    	targetHeading = _targetHeadingDeg;
    }
    
    @Override
    public void start() 
    {
    	firstUpdate = true;
    	lastError = 0.0;
    	dErrorFilt = 0.0;
    	iError = 0.0;
    	startTime = Timer.getFPGATimestamp();
    }

    
    @Override
    public void update() 
    {
    	heading = robotState.getLatestFieldToVehicle().getHeadingDeg();
    	error = Vector2d.normalizeAngleDeg( targetHeading - heading );

    	if (firstUpdate)
    	{
    		lastError = error;
    		firstUpdate = false;
    	}
    	
    	dError = error - lastError;
    	iError += error;
		lastError = error;
		
		dErrorFilt = (1-0.25)*dErrorFilt + 0.25*dError;
		
		output = Kp * error + Kd * dErrorFilt + Ki * iError;
		output = Util.limit(output, Constants.kPointTurnMaxOutput);
    	
   		// send drive control command (note: using brake mode to help finish at correct angle)
		DriveCommand cmd = new DriveCommand(DriveControlMode.OPEN_LOOP, -output, +output, NeutralMode.Brake);
		drive.setCommand(cmd);
    }

    @Override
    public boolean isFinished() 
    {
    	// don't allow point turn to take forever
    	double elapsedTime = Timer.getFPGATimestamp() - startTime;
    	if (elapsedTime > 1.0)
    		return true;
    	
    	heading = robotState.getLatestFieldToVehicle().getHeadingDeg();
       	error = Vector2d.normalizeAngleDeg( targetHeading - heading );
    	dError = error - lastError;	// note: lastError updated in update(), not here

		System.out.println(this.toString());
		
		// finished if angle is close to target and our turn rate is slow (indicating we aren't flying past the target)
    	return ((Math.abs(error) < Constants.kPointTurnCompletionToleranceDeg) && (Math.abs(dErrorFilt) < 0.01/Constants.kLoopDt));
    }

    @Override
    public void done()
    {
    	// keep brake on so that momentum doesn't take us too far.  Need to set back to coast mode elsewhere
		drive.setOpenLoop(DriveCommand.BRAKE());
    }

    public String toString()
    {
    	return String.format("PointTurnAction: target: %6.1f, actual: %6.1f, error: %6.2f, dError: %6.3f, iError: %6.3f, output: %.3f",  targetHeading, Vector2d.normalizeAngleDeg(heading), error, dErrorFilt, iError, output);
    }
    
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
    		put("AutoAction", "PointTurn" );
			put("PointTurn/targetHeading", targetHeading );
			put("PointTurn/heading", heading );
			put("PointTurn/error", error );
			put("PointTurn/output", output );
	    }
    };
	
    public DataLogger getLogger() { return logger; }
    	
}

