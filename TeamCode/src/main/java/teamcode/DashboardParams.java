package teamcode;
import com.acmerobotics.dashboard.config.Config;

import TrcCommonLib.trclib.TrcPidController;

@Config
public class DashboardParams
{
    public static double elevatorKp = 1.0;
    public static double elevatorKi = 0.0;
    public static double elevatorKd = 0.0;
    public static double elevatorTol = 0.1;
    public static double elevatorIZone = 0.0;

    public static double tuneDistance = 0;
    public static double tuneTurnAngle = 0;

        public static class TunePidX {
            public static TrcPidController.PidCoefficients tunexPosPidCoeff = new TrcPidController.PidCoefficients(0.095, 0.0, 0.001, 0.0);
        }

    public static double xPosKp = 0;
    public static double xPosKi = 0;
    public static double xPosKd = 0;
    public static double xPosKf = 0;

    public static double yPosKp = 0;
    public static double yPosKi = 0;
    public static double yPosKd = 0;
    public static double yPosKf = 0;

    public static double turnPosKp = 0;
    public static double turnPosKi = 0;
    public static double turnPosKd = 0;
    public static double turnPosKf = 0;
}

