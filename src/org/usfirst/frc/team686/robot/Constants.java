package org.usfirst.frc.team686.robot;

import org.usfirst.frc.team686.robot.lib.util.ConstantsBase;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;

/**
 * Attribution: adapted from FRC Team 254
 */


/**
 * A list of constants used by the rest of the robot code. This include physics
 * constants as well as constants determined through calibrations.
 */
public class Constants extends ConstantsBase
{
    private static ConstantsBase mInstance = new Constants();	// make sure we call constructor to set all robot-specific constants
    public static ConstantsBase getInstance() { return mInstance; }
	
    public enum RobotSelectionEnum { COMPETITION_BOT, PRACTICE_BOT; }
    public static RobotSelectionEnum kRobotSelection;
	
    public static double kLoopDt = 0.01;
    public static double kDriveWatchdogTimerThreshold = 0.500;    
    public static int kTalonTimeoutMs = 5;	// ms
    public static int kTalonPidIdx = 0;		// 0 for non-cascaded PIDs, 1 for cascaded PIDs
    	
    public static double kNominalBatteryVoltage = 12.0;
    
    
    // Bumpers
    public static double kCenterToFrontBumper;	// position of front bumper with respect to robot center of rotation
    public static double kCenterToExtendedIntake;	// position of intake sweetspot when extended with respect to robot center of rotation
    public static double kCenterToRearBumper;	// position of rear bumper with respect to robot center of rotation
    public static double kCenterToSideBumper;	// position of side bumper with respect to robot center of rotation
	public static double kCenterToCornerBumper;

    // Wheels
    public static double kDriveWheelCircumInches;
    public static double kDriveWheelDiameterInches;
    public static double kTrackLengthInches;
    public static double kTrackWidthInches;
    public static double kTrackEffectiveDiameter;
    public static double kTrackScrubFactor;

    // Wheel Encoder
    public static double kQuadEncoderGain ;	// number of drive shaft rotations per encoder shaft rotation
    
    public static int    kQuadEncoderCodesPerRev;
    public static int    kQuadEncoderUnitsPerRev;
    public static double kQuadEncoderStatusFramePeriod = 0.100;	// 100 ms
    
    // CONTROL LOOP GAINS
    
    public static double kDriveSecondsFromNeutralToFull = 0.375;		// decrease acceleration (reduces current, robot tipping)
    
    // PID gains for drive velocity loop (sent to Talon)
    // Units: error is 4*256 counts/rev. Max output is +/- 1023 units.
    public static double kDriveVelocityKp;
    public static double kDriveVelocityKi;
    public static double kDriveVelocityKd;
    public static double kDriveVelocityKf;
    public static int    kDriveVelocityIZone;
    public static double kDriveVelocityRampRate;
    public static int    kDriveVelocityAllowableError;

    // PID gains for drive base lock loop
    // Units: error is 4*256 counts/rev. Max output is +/- 1023 units.
    public static double kDriveBaseLockKp;
    public static double kDriveBaseLockKi;
    public static double kDriveBaseLockKd;
    public static double kDriveBaseLockKf;
    public static int    kDriveBaseLockIZone;
    public static double kDriveBaseLockRampRate;
    public static int    kDriveBaseLockAllowableError;

    // PID gains for constant heading velocity control
    // Units: Error is degrees. Output is inches/second difference to
    // left/right.
    public static double kDriveHeadingVelocityKp;
    public static double kDriveHeadingVelocityKi;
    public static double kDriveHeadingVelocityKd;
    
    // Point Turn constants
    public static double kPointTurnKp = 0.05;
    public static double kPointTurnKd = 0.50;
    public static double kPointTurnKi = 0.00;
    public static double kPointTurnKf = 0.00;
    public static double kPointTurnCompletionToleranceDeg = 3.0;
    public static double kPointTurnMaxOutput = 0.7; 
    
    // Path following constants
    public static double kPathFollowingMaxVel; // inches/sec  
    public static double kPathFollowingAccelTime = 0.5;	// sec to reach max velocity
    public static double kPathFollowingMaxAccel; // inches/sec^2	
    public static double kPathFollowingLookahead ; // inches
    public static double kPathFollowingCompletionTolerance;
    
