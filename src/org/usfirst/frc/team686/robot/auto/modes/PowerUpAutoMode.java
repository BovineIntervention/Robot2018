package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.SmartDashboardInteractions.CrossFieldOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.PriorityOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartPositionOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartDelayOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.WaitAction;
import org.usfirst.frc.team686.robot.lib.util.Pose;

/**
 * Go over the defenses in the starting configuration, then launch one ball (in
 * the robot at start)
 */
public class PowerUpAutoMode extends AutoModeBase {
	
	String gameData;
	StartDelayOption startDelay;
	StartPositionOption startPosition;
	PriorityOption priority;
	CrossFieldOption crossField;
	
	public AutoModeBase autoMode;
	
    public PowerUpAutoMode(String gameData, StartDelayOption startDelay, StartPositionOption startPosition, PriorityOption priority, CrossFieldOption crossField) 
    {
    	this.gameData = gameData;
    	this.startDelay = startDelay;
    	this.startPosition = startPosition;
    	this.priority = priority;
    	this.crossField = crossField;
    }
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	autoSequenceBuilder(gameData, startDelay, startPosition, priority, crossField);

    	System.out.println("Starting Auto Mode: PowerUpAutoMode");
    	autoMode.run();
   
    }


    public void stop(){
    	if(autoMode != null)
    		autoMode.done();
    	
    }
    
    public Pose getInitialPose(){ 
 System.out.println("Initial Pose in AutoMode: " + startPosition.initialPose.toString());
    	return startPosition.initialPose; 
    }
    
    
    public void autoSequenceBuilder(String gameData, StartDelayOption startDelay, StartPositionOption startPosition, PriorityOption priority, CrossFieldOption crossField) throws AutoModeEndedException
    {
		
    	System.out.printf("GameData: %s, switchPose = %c, scalePose = %c\n ", gameData, gameData.charAt(0), gameData.charAt(1));    	
		char switchSide = gameData.charAt(0);
		char scaleSide  = gameData.charAt(1);
		
		autoMode = new StandStillMode();

//		autoMode = new PracticeAuto(startPosition, switchSide, scaleSide, crossField);
//    }

		
		// optional delay at start of autonomous
		runAction( new WaitAction(startDelay.delaySec) );
		
		
		
		switch (priority)
		{
		case SWITCH_IF_SAME_SIDE: //go to switch first if same side
	
			switch( startPosition )
			{
			case LEFT_START:
				if( switchSide == 'L' )		autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);	// start on left, go to left side switch
				else {
					// switch is not on our side, check scale
					if ( scaleSide == 'L' )	autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);		// start on left, go to left side scale
					else					autoMode = new SideStartToFarSwitchMode(startPosition, switchSide, scaleSide, crossField);	// both switch and scale on wrong side, prioritize switch, start on left, go to right side switch
				}
				break;
			
			case RIGHT_START:
				if( switchSide == 'R' )		autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);	// start on right, go to right side switch
				else {
					// switch is not on our side, check scale
					if( scaleSide == 'R' ) 	autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);		// start on right, go to left side scale
					else					autoMode = new SideStartToFarSwitchMode(startPosition, switchSide, scaleSide, crossField);	// both switch and scale on wrong side, prioritize switch, start on right, go to right side switch 
				}
				break;
			
			case CENTER_START:
				autoMode = new CenterStartToSwitchMode(startPosition, switchSide, scaleSide, crossField);								// start in center, go to switch
				break;
			}
			break;	// case SWITCH_IF_SAME_SIDE
				
			
			
			
		case SCALE_IF_SAME_SIDE:
			switch( startPosition )
			{
			case LEFT_START:							
				if( scaleSide == 'L' )		autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);		// start on left side, go to left side scale
				else {
					// scale not on same side, check switch
					if( switchSide == 'L' ) autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);	// start on left, go to left side switch
					else 					autoMode = new SideStartToFarScaleMode(startPosition, switchSide, scaleSide, crossField);	// both switch and scale on wrong side, prioritize scale, start on left, go to right side scale 
				}
				break;
				
			case RIGHT_START:
				if( scaleSide == 'R' )		autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);		// start on right side, go to right side scale
				else {
					// scale not on same side, check switch
					if( switchSide == 'R' ) autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);	// start on right, go to right side scale
					else					autoMode = new SideStartToFarScaleMode(startPosition, switchSide, scaleSide, crossField);	// both switch and scale on wrong side, prioritize scale, start on left, go to right side scale 	
				}
				break;
			
			case CENTER_START:
				autoMode = new CenterStartToSwitchMode(startPosition, switchSide, scaleSide, crossField);								// start in center, go to scale
				break;
			}
			break;	// case SCALE_IF_SAME_SIDE
			
			
			
			
		case SWITCH_ALWAYS:
			
			switch( startPosition )
			{
			case LEFT_START:
				if (switchSide == 'L')	autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);
				else					autoMode = new SideStartToFarSwitchMode(startPosition, switchSide, scaleSide, crossField);
				break;
				
			case RIGHT_START:
				if (switchSide == 'R')	autoMode = new SideStartToNearSwitchMode(startPosition, switchSide, scaleSide, crossField);
				else					autoMode = new SideStartToFarSwitchMode(startPosition, switchSide, scaleSide, crossField);
				break;
				
			case CENTER_START:
				autoMode = new CenterStartToSwitchMode(startPosition, switchSide, scaleSide, crossField);
				break;
			}			
			break; // case: SWITCH_ALWAYS
			
			
			
			
		case SCALE_ALWAYS:
			switch( startPosition )
			{
			case LEFT_START:
				if (scaleSide == 'L')	autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);
				else					autoMode = new SideStartToFarScaleMode(startPosition, switchSide, scaleSide, crossField);
				break;
				
			case RIGHT_START:
				if (scaleSide == 'R') 	autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);
				else					autoMode = new SideStartToFarScaleMode(startPosition, switchSide, scaleSide, crossField);
				break;
				
			case CENTER_START:
				autoMode = new StartToNearScaleMode(startPosition, switchSide, scaleSide, crossField);
				break;
			}			
			break; // case: SCALE_ALWAYS
		}
		

	}
}