package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.Constants.RobotSelectionEnum;
import org.usfirst.frc.team686.robot.auto.*;
import org.usfirst.frc.team686.robot.auto.modes.*;
import org.usfirst.frc.team686.robot.command_status.DriveState;
import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.lib.joystick.ArcadeDriveJoystick;
import org.usfirst.frc.team686.robot.lib.joystick.ButtonBoard;
import org.usfirst.frc.team686.robot.lib.joystick.JoystickControlsBase;
import org.usfirst.frc.team686.robot.subsystems.Drive;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.subsystems.Intake;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.command_status.DriveCommand;

import java.util.Optional;
import java.util.TimeZone;

import org.usfirst.frc.team686.robot.lib.util.CrashTracker;
import org.usfirst.frc.team686.robot.lib.util.DataLogController;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.DriveLoop;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop;
import org.usfirst.frc.team686.robot.loops.IntakeLoop;
import org.usfirst.frc.team686.robot.loops.LoopController;
import org.usfirst.frc.team686.robot.loops.RobotStateLoop;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;


public class Robot extends IterativeRobot {
	
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	
	JoystickControlsBase controls = ArcadeDriveJoystick.getInstance();
	ButtonBoard buttonBoard = ButtonBoard.getInstance();

	RobotState robotState = RobotState.getInstance();
	Drive drive = Drive.getInstance();
	ElevatorArmBar elevatorArmBar = ElevatorArmBar.getInstance();
	ElevatorState elevatorState = ElevatorState.getInstance();
	Intake intake = Intake.getInstance();
	
	AutoModeExecuter autoModeExecuter = null;
	
	LoopController loopController;
	
	SmartDashboardInteractions smartDashboardInteractions;
	DataLogController robotLogger;

	CameraServer cameraServer = CameraServer.getInstance();
//	UsbCamera camera = new UsbCamera("USB Camera 1", 1);

	
	enum OperationalMode 
    {
    	DISABLED(0), AUTONOMOUS(1), TELEOP(2), TEST(3);
    	
    	private int val;
    	
    	private OperationalMode (int val) {this.val = val;}
    	public int getVal() {return val;}
    } 
    
    OperationalMode operationalMode = OperationalMode.DISABLED;
    
