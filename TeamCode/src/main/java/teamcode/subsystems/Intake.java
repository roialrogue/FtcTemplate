package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcCommonLib.trclib.TrcEvent;
import TrcCommonLib.trclib.TrcSensor;
import TrcCommonLib.trclib.TrcTrigger;
import TrcCommonLib.trclib.TrcTriggerThresholdZones;
import TrcFtcLib.ftclib.FtcDcMotor;
import TrcFtcLib.ftclib.FtcServo;
import TrcFtcLib.ftclib.FtcDistanceSensor;
import teamcode.Robot;
import teamcode.RobotParams;

public class Intake {
    private final TrcDbgTrace tracer;
    private final String instanceName;
    private final Robot robot;
    private final FtcServo intakeServoLeft;
    private final FtcServo intakeServoRight;
    private TrcEvent completionEvent = null;

    public Intake(String instanceName, Robot robot) {
        this.tracer = new TrcDbgTrace();
        this.instanceName = instanceName;
        this.robot = robot;
        intakeServoLeft = new FtcServo(instanceName + ".Servo"); //would i need to name them left and right in the string
        intakeServoLeft.setServoInverted(RobotParams.INTAKE_SERVO_LEFT_INVERTED);
        intakeServoRight = new FtcServo(instanceName + ".Servo");
        intakeServoRight.setServoInverted(RobotParams.INTAKE_SERVO_RIGHT_INVERTED);
    }

    //do you need a duration call with a servo?
    public void openClaw(double delay, double stepRate, TrcEvent event)
    {
        intakeServoLeft.setPosition(delay,RobotParams.INTAKE_SERVO_LEFT_MAX_POS, stepRate, event);
        intakeServoRight.setPosition(delay,RobotParams.INTAKE_SERVO_RIGHT_MAX_POS, stepRate, event);
    }

    public void openClaw()
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MAX_POS);
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MAX_POS);

    }

    public void closeClaw(double delay, double stepRate, TrcEvent event)
    {
        intakeServoLeft.setPosition(delay,RobotParams.INTAKE_SERVO_LEFT_MIN_POS, stepRate, event);
        intakeServoRight.setPosition(delay,RobotParams.INTAKE_SERVO_RIGHT_MIN_POS, stepRate, event);
    }

    public void closeClaw()
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MIN_POS);
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MIN_POS);
    }

    public void openLeft(double delay, double stepRate, TrcEvent event)
    {
        intakeServoLeft.setPosition(delay,RobotParams.INTAKE_SERVO_LEFT_MAX_POS, stepRate, event);
    }

    public void openLeft()
    {
        intakeServoLeft.setPosition(RobotParams.INTAKE_SERVO_LEFT_MAX_POS);
    }

    public void closeLeft(double delay, double stepRate, TrcEvent event)
    {
        intakeServoLeft.setPosition(delay,RobotParams.INTAKE_SERVO_LEFT_MIN_POS, stepRate, event);
    }

    public void openRight(double delay, double stepRate, TrcEvent event)
    {
        intakeServoRight.setPosition(delay,RobotParams.INTAKE_SERVO_RIGHT_MAX_POS, stepRate, event);
    }

    public void openRight()
    {
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MAX_POS);

    }

    public void closeRight(double delay, double stepRate, TrcEvent event)
    {
        intakeServoRight.setPosition(delay,RobotParams.INTAKE_SERVO_RIGHT_MIN_POS, stepRate, event);
    }

    public void closeRight()
    {
        intakeServoRight.setPosition(RobotParams.INTAKE_SERVO_RIGHT_MIN_POS);
    }



}



