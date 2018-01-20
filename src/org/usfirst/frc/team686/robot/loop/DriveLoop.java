package org.usfirst.frc.team686.robot.loop;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.command_status.DriveCommand;
import org.usfirst.frc.team686.robot.command_status.DriveStatus;
import org.usfirst.frc.team686.robot.lib.sensors.BNO055;
import org.usfirst.frc.team686.robot.subsystems.Drive;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;


/*
 * DriveLoop is the interface between Drive.java and the actual hardware.
 * It runs periodically, taking the commands sent by Drive and sending them to the hardware.
 * In this way, Drive.java does not access the hardware directly.  The benefits of this partition are: 
 * 1) Changes to drive hardware only requires changes to DriveLoop, not Drive
 * 2) DriveLoop can be easily replaced for simulation purposes.
 */

public class DriveLoop implements Loop 
{
	private static DriveLoop instance = new DriveLoop();
	public static DriveLoop getInstance() { return instance; }
	
    private static Drive drive;
	private static BNO055 imu;
    private DriveStatus driveStatus;
    
	public final CANTalon lMotorMaster, lMotorSlave1, lMotorSlave2;
	public final CANTalon rMotorMaster, rMotorSlave1, rMotorSlave2;

	private static final int kVelocityControlSlot = 0;
	private static final int kBaseLockControlSlot = 1;

	boolean currentLimitEnabled = false;
	
