package teamcode;
import com.acmerobotics.dashboard.config.Config;
import TrcCommonLib.trclib.TrcPidController;

public class DashboardParams
{
    @Config
    public static class elevatorPID {
        public static double elevatorKp = 1.0;
        public static double elevatorKi = 0.0;
        public static double elevatorKd = 0.0;
        public static double elevatorTol = 0.1;
        public static double elevatorIZone = 0.0;
    }

    @Config
    public static class driveTunePID {

        public static double tuneDistance = 8;
        public static double tuneAngleDistance = 90;
        public static double powerLimit = 0.7;

        public static TrcPidController.PidCoefficients tunePidCoeff = new TrcPidController.PidCoefficients(1, 0.0, 0, 0);
    }

}

