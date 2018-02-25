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
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Go over the defenses in the starting configuration, then launch one ball (in
 * the robot at start)
 */
public class PowerUpAutoMode extends AutoModeBase {
	
	public enum InitialStateEnum {
		
		CENTER(FieldDimensions.getCenterStartPose(), "Center"),
		LEFT(FieldDimensions.getLeftStartPose(), "Left"),
		RIGHT(FieldDimensions.getRightStartPose(), "Right");
		
		public Pose pose;
		public String name;
	
		InitialStateEnum( Pose _pose, String _name ){
			this.pose = _pose;
			this.name = _name;
		}
		
	}

	String gameData;
	StartDelayOption startDelay;
	StartOption startPose;
	PriorityOption priority;
	
	private Pose initialPose;
	
	public static AutoModeBase autoMode;
	
    public PowerUpAutoMode(String gameData, StartDelayOption startDelay, StartOption startPose, PriorityOption priority) 
    {
    	this.gameData = gameData;
    	this.startDelay = startDelay;
    	this.priority = priority;
    	this.startPose = startPose;
    	
		switch(startPose){
		case LEFT_START:
			initialPose = InitialStateEnum.LEFT.pose;
			break;
		case RIGHT_START:
			initialPose = InitialStateEnum.RIGHT.pose;
			break;
		case CENTER_START:
			initialPose = InitialStateEnum.CENTER.pose;
			break;
		default:
			initialPose = InitialStateEnum.CENTER.pose;
		}
System.out.println("INITIAL POSE in AutoSequenceBuilder: " + initialPose.toString());
    	
    	
    }
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	autoSequenceBuilder(gameData, startDelay, startPose, priority);
    	
