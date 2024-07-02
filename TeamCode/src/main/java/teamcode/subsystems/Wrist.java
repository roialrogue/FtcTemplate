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
    private final TrcServo wristUpDown;
    private final TrcServo wristLeftRight;
    private boolean upDwonIsGrounded = true;
    private boolean leftRightisFlat = true;

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

    public boolean isWirstUpDwonGrounded()
    {
        return upDwonIsGrounded;
    }

    public boolean isWristLeftRightFlat()
    {
        return leftRightisFlat;
    }
    public void wristGround(TrcEvent event) {
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_GROUNDED, event, RobotParams.WRIST_SERVO_TIME);
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_FLAT);
    }
    public void wristUpSquare(TrcEvent event) {
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_FLAT);
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_BOARD, event, RobotParams.WRIST_SERVO_TIME);
    }

    public void wristUpInverted(TrcEvent event) {
        wristLeftRight.setPosition(RobotParams.WRIST_LEFTRIGHT_INVERT);
        wristUpDown.setPosition(RobotParams.WRIST_UPDWON_BOARD, event, RobotParams.WRIST_SERVO_TIME);
    }
}
