/*
 * Copyright (c) 2022 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import TrcCommonLib.trclib.TrcDbgTrace;
import TrcCommonLib.trclib.TrcDriveBase;
import TrcCommonLib.trclib.TrcGameController;
import TrcCommonLib.trclib.TrcPose2D;
import TrcCommonLib.trclib.TrcRobot;
import TrcCommonLib.trclib.TrcTimer;
import TrcFtcLib.ftclib.FtcGamepad;
import TrcFtcLib.ftclib.FtcOpMode;
import teamcode.drivebases.SwerveDrive;

/**
 * This class contains the TeleOp Mode program.
 */

@TeleOp(name="FtcTeleOp", group="Ftcxxxx")
public class FtcTeleOp extends FtcOpMode
{
    private static final String moduleName = FtcTeleOp.class.getSimpleName();

    protected Robot robot;
    protected FtcGamepad driverGamepad;
    protected FtcGamepad operatorGamepad;
    private double drivePowerScale = RobotParams.DRIVE_POWER_SCALE_NORMAL;
    private double turnPowerScale = RobotParams.TURN_POWER_SCALE_NORMAL;
    private double hangPrevPower = 0.0;
    private double elevatorPrevPower = 0.0;
    private boolean slowDrive = false;
    private boolean wristPositionInverted = false;
    private double hangPos = RobotParams.HANG_MIN_POS;
    private boolean manualOverride = false;
    private boolean relocalizing = false;
    private TrcPose2D robotFieldPose = null;

    //
    // Implements FtcOpMode abstract method.
    //

    /**
     * This method is called to initialize the robot. In FTC, this is called when the "Init" button on the Driver
     * Station is pressed.
     */
    @Override
    public void robotInit()
    {
        //
        // Create and initialize robot object.
        //
        robot = new Robot(TrcRobot.getRunMode());
        //
        // Open trace log.
        //
        if (RobotParams.Preferences.useTraceLog)
        {
            String filePrefix = Robot.matchInfo != null?
                String.format(Locale.US, "%s%02d_TeleOp", Robot.matchInfo.matchType, Robot.matchInfo.matchNumber):
                "Unknown_TeleOp";
            TrcDbgTrace.openTraceLog(RobotParams.LOG_FOLDER_PATH, filePrefix);
        }
        //
        // Create and initialize Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this::driverButtonEvent);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this::operatorButtonEvent);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
        setDriveOrientation(TrcDriveBase.DriveOrientation.ROBOT);
    }   //robotInit

    //
    // Overrides TrcRobot.RobotMode methods.
    //

    /**
     * This method is called when the competition mode is about to start. In FTC, this is called when the "Play"
     * button on the Driver Station is pressed. Typically, you put code that will prepare the robot for start of
     * competition here such as resetting the encoders/sensors and enabling some sensors to start sampling.
     *
     * @param prevMode specifies the previous RunMode it is coming from (always null for FTC).
     * @param nextMode specifies the next RunMode it is going into.
     */
    @Override
    public void startMode(TrcRobot.RunMode prevMode, TrcRobot.RunMode nextMode)
    {
        if (TrcDbgTrace.isTraceLogOpened())
        {
            TrcDbgTrace.setTraceLogEnabled(true);
        }
        robot.globalTracer.traceInfo(
            moduleName, "***** Starting TeleOp: " + TrcTimer.getCurrentTimeString() + " *****");
        robot.dashboard.clearDisplay();
        //
        // Tell robot object opmode is about to start so it can do the necessary start initialization for the mode.
        //
        robot.startMode(nextMode);
        //
        // Enable AprilTag vision for re-localization.
        //
        if (robot.vision != null && robot.vision.aprilTagVision != null)
        {
            robot.globalTracer.traceInfo(moduleName, "Enabling AprilTagVision.");
            robot.vision.setAprilTagVisionEnabled(true);
        }
    }   //startMode

