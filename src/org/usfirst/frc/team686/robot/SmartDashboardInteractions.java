package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;

import java.util.ArrayList;
import java.util.List;

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

    SendableChooser<StartOption> startChooser;

    enum StartOption
    {
        Other_START("Other Start"),
        EXCHANGE_START("Exchange Start"),
        OTHER_START("Other Start");

        public final String name;

        StartOption(String name) {
            this.name = name;
        }
    }

    SendableChooser<SwitchOption> switchChooser;

    enum SwitchOption
    {
        LEFT_SWITCH("Left Switch"),
        RIGHT_SWITCH("Right Switch");

        public final String name;

        SwitchOption(String name){
            this.name = name;
        }
    }


    SendableChooser<ScaleOption> scaleChooser;

    enum ScaleOption
    {
        LEFT_SCALE("Left Scale"),
        RIGHT_SCALE("Right Scale");

        public final String name;

        ScaleOption(String name){
            this.name= name;
        }
    }

    enum AutoModeOption
    {
        STAND_STILL("Stand Still"),
        DRIVE_STRAIGHT("Drive Straight");
    	
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
        startChooser = new SendableChooser<StartOption>();
        startChooser.addDefault(StartOption.Other_START.toString(),    StartOption.Other_START);
        startChooser.addObject(StartOption.EXCHANGE_START.toString(),    StartOption.EXCHANGE_START);
        startChooser.addObject(StartOption.OTHER_START.toString(),    StartOption.OTHER_START);
        SmartDashboard.putData("Start Chooser", startChooser);

        switchChooser = new SendableChooser<SwitchOption>();
        switchChooser.addObject(SwitchOption.LEFT_SWITCH.toString(),    SwitchOption.LEFT_SWITCH);
        switchChooser.addObject(SwitchOption.RIGHT_SWITCH.toString(),    SwitchOption.RIGHT_SWITCH);
        SmartDashboard.putData("Switch Chooser", switchChooser);

        scaleChooser = new SendableChooser<ScaleOption>();
        scaleChooser.addObject(ScaleOption.LEFT_SCALE.toString(),    ScaleOption.LEFT_SCALE);
        scaleChooser.addObject(ScaleOption.RIGHT_SCALE.toString(),    ScaleOption.RIGHT_SCALE);
        SmartDashboard.putData("Scale Chooser", scaleChooser);

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
    
    
    public List<AutoModeBase> getAutoModeSelection()
    {

    	/*
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
        */

    	StartOption start = (StartOption)startChooser.getSelected();
    	SwitchOption switchOption = (SwitchOption)switchChooser.getSelected();
    	ScaleOption scale = (ScaleOption)scaleChooser.getSelected();
        
    	List<AutoModeBase> actions = new ArrayList<AutoModeBase>();
    	switch (start)
    	{
    	
    	case Other_START:
    		if( switchOption == SwitchOption.LEFT_SWITCH ){
    			actions.add(new OtherStartToLeftSwitchMode());
    			if( scale == ScaleOption.LEFT_SCALE ){
    				actions.add(new LeftSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new LeftSwitchToRightScaleMode());
    			}
    		}else if( switchOption == SwitchOption.RIGHT_SWITCH ){
    			actions.add(new OtherStartToRightSwitchMode());
    			if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToRightScaleMode());
    			}
    		}else if( scale == ScaleOption.LEFT_SCALE ){
    			actions.add(new OtherStartToLeftScaleMode());
    		}else if( scale == ScaleOption.RIGHT_SCALE ){
    			actions.add(new OtherStartToRightScaleMode());
    		}
			return actions;

    	case EXCHANGE_START:
    		if( switchOption == SwitchOption.LEFT_SWITCH ){
    			actions.add(new ExchangeStartToLeftSwitchMode());
    			if( scale == ScaleOption.LEFT_SCALE ){
    				actions.add(new LeftSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new LeftSwitchToRightScaleMode());
    			}
    		}else if( switchOption == SwitchOption.RIGHT_SWITCH ){
    			actions.add(new ExchangeStartToRightSwitchMode());
    			if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToRightScaleMode());
    			}
    		}else if( scale == ScaleOption.LEFT_SCALE ){
    			actions.add(new ExchangeStartToLeftScaleMode());
    		}else if( scale == ScaleOption.RIGHT_SCALE ){
    			actions.add(new ExchangeStartToRightScaleMode());
    		}
			return actions;
			
    	case OTHER_START:
    		if( switchOption == SwitchOption.LEFT_SWITCH ){
    			actions.add(new OtherStartToLeftSwitchMode());
    			if( scale == ScaleOption.LEFT_SCALE ){
    				actions.add(new LeftSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new LeftSwitchToRightScaleMode());
    			}
    		}else if( switchOption == SwitchOption.RIGHT_SWITCH ){
    			actions.add(new OtherStartToRightSwitchMode());
    			if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToLeftScaleMode());
    			}else if( scale == ScaleOption.RIGHT_SCALE ){
    				actions.add(new RightSwitchToRightScaleMode());
    			}
    		}else if( scale == ScaleOption.LEFT_SCALE ){
    			actions.add(new OtherStartToLeftScaleMode());
    		}else if( scale == ScaleOption.RIGHT_SCALE ){
    			actions.add(new OtherStartToRightScaleMode());
    		}
			return actions;
			
		default:
            System.out.println("ERROR: unexpected start: " + start);
            actions.add(new StandStillMode());
			return actions;
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
    
   


