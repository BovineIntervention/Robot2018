package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.Constants;

public class FieldDimensionsRed extends FieldDimensions
{
	// Field Measurements
	public static double distXWallToAirship 		= 114.5;		// driver station wall to wall behind center peg 
	public static double distXWallToBoilerHopper	= 120.0;		// driver station wall to center of hopper on boiler side
	public static double distXWallToOtherHopper		= 207.0;		// driver station wall to center of hopper on boiler side
	
	public static double distYOriginToEdgeofDSWallOnBoilerSide 	= 124.875;	// origin to edge of driver station wall closest to boiler 
	public static double distYOriginToEdgeofDSWallOnOtherSide 	= 124.875;	// origin to edge of driver station wall opposite the boiler 
	public static double distYOriginToBoilerField 				= 160.8;	// center of field to wall on boiler side
	public static double distYOriginToOtherFieldWall 			= 158.5;	// center of field to wall on other side

	public static double distXOriginToCenterOfBoiler	= 18;			// origin to center of boiler 
	public static double distYOriginToCenterOfBoiler	= 143.2;		// origin to center of boiler

	
	
	
	// Robot Starting Positions
    Pose kCenterStartPose = new Pose(Constants.kCenterToRearBumper, 0, 0);
    Pose kBoilerStartPose = new Pose(Constants.kCenterToRearBumper, -distYOriginToEdgeofDSWallOnBoilerSide + Constants.kCenterToSideBumper, 0);
    Pose kOtherStartPose  = new Pose(Constants.kCenterToRearBumper, +distYOriginToEdgeofDSWallOnOtherSide  - Constants.kCenterToSideBumper, 0);

    // Peg Locations
	double kCenterPegHeading =  180 * Math.PI/180;
	double kBoilerPegHeading = -120 * Math.PI/180;
	double kOtherPegHeading  = +120 * Math.PI/180;
    Pose kCenterPegBasePose = new Pose(distXWallToAirship,                                            0, kCenterPegHeading);
	Pose kBoilerPegBasePose = new Pose(distXWallToAirship + kNonCenterPegOffsetX, -kNonCenterPegOffsetY, kBoilerPegHeading);
	Pose kOtherPegBasePose  = new Pose(distXWallToAirship + kNonCenterPegOffsetX, +kNonCenterPegOffsetY, kOtherPegHeading);

	// Boiler Location
	double kBoilerHeading =  45 * Math.PI/180;
	Pose kBoilerPose = new Pose(distXOriginToCenterOfBoiler, -distYOriginToCenterOfBoiler,    kBoilerHeading);	
	
	// Hopper Locations
	double kBoilerHopperHeading =  30 * Math.PI/180;
	double kOtherHopperHeading  = -30 * Math.PI/180;
	Pose kBoilerHopperPose = new Pose(distXWallToBoilerHopper, -distYOriginToBoilerField,    kBoilerHopperHeading);
	Pose kOtherHopperPose  = new Pose(distXWallToOtherHopper,  +distYOriginToOtherFieldWall, kOtherHopperHeading);

	// Ending point of run to other side of field
	Pose kFarSideOfFieldPose = new Pose(kDistXFarSideOfField, +distYOriginToEdgeofDSWallOnOtherSide, 0);

	
	
	
	// Robot Starting Positions
    public Pose getCenterStartPose() { return kCenterStartPose; };
    public Pose getBoilerStartPose() { return kBoilerStartPose; };
    public Pose getOtherStartPose() { return kOtherStartPose; };

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
	public Pose getFarSideOfFieldPose() { return kFarSideOfFieldPose; };
}

