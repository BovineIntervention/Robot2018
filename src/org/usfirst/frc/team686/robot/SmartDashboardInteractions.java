package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.AutoActions;
import org.usfirst.frc.team686.robot.auto.actions.AutoActions.InitialStateEnum;
import org.usfirst.frc.team686.robot.auto.actions.AutoActions.TargetEnum;
import org.usfirst.frc.team686.robot.auto.actions.SeriesAction;
import org.usfirst.frc.team686.robot.auto.actions.WaitAction;

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

    static SendableChooser<StartOption> startChooser;

    enum StartOption
    {
        LEFT_START("Left Start"),
        CENTER_START("Center Start"),
        RIGHT_START("Right Start");

        public final String name;

        StartOption(String name) {
            this.name = name;
        }
    }

    
    static SendableChooser<PriorityOption> priorityChooser;
    
    enum PriorityOption
    {
    	SWITCH_IF_SAME_SIDE("Switch If Same Side"),
    	SCALE_IF_SAME_SIDE("Scale If Same Side"),
    	SWITCH_ALWAYS("Switch Always"),
    	SCALE_ALWAYS("Scale Always");
    	
    	public final String name;
    	
    	PriorityOption(String name) {
    		this.name= name;
    	}
    	
    }
    
    
    SendableChooser startDelayChooser;
   
    
    
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
        startChooser.addDefault(StartOption.LEFT_START.toString(),    StartOption.LEFT_START);
        startChooser.addObject(StartOption.CENTER_START.toString(),    StartOption.CENTER_START);
        startChooser.addObject(StartOption.RIGHT_START.toString(),    StartOption.RIGHT_START);
        SmartDashboard.putData("Start Chooser", startChooser);
        
        priorityChooser = new SendableChooser<PriorityOption>();
        priorityChooser.addDefault(PriorityOption.SWITCH_IF_SAME_SIDE.toString(), PriorityOption.SWITCH_IF_SAME_SIDE);
        priorityChooser.addObject(PriorityOption.SCALE_IF_SAME_SIDE.toString(), PriorityOption.SCALE_IF_SAME_SIDE);
        priorityChooser.addObject(PriorityOption.SWITCH_ALWAYS.toString(), PriorityOption.SWITCH_ALWAYS);
        priorityChooser.addObject(PriorityOption.SCALE_ALWAYS.toString(), PriorityOption.SCALE_ALWAYS);
        SmartDashboard.putData("Priority Chooser", priorityChooser);
        
        startDelayChooser = new SendableChooser();
        startDelayChooser.addDefault(Integer.toString(1), 1);
        startDelayChooser.addObject(Integer.toString(2), 2);
        startDelayChooser.addObject(Integer.toString(3), 3);
        startDelayChooser.addObject(Integer.toString(4), 4);
        startDelayChooser.addObject(Integer.toString(5), 5);
       
    	
    	joystickModeChooser = new SendableChooser<JoystickOption>();
    	joystickModeChooser.addDefault(JoystickOption.ARCADE_DRIVE.name,        JoystickOption.ARCADE_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.TRIGGER_DRIVE.name,        JoystickOption.TRIGGER_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.TANK_DRIVE.name, 	      JoystickOption.TANK_DRIVE);
     	joystickModeChooser.addObject(JoystickOption.CHEESY_ARCADE_DRIVE.name,  JoystickOption.CHEESY_ARCADE_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.CHEESY_TRIGGER_DRIVE.name, JoystickOption.CHEESY_TRIGGER_DRIVE);
    	joystickModeChooser.addObject(JoystickOption.CHEESY_2STICK_DRIVE.name,  JoystickOption.CHEESY_2STICK_DRIVE);
    	SmartDashboard.putData("Joystick Chooser", joystickModeChooser);
    	
     }
    
    
    public static SeriesAction autoSequenceBuilder(){
    	
    	AutoActions autoActions = new AutoActions();
    	
    	String gameData = DriverStation.getInstance().getGameSpecificMessage();
    	while(gameData.length() < 1){
    		gameData = DriverStation.getInstance().getGameSpecificMessage(); 
    	}
System.out.printf("GameData: %s, switchPose = %c, scalePose = %c\n ", gameData, gameData.charAt(0), gameData.charAt(1));    	
    	
    	char switchPose = gameData.charAt(0);
    	char scalePose = gameData.charAt(1);
    	StartOption startPose = (StartOption)startChooser.getSelected();
    	
    	PriorityOption priority = (PriorityOption)priorityChooser.getSelected();
    	
    	List<Action> actionSequence = new ArrayList<Action>();

    	switch (priority)
    	{
    	case SWITCH_IF_SAME_SIDE: //go to switch first if same side

    		switch( startPose )
    		{
    		case LEFT_START:
    			autoActions.setInitialState(InitialStateEnum.LEFT);
    			if( switchPose == 'L' )
    			{
    	    		autoActions.setTarget(TargetEnum.SWITCH);
    				autoActions.isRight(false);
    				// SCORE CUBE
    				//actionSequence.add( new LeftSwitchEdgeToLeftZoneCubeAction() );
    				//if (scalePose == 'L')
    				//	actionSequence.add( new LeftZoneCubeToLeftScaleEdgeAction() );
    				//else
    				//	actionSequence.add( new LeftZoneCubeToRightScaleEdgeAction() );
    			}
    			else
    			{
    				if( scalePose == 'L' ){
    					autoActions.setTarget(TargetEnum.SCALE);
    					autoActions.isRight(false);
    					// SCORE CUBE
    					//actionSequence.add( new LeftScaleEdgeToRightSwitchCubeAction() );
    					//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
    				}
    				else
    				{
    					autoActions.isRight(true);
        				// SCORE CUBE
        				//actionSequence.add( new RightScaleEdgeToRightSwitchCubeAction(); )
        				//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
    				}
    			}
    			break;
    		
    		case RIGHT_START:
    			autoActions.setInitialState(InitialStateEnum.RIGHT);
    			if( switchPose == 'R' )
    			{
    				autoActions.setTarget(TargetEnum.SWITCH);
    				autoActions.isRight(true);
    				// SCORE CUBE
    			}
    			else
    			{
    				if( scalePose == 'L' ){
    					autoActions.setTarget(TargetEnum.SCALE);
    					autoActions.isRight(false);
    					// SCORE CUBE
    				}
    				else
    				{
    					autoActions.isRight(true);
        				// SCORE CUBE
    				}
    			}
    			break;
    		
    		case CENTER_START:
    			autoActions.setInitialState(InitialStateEnum.CENTER);
   				autoActions.setTarget(TargetEnum.SWITCH);
    			if( switchPose == 'L' )
    			{
    				autoActions.isRight(false);
    				// SCORE CUBE
    			}
    			else
    			{
    				autoActions.isRight(true);
    			}
    			break;
    		}
    		break;	// case SWITCH_IF_SAME_SIDE
    			
    	case SCALE_IF_SAME_SIDE:
    		switch( startPose )
    		{
    		case LEFT_START:
    			autoActions.setInitialState(InitialStateEnum.LEFT);
    			if( scalePose == 'L' )
    			{
    				autoActions.setTarget(TargetEnum.SCALE);
    				autoActions.isRight(false);
    				// SCORE CUBE
    				//if (switchPose == 'L')
    				//{
        			//	actionSequence.add( new LeftScaleEdgeToLeftSwitchCubeAction() );
    				//	actionSequence.add( new LeftSwitchCubeToLeftSwitchEdgeAction() );
    				//}
    				//else
    				//{
    				//	actionSequence.add( new LeftScaleEdgeToRightSwitchCubeAction() );
    				//	actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
    				//}
    			}
    			else
    			{
    				if( switchPose == 'L' ){
    					autoActions.setTarget(TargetEnum.SWITCH);
    					autoActions.isRight(false);
    					// SCORE CUBE
    					//actionSequence.add( new LeftSwitchEdgeToRightSwitchCubeAction() );
    					//actionSequence.add( new RightSwitchCubeToRightScaleEdgeAction() );
    				}
    				else
    				{
        				autoActions.setTarget(TargetEnum.SCALE);
        				autoActions.isRight(true);
        				// SCORE CUBE
        				//actionSequence.add( new RightScaleEdgeToRightSwitchCubeAction(); )
        				//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
    				}
    			}
    			break;
    			
    		case RIGHT_START:
    			autoActions.setInitialState(InitialStateEnum.RIGHT);
    			if( scalePose == 'R' )
    			{
    				autoActions.setTarget(TargetEnum.SCALE);
    				autoActions.isRight(true);
    				// SCORE CUBE
    			}
    			else
    			{
    				if( switchPose == 'R' ){
    					autoActions.setTarget(TargetEnum.SWITCH);
    					autoActions.isRight(true);
    					// SCORE CUBE
    				}
    				else
    				{
        				autoActions.setTarget(TargetEnum.SCALE);
        				autoActions.isRight(false);
        				// SCORE CUBE
    				}
    			}
    			break;
    		
    		case CENTER_START:
    			autoActions.setInitialState(InitialStateEnum.CENTER);
    			autoActions.setTarget(TargetEnum.SCALE);
    			if( scalePose == 'L' )
    			{
    				autoActions.isRight(false);
    				// SCORE CUBE
    			}
    			else
    			{
    				autoActions.isRight(true);
    			}
    			break;
    		
    		default:
    			break;
    		}
    		
    		break;	// case SCALE_IF_SAME_SIDE
    			
    	case SWITCH_ALWAYS:
    		autoActions.setTarget(TargetEnum.SWITCH);
    		if( switchPose == 'L' )
    		{
    			autoActions.isRight(false);
    		}
    		else
    		{
    			autoActions.isRight(true);
    		}
    		
			switch( startPose )
			{
			case LEFT_START:
				autoActions.setInitialState(InitialStateEnum.LEFT);
				break;
			case RIGHT_START:
				autoActions.setInitialState(InitialStateEnum.RIGHT);
				break;
			case CENTER_START:
				autoActions.setInitialState(InitialStateEnum.CENTER);
				break;
			}
    		
    		break;
    		
    	case SCALE_ALWAYS:
    		
    		autoActions.setTarget(TargetEnum.SCALE);
    		if( switchPose == 'L' )
    		{
    			autoActions.isRight(false);
    		}
    		else
    		{
    			autoActions.isRight(true);
    		}
    		
			switch( startPose )
			{
			case LEFT_START:
				autoActions.setInitialState(InitialStateEnum.LEFT);
				break;
			case RIGHT_START:
				autoActions.setInitialState(InitialStateEnum.RIGHT);
				break;
			case CENTER_START:
				autoActions.setInitialState(InitialStateEnum.CENTER);
				break;
			}
    		break; // case: SCALE_ALWAYS
    	}
    	
    	actionSequence.add(autoActions.getActions(true));
    	return new SeriesAction(actionSequence);
    }
    
    public List<AutoModeBase> getAutoModeSelection()
    {
    	return null;
    	/*
        DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();
        boolean isBlue = (alliance == DriverStation.Alliance.Blue);
        
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
    	/*
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
	    */
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
    
   


