package CleaningRobot.RobotP2P.MechanicHandler;

import CleaningRobot.RobotP2P.Communications.Communications;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;

import java.util.ArrayList;
import java.util.List;

public class RobotsAnswers {
    private final List<String> positiveAnswers;
    private final List<String> negativeAnswers;

    private static final RobotsAnswers instance = new RobotsAnswers();
    public static RobotsAnswers getInstance() {
        return instance;
    }

    private RobotsAnswers() {
        positiveAnswers = new ArrayList<>();
        negativeAnswers = new ArrayList<>();
    }

    public void addPositive(String robotID) {
        synchronized (positiveAnswers) {
            positiveAnswers.add(robotID);
            if(positiveAnswers.size() == RobotList.getInstance().size())
                positiveAnswers.notifyAll();
        }
    }

    public void addNegative(String robotID) {
        synchronized (negativeAnswers) {
            negativeAnswers.add(robotID);
        }
    }

    public void notifyChange() {
        synchronized (positiveAnswers) {
            positiveAnswers.notifyAll();
        }

    }

    public void waitForAllAnswers() throws InterruptedException {
        synchronized (positiveAnswers) {
            while(positiveAnswers.size() != RobotList.getInstance().size()) {
                positiveAnswers.wait(10000);
                if (positiveAnswers.size() != RobotList.getInstance().size())
                    areRobotsOK();
            }
            positiveAnswers.clear();
        }
        synchronized (negativeAnswers) {
            negativeAnswers.clear();
        }
    }

    public void areRobotsOK() {
        synchronized (negativeAnswers) {
            for (String robotID : negativeAnswers) {
                RobotData robot = RobotList.getInstance().getRobot(robotID);
                if (robot != null)
                    Communications.areYouOK(robot);
            }
        }
    }
}
