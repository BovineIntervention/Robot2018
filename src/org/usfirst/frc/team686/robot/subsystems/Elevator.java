package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.loops.ArmBarLoop;
import org.usfirst.frc.team686.robot.loops.ElevatorLoop;

public class Elevator extends Subsystem {
	
	private static Elevator instance = new Elevator();
	public static Elevator getInstance() { return instance; }
	
	public ElevatorLoop elevatorLoop = ElevatorLoop.getInstance();

	public enum ElevatorHeight { START_OF_MATCH, INTAKE, EXCHANGE, DRIVE, SWITCH, SCALE_LOW, SCALE_MED, SCALE_HIGH; }
	
	private Elevator()
	{
		disable();
	}
	
	public void disable() { elevatorLoop.disable(); }
	public void enable()  { elevatorLoop.enable(); }
	
	public void set(ElevatorHeight _heightSel)
	{
		double goal = 0.0;
		
		switch (_heightSel)
		{
		case START_OF_MATCH:	goal = 0.0;		break;
		case INTAKE:			goal = 0.0;		break;
		case EXCHANGE:			goal = 0.0;		break;	// arm bar must move up a little
		case DRIVE:				goal = 0.0;		break;
		case SWITCH:			goal = calcElevatorHeight(Constants.kSwitchHeight    + Constants.kCubeClearance, 0.0);		break;
		case SCALE_LOW:			goal = calcElevatorHeight(Constants.kScaleHeightLow  + Constants.kCubeClearance, 0.0);		break;
		case SCALE_MED:			goal = calcElevatorHeight(Constants.kScaleHeightMed  + Constants.kCubeClearance, 0.0);		break;
		case SCALE_HIGH:		goal = calcElevatorHeight(Constants.kScaleHeightHigh + Constants.kCubeClearance, 0.0);		break;
		default:				goal = 0.0;		break;
		}
		
		System.out.printf("Elevator Height: %.1f (%s)\n", goal, _heightSel.toString());
		elevatorLoop.setGoal(goal);
	}
	
	
	
	private double calcElevatorHeight(double _bottomOfCubeHeight, double _expectedArmBarAngleDeg)
	{
		// adjust height of elevator based on change in angle of arm bar from intake angle to expected outtake angle
		return _bottomOfCubeHeight - Constants.kArmBarLength * Math.sin((_expectedArmBarAngleDeg-Constants.kArmBarDownAngleDeg) * Math.PI / 180.0);
	}

	@Override
	public void stop() {
		elevatorLoop.stop();
	}

	@Override
	public void zeroSensors() {
		// TODO Auto-generated method stub
	}

	
	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
			try // pathFollowingController doesn't exist until started
			{
				put("Elevator/State", elevatorLoop.toString() );
				put("Elevator/Position", elevatorLoop.getPosition() );
				put("Elevator/Goal", elevatorLoop.getGoal() );
				put("Elevator/LimitSwitch", elevatorLoop.getLimitSwitch() );
			} catch (NullPointerException e) {
				// skip logging pathFollowingController when it doesn't exist
			}
        }
    };
    
    public DataLogger getLogger() { return logger; }
	
	
}