    public static double kCollisionVel 			= 24;
    public static double kCollisionAccelTime = 0.5;	// sec to reach max velocity
    public static double kCollisionAccel 		= kCollisionVel / kCollisionAccelTime;
    public static double kCollisionJerkThreshold 	= 0.9;		// maximum JerkY was 0.9 for a 24 inch/sec collision into wall (<0.1 when driving normal)
    public static double kCollisionCurrentThreshold = 20;		// threshold to detect stall current
    
    // Vision constants
    public static double kCameraPoseX ;	// camera location with respect to robot center of rotation, +X axis is in direction of travel
    public static double kCameraPoseY;	// camera location with respect to robot center of rotation, +Y axis is positive to the left
    public static double kCameraPoseTheta;	// camera angle with respect to robot heading
    
    public static double kVisionMaxVel; // inches/sec  		
    public static double kVisionMaxAccel; // inches/sec^2		
    public static double kTargetWidthInches;
    public static double kPegTargetDistanceThresholdFromBumperInches;		// inches to stop from target, measured from front bumper
    public static double kPegTargetDistanceThresholdFromCameraInches;
    public static double kVisionCompletionTolerance; 
    public static double kVisionMaxDistanceInches;		// ignore targets greater than this distance
    public static double kVisionLookaheadDist;	// inches
    public static double kCameraFOVDegrees;			// Camera Field of View (degrees)
    public static double kCameraHalfFOVRadians;			// Half of Camera Field of View (radians)
    public static double kTangentCameraHalfFOV;
    public static double kCameraLatencySeconds;			// Camera image capturing latency
    public static double kTargetLocationFilterConstant;		// 30 time constants in 1 second
    

    // POWER UP FIELD
    public static double kScaleWallHeight =  3.50;	// field drawing 18018: plate front wall rises ~3.5" above platform
    public static double kCubeHeight = 		13.00;	// assuming cube is on its side
    public static double kCubeClearance = 	 4.00;	// allow a few inches for clearing wall

    public static double kScaleHeightHigh = 72.00;	// game manual: 72"
    public static double kScaleHeightMed = 	60.00;	// game manual: 60"
    public static double kScaleHeightLow = 	48.00;	// game manual: 48"
    public static double kSwitchHeight = 	18.75;	// field drawing 18150: 18.75" to top of fence	
    public static double kExchangeHeight = 	 0.00;	// field drawing 18127: 0.5" plywood + 1.0" pine + 0.125" polycarbonate + clearance 
    public static double kGroundHeight = 	 0.00;

    // DESIRED CUBE HEIGHT (bottom of cube)
    public static double kCubeScaleHeightHigh = kScaleHeightHigh + 1*kCubeHeight + kCubeClearance;
    public static double kCubeScaleHeightMed = 	kScaleHeightMed  + 1*kCubeHeight + kCubeClearance;
    public static double kCubeScaleHeightLow = 	kScaleHeightLow  + 1*kCubeHeight + kCubeClearance;
    public static double kCubeSwitchHeight =	kSwitchHeight                    + kCubeClearance + 6; //to raise the arm a little higher		
    public static double kCubeExchangeHeight = 	kExchangeHeight; 
    public static double kCubeGroundHeight = 	kGroundHeight;
    
    
    
    // ELEVATOR    
    public static double kElevatorMinHeightLimit =  0.0;	// stop at min height
	public static double kElevatorMaxHeightLimit = 65.0;	// stop at max height	// TODO: figure out what this is
	//public static double kElevatorMaxHeightLimit = 65.0;	// protect the ceiling!  TODO: comment out for competition

    public static double kElevatorZeroingVelocity = 4;		// inches per second

	public static double kElevatorQuadEncoderGain = 1.0;			// encoder is directly attached to motor drive shaft.  No gearing.
	public static double kElevatorQuadEncoderUnitsPerRev = 4096;
	public static double kElevatorGearCircum = 4.538;				// ~0.72 inch radius gear
	public static double kElevatorEncoderUnitsPerInch = kElevatorQuadEncoderUnitsPerRev / kElevatorGearCircum * kElevatorQuadEncoderGain; 