    /**
     * This method is called when competition mode is about to end. Typically, you put code that will do clean
     * up here such as disabling the sampling of some sensors.
     *
     * @param prevMode specifies the previous RunMode it is coming from.
     * @param nextMode specifies the next RunMode it is going into (always null for FTC).
     */
    @Override
    public void stopMode(TrcRobot.RunMode prevMode, TrcRobot.RunMode nextMode)
    {
        //
        // Tell robot object opmode is about to stop so it can do the necessary cleanup for the mode.
        //
        robot.stopMode(prevMode);
        printPerformanceMetrics();
        robot.globalTracer.traceInfo(
            moduleName, "***** Stopping TeleOp: " + TrcTimer.getCurrentTimeString() + " *****");

        if (TrcDbgTrace.isTraceLogOpened())
        {
            TrcDbgTrace.closeTraceLog();
        }
    }   //stopMode

    /**
     * This method is called periodically on the main robot thread. Typically, you put TeleOp control code here that
     * doesn't require frequent update For example, TeleOp joystick code or status display code can be put here since
     * human responses are considered slow.
     *
     * @param elapsedTime specifies the elapsed time since the mode started.
     * @param slowPeriodicLoop specifies true if it is running the slow periodic loop on the main robot thread,
     *        false otherwise.
     */

    @Override
    public void periodic(double elapsedTime, boolean slowPeriodicLoop)
    {
        if (slowPeriodicLoop)
        {
            //
            // DriveBase subsystem.
            //
            if (robot.robotDrive != null)
            {
                // We are trying to re-localize the robot and vision hasn't seen AprilTag yet.
                if (relocalizing)
                {
                    if (robotFieldPose == null)
                    {
                        robotFieldPose = robot.vision.getRobotFieldPose();
                    }
                }
                else
                {
                    double[] inputs = driverGamepad.getDriveInputs(
                        RobotParams.ROBOT_DRIVE_MODE, true, drivePowerScale, turnPowerScale);

                    if (robot.robotDrive.driveBase.supportsHolonomicDrive())
                    {
                        robot.robotDrive.driveBase.holonomicDrive(
                            null, inputs[0], inputs[1], inputs[2], robot.robotDrive.driveBase.getDriveGyroAngle());
                    }
                    else
                    {
                        robot.robotDrive.driveBase.arcadeDrive(inputs[1], inputs[2]);
                    }
                    robot.dashboard.displayPrintf(
                        1, "RobotDrive: Power=(%.2f,y=%.2f,rot=%.2f),Mode:%s",
                        inputs[0], inputs[1], inputs[2], robot.robotDrive.driveBase.getDriveOrientation());
                }
            }
            //
            // Other subsystems.
            //
            if (RobotParams.Preferences.useSubsystems)
            {
                //hang subsystem
                if(robot.hang != null)
                {
                    double hangPower = operatorGamepad.getRightStickY(true) * RobotParams.HANG_POWER_LIMIT;
                    if(hangPower != hangPrevPower)
                    {
                        if (manualOverride)
                        {
                            robot.hang.setPower(hangPower);
                        }
                        else
                        {
                            robot.hang.setPidPower(null, hangPower, RobotParams.HANG_MIN_POS, RobotParams.HANG_MAX_POS, false);
                        }
                        hangPrevPower = hangPower;
                    }
                }
                //elevator subsystem
                if(robot.elevator != null)
                {
                    double elevatorPower = operatorGamepad.getLeftStickY(true) * RobotParams.ELEVATOR_POWER_LIMIT;
                    if(elevatorPower != elevatorPrevPower)
                    {
                        if (manualOverride)
                        {
                            robot.elevator.setPower(elevatorPower);
                        }
                        else
                        {
                            robot.elevator.setPidPower(null, elevatorPower, RobotParams.ELEVATOR_MIN,RobotParams.ELEVATOR_MAX, true);
                        }
                        elevatorPrevPower = elevatorPower;
                    }
                }
                if(robot.wrist != null && robot.elevator != null)
                {
                    if (robot.elevator.getPosition() >= RobotParams.WRIST_EDIT_ELEVATOR_HEIGHT)
                    {
                        if (wristPositionInverted)
                        {
                            robot.wrist.wristUpInverted(null);
                        } else
                        {
                            robot.wrist.wristUpSquare(null);
                        }
                    }
                    else
                    {
                        robot.wrist.wristGround(null);
                    }
                }

                boolean slowDriveTriggered = driverGamepad.getLeftTrigger() >= .3;
                // Press and hold for slow drive.
                if (!slowDrive && slowDriveTriggered)
                {
                        robot.globalTracer.traceInfo(moduleName, ">>>>> DrivePower slow.");
                        drivePowerScale = RobotParams.DRIVE_POWER_SCALE_SLOW;
                        turnPowerScale = RobotParams.TURN_POWER_SCALE_SLOW;
                }
                else if(slowDrive && !slowDriveTriggered)
                {
                        robot.globalTracer.traceInfo(moduleName, ">>>>> DrivePower normal.");
                        drivePowerScale = RobotParams.DRIVE_POWER_SCALE_NORMAL;
                        turnPowerScale = RobotParams.TURN_POWER_SCALE_NORMAL;
                }
                slowDrive = slowDriveTriggered;

            }
            // Display subsystem status.
            if (RobotParams.Preferences.doStatusUpdate)
            {
                robot.updateStatus();
            }
        }
    }   //periodic

