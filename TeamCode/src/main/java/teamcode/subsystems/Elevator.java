package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcCommonLib.trclib.TrcMotor;
import TrcFtcLib.ftclib.FtcMotorActuator;
import teamcode.RobotParams;

public class Elevator {
    private final TrcMotor elevator;

    public Elevator() {

        FtcMotorActuator.Params elevatorParams = new FtcMotorActuator.Params()
                .setMotorInverted(RobotParams.ELEVATOR_MOTOR_INVERTED)
                .setLowerLimitSwitch(
                        RobotParams.ELEVATOR_HAS_LOWER_LIMIT_SWITCH,
                        RobotParams.ELEVATOR_LOWER_LIMIT_INVERTED)
                .setUpperLimitSwitch(
                        RobotParams.ELEVATOR_HAS_UPPER_LIMIT_SWITCH,
                        RobotParams.ELEVATOR_UPPER_LIMIT_INVERTED)
                .setVoltageCompensationEnabled(RobotParams.ELEVATOR_VOLTAGE_COMP_ENABLED)
                .setPositionScaleAndOffset(RobotParams.ELEVATOR_INCHES_PER_COUNT, RobotParams.ELEVATOR_OFFSET)
                .setPositionPresets(RobotParams.ELEVATOR_PRESETS_TOLERANCE, RobotParams.ELEVATOR_PRESETS);;
        elevator = new FtcMotorActuator(RobotParams.HWNAME_ELEVATOR, elevatorParams).getActuator();
        elevator.setTraceLevel(TrcDbgTrace.MsgLevel.DEBUG, false,false,null);
        elevator.setSoftwarePidEnabled(true);
        elevator.setPositionPidParameters(
                RobotParams.ELEVATOR_KP, RobotParams.ELEVATOR_KI, RobotParams.ELEVATOR_KD, RobotParams.ELEVATOR_KF,
                RobotParams.ELEVATOR_IZONE, RobotParams.ELEVATOR_TOLERANCE);
        elevator.setStallProtection(
                RobotParams.ELEVATOR_STALL_MIN_POWER, RobotParams.ELEVATOR_STALL_TOLERANCE,
                RobotParams.ELEVATOR_STALL_TIMEOUT, RobotParams.ELEVATOR_STALL_RESET_TIMEOUT);

    }
    public TrcMotor getElevator()
    {
        return elevator;
    }
}