    public static double kElevataorMaxEncoderPulsePer100ms = 3200;		// velocity at a max throttle (measured using NI web interface)
    public static double kElevatorMaxPercentOutput 		= 1.0;		// percent output of motor at above throttle (using NI web interface)

    public static double kElevatorCruiseVelocity = 0.75 * kElevataorMaxEncoderPulsePer100ms;		// cruise below top speed
    public static double kElevatorTimeToCruiseVelocity = 0.25;				// seconds to reach cruise velocity
    public static double kElevatorAccel = kElevatorCruiseVelocity / kElevatorTimeToCruiseVelocity; 
    
	public static double kElevatorKf = kElevatorMaxPercentOutput * 1023.0 / kElevataorMaxEncoderPulsePer100ms;
	public static double kElevatorKp = 0.4;	   
	public static double kElevatorKd = 0.0;	// to resolve any overshoot, start at 10*Kp 
	public static double kElevatorKi = 0.0;
	
	public static int    kElevatorAllowableError = (int)(.25 * kElevatorEncoderUnitsPerInch);
	
    public static double kMinElevatorOutput = 0.2;
    public static double kMaxElevatorOutput = 1.0;
    public static double kElevatorManualOutput = 0.5;
//    public static double kElevatorMotorStallCurrentThreshold = 15.0;	// current at which we will assume the limit switch didn't catch it and we are stalled
    // 07/28/18: increasing to 25A when using new limit switches
    public static double kElevatorMotorStallCurrentThreshold = 25.0;	// current at which we will assume the limit switch didn't catch it and we are stalled
    
    public static double kDriveScaleFactorAtMaxElevatorHeight = 0.5;    
    
    // ARM BAR
	// absolute encoder values (to be used in place of limit switches)
    public static double kArmBarLength = 14.0;
    
    public static double kArmBarZeroingVelocity =	30.0;	// in degrees per second
    public static double kArmBarVelocity = 		   180.0;	// in degrees per second

    /*
     * One of the 9:1 stages broke -- replaced with 7:1
	public static double kArmBarQuadEncoderGain = 81.0 * 2.0;			// two 9:1 gear stages plus a 24:12 tooth reduction after the encoder 
	public static double kArmBarQuadEncoderUnitsPerRev = 4096;
	public static double kArmBarEncoderUnitsPerDeg = kArmBarQuadEncoderUnitsPerRev / 360.0 * kArmBarQuadEncoderGain; 
	
    public static final int kArmBarEncoderLimitUp =    +35000;	// DO NOT CHANGE!!!
    public static final int kArmBarEncoderAtZeroDeg = -124700;	
    public static final int kArmBarEncoderLimitDown = -185000;	// DO NOT CHANGE!!!
    */
    
	public static double kArmBarQuadEncoderGain = 9.0 * 7.0 * 2.0;			// two 9:1 gear stages plus a 24:12 tooth reduction after the encoder 
	public static double kArmBarQuadEncoderUnitsPerRev = 4096;
	public static double kArmBarEncoderUnitsPerDeg = kArmBarQuadEncoderUnitsPerRev / 360.0 * kArmBarQuadEncoderGain; 
	
    public static final int kArmBarEncoderLimitUp =  165000;
    public static final int kArmBarEncoderAtZeroDeg = 58500;	
    public static final int kArmBarEncoderLimitDown = 10000;
    
    public static double kArmBarCalAngleDeg =   86.6;	// at upper limit
    public static double kArmBarUpAngleDeg =    65.0;	
    public static double kArmBarFlatAngleDeg = 	 0.0;	
    public static double kArmBarDownAngleDeg = -30.0; 	// at lower limit

	
	public static double kArmBarKf = 0.0;
	public static double kArmBarKp = 0.4;
	public static double kArmBarKd = 0.0;
	public static double kArmBarKi = 0.0;
   
	public static double kMaxArmBarVoltage = 12.0;	// may be less than 12V battery voltage when testing	
//	public static double kArmBarMotorStallCurrentThreshold = 10.0;	// current at which we will assume the limit switch didn't catch it and we are stalled
	// 07/28/18: increasing to 20A when using new hall effect sensors (don't want to false trigger on current any more)
	public static double kArmBarMotorStallCurrentThreshold = 20.0;	// current at which we will assume the limit switch didn't catch it and we are stalled