    	autoMode.run();
    	System.out.println("Starting Auto Mode: PowerUpAutoMode");
   
    }


    public void stop(){
    	if(autoMode != null)
    		autoMode.done();
    	
    }
    
    public Pose getInitialPose(){ 
 System.out.println("Initial Pose in AutoMode: " + initialPose.toString());
    	return initialPose; 
    }
    
    
    public void autoSequenceBuilder(String gameData, StartDelayOption startDelay, StartOption startPose, PriorityOption priority)
    {
		
System.out.printf("GameData: %s, switchPose = %c, scalePose = %c\n ", gameData, gameData.charAt(0), gameData.charAt(1));    	
		char switchPose = gameData.charAt(0);
		char scalePose = gameData.charAt(1);
		
	
		/************************************
		 * TODO: add start delay action
		 ************************************/
		
		
		switch (priority)
		{
		case SWITCH_IF_SAME_SIDE: //go to switch first if same side
	
			switch( startPose )
			{
			case LEFT_START:
				if( switchPose == 'L' )
				{
					autoMode = new SideStartToNearSwitchMode(InitialStateEnum.LEFT, false);
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
						//autoActions.setTarget(TargetEnum.SCALE);
						//autoActions.isRight(false);
						// SCORE CUBE
						//actionSequence.add( new LeftScaleEdgeToRightSwitchCubeAction() );
						//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
					}
					else
					{
						//autoActions.isRight(true);
	    				// SCORE CUBE
	    				//actionSequence.add( new RightScaleEdgeToRightSwitchCubeAction(); )
	    				//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
					}
				}
				break;
			
			case RIGHT_START:
				if( switchPose == 'R' )
				{
					autoMode = new SideStartToNearSwitchMode(InitialStateEnum.RIGHT, true);
					// SCORE CUBE
				}
				else
				{
					if( scalePose == 'L' ){
						//autoActions.setTarget(TargetEnum.SCALE);
						//autoActions.isRight(false);
						// SCORE CUBE
					}
					else
					{
						//autoActions.isRight(true);
	    				// SCORE CUBE
					}
				}
				break;
			
			case CENTER_START:

				if( switchPose == 'L' )
				{
					autoMode = new CenterStartToNearSwitchMode(false);
					// SCORE CUBE
				}
				else
				{
					autoMode = new CenterStartToNearSwitchMode(true);
				}
				break;
			}
			break;	// case SWITCH_IF_SAME_SIDE
				
		case SCALE_IF_SAME_SIDE:
			switch( startPose )
			{
			case LEFT_START:
				//autoActions.setInitialState(InitialStateEnum.LEFT);
				if( scalePose == 'L' )
				{
					//autoActions.setTarget(TargetEnum.SCALE);
					//autoActions.isRight(false);
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
						autoMode = new SideStartToNearSwitchMode(InitialStateEnum.LEFT, false);
						// SCORE CUBE
						//actionSequence.add( new LeftSwitchEdgeToRightSwitchCubeAction() );
						//actionSequence.add( new RightSwitchCubeToRightScaleEdgeAction() );
					}
					else
					{
	    				//autoActions.setTarget(TargetEnum.SCALE);
	    				//autoActions.isRight(true);
	    				// SCORE CUBE
	    				//actionSequence.add( new RightScaleEdgeToRightSwitchCubeAction(); )
	    				//actionSequence.add( new RightSwitchCubeToRightSwitchEdgeAction() );
					}
				}
				break;
				
			case RIGHT_START:

				if( scalePose == 'R' )
				{
					//autoActions.setTarget(TargetEnum.SCALE);
					//autoActions.isRight(true);
					// SCORE CUBE
				}
				else
				{
					if( switchPose == 'R' ){
						autoMode = new SideStartToNearSwitchMode(InitialStateEnum.RIGHT, true);
						// SCORE CUBE
					}
					else
					{
	    				//autoActions.setTarget(TargetEnum.SCALE);
	    				//autoActions.isRight(false);
	    				// SCORE CUBE
					}
				}
				break;
			
			case CENTER_START:
				//autoActions.setInitialState(InitialStateEnum.CENTER);
				//autoActions.setTarget(TargetEnum.SCALE);
				if( scalePose == 'L' )
				{
					//autoActions.isRight(false);
					// SCORE CUBE
				}
				else
				{
					//autoActions.isRight(true);
				}
				break;
			
			default:
				break;
			}
			
			break;	// case SCALE_IF_SAME_SIDE
				
		case SWITCH_ALWAYS:
			
			switch( startPose )
			{
			case LEFT_START:
				autoMode = new SideStartToNearSwitchMode(InitialStateEnum.LEFT, false);
				if(switchPose == 'R')
					autoMode = new SideStartToFarSwitchMode(InitialStateEnum.LEFT);
				break;
			case RIGHT_START:
				autoMode = new SideStartToNearSwitchMode(InitialStateEnum.RIGHT, true);
				if(switchPose == 'L')
					autoMode = new SideStartToFarSwitchMode(InitialStateEnum.RIGHT);
				break;
			case CENTER_START:
				autoMode = new CenterStartToNearSwitchMode(false);
				if(switchPose == 'R')
					autoMode = new CenterStartToNearSwitchMode(true); 
				break;
			}
			
			break;
			
		case SCALE_ALWAYS:
			switch(startPose){
			case LEFT_START:
				autoMode = new StartToScaleMode(InitialStateEnum.LEFT, true);
				if(switchPose == 'L')
					autoMode = new StartToScaleMode(InitialStateEnum.LEFT, false);
				break;
			case RIGHT_START:
				autoMode = new StartToScaleMode(InitialStateEnum.RIGHT, true);
				if(switchPose == 'L')
					autoMode = new StartToScaleMode(InitialStateEnum.RIGHT, false);
				break;
			case CENTER_START:
				autoMode = new StartToScaleMode(InitialStateEnum.CENTER, true);
				if(switchPose == 'L')
					autoMode = new StartToScaleMode(InitialStateEnum.CENTER, false);
				break;
			}
			
			break; // case: SCALE_ALWAYS
		}
		

	}
}