    /**
     * This method sets the drive orientation mode and updates the LED to indicate so.
     *
     * @param orientation specifies the drive orientation (FIELD, ROBOT, INVERTED).
     */
    public void setDriveOrientation(TrcDriveBase.DriveOrientation orientation)
    {
        if (robot.robotDrive != null)
        {
            robot.globalTracer.traceInfo(moduleName, "driveOrientation=" + orientation);
            robot.robotDrive.driveBase.setDriveOrientation(
                orientation, orientation == TrcDriveBase.DriveOrientation.FIELD);
            if (robot.blinkin != null)
            {
                robot.blinkin.setDriveOrientation(orientation);
            }
        }
    }   //setDriveOrientation

    //
    // Implements TrcGameController.ButtonHandler interface.
    //

    /**
     * This method is called when driver gamepad button event is detected.
     *
     * @param gamepad specifies the game controller object that generated the event.
     * @param button specifies the button ID that generates the event.
     * @param pressed specifies true if the button is pressed, false otherwise.
     */
    public void driverButtonEvent(TrcGameController gamepad, int button, boolean pressed)
    {
        robot.dashboard.displayPrintf(8, "%s: %04x->%s", gamepad, button, pressed? "Pressed": "Released");

        switch (button)
        {
            case FtcGamepad.GAMEPAD_A:
                break;

            case FtcGamepad.GAMEPAD_B:
                break;

            case FtcGamepad.GAMEPAD_X:
                break;

            case FtcGamepad.GAMEPAD_Y:
                if (pressed && robot.launcher != null && TrcTimer.getModeElapsedTime() >= RobotParams.END_GAME_TIME) {
                    robot.launcher.startLaunch(null);
                }
                break;

            case FtcGamepad.GAMEPAD_LBUMPER:
                if(robot.intake != null)
                {
                    if(pressed)
                    {
                        robot.intake.openLeft(null);
                    }
                    else
                    {
                        robot.intake.closeLeft(null);
                    }
                }
                break;

            case FtcGamepad.GAMEPAD_RBUMPER:
                if(robot.intake != null)
                {
                    if(pressed)
                    {
                        robot.intake.openRight(null);
                    }
                    else
                    {
                        robot.intake.closeRight(null);
                    }
                }
                break;

            case FtcGamepad.GAMEPAD_DPAD_UP:
                // Toggle between field or robot oriented driving, only applicable for holonomic drive base.
                if (pressed && robot.robotDrive != null && robot.robotDrive.driveBase.supportsHolonomicDrive())
                {
                    if (robot.robotDrive.driveBase.getDriveOrientation() != TrcDriveBase.DriveOrientation.FIELD)
                    {
                        robot.globalTracer.traceInfo(moduleName, ">>>>> Enabling FIELD mode.");
                        setDriveOrientation(TrcDriveBase.DriveOrientation.FIELD);
                    }
                    else
                    {
                        robot.globalTracer.traceInfo(moduleName, ">>>>> Enabling ROBOT mode.");
                        setDriveOrientation(TrcDriveBase.DriveOrientation.ROBOT);
                    }
                }
                break;

            case FtcGamepad.GAMEPAD_DPAD_DOWN:
                if (pressed)
                {
                    robot.globalTracer.traceInfo(moduleName, ">>>>> CancelAll is pressed.");
                    if (robot.robotDrive != null)
                    {
                        // Cancel all auto-assist driving.
                        robot.robotDrive.cancel();
                    }
                    if (robot.hang != null)
                    {
                        robot.hang.cancel();
                    }
                }
                break;

            case FtcGamepad.GAMEPAD_DPAD_LEFT:
                break;

            case FtcGamepad.GAMEPAD_DPAD_RIGHT:
                break;

            case FtcGamepad.GAMEPAD_START:
                if (robot.vision != null && robot.vision.aprilTagVision != null && robot.robotDrive != null)
                {
                    // On press of the button, we will start looking for AprilTag for re-localization.
                    // On release of the button, we will set the robot's field location if we found the AprilTag.
                    relocalizing = pressed;
                    if (!pressed)
                    {
                        if (robotFieldPose != null)
                        {
                            // Vision found an AprilTag, set the new robot field location.
                            robot.globalTracer.traceInfo(
                                moduleName, ">>>>> Finish re-localizing: pose=" + robotFieldPose);
                            robot.robotDrive.driveBase.setFieldPosition(robotFieldPose, false);
                            robotFieldPose = null;
                        }
                    }
                    else
                    {
                        robot.globalTracer.traceInfo(moduleName, ">>>>> Start re-localizing ...");
                    }
                }
                break;

            case FtcGamepad.GAMEPAD_BACK:
                break;
        }
    }   //driverButtonEvent