	public static int kArmBarPeakCurrentLimit = 30;	// current at which current limit is activated
	public static int kArmBarPeakCurrentDuration = 200;	// duration at which current limit is activated
	public static int kArmBarContinuousCurrentLimit = 20;	// limit to this value when current limit is activated
	
    // CUBE HEIGHT
    public static double kCubeGrabHeight = 8.5;		// inches above ground where cube is grabbed
       
    
    // INTAKE
    public static double kBagMotorRPM = 13180;
    public static double kIntakeMotorGain = 7.0;
    public static double kIntakeMaxRPM = kBagMotorRPM / kIntakeMotorGain;
    public static double kIntakeRPM = 1200;
    public static double kIntakeSpeed = 0.6;
    public static double kIntakeHoldSpeed = 0.2;	// apply some force to keep in    
    public static double kOuttakeSpeed = -0.7;		// full speed reverse
    
    public static boolean kIntakeLeftMotorInverted;
    public static boolean kIntakeRightMotorInverted;
    
    public static int kCubeInProximitySensorPort = 2;    
//    public static int kCubeCloseProximitySensorPort = 1;    
	public static DigitalInput cubeInProximitySensor;
//	public static DigitalInput cubeCloseProximitySensor;

    // Do not change anything after this line!
    
    // Motor Controllers
    // (Note that if multiple Talons are dedicated to a mechanism, any sensors are attached to the master)
	public static int kRightMotorMasterTalonId;
	public static int kRightMotorSlave1TalonId;
	public static int kRightMotorSlave2TalonId;
	public static int kElevatorTalonId;
    public static int kLeftMotorMasterTalonId;
	public static int kLeftMotorSlave1TalonId;
	public static int kLeftMotorSlave2TalonId;
	public static int kArmBarTalonId;
	public static int kLeftIntakePwmChannel;
	public static int kRightIntakePwmChannel;
	
	public static int kIntakeSolenoidForwardChannel;
	public static int kIntakeSolenoidReverseChannel;

    // left motors are inverted
    public static boolean	kLeftMotorInverted;
    public static boolean	kRightMotorInverted;
    public static boolean	kLeftMotorSensorPhase;
    public static boolean	kRightMotorSensorPhase;

	public static int kDriveTrainCurrentLimit;
	
	public static int kElevatorLimitSwitchPwmId;
	public static int kArmBarLimitSwitchPwmId;
	

    // Joystick Controls
    public static int kXboxButtonA  = 1;
    public static int kXboxButtonB  = 2;
    public static int kXboxButtonX  = 3;
    public static int kXboxButtonY  = 4;
    public static int kXboxButtonLB = 5;
    public static int kXboxButtonRB = 6;
    
    public static int kXboxLStickXAxis  = 0;
    public static int kXboxLStickYAxis  = 1;
    public static int kXboxLTriggerAxis = 2;
    public static int kXboxRTriggerAxis = 3;
    public static int kXboxRStickXAxis  = 4;
    public static int kXboxRStickYAxis  = 5;

	public static int kIntakeButton 			= kXboxButtonRB;
	public static int kOuttakeButton 			= kXboxButtonLB;
	public static int kElevatorManualUpButton	= kXboxButtonY;
	public static int kElevatorManualDownButton	= kXboxButtonA;
	public static int kGrabberButton 			= kXboxButtonX;
	public static int kQuickTurnButton 			= kXboxButtonX;

	
	// Button Board Controls
	public static int kElevatorGroundButton 	= 2;
	public static int kElevatorExchangeButton 	= 1;
	public static int kElevatorSwitchButton 	= 6;
	public static int kElevatorScaleLowButton 	= 4;
	public static int kElevatorScaleMedButton 	= 3;
	public static int kElevatorScaleHighButton 	= 5;


        
    //Robot stops when joystick axis < 0.1 and >-0.1
    public static double kDriveDeadzone = 0.2;

    // Relay Ports
    public static int kLedRelayPort = 0;
    
