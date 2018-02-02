package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeExecuter;
import org.usfirst.frc.team686.robot.command_status.DriveStatus;
import org.usfirst.frc.team686.robot.command_status.RobotState;
import org.usfirst.frc.team686.robot.lib.joystick.ArcadeDriveJoystick;
import org.usfirst.frc.team686.robot.lib.joystick.JoystickControlsBase;
import org.usfirst.frc.team686.robot.lib.sensors.SwitchableCameraServer;
import org.usfirst.frc.team686.robot.loop.DriveLoop;
import org.usfirst.frc.team686.robot.loop.LoopController;
import org.usfirst.frc.team686.robot.loop.RobotStateLoop;
import org.usfirst.frc.team686.robot.subsystems.Drive;
import org.usfirst.frc.team686.robot.util.DataLogController;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.command_status.DriveCommand;

import java.util.List;
import java.util.TimeZone;

import org.usfirst.frc.team686.robot.Robot.OperationalMode;
import org.usfirst.frc.team686.robot.lib.util.CrashTracker;
import org.usfirst.frc.team686.robot.lib.util.Pose;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;


public class Robot extends IterativeRobot {
	
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	
	JoystickControlsBase controls = ArcadeDriveJoystick.getInstance();
	RobotState robotState = RobotState.getInstance();
	Drive drive = Drive.getInstance();
	
	AutoModeExecuter autoModeExecuter = null;
	
	LoopController loopController;
	
	SmartDashboardInteractions smartDashboardInteractions;
	DataLogController robotLogger;
	
	SwitchableCameraServer switchableCameraServer;
	boolean cameraSwitchButtonPrev = false;
	
	CameraServer cameraServer;

	
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
    		
    		loopController = new LoopController();
    		loopController.register(drive.getVelocityPIDLoop());
    		loopController.register(DriveLoop.getInstance());
    		loopController.register(RobotStateLoop.getInstance());
    		
    		smartDashboardInteractions = new SmartDashboardInteractions();
    		smartDashboardInteractions.initWithDefaults();
    		
    		// set datalogger and time info
    		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    		
    		robotLogger = DataLogController.getRobotLogController();
    		robotLogger.register(Drive.getInstance().getLogger());
    		robotLogger.register(drive.getCommand().getLogger());
    		robotLogger.register(DriveStatus.getInstance().getLogger());
    		robotLogger.register(RobotState.getInstance().getLogger());
    		
    		setInitialPose(new Pose());
    		
    		//switchableCameraServer = new SwitchableCameraServer("cam0");
    		
    		//cameraServer.getInstance().startAutomaticCapture();
    	}
    	catch(Throwable t)
    	{
    		CrashTracker.logThrowableCrash(t);
    		throw t;
    	}
	}
	
	public void setInitialPose (Pose _initialPose){
    	robotState.reset(Timer.getFPGATimestamp(), DriveStatus.getInstance().getLeftDistanceInches(), DriveStatus.getInstance().getRightDistanceInches(), _initialPose);
    	System.out.println("InitialPose: " + _initialPose);
    }
    
    public void zeroAllSensors()
    {
    	drive.zeroSensors();
    }
    
    public void stopAll()
    {
    	drive.stop();
    }


	@Override
	public void autonomousInit() {
    	operationalMode = OperationalMode.AUTONOMOUS;
    	boolean logToFile = true;
    	boolean logToSmartDashboard = true;
    	robotLogger.setOutputMode(logToFile, logToSmartDashboard);
    	
    	try{
    		
    		CrashTracker.logAutoInit();
    		if(autoModeExecuter != null){
    			autoModeExecuter.stop();
    		}
    		autoModeExecuter = null;
    		
    		autoModeExecuter = new AutoModeExecuter();
    		List<AutoModeBase> actions = smartDashboardInteractions.getAutoModeSelection();
    		
    		for(int i = 0; i < actions.size(); i++){
    			autoModeExecuter.setAutoMode(actions.get(i));
    		}
    		
    		setInitialPose(autoModeExecuter.getAutoMode().getInitialPose());
    		
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

			drive.setOpenLoop(DriveCommand.NEUTRAL());

		} 
		catch (Throwable t) 
		{
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}
	@Override
	public void teleopPeriodic() {
		try{
			drive.setOpenLoop(controls.getDriveCommand());
			
			// CAMERA
			/*
			if (cameraSwitchButton && !camaraSwitchButtonPrev)
			{
				switchableCameraServer.changeToNextCamera();
			}
			camaraSwitchButtonPrev = cameraSwitchButton;
			*/
		}
		catch (Throwable t){
			CrashTracker.logThrowableCrash(t);
			throw t;
		}
	}

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
		//switchableCameraServer.outputToSmartDashboard();
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

