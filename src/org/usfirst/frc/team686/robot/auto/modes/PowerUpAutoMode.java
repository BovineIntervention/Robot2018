package org.usfirst.frc.team686.robot.auto.modes;

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
	
	public AutoModeBase autoMode;
	
    public PowerUpAutoMode(String gameData, StartDelayOption startDelay, StartPositionOption startPosition, PriorityOption priority) 
    {
    	this.gameData = gameData;
    	this.startDelay = startDelay;
    	this.startPosition = startPosition;
    	this.priority = priority;
    	
System.out.println("INITIAL POSE in AutoSequenceBuilder: " + initialPose.toString());
    	
    }
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	autoSequenceBuilder(gameData, startDelay, startPosition, priority);
    	
    	autoMode.run();
    	System.out.println("Starting Auto Mode: PowerUpAutoMode");
   
    }


    public void stop(){
    	if(autoMode != null)
    		autoMode.done();
    	
    }
    
    public Pose getInitialPose(){ 
 System.out.println("Initial Pose in AutoMode: " + startPosition.initialPose.toString());
    	return startPosition.initialPose; 
    }
    
    
    public void autoSequenceBuilder(String gameData, StartDelayOption startDelay, StartPositionOption startPosition, PriorityOption priority) throws AutoModeEndedException
    {
		
System.out.printf("GameData: %s, switchPose = %c, scalePose = %c\n ", gameData, gameData.charAt(0), gameData.charAt(1));    	
		char switchSide = gameData.charAt(0);
		char scaleSide  = gameData.charAt(1);
		boolean toRight = false;

		autoMode = new StandStillMode();

		
		// optional delay at start of autonomous
		runAction( new WaitAction(startDelay.delaySec) );
		
		
		
		switch (priority)
		{
		case SWITCH_IF_SAME_SIDE: //go to switch first if same side
	
			switch( startPosition )
			{
			case LEFT_START:
				if( switchSide == 'L' ) {
					// start on left, go to left side switch
					toRight = false;
					autoMode = new SideStartToNearSwitchMode(startPosition, toRight);
				} else {
					// switch is not on our side, check scale
					if( scaleSide == 'L' ) {
						// start on left, go to left side scale
						toRight = false;
						autoMode = new StartToNearScaleMode(startPosition, toRight);
					} else {
						// both switch and scale on wrong side, prioritize switch
						// start on left, go to right side switch
						toRight = false;
						autoMode = new SideStartToFarSwitchMode(startPosition, toRight);
					}
				}
				break;
			
			case RIGHT_START:
				if( switchSide == 'R' ) {
					// start on right, go to right side switch
					toRight = true;
					autoMode = new SideStartToNearSwitchMode(startPosition, toRight);
				} else {
					// switch is not on our side, check scale
					if( scaleSide == 'R' ) {
						// start on right, go to left side scale
						toRight = true;
						autoMode = new StartToNearScaleMode(startPosition, toRight);
					} else {
						// both switch and scale on wrong side, prioritize switch
						// start on right, go to right side switch
						toRight = false;
						autoMode = new SideStartToFarSwitchMode(startPosition, toRight);
					}
				}
				break;
			
			case CENTER_START:
				// start in center, go to switch
				toRight = ( switchSide == 'R' );
				autoMode = new CenterStartToSwitchMode(startPosition, toRight);
				break;
			}
			break;	// case SWITCH_IF_SAME_SIDE
				
			
			
			
		case SCALE_IF_SAME_SIDE:
			switch( startPosition )
			{
			case LEFT_START:
				if( scaleSide == 'L' ) {
					// start on left side, go to left side scale
					toRight = false;
					autoMode = new StartToNearScaleMode(startPosition, toRight);
				} else {
					// scale not on same side, check switch
					if( switchSide == 'L' ) {
						// start on left, go to left side switch
						toRight = false;
						autoMode = new SideStartToNearSwitchMode(startPosition, toRight);
					} else {
						// both switch and scale on wrong side, prioritize scale
						// start on left, go to right side scale
						toRight = true;
						autoMode = new SideStartToFarScaleMode(startPosition, toRight);
					}
				}
				break;
				
			case RIGHT_START:
				if( scaleSide == 'R' ) {
					// start on right side, go to right side scale
					toRight = true;
					autoMode = new StartToNearScaleMode(startPosition, toRight);
				} else {
					// scale not on same side, check switch
					if( switchSide == 'R' ) {
						// start on right, go to right side scale
						toRight = true;
						autoMode = new SideStartToNearSwitchMode(startPosition, true);
					} else {
						// both switch and scale on wrong side, prioritize scale
						// start on left, go to right side scale
						toRight = false;
						autoMode = new SideStartToFarScaleMode(startPosition, toRight);
					}
				}
				break;
			
			case CENTER_START:
				// start in center, go to scale
				toRight = ( scaleSide == 'R' );
				autoMode = new CenterStartToSwitchMode(startPosition, toRight);
				break;
			}
			break;	// case SCALE_IF_SAME_SIDE
			
			
			
			
		case SWITCH_ALWAYS:
			
			switch( startPosition )
			{
			case LEFT_START:
				if (switchSide == 'L') {
					toRight = false;
					autoMode = new SideStartToNearSwitchMode(startPosition, toRight);
				} else {
					toRight = true;
					autoMode = new SideStartToFarSwitchMode(startPosition, toRight);
				}
				break;
				
			case RIGHT_START:
				if (switchSide == 'R') {
					toRight = true;
					autoMode = new SideStartToNearSwitchMode(startPosition, toRight);
				} else {
					toRight = false;
					autoMode = new SideStartToFarSwitchMode(startPosition, toRight);
				}
				break;
				
			case CENTER_START:
				toRight = (switchSide == 'R');
				autoMode = new CenterStartToSwitchMode(startPosition, toRight);
				break;
			}			
			break; // case: SWITCH_ALWAYS
			
			
			
			
		case SCALE_ALWAYS:
			switch( startPosition )
			{
			case LEFT_START:
				if (scaleSide == 'L') {
					toRight = false;
					autoMode = new StartToNearScaleMode(startPosition, toRight);
				} else {
					toRight = true;
					autoMode = new SideStartToFarScaleMode(startPosition, toRight);
				}
				break;
				
			case RIGHT_START:
				if (scaleSide == 'R') {
					toRight = true;
					autoMode = new StartToNearScaleMode(startPosition, toRight);
				} else {
					toRight = false;
					autoMode = new SideStartToFarScaleMode(startPosition, toRight);
				}
				break;
				
			case CENTER_START:
				toRight = (scaleSide == 'R');
				autoMode = new StartToNearScaleMode(startPosition, toRight);
				break;
			}			
			break; // case: SCALE_ALWAYS
		}
		

	}
}