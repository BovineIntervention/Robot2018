package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class SecondCubeForScaleMode  extends AutoModeBase {

	Vector2d startPosition;
	char switchSide;
	char scaleSide;
	
	public SecondCubeForScaleMode(Vector2d _currentPosition, char _switchSide, char _scaleSide)
	{
		startPosition = _currentPosition;
		switchSide = _switchSide;
		scaleSide = _scaleSide;
	}
	
	protected void routine() throws AutoModeEndedException
	{
		// TODO: Get 2nd cube for switch working first, then modify for 2nd cube to scale
	}
}	
