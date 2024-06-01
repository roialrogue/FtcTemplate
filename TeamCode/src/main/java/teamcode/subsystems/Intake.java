package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcCommonLib.trclib.TrcEvent;
import TrcFtcLib.ftclib.FtcServo;
import teamcode.Robot;
import teamcode.RobotParams;

public class Intake {
    private final TrcDbgTrace tracer;
    private final String instanceName;
    private final Robot robot;
    private final FtcServo intakeServoLeft;
    private final FtcServo intakeServoRight;
    private boolean leftClawClosed;
    private boolean rightClawClosed;

    public Intake(String instanceName, Robot robot) {
        this.tracer = new TrcDbgTrace();
        this.instanceName = instanceName;
        this.robot = robot;
        intakeServoLeft = new FtcServo(instanceName + ".leftServo");
        intakeServoLeft.setInverted(RobotParams.INTAKE_SERVO_LEFT_INVERTED);
        intakeServoRight = new FtcServo(instanceName + ".rightServo");
        intakeServoRight.setInverted(RobotParams.INTAKE_SERVO_RIGHT_INVERTED);
    }

    public boolean isLeftClawClosed() {

        return leftClawClosed;
    }

    public boolean isRightClawClosed() {

        return rightClawClosed;
    }


    public void openClaw(TrcEvent event)
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MAX_POS, event, RobotParams.INTAKE_SERVO_TIME);
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MAX_POS);
        leftClawClosed = false;
        rightClawClosed = false;
    }

    public void closeClaw(TrcEvent event)
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MIN_POS, event, RobotParams.INTAKE_SERVO_TIME);
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MIN_POS);
        leftClawClosed = true;
        rightClawClosed = true;
    }

    public void openLeft(TrcEvent event)
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MAX_POS, event, RobotParams.INTAKE_SERVO_TIME);
        leftClawClosed = false;
    }

    public void closeLeft(TrcEvent event) {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MIN_POS, event, RobotParams.INTAKE_SERVO_TIME);
        leftClawClosed = true;
    }

    public void openRight(TrcEvent event)
    {
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MAX_POS, event, RobotParams.INTAKE_SERVO_TIME);
        rightClawClosed = false;

    }

    public void closeRight(TrcEvent event)
    {
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MIN_POS, event, RobotParams.INTAKE_SERVO_TIME);
        rightClawClosed = true;
    }
}