    /**
     * This method is called when operator gamepad button event is detected.
     *
     * @param gamepad specifies the game controller object that generated the event.
     * @param button specifies the button ID that generates the event.
     * @param pressed specifies true if the button is pressed, false otherwise.
     */
    public void operatorButtonEvent(TrcGameController gamepad, int button, boolean pressed)
    {
        robot.dashboard.displayPrintf(8, "%s: %04x->%s", gamepad, button, pressed? "Pressed": "Released");

        switch (button)
        {
            case FtcGamepad.GAMEPAD_A:
                break;

            case FtcGamepad.GAMEPAD_B:
                robot.globalTracer.traceInfo(moduleName, ">>>>> ManulOverride=" + pressed);
                manualOverride = pressed;
                break;

            case FtcGamepad.GAMEPAD_X:
                if (pressed && robot.hang != null && TrcTimer.getModeElapsedTime() >= RobotParams.END_GAME_TIME) {
                    if (hangPos == RobotParams.HANG_MIN_POS)
                    {
                        hangPos = RobotParams.HANG_SETUP_POS;
                    }
                    else if (hangPos == RobotParams.HANG_SETUP_POS)
                    {
                        hangPos = RobotParams.HANG_HANGING_POS;
                    }
                    else
                    {
                        hangPos = RobotParams.HANG_MIN_POS;
                    }
                    robot.hang.setPosition(moduleName, 0.0, hangPos, false, RobotParams.HANG_POWER_LIMIT, null, 0.0);
                }
                break;

            case FtcGamepad.GAMEPAD_Y:
                break;

            case FtcGamepad.GAMEPAD_LBUMPER:
                if (pressed && robot.wrist != null)
                {
                    wristPositionInverted = !wristPositionInverted;
                }

            case FtcGamepad.GAMEPAD_RBUMPER:
                if(pressed && robot.elevator != null)
                {
                    robot.elevator.setPosition(RobotParams.ELEVATOR_MIN,false,RobotParams.ELEVATOR_POWER_LIMIT);
                }
                break;

            case FtcGamepad.GAMEPAD_DPAD_UP:
                break;

            case FtcGamepad.GAMEPAD_DPAD_DOWN:
                break;

            case FtcGamepad.GAMEPAD_DPAD_LEFT:
                break;

            case FtcGamepad.GAMEPAD_DPAD_RIGHT:
                break;

            case FtcGamepad.GAMEPAD_BACK:
                if (pressed)
                {
                    // Zero calibrate all subsystems (arm, elevator and turret).
                    robot.globalTracer.traceInfo(moduleName, ">>>>> ZeroCalibrate pressed.");
                    robot.zeroCalibrate(moduleName);
                }
                break;
        }
    }   //operatorButtonEvent

}   //class FtcTeleOp
