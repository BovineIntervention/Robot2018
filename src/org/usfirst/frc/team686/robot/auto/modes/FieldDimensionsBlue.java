package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.Constants;

public class FieldDimensionsBlue extends FieldDimensions
{
	// Field Measurements (change values below if different from Red side)
	public static double distXWallToAirship 		= FieldDimensionsRed.distXWallToAirship;		// driver station wall to wall behind center peg 
	public static double distXWallToBoilerHopper	= FieldDimensionsRed.distXWallToBoilerHopper;		// driver station wall to center of hopper on boiler side
	public static double distXWallToOtherHopper		= FieldDimensionsRed.distXWallToOtherHopper;		// driver station wall to center of hopper on boiler side
	
	public static double distYOriginToEdgeofDSWallOnBoilerSide 	= FieldDimensionsRed.distYOriginToEdgeofDSWallOnBoilerSide;	// origin to edge of driver station wall closest to boiler 
	public static double distYOriginToEdgeofDSWallOnOtherSide 	= FieldDimensionsRed.distYOriginToEdgeofDSWallOnOtherSide;	// origin to edge of driver station wall opposite the boiler 
	public static double distYOriginToBoilerField 				= FieldDimensionsRed.distYOriginToBoilerField;	// center of field to wall on boiler side
	public static double distYOriginToOtherFieldWall 			= FieldDimensionsRed.distYOriginToOtherFieldWall;	// center of field to wall on other side

	public static double distXOriginToCenterOfBoiler	= FieldDimensionsRed.distXOriginToCenterOfBoiler;			// origin to center of boiler 
	public static double distYOriginToCenterOfBoiler	= FieldDimensionsRed.distYOriginToCenterOfBoiler;		// origin to center of boiler

	
	
	
	// Robot Starting Positions
    public static Pose kCenterStartPosition = new Pose(Constants.kCenterToRearBumper, 0, 0);
    public static Pose kBoilerStartPosition = new Pose(Constants.kCenterToRearBumper, +distYOriginToEdgeofDSWallOnBoilerSide - Constants.kCenterToSideBumper, 0);	// same as red, but negate Y
    public static Pose kOtherStartPosition  = new Pose(Constants.kCenterToRearBumper, -distYOriginToEdgeofDSWallOnOtherSide  + Constants.kCenterToSideBumper, 0);	// same as red, but negate Y

    // Peg Locations
	public static double kCenterPegHeading =  180 * Math.PI/180;
	public static double kBoilerPegHeading = +120 * Math.PI/180;	// negative of red
	public static double kOtherPegHeading  = -120 * Math.PI/180;	// negative of red
    public static Pose kCenterPegBasePose = new Pose(distXWallToAirship,                                            0, kCenterPegHeading);
	public static Pose kBoilerPegBasePose = new Pose(distXWallToAirship + kNonCenterPegOffsetX, +kNonCenterPegOffsetY, kBoilerPegHeading);	// same as red, but negate Y
	public static Pose kOtherPegBasePose  = new Pose(distXWallToAirship + kNonCenterPegOffsetX, -kNonCenterPegOffsetY, kOtherPegHeading);	// same as red, but negate Y

	// Boiler Location
	public static double kBoilerHeading =  -45 * Math.PI/180;	// negative of red
	public static Pose kBoilerPose = new Pose(distXOriginToCenterOfBoiler, +distYOriginToCenterOfBoiler,    kBoilerHeading);		// same as red, but negate Y
	
	// Hopper Locations
	public static double kBoilerHopperHeading = -30 * Math.PI/180;	// negative of red
	public static double kOtherHopperHeading  = +30 * Math.PI/180;	// negative of red
	public static Pose kBoilerHopperPose = new Pose(distXWallToBoilerHopper, +distYOriginToBoilerField,    kBoilerHopperHeading);	// same as red, but negate Y
	public static Pose kOtherHopperPose  = new Pose(distXWallToOtherHopper,  -distYOriginToOtherFieldWall, kOtherHopperHeading);	// same as red, but negate Y

	// Ending point of run to other side of field
	public static Pose kFarSideOfFieldPose = new Pose(kDistXFarSideOfField, -distYOriginToEdgeofDSWallOnOtherSide, 0);

	
	
	
	// Robot Starting Positions
    public Pose getCenterStartPose() { return kCenterStartPosition; };
    public Pose getBoilerStartPose() { return kBoilerStartPosition; };
    public Pose getOtherStartPose() { return kOtherStartPosition; };

    // Peg Locations
    public Pose getCenterPegBasePose() { return kCenterPegBasePose; };
	public Pose getBoilerPegBasePose() { return kBoilerPegBasePose; };
	public Pose getOtherPegBasePose() { return kOtherPegBasePose; };

	// Boiler Location
	public Pose getBoilerPose() { return kBoilerPose; };	
	
	// Hopper Locations
	public Pose getBoilerHopperPose() { return kBoilerHopperPose; };
	public Pose getOtherHopperPose() { return kOtherHopperPose; };

	// Ending point of run to other side of field
	public Pose getFarSideOfFieldPose() { return kFarSideOfFieldPose; }
}

