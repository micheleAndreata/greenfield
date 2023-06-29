package CleaningRobot.RobotP2P.Mechanic;

import Utils.SharedBeans.RobotList;

import java.util.ArrayList;
import java.util.List;

public class RobotsAnswers {
    private final List<String> answers;

    private static RobotsAnswers instance;
    public static RobotsAnswers getInstance() {
        if (instance == null) {
            instance = new RobotsAnswers();
        }
        return instance;
    }

    private RobotsAnswers() {
        answers = new ArrayList<>();
    }

    public synchronized void add(String robotID) {
        answers.add(robotID);
        if (answers.size() == RobotList.getInstance().size()) {
            notifyAll();
        }
    }

    public synchronized List<String> readAllAndClean() {
        List<String> ans = new ArrayList<>(answers);
        answers.clear();
        return ans;
    }

    public synchronized void waitForAllAnswers() throws InterruptedException {
        while(answers.size() != RobotList.getInstance().size()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                throw new InterruptedException();
            }
        }
    }
}
