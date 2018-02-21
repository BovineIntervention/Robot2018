package org.usfirst.frc.team686.robot.auto.modes;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.PriorityOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartDelayOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.PowerUpAutoActions;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.auto.actions.SeriesAction;
import org.usfirst.frc.team686.robot.auto.actions.PowerUpAutoActions.InitialStateEnum;
import org.usfirst.frc.team686.robot.auto.actions.PowerUpAutoActions.TargetEnum;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Go over the defenses in the starting configuration, then launch one ball (in
 * the robot at start)
 */
public class PowerUpAutoMode extends AutoModeBase {

	String gameData;
	StartDelayOption startDelay;
	StartOption startPose;
	PriorityOption priority;
	
    public PowerUpAutoMode(String gameData, StartDelayOption startDelay, StartOption startPose, PriorityOption priority) 
    {
    	this.gameData = gameData;
    	this.startDelay = startDelay;
    	this.startPose = startPose;
    	this.priority = priority;
    }
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	Action autoSequence = autoSequenceBuilder(gameData, startDelay, startPose, priority);
    	
    	System.out.println("Starting Auto Mode: PowerUpAutoMode");
    	runAction(autoSequence);
    }


    
    
    
    public SeriesAction autoSequenceBuilder(String gameData, StartDelayOption startDelay, StartOption startPose, PriorityOption priority)
    {
		PowerUpAutoActions autoActions = new PowerUpAutoActions();
		
System.out.printf("GameData: %s, switchPose = %c, scalePose = %c\n ", gameData, gameData.charAt(0), gameData.charAt(1));    	
		char switchPose = gameData.charAt(0);
		char scalePose = gameData.charAt(1);
		
		List<Action> actionSequence = new ArrayList<Action>();
	
		/************************************
		 * TODO: add start delay action
		 ************************************/
		
		
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
}