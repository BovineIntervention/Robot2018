package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.Constants.RobotSelectionEnum;
import org.usfirst.frc.team686.robot.command_status.ArmBarState;
import org.usfirst.frc.team686.robot.command_status.ElevatorState;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class ElevatorArmBar extends Subsystem {
	
	private static ElevatorArmBar instance = new ElevatorArmBar();
	public static ElevatorArmBar getInstance() { return instance; }
	
	public enum ManualAutoStateEnum { MANUAL, AUTO };
	ManualAutoStateEnum manualAutoState = ManualAutoStateEnum.AUTO; 
			
	public enum ElevatorArmBarStateEnum 
	{
		// state		Bottom of Cube Height				Retracted Arm Angle				Extended Arm Angle
		// -----		---------------------				-------------------				------------------
		START_OF_MATCH(	Constants.kCubeGroundHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),	// needed for elevator / armbar limit switch calibration
		GROUND(			Constants.kCubeGroundHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarDownAngleDeg),
		EXCHANGE(		Constants.kCubeExchangeHeight, 		Constants.kArmBarDownAngleDeg, 	Constants.kArmBarDownAngleDeg),
//		EXCHANGE(		Constants.kCubeExchangeHeight, 		-25, 	-25),
		SWITCH(			Constants.kCubeSwitchHeight, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_LOW(		Constants.kCubeScaleHeightLow, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_MED(		Constants.kCubeScaleHeightMed, 		Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		SCALE_HIGH(		Constants.kCubeScaleHeightHigh, 	Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg),
		MANUAL(			0, 									Constants.kArmBarUpAngleDeg, 	Constants.kArmBarUpAngleDeg);
		
		public double bottomOfCubeHeight;	// in inches 
		public double retractedArmAngle; 	// in degrees
		public double extendedArmAngle; 	// in degrees
		
		ElevatorArmBarStateEnum(double _bottomOfCubeHeight, double _retractedArmAngle, double _extendedArmAngle)
		{
			this.bottomOfCubeHeight = _bottomOfCubeHeight;
			this.retractedArmAngle =  _retractedArmAngle;
			this.extendedArmAngle =   _extendedArmAngle;
		}
	}
	public ElevatorArmBarStateEnum state = ElevatorArmBarStateEnum.START_OF_MATCH;
	
	public ElevatorLoop elevatorLoop;
	public ArmBarLoop armBarLoop;

	boolean extended = false;
	double armBarAngle = 0.0;
	double elevatorHeight = 0.0;
	
	Intake intake = Intake.getInstance();
	boolean prevIntakeButton = false;
	boolean intakeToggle = false;
	
	private ElevatorArmBar()
	{
		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
			elevatorLoop = ElevatorLoop.getInstance();
		armBarLoop = ArmBarLoop.getInstance();

		disable();
		state = ElevatorArmBarStateEnum.START_OF_MATCH;
		set(state, false);
	}
	
	public void disable()
	{
		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
			elevatorLoop.disable();
		armBarLoop.disable();
	}
	
	public void enable()
	{
		prevIntakeButton = false;
		intakeToggle = false;

		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
			elevatorLoop.enable();
		armBarLoop.enable();
	}

	
	// elevator controls
	public void processInputs( boolean _manualUpButton, boolean _manualDownButton, boolean _intakeButton, boolean _outtakeButton,
			boolean _groundButton, boolean _exchangeButton, boolean _switchButton, boolean _scaleLowButton, boolean _scaleMedButton, boolean _scaleHighButton)
	{
		// manual / auto state machine
		if (_manualUpButton || _manualDownButton)
			manualAutoState = ManualAutoStateEnum.MANUAL;

		if (_groundButton || _exchangeButton || _switchButton || _scaleLowButton || _scaleMedButton || _scaleHighButton)
			manualAutoState = ManualAutoStateEnum.AUTO;
		
		
		
		if (manualAutoState == ManualAutoStateEnum.MANUAL)
		{
			if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
			{
				if (_manualUpButton)
					elevatorLoop.manualUp();
				else if (_manualDownButton)
					elevatorLoop.manualDown();
				else
					elevatorLoop.manualStop();
			}
		}
		
		if (manualAutoState == ManualAutoStateEnum.AUTO)
		{
			if (_groundButton) 		{ set(ElevatorArmBarStateEnum.GROUND, extended); }
			if (_exchangeButton) 	{ set(ElevatorArmBarStateEnum.EXCHANGE, extended); }
			if (_switchButton) 		{ set(ElevatorArmBarStateEnum.SWITCH, extended); }
			if (_scaleLowButton) 	{ set(ElevatorArmBarStateEnum.SCALE_LOW, extended); }
			if (_scaleMedButton) 	{ set(ElevatorArmBarStateEnum.SCALE_MED, extended); }
			if (_scaleHighButton) 	{ set(ElevatorArmBarStateEnum.SCALE_HIGH, extended); }
		}

		boolean john_wants_intake_toggle = false;
		
		if (john_wants_intake_toggle)
		{
			if (_intakeButton != prevIntakeButton)
			{
				if (_intakeButton && state == ElevatorArmBarStateEnum.GROUND)
				{
					intakeToggle = !intakeToggle;
					if (intakeToggle)
					{
						set(state, true);
						intake.grabberOut();
						intake.startIntake();
					}
					else
					{
						set(state, false);
						intake.grabberIn();
						intake.startHold();
					}
				}
				prevIntakeButton = _intakeButton;
			} 
		}
		else
		{
			// nope -- john wants intake button to be held down
			if (_intakeButton)
			{
				set(state, true);
				intake.grabberOut();
				intake.startIntake();
			}
			prevIntakeButton = _intakeButton;
		}
*/
		
		if (_intakeButton)
		{
			set(state, true);
			if (_intakeButton != prevIntakeButton)
			{
				intake.grabberIn();
				intake.startIntake();
			}
		}
		else
		{
			set(state, false);
			if (_intakeButton != prevIntakeButton)
			{			
				intake.grabberIn();
				intake.startHold();
			}
		}
		prevIntakeButton = _intakeButton;
		
		
		// grabber is always in and intake stopped when off the ground
		if (state != ElevatorArmBarStateEnum.GROUND)
		{
			intake.grabberIn();
			//intake.stopIntake();
		}		
	}
	
	
	public void set(ElevatorArmBarStateEnum _newState, boolean _extended)
	{
		if (state != _newState || extended != _extended)
		{
			state = _newState;
			extended = _extended;
			
			if (state == ElevatorArmBarStateEnum.START_OF_MATCH)
			{
				// special case where we need to force elevator and arm bar to correct positions for limit switch calibration
				armBarAngle = state.retractedArmAngle;
				elevatorHeight = state.bottomOfCubeHeight;
			}
			else if (state == ElevatorArmBarStateEnum.GROUND)
			{
				// special case where we need to force elevator and arm bar to correct positions for limit switch calibration
				armBarAngle = (extended ? state.extendedArmAngle : state.retractedArmAngle);
				elevatorHeight = 0;
			}
			else
			{
				// first, set arm bar angle based on current elevator state
				armBarAngle = (extended ? state.extendedArmAngle : state.retractedArmAngle);
				// next, command elevator to correct height
				elevatorHeight = calcElevatorHeight(state.bottomOfCubeHeight, armBarAngle);
			}

			armBarLoop.setTarget(armBarAngle);
			if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
				elevatorLoop.setTarget(elevatorHeight);
			
			//System.out.println(state.bottomOfCubeHeight + " " + state.retractedArmAngle + " " + state.extendedArmAngle);
			//System.out.printf("ElevatorArmState = %s, Extend = %d, Elevator Target: %.1f, ArmBar Target = %.1f\n", state.toString(), (extended ? 1 : 0), elevatorLoop.getTarget(), armBarLoop.getTarget());
		}
	}
	
	public double get()
	{
		return elevatorHeight;
	}

	
	private double calcElevatorHeight(double _bottomOfCubeHeight, double _armBarAngleDeg)
	{
//		System.out.println("_bottomOfCubeHeight: " + _bottomOfCubeHeight);
//		System.out.println("_armBarAngleDeg: " + _armBarAngleDeg);
//		System.out.println("-Lsin(theta): " + (-Constants.kArmBarLength * Math.sin(_armBarAngleDeg * Math.PI / 180.0)));
//		System.out.println("+Lsin(theta_down): " + (Constants.kArmBarLength * Math.sin(Constants.kArmBarDownAngleDeg * Math.PI / 180.0)));
		
		// adjust height of elevator based on change in angle of arm bar from intake angle to expected outtake angle
		return _bottomOfCubeHeight - Constants.kArmBarLength * Math.sin(_armBarAngleDeg * Math.PI / 180.0) + 
									 Constants.kArmBarLength * Math.sin(Constants.kArmBarDownAngleDeg * Math.PI / 180.0);
	}

	@Override
	public void stop()
	{
		armBarLoop.stop();
		if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
			elevatorLoop.stop();
		intake.stop();
	}

	@Override
	public void zeroSensors() {}

	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			put("ElevatorArmBar/state", state.toString() );
			put("ElevatorArmBar/bottomOfCubeHeight", state.bottomOfCubeHeight );
			put("ElevatorArmBar/armBarAngle", armBarAngle );
			put("ElevatorArmBar/elevatorHeight", elevatorHeight );
			
			ArmBarState.getInstance().getLogger().log();
			if (Constants.kRobotSelection == RobotSelectionEnum.COMPETITION_BOT)		 	
				ElevatorState.getInstance().getLogger().log();
        }
        
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
