package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;

import java.util.ArrayList;
import java.util.List;

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
    		
    	joystickModeChooser = new SendableChooser<JoystickOption>();
    	joystickModeChooser.addDefault(JoystickOption.ARCADE_DRIVE.toString(),        JoystickOption.ARCADE_DRIVE);
    	//SmartDashboard.putData("Joystick Chooser", joystickModeChooser);
    	
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
			

    	default:
            System.out.println("ERROR: unexpected joystick selection: " + selMode);
			return ArcadeDriveJoystick.getInstance();
    	}
    
    }
}
    
   


