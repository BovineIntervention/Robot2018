package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.*;
import org.usfirst.frc.team686.robot.auto.modes.*;

import org.usfirst.frc.team686.robot.lib.joystick.*;

import org.usfirst.frc.team686.robot.lib.util.Pose;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;




// TODO: learn how to make custom LabView? GUI interface


/**
 * Controls the interactive elements of SmartDashboard.
 *
 * Keeps the network tables keys in one spot and enforces autonomous mode
 * invariants.
 */
public class SmartDashboardInteractions 
{

    SendableChooser<AutoModeOption> autoModeChooser;
    
    enum AutoModeOption 
    {
        STAND_STILL("Stand Still"),
        DRIVE_STRAIGHT("Drive Straight"),
    	POINT_TURN_TEST("Point Turn Test"),
    	PATH_FOLLWING_DEBUG("Debug Path Follower");
    	
        public final String name;

        AutoModeOption(String name) {
            this.name = name;
        }
    }

    
    SendableChooser<AutoStartOption> startPositionChooser;
    
    enum AutoStartOption 
    {
        START_POS_1("Left Start Pos", 1, new Pose(1,1,0)),	// TODO: enter correct start position 
        START_POS_2("Center Start Pos", 2, new Pose(2,2,0)), 	// TODO: enter correct start position
        START_POS_3("Right Start Pos", 3, new Pose(3,3,0));	// TODO: enter correct start position

        public String name;
        public int number;
        public Pose startPose;

        AutoStartOption(String _name, int _number, Pose _startPose)
        {
            name = _name;
            number = _number;
            startPose = _startPose;
        }
    }

    
    SendableChooser<JoystickOption> joystickModeChooser;
    
    enum JoystickOption 
    {
        ARCADE_DRIVE("Arcade Drive"),
        TRIGGER_DRIVE("Trigger Drive"),				// works for Xbox controller and Xbox steering wheel
        TANK_DRIVE("Tank Drive"),
        CHEESY_ARCADE_DRIVE("Cheesy Arcade Drive"),
        CHEESY_TRIGGER_DRIVE("Cheesy Trigger Drive"),
        CHEESY_2STICK_DRIVE("Cheesy Two-Stick Drive");
        public final String name;

        JoystickOption(String name) {
            this.name = name;
        }
    }
    
    
    public void initWithDefaults() 
    {
    	autoModeChooser = new SendableChooser<AutoModeOption>();
    	autoModeChooser.addDefault(AutoModeOption.STAND_STILL.name,    AutoModeOption.STAND_STILL);
    	autoModeChooser.addObject(AutoModeOption.DRIVE_STRAIGHT.name,    AutoModeOption.DRIVE_STRAIGHT);
    	autoModeChooser.addObject( AutoModeOption.POINT_TURN_TEST.name, AutoModeOption.POINT_TURN_TEST);
    	autoModeChooser.addObject( AutoModeOption.PATH_FOLLWING_DEBUG.name, AutoModeOption.PATH_FOLLWING_DEBUG);
    	SmartDashboard.putData("Auto Mode Chooser", autoModeChooser);
    	
    	startPositionChooser = new SendableChooser<AutoStartOption>();
    	startPositionChooser.addDefault(AutoStartOption.START_POS_1.name, AutoStartOption.START_POS_1);
    	startPositionChooser.addObject( AutoStartOption.START_POS_2.name, AutoStartOption.START_POS_2);
    	startPositionChooser.addObject( AutoStartOption.START_POS_3.name, AutoStartOption.START_POS_3);
    	SmartDashboard.putData("Start Position Chooser", startPositionChooser);
    	
    	joystickModeChooser = new SendableChooser<JoystickOption>();
    	joystickModeChooser.addDefault(JoystickOption.ARCADE_DRIVE.name,        JoystickOption.ARCADE_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.TRIGGER_DRIVE.name,        JoystickOption.TRIGGER_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.TANK_DRIVE.name, 	      JoystickOption.TANK_DRIVE);
     	joystickModeChooser.addObject(JoystickOption.CHEESY_ARCADE_DRIVE.name,  JoystickOption.CHEESY_ARCADE_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.CHEESY_TRIGGER_DRIVE.name, JoystickOption.CHEESY_TRIGGER_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.CHEESY_2STICK_DRIVE.name,  JoystickOption.CHEESY_2STICK_DRIVE);
    	SmartDashboard.putData("Joystick Chooser", joystickModeChooser);
    	
     }
    
    
    public AutoModeBase getAutoModeSelection() 
    {
    	AutoModeOption selMode = (AutoModeOption)autoModeChooser.getSelected(); 

        DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();
        boolean isBlue = (alliance == DriverStation.Alliance.Blue);
        
        FieldDimensions fieldDimensions = new FieldDimensionsRed();
        if (isBlue) {
        	fieldDimensions = new FieldDimensionsBlue();
        }

        System.out.print("Alliance detected as: ");
        if (alliance == DriverStation.Alliance.Red) {
            System.out.println("Red");
        } else if (alliance == DriverStation.Alliance.Blue) {
                System.out.println("Blue");
        } else {
            System.out.println("INVALID");
        }
        
        
    	switch (selMode)
    	{
    	
    	case STAND_STILL:
			return new StandStillMode();
			
    	case DRIVE_STRAIGHT:
			return new DriveStraightMode(0, false);
			
    	case POINT_TURN_TEST:
    		return new PointTurnTestMode();
    		
    	case PATH_FOLLWING_DEBUG:
    		return new DebugPathFollowingMode();
    		
		default:
            System.out.println("ERROR: unexpected auto mode: " + selMode);
			return new StandStillMode();
	    }
    }


    public JoystickControlsBase getJoystickControlsMode() 
    {
    	JoystickOption selMode = (JoystickOption)joystickModeChooser.getSelected(); 
    	
   	
    	
    	switch (selMode)
    	{
    	case ARCADE_DRIVE:
			return ArcadeDriveJoystick.getInstance();
			
    	case TRIGGER_DRIVE:
			return TriggerDriveJoystick.getInstance();
			
    	case TANK_DRIVE:
    		return TankDriveJoystick.getInstance();
    		
    	case CHEESY_ARCADE_DRIVE:
    		return CheesyArcadeDriveJoystick.getInstance();
    		
    	case CHEESY_TRIGGER_DRIVE:
    		return CheesyTriggerDriveJoystick.getInstance();
    		
    	case CHEESY_2STICK_DRIVE:
    		return CheesyTwoStickDriveJoystick.getInstance();


    	default:
            System.out.println("ERROR: unexpected joystick selection: " + selMode);
			return ArcadeDriveJoystick.getInstance();
    	}
    
    }
}
    
   


