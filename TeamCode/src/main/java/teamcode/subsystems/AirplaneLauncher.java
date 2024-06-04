package teamcode.subsystems;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcFtcLib.ftclib.FtcDcMotor;
import TrcFtcLib.ftclib.FtcServo;
import teamcode.Robot;
import teamcode.RobotParams;
import TrcCommonLib.trclib.TrcEvent;
import TrcCommonLib.trclib.TrcRobot;
import TrcCommonLib.trclib.TrcStateMachine;
import TrcCommonLib.trclib.TrcTaskMgr;

public class AirplaneLauncher
{
    private enum State
    {
        START,
        LAUNCH,
        DONE
    }
    private final TrcDbgTrace tracer;
    private final String instanceName;
    private final Robot robot;
    private final FtcDcMotor launcherMotor;
    private final FtcServo launcherServo;
    private boolean ServoLaunchedPos;
    private final TrcTaskMgr.TaskObject launchTaskObj;
    private final TrcEvent event;
    private final TrcStateMachine<State> sm;
    private TrcEvent completionEvent = null;

    public AirplaneLauncher(String instanceName, Robot robot)
    {
        this.tracer = new TrcDbgTrace();
        this.instanceName = instanceName;
        this.robot = robot;
        launcherMotor = new FtcDcMotor(instanceName + ".motor");
        launcherMotor.setMotorInverted(RobotParams.LAUNCHER_MOTOR_INVERTED);
        launcherMotor.setPositionSensorScaleAndOffset(RobotParams.LAUNCHER_REV_PER_COUNT, 0.0);
        launcherServo = new FtcServo(instanceName + ".servo");
        launcherServo.setInverted(RobotParams.LAUNCHER_SERVO_INVERTED);
        launchTaskObj = TrcTaskMgr.createTask(instanceName + ".task", this::launchTask);
        event = new TrcEvent(instanceName);
        sm = new TrcStateMachine<>(instanceName);
        launcherServo.setPosition(RobotParams.LAUNCHER_SERVO_MIN_POS);
        ServoLaunchedPos = false;
    }

    public boolean isServoLaunchedPos() {

        return ServoLaunchedPos;
    }

    private void finish(boolean canceled)
    {
        if (sm.isEnabled())
        {
            // Launch task is active, finish it.
            launcherMotor.stop();
            launcherServo.setPosition(RobotParams.LAUNCHER_SERVO_MIN_POS);
            ServoLaunchedPos = false;
            sm.stop();
            launchTaskObj.unregisterTask();
            if (completionEvent != null)
            {
                if (canceled)
                {
                    completionEvent.cancel();
                }
                else
                {
                    completionEvent.signal(); // what does signal mean
                }
                completionEvent = null;
            }
            tracer.traceInfo(instanceName, "Finish launch operation: cancel=" + canceled);
        }
    }   //finish

    public void cancel()
    {
        finish(true);
    }   //cancel

    public void launch(TrcEvent completionEvent)
    {
        if (!sm.isEnabled())
        {
            this.completionEvent = completionEvent;
            sm.start(State.START);
            launchTaskObj.registerTask(TrcTaskMgr.TaskType.POST_PERIODIC_TASK);
            tracer.traceInfo(instanceName, "Start launch operation.");
        }
    }   //launch

    private void launchTask(TrcTaskMgr.TaskType taskType, TrcRobot.RunMode runMode, boolean slowPeriodicLoop)
    {
        State state = sm.checkReadyAndGetState();

        if (state != null)
        {
            tracer.traceInfo(instanceName, "State=" + state);
            switch (state)
            {
                case START:
                    // Set launch motor velocity and wait for it to reach target speed.
                    launcherMotor.setVelocity(0.0, RobotParams.LAUNCH_VELOCITY, 0.0, event);
                    sm.waitForSingleEvent(event, State.LAUNCH);
                    break;

                case LAUNCH:
                    // Launch airplane.
                    launcherServo.setPosition(RobotParams.LAUNCHER_SERVO_MAX_POS, event, RobotParams.LAUCNHER_TRIGGER_TIME);
                    ServoLaunchedPos = true;
                    sm.waitForSingleEvent(event, State.DONE);
                    break;

                case DONE:
                default:
                    finish(false);
                    break;
            }
        }
    }   //launchTask
}