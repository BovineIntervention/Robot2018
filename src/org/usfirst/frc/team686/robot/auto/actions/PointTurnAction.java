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
 
    boolean firstUpdate = true;
    double lastError = 0.0;
    double dError = 0.0;
    double iError = 0.0;
    
    private Drive drive = Drive.getInstance();
    private RobotState robotState = RobotState.getInstance();
    
    static final double Kp = 0.05;
    static final double Kd = 0.40;
    static final double Ki = 0.00;
    static final double Kf = 0.00;
    
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
    	
    	if (Math.abs(error) < Constants.kPointTurnCompletionToleranceDeg)
    		error = 0.0;
    	
    	dError = error - lastError;
    	iError += error;
		lastError = error;
		
		output = Kp * error + Kd * dError + Ki * iError;
		output = Util.limit(output, 1.0);
    	
   		// send drive control command (note: using brake mode to help finish at correct angle)
		DriveCommand cmd = new DriveCommand(DriveControlMode.OPEN_LOOP, -output, +output, NeutralMode.Brake);
		drive.setCommand(cmd);
    }

    @Override
    public boolean isFinished() 
    {
    	heading = robotState.getLatestFieldToVehicle().getHeadingDeg();
       	error = Vector2d.normalizeAngleDeg( targetHeading - heading );
    	dError = error - lastError;	// note: lastError updated in update(), not here

		System.out.println(this.toString());
		
		// finished if angle is close to target and our turn rate is slow (indicating we aren't flying past the target)
    	return ((Math.abs(error) < Constants.kPointTurnCompletionToleranceDeg) && (Math.abs(dError) < 0.1/Constants.kLoopDt));
    }

    @Override
    public void done()
    {
    	// brake off -- back to coasting
		drive.setOpenLoop(DriveCommand.COAST());
    }

    public String toString()
    {
    	return String.format("PointTurnAction: target: %6.1f, actual: %6.1f, error: %6.1f, dError: %6.1f, iError: %6.1f, output: %.2f",  targetHeading, Vector2d.normalizeAngleDeg(heading), error, dError, iError, output);
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

