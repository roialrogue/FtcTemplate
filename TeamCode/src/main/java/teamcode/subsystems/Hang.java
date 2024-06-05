package teamcode.subsystems;

import TrcCommonLib.trclib.TrcMotor;
import TrcFtcLib.ftclib.FtcMotorActuator;
import teamcode.RobotParams;

public class Hang
{
//    private final TrcMotor hangMotor;

    public Hang()
    {
//        FtcMotorActuator.Params hangParams = new FtcMotorActuator.Params()
//                .setMotorInverted(RobotParams.HANG_MOTOR_INVERTED)
//                .setLowerLimitSwitch(
//                        RobotParams.HANG_HAS_LOWER_LIMIT_SWITCH,
//                        RobotParams.HANG_LOWER_LIMIT_INVERTED)
//                .setUpperLimitSwitch(
//                        RobotParams.HANG_HAS_UPPER_LIMIT_SWITCH,
//                        RobotParams.HANG_UPPER_LIMIT_INVERTED)
//                .setVoltageCompensationEnabled(RobotParams.HANG_VOLTAGE_COMP_ENABLED)
//                .setPositionScaleAndOffset(RobotParams.HANG_INCHES_PER_COUNT, RobotParams.HANG_OFFSET)
//                .setPositionPresets(RobotParams.HANG_PRESET_TOLERANCE,RobotParams.HANG_PRESETS);
//        hangMotor = new FtcMotorActuator(RobotParams.HWNAME_HANG, hangParams).getActuator();
//        hangMotor.setSoftwarePidEnabled(true);
//        hangMotor.setPositionPidCoefficients(
//                RobotParams.HANG_KP, RobotParams.HANG_KI, RobotParams.HANG_KD, RobotParams.HANG_KF, RobotParams.HANG_IZONE);
//        hangMotor.setPositionPidTolerance(RobotParams.HANG_TOLERANCE);
//        hangMotor.setStallDetectionEnabled(
//                RobotParams.HANG_STALL_DETECTION_DELAY,RobotParams.HANG_STALL_DETECTION_TIMEOUT,RobotParams.HANG_STALL_ERR_RATE_THRESHOLD
//        );
    }

    public TrcMotor getHangMotor()
    {
        return hangMotor;
    }
}