	private DriveLoop() 
	{
		drive = Drive.getInstance();
		imu = BNO055.getInstance(Constants.BNO055_PORT);
		driveStatus = DriveStatus.getInstance();
		
		lMotorMaster = new CANTalon(Constants.kLeftMotorMasterTalonId);
        lMotorSlave1  = new CANTalon(Constants.kLeftMotorSlave1TalonId);
        lMotorSlave2  = new CANTalon(Constants.kLeftMotorSlave2TalonId);

		rMotorMaster = new CANTalon(Constants.kRightMotorMasterTalonId);
        rMotorSlave1  = new CANTalon(Constants.kRightMotorSlave1TalonId);
        rMotorSlave2  = new CANTalon(Constants.kRightMotorSlave2TalonId);

		lMotorSlave1.changeControlMode(CANTalon.TalonControlMode.Follower);
		rMotorSlave1.changeControlMode(CANTalon.TalonControlMode.Follower);
		lMotorSlave1.set(Constants.kLeftMotorMasterTalonId);		// give slave the TalonID of it's master
		rMotorSlave1.set(Constants.kRightMotorMasterTalonId);	// give slave the TalonID of it's master
		lMotorSlave2.changeControlMode(CANTalon.TalonControlMode.Follower);
		rMotorSlave2.changeControlMode(CANTalon.TalonControlMode.Follower);
		lMotorSlave2.set(Constants.kLeftMotorMasterTalonId);		// give slave the TalonID of it's master
		rMotorSlave2.set(Constants.kRightMotorMasterTalonId);	// give slave the TalonID of it's master
        
		// Get status at 100Hz
		lMotorMaster.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 10);
		rMotorMaster.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 10);

		// Set initial settings
		DriveCommand neutralCmd = DriveCommand.NEUTRAL();
		setControlMode(neutralCmd);
		setMotors(neutralCmd);
		setBrakeMode(neutralCmd);
		resetEncoders(neutralCmd);
		
        lMotorMaster.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        rMotorMaster.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		lMotorMaster.set(0);
		rMotorMaster.set(0);
		lMotorMaster.enableBrakeMode(false);
		rMotorMaster.enableBrakeMode(false);
		lMotorSlave1.enableBrakeMode(false);
		lMotorSlave2.enableBrakeMode(false);
		rMotorSlave1.enableBrakeMode(false);
		rMotorSlave2.enableBrakeMode(false);

		lMotorMaster.setCurrentLimit(Constants.kTalonCurrentLimit);
		lMotorSlave1.setCurrentLimit(Constants.kTalonCurrentLimit);
		lMotorSlave2.setCurrentLimit(Constants.kTalonCurrentLimit);
		rMotorMaster.setCurrentLimit(Constants.kTalonCurrentLimit);
		rMotorSlave1.setCurrentLimit(Constants.kTalonCurrentLimit);
		rMotorSlave2.setCurrentLimit(Constants.kTalonCurrentLimit);
		
		enableCurrentLimit(true);
		
		
		// Set up the encoders
		lMotorMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rMotorMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		lMotorMaster.configEncoderCodesPerRev(Constants.kQuadEncoderCodesPerRev);	// using this API lets us program velocity in RPM in closed-loop modes
		rMotorMaster.configEncoderCodesPerRev(Constants.kQuadEncoderCodesPerRev);	// Talon SRX Software Reference Manual Section 17.2 API Unit Scaling
		lMotorMaster.setInverted(false);
		rMotorMaster.setInverted(false);
		lMotorMaster.reverseSensor(true);
		rMotorMaster.reverseSensor(true);
		lMotorMaster.reverseOutput(false);
		rMotorMaster.reverseOutput(false);
		lMotorSlave1.reverseOutput(false);
		rMotorSlave1.reverseOutput(false);
		lMotorSlave2.reverseOutput(false);
		rMotorSlave2.reverseOutput(false);

		// Load velocity control gains
		lMotorMaster.setPID(Constants.kDriveVelocityKp, Constants.kDriveVelocityKi, Constants.kDriveVelocityKd,
				Constants.kDriveVelocityKf, Constants.kDriveVelocityIZone, Constants.kDriveVelocityRampRate,
				kVelocityControlSlot);
		rMotorMaster.setPID(Constants.kDriveVelocityKp, Constants.kDriveVelocityKi, Constants.kDriveVelocityKd,
				Constants.kDriveVelocityKf, Constants.kDriveVelocityIZone, Constants.kDriveVelocityRampRate,
				kVelocityControlSlot);
		// Load base lock control gains
		lMotorMaster.setPID(Constants.kDriveBaseLockKp, Constants.kDriveBaseLockKi, Constants.kDriveBaseLockKd,
				Constants.kDriveBaseLockKf, Constants.kDriveBaseLockIZone, Constants.kDriveBaseLockRampRate,
				kBaseLockControlSlot);
		rMotorMaster.setPID(Constants.kDriveBaseLockKp, Constants.kDriveBaseLockKi, Constants.kDriveBaseLockKd,
				Constants.kDriveBaseLockKf, Constants.kDriveBaseLockIZone, Constants.kDriveBaseLockRampRate,
				kBaseLockControlSlot);
	}
	
	
	@Override public void onStart()
	{
		// nothing
	}

	@Override public void onLoop()
	{
		// get status from hardware
		getStatus();
		
		// send new commands to hardware
		sendCommands();
	}

	@Override public void onStop()
	{
		stopMotors();
	}

	private void stopMotors()
	{
		drive.setCommand(DriveCommand.NEUTRAL());		// override any incoming commands 
		sendCommands();
	}

	private void getStatus()
	{
		synchronized(driveStatus)	// lock DriveStatus until we update it, so that objects reading DriveStatus don't get partial updates	
		{
			// get Talon control & brake modes (assume right motor is configured identically)
			driveStatus.setTalonControlMode( lMotorMaster.getControlMode() );
			driveStatus.setBrakeMode( lMotorMaster.getBrakeEnableDuringNeutral() );
			
			// get encoder values from hardware, set in Drive
			driveStatus.setLeftDistanceInches(  Constants.lMotorPolarity * rotationsToInches( lMotorMaster.getPosition() ));
			driveStatus.setRightDistanceInches( Constants.rMotorPolarity * rotationsToInches( rMotorMaster.getPosition() ));
	
			driveStatus.setLeftSpeedInchesPerSec(  Constants.lMotorPolarity * rpmToInchesPerSecond( lMotorMaster.getSpeed() ));
			driveStatus.setRightSpeedInchesPerSec( Constants.rMotorPolarity * rpmToInchesPerSecond( rMotorMaster.getSpeed() ));
				
			/*
			 * measured angle decreases with clockwise rotation
			 * it should increase with clockwise rotation (according to
			 * documentation, and standard right hand rule convention
			 * negate it here to correct
			 */
			driveStatus.setHeadingDeg( -imu.getHeading() );
	
			driveStatus.setMotorCurrent(lMotorMaster.getOutputCurrent(), rMotorMaster.getOutputCurrent() );
			driveStatus.setMotorStatus(lMotorMaster.get(), rMotorMaster.get() );
			driveStatus.setMotorPIDError(lMotorMaster.getClosedLoopError(), rMotorMaster.getClosedLoopError() );
		}
	}
		
	private void sendCommands()
	{
		DriveCommand newCmd = drive.getCommand();
		
		// Watchdog timer  
		double currentTime = Timer.getFPGATimestamp();
		if (currentTime - newCmd.getCommandTime() > Constants.kDriveWatchdogTimerThreshold)
		{
			// Halt robot if new command hasn't been sent in a while
			stopMotors();
			return;
		}
				
		synchronized(newCmd)	// lock DriveCommand so no one changes it under us while we are sending the commands
		{
			setControlMode(newCmd);
			setMotors(newCmd);
			setBrakeMode(newCmd);
			resetEncoders(newCmd);
		}
	}
	
	
	private void setControlMode(DriveCommand newCmd)
    {
		TalonControlMode newMode = newCmd.getTalonControlMode();
		
		if (newMode != driveStatus.getTalonControlMode())
		{
            lMotorMaster.changeControlMode(newMode);
            rMotorMaster.changeControlMode(newMode);
			
	        switch (newMode)
	        {
	        	case PercentVbus: 
	                break;
	
	        	case Position:
	    			lMotorMaster.setProfile(kBaseLockControlSlot);
	    			rMotorMaster.setProfile(kBaseLockControlSlot);

	    			lMotorMaster.setAllowableClosedLoopErr(Constants.kDriveBaseLockAllowableError);
	    			rMotorMaster.setAllowableClosedLoopErr(Constants.kDriveBaseLockAllowableError);

	        		lMotorMaster.set(lMotorMaster.getPosition());
	        		rMotorMaster.set(rMotorMaster.getPosition());
	        		break;
	        		
	        	case Speed:
	        		lMotorMaster.setProfile(kVelocityControlSlot);
	        		rMotorMaster.setProfile(kVelocityControlSlot);
	        		
	        		lMotorMaster.setAllowableClosedLoopErr(Constants.kDriveVelocityAllowableError);
	        		rMotorMaster.setAllowableClosedLoopErr(Constants.kDriveVelocityAllowableError);
	        		break;
	        		
	        	case Disabled:
	        	default:
	        		break;
	        }
		}
	}
	
	
	
	private void setBrakeMode(DriveCommand newCmd)
	{
		boolean newBrake = newCmd.getBrake();
		setBrakeMode(newBrake);
	}
	
	
	private void setBrakeMode(boolean newBrake) 
	{
		if (newBrake != driveStatus.getBrakeMode()) 
		{
			lMotorMaster.enableBrakeMode(newBrake);
			rMotorMaster.enableBrakeMode(newBrake);
			lMotorSlave1.enableBrakeMode(newBrake);
			lMotorSlave2.enableBrakeMode(newBrake);
			rMotorSlave1.enableBrakeMode(newBrake);
			rMotorSlave2.enableBrakeMode(newBrake);
		}
	}
	

	public void enableCurrentLimit(boolean enable)
	{
		if (enable != currentLimitEnabled)
		{
			lMotorMaster.EnableCurrentLimit(enable);
			lMotorSlave1.EnableCurrentLimit(enable);
			lMotorSlave2.EnableCurrentLimit(enable);
			rMotorMaster.EnableCurrentLimit(enable);
			rMotorSlave1.EnableCurrentLimit(enable);
			rMotorSlave2.EnableCurrentLimit(enable);
			currentLimitEnabled = enable;
		}
	}
	
		
	private void setMotors(DriveCommand newCmd)
    {
		double lMotorCtrl = newCmd.getLeftMotor();
		double rMotorCtrl = newCmd.getRightMotor();
		
        switch (newCmd.getTalonControlMode())	// assuming new mode is already configured
        {
        	case PercentVbus:
        		// DriveCommand given in range +/-1, with 1 representing full throttle
        		lMotorMaster.set(Constants.lMotorPolarity * lMotorCtrl);
        		rMotorMaster.set(Constants.rMotorPolarity * rMotorCtrl); 
        		break;

        	case Position:
        		// initial position already set on mode change
        		break;
        		
        	case Speed:
        		// DriveCommand given in inches/sec
        		// Talon SRX needs RPM in closed-loop mode.
        		// convert inches/sec to RPM
           		lMotorMaster.set(Constants.lMotorPolarity * inchesPerSecondToRpm(lMotorCtrl)); 
        		rMotorMaster.set(Constants.rMotorPolarity * inchesPerSecondToRpm(rMotorCtrl));
        		break;
        		
        	case Disabled:
        	default:
        		lMotorMaster.set(0);
        		rMotorMaster.set(0);
        		break;
        }
	}

	// Talon SRX reports position in rotations while in closed-loop Position mode
	private static double rotationsToInches(double _rotations) {	return _rotations * Constants.kQuadEncoderGain * Constants.kDriveWheelCircumInches; }
	private static double inchesToRotations(double _inches) { return _inches / (Constants.kQuadEncoderGain * Constants.kDriveWheelCircumInches); }

	// Talon SRX reports speed in RPM while in closed-loop Speed mode
	private static double rpmToInchesPerSecond(double _rpm) { return rotationsToInches(_rpm) / 60.0; }
	private static double inchesPerSecondToRpm(double _inches_per_second) { return inchesToRotations(_inches_per_second) * 60.0; }

	
	

	private void resetEncoders(DriveCommand newCmd)
	{
		if (newCmd.getResetEncoders())
		{
			lMotorMaster.setPosition(0);
			rMotorMaster.setPosition(0);
				
			lMotorMaster.setEncPosition(0);
			rMotorMaster.setEncPosition(0);
			
			// cannot reset gyro heading in hardware.  
			// calibration to desired initial pose is done in RobotState.reset() called from Robot.autonomousInit()  
		}
	}	


};
