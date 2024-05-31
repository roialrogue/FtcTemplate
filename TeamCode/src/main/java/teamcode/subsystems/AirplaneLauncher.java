package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcFtcLib.ftclib.FtcDcMotor;
import TrcFtcLib.ftclib.FtcServo;
import teamcode.Robot;
import teamcode.RobotParams;
public class AirplaneLauncher
{
    private final TrcDbgTrace tracer;
    private final String instanceName;
    private final Robot robot;
    private final FtcDcMotor launcherMotor;
    private final FtcServo launcherServo;

    public AirplaneLauncher(String instanceName, Robot robot)
    {
        this.tracer = new TrcDbgTrace();
        this.instanceName = instanceName;
        this.robot = robot;
        launcherMotor = new FtcDcMotor(instanceName + ".motor");
        launcherMotor.setMotorInverted(RobotParams.LAUNCHER_MOTOR_INVERTED);
        launcherServo = new FtcServo(instanceName + ".servo");
        launcherServo.setServoInverted(RobotParams.LAUNCHER_SERVO_INVERTED);
    }

    public void setUpLauncher()
    {
        launcherServo.setPower(RobotParams.LAUNCHER_SERVO_MIN_POS);
    }

    public void AirplaneLaunch()
    {
        launcherMotor.setPower(0,RobotParams.LAUNCHER_MOTOR_POWER,2);
        launcherServo.setPower(.5,RobotParams.LAUNCHER_SERVO_MAX_POS);
        launcherServo.setPower(.5,RobotParams.LAUNCHER_SERVO_MIN_POS);
        launcherServo.setPower(.5,0); //i dont need this becuase the duration on the first setPower call right?
    }
}