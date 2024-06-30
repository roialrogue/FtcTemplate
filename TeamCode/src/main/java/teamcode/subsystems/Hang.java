package teamcode.subsystems;

import TrcCommonLib.trclib.TrcMotor;
import TrcFtcLib.ftclib.FtcMotorActuator;
import teamcode.RobotParams;

public class Hang
{
    private final TrcMotor hangMotor;

    public Hang()
    {
        FtcMotorActuator.Params hangParams = new FtcMotorActuator.Params()
                .setMotorInverted(RobotParams.HANG_MOTOR_INVERTED)
                .setLowerLimitSwitch(
                        RobotParams.HANG_HAS_LOWER_LIMIT_SWITCH,
                        RobotParams.HANG_LOWER_LIMIT_INVERTED)
                .setUpperLimitSwitch(
                        RobotParams.HANG_HAS_UPPER_LIMIT_SWITCH,
                        RobotParams.HANG_UPPER_LIMIT_INVERTED)
                .setVoltageCompensationEnabled(RobotParams.HANG_VOLTAGE_COMP_ENABLED)
                .setPositionScaleAndOffset(RobotParams.HANG_DEG_PER_COUNT, RobotParams.HANG_OFFSET);
        hangMotor = new FtcMotorActuator(RobotParams.HWNAME_HANG, hangParams).getActuator();
        hangMotor.setPositionPidTolerance(RobotParams.HANG_TOLERANCE);
        hangMotor.setStallProtection(
                RobotParams.HANG_STALL_MIN_POWER, RobotParams.HANG_STALL_TOLERANCE,
                RobotParams.HANG_STALL_TIMEOUT, RobotParams.HANG_STALL_RESET_TIMEOUT);
    }

    public TrcMotor getHangMotor()
    {
        return hangMotor;
    }

}