    // Gyro
    public enum GyroSelectionEnum { BNO055, NAVX; }
    public static GyroSelectionEnum GyroSelection = GyroSelectionEnum.NAVX;

	// The I2C port the BNO055 is connected to
    public static final I2C.Port BNO055_PORT = I2C.Port.kOnboard;
    
    // BNO055 accelerometer calibration constants
    // ( -7, -34,  33, -24) - taken 10/14/2016
    // (-13, -53,  18, -24) - taken 10/14/2016
    // (  0, -59,  25, -24) - taken 10/14/2016
    // using average of the above
    public static short kAccelOffsetX =  -7;
    public static short kAccelOffsetY = -53;
    public static short kAccelOffsetZ =   25;
    public static short kAccelRadius  = -24;
    
    // The SPI port the NavX is connected to
    // (see https://www.pdocs.kauailabs.com/navx-mxp/guidance/selecting-an-interface/)
    public static final SPI.Port NAVX_PORT = SPI.Port.kMXP;						// the SPI port has low latency (<0.1 ms)

    public static byte NAVX_UPDATE_RATE = (byte) (1.0 / Constants.kLoopDt);		// the SPI port supports update rates from 4-200 Hz
   
    
    
    
    
    
    
    
    public Constants()
    {
    kRobotSelection = RobotSelectionEnum.COMPETITION_BOT;	// select which robot we are building code for (TODO: make this automatic?)
//    kRobotSelection = RobotSelectionEnum.PRACTICE_BOT;	// select which robot we are building code for (TODO: make this automatic?)
    	
    	// place robot-specific constants here
    	
    	switch (kRobotSelection)
    	{
    		case COMPETITION_BOT:
    			GyroSelection = GyroSelectionEnum.NAVX;

    			kCenterToFrontBumper = 25.0;	// position of front bumper with respect to robot center of rotation
       		    kCenterToRearBumper = 16.0;	// position of rear bumper with respect to robot center of rotation
       		    kCenterToSideBumper = 17.5;	// position of side bumper with respect to robot center of rotation
    		    kCenterToExtendedIntake = 20.0;	//measure distance from the front bumper and add it to this value then change this value
       		    

    			kDriveWheelCircumInches = 18.800 * (244.0/241.72);	// empirically corrected over a 20' test run
    		    kTrackLengthInches = 11.500;	// 23.000 counting the omniwheels
    		    kTrackWidthInches = 21.500;
    		    kTrackScrubFactor = 0.5;

    		    // Wheel Encoder
    		    kQuadEncoderGain = 1.0;			// number of drive shaft rotations per encoder shaft rotation
    											// single speed, double reduction encoder is directly coupled to the drive shaft 
    		    kQuadEncoderCodesPerRev = 64;
     		    
    		    // CONTROL LOOP GAINS
    		    double kNominalEncoderPulsePer100ms = 85;		// velocity at a nominal throttle (measured using NI web interface)
    		    double kNominalPercentOutput 		 = 0.4447;	// percent output of motor at above throttle (using NI web interface)
    		    
    		    
    		    kIntakeLeftMotorInverted = true;
    		    kIntakeRightMotorInverted = false;
    		    
    		    kDriveVelocityKp = 20.0;
    		    kDriveVelocityKi = 0.01;
    		    kDriveVelocityKd = 70.0;
    		    kDriveVelocityKf = kNominalPercentOutput * 1023.0 / kNominalEncoderPulsePer100ms;
    		    kDriveVelocityIZone = 0;
    		    kDriveVelocityRampRate = 0.375;
    		    kDriveVelocityAllowableError = 0;
 
    		    // PID gains for drive base lock loop
    		    // Units: error is 4*256 counts/rev. Max output is +/- 1023 units.
    		    kDriveBaseLockKp = 0.5;
    		    kDriveBaseLockKi = 0;
    		    kDriveBaseLockKd = 0;
    		    kDriveBaseLockKf = 0;
    		    kDriveBaseLockIZone = 0;
    		    kDriveBaseLockRampRate = 0;
    		    kDriveBaseLockAllowableError = 10;

    		    // PID gains for constant heading velocity control
    		    // Units: Error is degrees. Output is inches/second difference to
    		    // left/right.
    		    kDriveHeadingVelocityKp = 4.0;
    		    kDriveHeadingVelocityKi = 0.0;
    		    kDriveHeadingVelocityKd = 50.0;
    		    
    		    
    		    // Motor Controllers
    		    // (Note that if multiple Talons are dedicated to a mechanism, any sensors are attached to the master)

    		    kLeftMotorMasterTalonId     = 1;
    			kLeftMotorSlave1TalonId 	= 2;
    			kElevatorTalonId 			= 3;
    			kRightMotorMasterTalonId 	= 4;
    			kRightMotorSlave1TalonId 	= 5;
    			kArmBarTalonId 				= 6;
    			
    			kLeftIntakePwmChannel		= 0;
    			kRightIntakePwmChannel		= 1;
    			kIntakeSolenoidForwardChannel = 0;
    			kIntakeSolenoidReverseChannel = 1;

    		    // left motors are inverted
    		    kLeftMotorInverted  = false;
    		    kRightMotorInverted = true;
    		    kLeftMotorSensorPhase = false;
    		    kRightMotorSensorPhase = false;
    		    
    			kDriveTrainCurrentLimit = 25;
    			
    			kElevatorLimitSwitchPwmId = 5;
    			kArmBarLimitSwitchPwmId = 1;
    			
    			break;
    			
    			
    			
    			
    			
    			
    			
    			
    		case PRACTICE_BOT:
//    			GyroSelection = GyroSelectionEnum.BNO055;
    			GyroSelection = GyroSelectionEnum.NAVX;
    			
       		    kCenterToFrontBumper = 19.0;	// position of front bumper with respect to robot center of rotation
    		    kCenterToRearBumper = 19.5;	// position of rear bumper with respect to robot center of rotation
    		    kCenterToSideBumper = 17.5;	// position of side bumper with respect to robot center of rotation
    		    kCenterToExtendedIntake = 24.0;
    		    
     		    kDriveWheelCircumInches = 13.229;//13.250;
    		    kTrackLengthInches = 25.000;
    		    kTrackWidthInches = 23.000;
    		    kTrackScrubFactor = 0.5;

    		    // Wheel Encoder
    		    kQuadEncoderGain = ( 30.0 / 54.0 ) * ( 12.0 / 36.0 );	// number of drive shaft rotations per encoder shaft rotation
    																	// 54:30 drive shaft --> 3rd stage, 36:12 3rd stage --> encoder shaft     
    		    kQuadEncoderCodesPerRev = 64;
    		    
    		    // CONTROL LOOP GAINS
    		    kNominalEncoderPulsePer100ms = 900; //290;		// velocity at a nominal throttle (measured using NI web interface)
    		    kNominalPercentOutput 		 = 0.4995;	// percent output of motor at above throttle (using NI web interface)
    		    
    		    
    		    
    		    kIntakeLeftMotorInverted = false;
    		    kIntakeRightMotorInverted = true;

System.out.printf("PRACTICE_BOT: kIntakeLeftMotorInverted = %b (should be false)\n", kIntakeLeftMotorInverted);    		    
    		    
    		    kDriveVelocityKp = 2.0;
    		    kDriveVelocityKi = 0.001;
    		    kDriveVelocityKd = 100.0;
    		    kDriveVelocityKf = kNominalPercentOutput * 1023.0 / kNominalEncoderPulsePer100ms;
    		    kDriveVelocityIZone = 0;
    		    kDriveVelocityRampRate = 0.0;
    		    kDriveVelocityAllowableError = 0;

    		    // PID gains for drive base lock loop
    		    // Units: error is 4*256 counts/rev. Max output is +/- 1023 units.
    		    kDriveBaseLockKp = 0.5;
    		    kDriveBaseLockKi = 0;
    		    kDriveBaseLockKd = 0;
    		    kDriveBaseLockKf = 0;
    		    kDriveBaseLockIZone = 0;
    		    kDriveBaseLockRampRate = 0;
    		    kDriveBaseLockAllowableError = 10;

    		    // PID gains for constant heading velocity control
    		    // Units: Error is degrees. Output is inches/second difference to
    		    // left/right.
    		    kDriveHeadingVelocityKp = 0.4;
    		    kDriveHeadingVelocityKi = 0.0;
    		    kDriveHeadingVelocityKd = 5.0;
    		    
    		    
    		    // Motor Controllers
    		    // (Note that if multiple Talons are dedicated to a mechanism, any sensors are attached to the master)
  
    		    
    		    kLeftMotorMasterTalonId 	= 1;
    			kLeftMotorSlave1TalonId 	= 2;
    			kElevatorTalonId 			= 4;
    			kRightMotorMasterTalonId 	= 5;
    			kRightMotorSlave1TalonId 	= 3;
    			kArmBarTalonId 				= 6;
    			
    			
    			kLeftIntakePwmChannel		= 0;
    			kRightIntakePwmChannel		= 1;
    			kIntakeSolenoidForwardChannel = 0;
    			kIntakeSolenoidReverseChannel = 1;

    		    // left motors are inverted
    		    kLeftMotorInverted  = true;
    		    kRightMotorInverted = false;
    		    kLeftMotorSensorPhase = true;
    		    kRightMotorSensorPhase = true;
    			
    			kDriveTrainCurrentLimit = 25;
    			
    			kElevatorLimitSwitchPwmId = 0;
    			kArmBarLimitSwitchPwmId = 1;
    			
    		    break;
    	}

    	// calculated constants
    	kCenterToCornerBumper = Math.sqrt(kCenterToRearBumper*kCenterToRearBumper + kCenterToSideBumper*kCenterToSideBumper);
    	
	    kDriveWheelDiameterInches = kDriveWheelCircumInches / Math.PI;
	    kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches + kTrackLengthInches * kTrackLengthInches) / kTrackWidthInches;

