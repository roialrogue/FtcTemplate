package teamcode.subsystems;

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
        elevator.setSoftwarePidEnabled(true);
        elevator.setPositionPidParameters(
                RobotParams.ELEVATOR_KP, RobotParams.ELEVATOR_KI, RobotParams.ELEVATOR_KD, RobotParams.ELEVATOR_KF,
                RobotParams.ELEVATOR_IZONE, RobotParams.ELEVATOR_TOLERANCE);
        elevator.setPositionPidPowerComp(this::getPowerComp);
        elevator.setStallProtection(
                RobotParams.ELEVATOR_STALL_MIN_POWER, RobotParams.ELEVATOR_STALL_TOLERANCE,
                RobotParams.ELEVATOR_STALL_TIMEOUT, RobotParams.ELEVATOR_STALL_RESET_TIMEOUT);

    }
    public TrcMotor getElevator()
    {
        return elevator;
    }

    private double getPowerComp(double power)
    {
        double elevatorPos = elevator.getPosition();
        double distanceToTop = Math.abs(RobotParams.ELEVATOR_MAX - elevatorPos);
        double distanceToBottom = Math.abs(elevatorPos - RobotParams.ELEVATOR_MIN);
        double powerComp = distanceToBottom > RobotParams.ELEVATOR_TOLERANCE ? RobotParams.ELEVATOR_GRAVITY_COMP: 0.0;

//        if(power < 0.0) {
//            if (distanceToBottom <= RobotParams.ELEVATOR_POWERCOMP_DISTANCE_THRESHOLD_LOWER) {
//                powerComp += power * (RobotParams.ELEVATOR_SLOWDOWN_SCALE_LOWER - 1.0);
//            }
//        }
//        else
//        {
//            if (distanceToTop <= RobotParams.ELEVATOR_POWERCOMP_DISTANCE_THRESHOLD_UPPER) {
//                powerComp = power * (RobotParams.ELEVATOR_SLOWDOWN_SCALE_UPPER - 1.0);
//            }
//        }
        return powerComp;
    }
}



