package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop;

public class ElevatorArmBar extends Subsystem {
	
	private static ElevatorArmBar instance = new ElevatorArmBar();
	public static ElevatorArmBar getInstance() { return instance; }
	
	public enum ElevatorArmBarState 
	{
		// state		Bottom of Cube Height				Retracted Arm Angle				Extended Arm Angle
		// -----		---------------------				-------------------				------------------
		START_OF_MATCH(	Constants.kCubeGroundHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),	// needed for elevator / armbar limit switch calibration
		DRIVE(			Constants.kCubeDriveHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarFlatAngleDeg),
		GROUND(			Constants.kCubeGroundHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarFlatAngleDeg),
		EXCHANGE(		Constants.kCubeExchangeHeight, 		Constants.kArmBarDownAngleDeg, 	Constants.kArmBarDownAngleDeg),
		SWITCH(			Constants.kCubeSwitchHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_LOW(		Constants.kCubeScaleHeightLow, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_MED(		Constants.kCubeScaleHeightMed, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_HIGH(		Constants.kCubeScaleHeightHigh, 	Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg);
		
		public double bottomOfCubeHeight;	// in inches 
		public double retractedArmAngle; 	// in degrees
		public double extendedArmAngle; 	// in degrees
		
		ElevatorArmBarState(double _bottomOfCubeHeight, double _retractedArmAngle, double _extendedArmAngle)
		{
			this.bottomOfCubeHeight = _bottomOfCubeHeight;
			this.retractedArmAngle =  _retractedArmAngle;
			this.extendedArmAngle =   _extendedArmAngle;
		}
	}
	ElevatorArmBarState state = ElevatorArmBarState.START_OF_MATCH;
	
	public ElevatorLoop elevatorLoop = ElevatorLoop.getInstance();
	public ArmBarLoop armBarLoop = ArmBarLoop.getInstance();
	boolean extended = false;
	
	
	private ElevatorArmBar()
	{
		disable();
		state = ElevatorArmBarState.START_OF_MATCH;
		set(state, false);
	}
	
	public void disable()
	{
		elevatorLoop.disable();
		armBarLoop.disable();
	}
	
	public void enable()
	{
		elevatorLoop.enable();
		armBarLoop.enable();
	}

	public void retract()
	{
		set(state, false);
	}
	
	public void extend()
	{
		set(state, true);
	}

	public void setHeight(ElevatorArmBarState _newState)
	{
		set(_newState, extended);
	}
	

	private void set(ElevatorArmBarState _newState, boolean _extended)
	{
		state = _newState;
		extended = _extended;
		
		if (state == ElevatorArmBarState.START_OF_MATCH)
		{
			// special case where we need to force elevator and arm bar to correct positions for limit switch calibration
			armBarLoop.setGoal(state.retractedArmAngle);
			elevatorLoop.setGoal(state.bottomOfCubeHeight);
		}
		else
		{
			// first, set arm bar angle based on current elevator state
			double armBarAngle = (extended ? state.extendedArmAngle : state.retractedArmAngle);
			armBarLoop.setGoal(armBarAngle);

			// next, command elevator to correct height
			double elevatorHeight = calcElevatorHeight(state.bottomOfCubeHeight, armBarAngle);
			elevatorLoop.setGoal(elevatorHeight);
		}
		
//		System.out.println(state.bottomOfCubeHeight + " " + state.retractedArmAngle + " " + state.extendedArmAngle);
		
//		System.out.printf("ElevatorArmState = %s, Extend = %d, Elevator Goal: %.1f, ArmBar Goal = %.1f\n", state.toString(), (extended ? 1 : 0), elevatorLoop.getGoal(), armBarLoop.getGoal());
	}
	
	
	private double calcElevatorHeight(double _bottomOfCubeHeight, double _armBarAngleDeg)
	{
//		System.out.println("_bottomOfCubeHeight: " + _bottomOfCubeHeight);
//		System.out.println("_armBarAngleDeg: " + _armBarAngleDeg);
//		System.out.println("-Lsin(theta): " + (-Constants.kArmBarLength * Math.sin(_armBarAngleDeg * Math.PI / 180.0)));
//		System.out.println("+Lsinn(theta_down): " + (Constants.kArmBarLength * Math.sin(Constants.kArmBarDownAngleDeg * Math.PI / 180.0)));
		
		// adjust height of elevator based on change in angle of arm bar from intake angle to expected outtake angle
		return _bottomOfCubeHeight - Constants.kArmBarLength * Math.sin(_armBarAngleDeg * Math.PI / 180.0) + 
									 Constants.kArmBarLength * Math.sin(Constants.kArmBarDownAngleDeg * Math.PI / 180.0);
	}

	@Override
	public void stop()
	{
		armBarLoop.stop();
		elevatorLoop.stop();
	}

	@Override
	public void zeroSensors() {}

	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			put("ElevatorArmBar/Extended", extended );
			
			put("ArmBar/State", armBarLoop.state.toString() );
			put("ArmBar/Position", armBarLoop.getPosition() );
			put("ArmBar/Goal", armBarLoop.getGoal() );
			put("ArmBar/LimitSwitch", armBarLoop.getLimitSwitch() );

			put("Elevator/State", elevatorLoop.toString() );
			put("Elevator/Position", elevatorLoop.getPosition() );
			put("Elevator/Goal", elevatorLoop.getGoal() );
			put("Elevator/LimitSwitch", elevatorLoop.getLimitSwitch() );
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
