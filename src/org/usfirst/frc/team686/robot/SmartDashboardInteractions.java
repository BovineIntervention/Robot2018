package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.*;
import org.usfirst.frc.team686.robot.auto.modes.*;
import org.usfirst.frc.team686.robot.lib.joystick.ArcadeDriveJoystick;
import org.usfirst.frc.team686.robot.lib.joystick.JoystickControlsBase;

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
        DRIVE_STRAIGHT("Drive Straight");
    	
        public final String name;

        AutoModeOption(String name) {
            this.name = name;
        }
    }

    SendableChooser<JoystickOption> joystickModeChooser;
    
    enum JoystickOption 
    {
        ARCADE_DRIVE("Arcade Drive");
        public final String name;

        JoystickOption(String name) {
            this.name = name;
        }
    }
    
    
    public void initWithDefaults() 
    {
    	autoModeChooser = new SendableChooser<AutoModeOption>();
    	autoModeChooser.addDefault(AutoModeOption.STAND_STILL.toString(),    AutoModeOption.STAND_STILL);
    	autoModeChooser.addObject(AutoModeOption.DRIVE_STRAIGHT.toString(),    AutoModeOption.DRIVE_STRAIGHT);
    	SmartDashboard.putData("Auto Mode Chooser", autoModeChooser);
    	
    	joystickModeChooser = new SendableChooser<JoystickOption>();
    	joystickModeChooser.addDefault(JoystickOption.ARCADE_DRIVE.toString(),        JoystickOption.ARCADE_DRIVE);
    	//SmartDashboard.putData("Joystick Chooser", joystickModeChooser);
    	
     }
    
    
    public AutoModeBase getAutoModeSelection() 
    {
    	AutoModeOption selMode = (AutoModeOption)autoModeChooser.getSelected(); 

        //DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();
        //boolean isBlue = (alliance == DriverStation.Alliance.Blue);
        
        FieldDimensions fieldDimensions = new FieldDimensionsRed();

        /*
        System.out.print("Alliance detected as: ");
        if (alliance == DriverStation.Alliance.Red) {
            System.out.println("Red");
        } else if (alliance == DriverStation.Alliance.Blue) {
                System.out.println("Blue");
        } else {
            System.out.println("INVALID");
        }
        */
        
    	switch (selMode)
    	{
    	
    	case STAND_STILL:
    		System.out.println("RUNNING: StandStillMode");
			return new StandStillMode();
			
    	case DRIVE_STRAIGHT:
    		System.out.println("RUNNING: DriveStraightMode");
			return new DriveStraightMode(0, false);
			
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
			

    	default:
            System.out.println("ERROR: unexpected joystick selection: " + selMode);
			return ArcadeDriveJoystick.getInstance();
    	}
    
    }
}
    
   