    public Robot() {
    	CrashTracker.logRobotConstruction();
    }
    
    
	@Override
	public void robotInit() {
		try
    	{
    		CrashTracker.logRobotInit();
    		
    		LiveWindow.disableTelemetry(pdp);	// workaround to get rid of CTRE CAN Receive Timeout errors for PowerDistributionPanel.getPDPTotalCurrent()
    											// TODO: re-enable to see if WPILib fixes this in the future
    		
    		loopController = new LoopController();
    		loopController.register(drive.getVelocityPIDLoop());
    		loopController.register(DriveLoop.getInstance());
    		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)
    			loopController.register(ElevatorLoop.getInstance());
      		loopController.register(ArmBarLoop.getInstance());
      		loopController.register(IntakeLoop.getInstance());
       		loopController.register(RobotStateLoop.getInstance());
    		
    		smartDashboardInteractions = new SmartDashboardInteractions();
    		smartDashboardInteractions.initWithDefaults();
    		
    		// set datalogger and time info
    		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    		
    		robotLogger = DataLogController.getRobotLogController();
    		robotLogger.register(Drive.getInstance().getLogger());
    		robotLogger.register(drive.getCommand().getLogger());
    		robotLogger.register(DriveState.getInstance().getLogger());
    		robotLogger.register(ElevatorArmBar.getInstance().getLogger());
    		robotLogger.register(Intake.getInstance().getLogger());
    		robotLogger.register(RobotState.getInstance().getLogger());
    		
    		setInitialPose(new Pose());

    		cameraServer.startAutomaticCapture();
//    		cameraServer.getVideo();
//    		cameraServer.putVideo("cam", 640, 480);
//    		
//    		MjpegServer mjpegServer = new MjpegServer("serve_Blur", "http://roborio-686-frc.local:1181/stream.mjpg", 1181);
//    		mjpegServer.setSource(camera); 
//    		CvSink cvSink = new CvSink("USB Camera 1");
//    		cvSink.setSource(camera);

    		
    	}
    	catch(Throwable t)
    	{
    		CrashTracker.logThrowableCrash(t);
    		throw t;
    	}
	}
	
	public void setInitialPose (Pose _initialPose){
		robotState.reset(Timer.getFPGATimestamp(), DriveState.getInstance().getLeftDistanceInches(), DriveState.getInstance().getRightDistanceInches(), _initialPose);
    	System.out.println("InitialPose: " + _initialPose);
    }
    
    public void zeroAllSensors()
    {
    	drive.zeroSensors();
    	elevatorArmBar.zeroSensors();
		// mSuperstructure.zeroSensors();
    }
    
    public void stopAll()
    {
    	drive.stop();
    	elevatorArmBar.stop();
		// mSuperstructure.stop();
    }



	/****************************************************************
	 * DISABLED MODE
	 ****************************************************************/

	@Override
	public void disabledInit()
	{
		operationalMode = OperationalMode.DISABLED;
		boolean logToFile = true;
		boolean logToSmartDashboard = true;
		robotLogger.setOutputMode(logToFile, logToSmartDashboard);

		try
		{
			CrashTracker.logDisabledInit();
			if (autoModeExecuter != null)
			{
				autoModeExecuter.stop();
			}
			autoModeExecuter = null;

			stopAll(); // stop all actuators
			loopController.start();
			elevatorArmBar.disable();
		}
		catch (Throwable t)
		{
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}

	@Override
	public void disabledPeriodic()
	{
		try
		{
			stopAll(); // stop all actuators

			System.gc(); // runs garbage collector
		}
		catch (Throwable t)
		{
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}



	/****************************************************************
	 * AUTONOMOUS MODE
	 ****************************************************************/

	@Override
	public void autonomousInit() {
    	operationalMode = OperationalMode.AUTONOMOUS;
    	boolean logToFile = true;
    	boolean logToSmartDashboard = true;
    	robotLogger.setOutputMode(logToFile, logToSmartDashboard);

    	try
    	{
			elevatorArmBar.enable();

			CrashTracker.logAutoInit();

			if(autoModeExecuter != null){
    			autoModeExecuter.stop();
    		}
    		autoModeExecuter = null;
    		
			autoModeExecuter = new AutoModeExecuter();
			autoModeExecuter.setAutoMode( smartDashboardInteractions.getAutoModeSelection() );

			setInitialPose( autoModeExecuter.getAutoMode().getInitialPose() );
 
			autoModeExecuter.start();
    	}
    	catch(Throwable t)
    	{
    		CrashTracker.logThrowableCrash(t);
    		throw t;
    	}
		
	}

	@Override
	public void autonomousPeriodic() {
    	try
    	{
    		
    	}
    	catch (Throwable t)
    	{
    		CrashTracker.logThrowableCrash(t);
    		throw t;
    	}
	}
	
	
	/****************************************************************
	 * TELEOP MODE
	 ****************************************************************/

	PointTurnMode pointTurnAutoMode = new PointTurnMode( 0.0 );	
	
	@Override
	public void teleopInit(){
		operationalMode = OperationalMode.TELEOP;
		boolean logToFile = true;
		boolean logToSmartDashboard = true;
		robotLogger.setOutputMode(logToFile, logToSmartDashboard);

		try 
		{
			CrashTracker.logTeleopInit();

			// Select joystick control method
			controls = smartDashboardInteractions.getJoystickControlsMode();

			// Configure looper
			loopController.start();
			elevatorArmBar.enable();
			
			if(autoModeExecuter != null){
    			autoModeExecuter.stop();
    		}
			// configure PointTurn as auto mode during teleop
			autoModeExecuter = new AutoModeExecuter();
			autoModeExecuter.setAutoMode( pointTurnAutoMode );
    		
			drive.setOpenLoop(DriveCommand.COAST());
			
			elevatorArmBar.set(ElevatorArmBarStateEnum.GROUND, false);	// prepare to intake during teleop
			intake.stopIntake();
			intake.grabberIn();
		} 
		catch (Throwable t) 
		{
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}
	
	int prevButtonBoardDirection = -1;
	
	@Override
	public void teleopPeriodic() 
	{
		try
		{
			// elevator & arm bar controls
			elevatorArmBar.processInputs(
					controls.getButton(Constants.kElevatorManualUpButton),
					controls.getButton(Constants.kElevatorManualDownButton),
					controls.getButton(Constants.kIntakeButton),
					controls.getButton(Constants.kOuttakeButton),
					buttonBoard.getButton(Constants.kElevatorGroundButton),
					buttonBoard.getButton(Constants.kElevatorExchangeButton),
					buttonBoard.getButton(Constants.kElevatorSwitchButton),
					buttonBoard.getButton(Constants.kElevatorScaleLowButton),
					buttonBoard.getButton(Constants.kElevatorScaleMedButton),
					buttonBoard.getButton(Constants.kElevatorScaleHighButton));
								
			// intake controls
			intake.processInputs(
					controls.getButton(Constants.kIntakeButton), 
					controls.getButton(Constants.kOuttakeButton), 
					controls.getButton(Constants.kGrabberButton));
						
			// turn-to-angle controls (assumes that AutoModeExectuer is already set to PointTurnAutoMode)
//			int pointTurnDirection = buttonBoard.getPOV();
//			if (pointTurnDirection >= 0 && (pointTurnDirection != prevButtonBoardDirection))
//			{
//				System.out.println(pointTurnDirection);
//				pointTurnAutoMode.setHeading(-pointTurnDirection);	// joystick and robot directions are opposites of each other
//				if (!autoModeExecuter.getAutoMode().isActive())
//					autoModeExecuter.start();
//			}
//			prevButtonBoardDirection = pointTurnDirection;
				
			// drive controls
			if (!autoModeExecuter.getAutoMode().isActive())	// ignore joystick when doing auto turns
			{
				autoModeExecuter.stop();	// if point turn action has completed, stop the auto mode so that we can start a new point turn action
				
				// slow down drivetrain when elevator is extended
				double elevatorHeight = elevatorState.getPositionInches();
				double normalizedHeight = (elevatorHeight/Constants.kElevatorMaxHeightLimit);
				double slope = -(1.0 - Constants.kDriveScaleFactorAtMaxElevatorHeight);
				double scaleFactor = slope*normalizedHeight + 1.0;
				DriveCommand driveCmd = controls.getDriveCommand();
				driveCmd.scale(scaleFactor);
				
				drive.setOpenLoop(driveCmd);
			}
		
		}
		catch (Throwable t)
		{
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}



	/****************************************************************
	 * TEST MODE
	 ****************************************************************/

	@Override
	public void testInit() 
	{
		loopController.start();
	}

	@Override
	public void testPeriodic()
	{
		drive.testDriveSpeedControl();
	}
	
	
	// called after disabledPeriodic, autoPeriodic, and teleopPeriodic 
	@Override
	public void robotPeriodic()
	{
		robotLogger.log();
	}


	
	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			put("OperationalMode", operationalMode.getVal());
        }
    };
    
    public DataLogger getLogger() { return logger; }
}