	    kQuadEncoderUnitsPerRev = (int)(4*kQuadEncoderCodesPerRev / kQuadEncoderGain);    
	    
	    // Path following constants
	    kPathFollowingMaxVel    = 72.0; // inches/sec  		
	    kPathFollowingAccelTime = 0.5;
	    kPathFollowingMaxAccel  = kPathFollowingMaxVel / kPathFollowingAccelTime; // inches/sec^2	 
	    kPathFollowingLookahead = 24.0; // inches
	    kPathFollowingCompletionTolerance = 4.0; 
	    
	    // Vision constants
	    kCameraPoseX     = +7.25;	// camera location with respect to robot center of rotation, +X axis is in direction of travel
	    kCameraPoseY     =     0;	// camera location with respect to robot center of rotation, +Y axis is positive to the left
	    kCameraPoseTheta =     0;	// camera angle with respect to robot heading
	    
	    kVisionMaxVel    = 20.0; // inches/sec  		
	    kVisionMaxAccel  = 20.0; // inches/sec^2		
	    kTargetWidthInches = 10.25;
	    kPegTargetDistanceThresholdFromBumperInches = 18;		// inches to stop from target, measured from front bumper
	    kPegTargetDistanceThresholdFromCameraInches = kCenterToFrontBumper - kCameraPoseX + kPegTargetDistanceThresholdFromBumperInches;
	    kVisionCompletionTolerance = 1.0; 
	    kVisionMaxDistanceInches = 240;		// ignore targets greater than this distance
	    kVisionLookaheadDist = 24.0;	// inches
	    kCameraFOVDegrees = 42.5;			// Camera Field of View (degrees)
	    kCameraHalfFOVRadians = kCameraFOVDegrees/2.0 * Math.PI/180.0;			// Half of Camera Field of View (radians)
	    kTangentCameraHalfFOV = Math.tan(kCameraHalfFOVRadians);
	    kCameraLatencySeconds = 0.240;			// Camera image capturing latency
	    kTargetLocationFilterConstant = (30.0 * kLoopDt);		// 30 time constants in 1 second
    
	    if (cubeInProximitySensor == null)
	    {
	    	
	    	cubeInProximitySensor = new DigitalInput(Constants.kCubeInProximitySensorPort);
	    }
	   
//	    if (cubeCloseProximitySensor == null)
//	    {
//	    	cubeCloseProximitySensor = new DigitalInput(Constants.kCubeCloseProximitySensorPort);
//	    }
	    
    }
}
