package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcCommonLib.trclib.TrcEvent;
import TrcCommonLib.trclib.TrcServo;
import TrcFtcLib.ftclib.FtcServo;
import teamcode.Robot;
import teamcode.RobotParams;

public class Wrist {
    private final TrcDbgTrace tracer;
    private final String instanceName;
    private final Robot robot;
    private final FtcServo wristUpDown;
    private final FtcServo wristLeftRight;
    private String upDownPosition;
    private boolean leftRightisFlat;

    public Wrist(String instanceName, Robot robot)
    {
        this.tracer = new TrcDbgTrace();
        this.instanceName = instanceName;
        this.robot = robot;
        wristUpDown = new FtcServo(instanceName + ".upDown");
        wristUpDown.setInverted(RobotParams.WRIST_UPDWON_INVERTED);
        wristLeftRight = new FtcServo(instanceName + ".leftRight");
        wristLeftRight.setInverted(RobotParams.WRIST_LEFTRIGHT_INVERTED);
        wristGround(null);
    }

    public String WirstUpDwonPosition()
    {
        return upDownPosition;
    }

    public boolean isWristLeftRightFlat()
    {
        return leftRightisFlat;
    }

    public void wristGround(TrcEvent event)
    {
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_GROUNDED, event, RobotParams.WRIST_SERVO_TIME);
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_FLAT);
        leftRightisFlat = true;
        upDownPosition = "GROUNDED";
    }
    public void wristUpSquare(TrcEvent event)
    {
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_FLAT);
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_BOARD, event, RobotParams.WRIST_SERVO_TIME);
        leftRightisFlat = true;
        upDownPosition = "BOARD";
    }

    public void wristUpInverted(TrcEvent event)
    {
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_INVERT);
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_BOARDINVERT, event, RobotParams.WRIST_SERVO_TIME);
        leftRightisFlat = false;
        upDownPosition = "BOARDINVERT";
    }
}
