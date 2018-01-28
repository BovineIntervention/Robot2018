package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.Constants;

/**
 * Interface that holds all the field measurements 
 */
public abstract class FieldDimensions 
{
	public static double kDistanceToStopFromPeg = Constants.kCenterToFrontBumper + 10.0;		// stop 10" from airship wall (5" from base of peg)
	public static double kDistXFarSideOfField  = 400;
	public static double kNonCenterPegOffsetX = 17.563;
	public static double kNonCenterPegOffsetY = 30.518;


	public static double getDistanceToStopFromPeg() { return kDistanceToStopFromPeg; }
	public static double getDistXFarSideOfField() { return kDistXFarSideOfField; }
	public static double getNonCenterPegOffsetX() { return kNonCenterPegOffsetX; }
	public static double getNonCenterPegOffsetY() { return kNonCenterPegOffsetY; }
	

	
	// origin is along driver station wall in line with center peg

	// Robot Starting Positions
    public abstract Pose getCenterStartPose();
    public abstract Pose getBoilerStartPose();
    public abstract Pose getOtherStartPose();

    // Peg Locations
    public abstract Pose getCenterPegBasePose();
	public abstract Pose getBoilerPegBasePose();
	public abstract Pose getOtherPegBasePose();

	// Boiler Location
	public abstract Pose getBoilerPose();	
	
	// Hopper Locations
	public abstract Pose getBoilerHopperPose();
	public abstract Pose getOtherHopperPose();

	// Ending point of run to other side of field
	public abstract Pose getFarSideOfFieldPose();

	// stuff needed to compile without errors.  Susanna to fix
    public Pose getExchangeStartPose() { return new Pose(); }
    public Pose getPowerCubeZonePose() { return new Pose(); }
    public Pose switchStopPosition() { return new Pose(); }
    public Pose getLeftScalePose() { return new Pose(); }
    public Pose getRightScalePose() { return new Pose(); }
    public Pose getLeftSwitchPose() { return new Pose(); }
    public Pose getRightSwitchPose() { return new Pose(); }
    public static double kScaleTurnPositionOffsetX = 0;
    public static double kScaleTurnPositionOffsetY = 0;
    public static double kSwitchTurnPositionOffsetX = 0;
    public static double kSwitchTurnPositionOffsetY = 0;
    public static double kBackupDistY = 0;

}
