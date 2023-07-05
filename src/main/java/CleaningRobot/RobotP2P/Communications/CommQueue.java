package CleaningRobot.RobotP2P.Communications;

import Utils.SharedBeans.RobotData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommQueue {
    private final int[] openChannels;
    private final Set<RobotData> failedRobots;

    private static final CommQueue instance = new CommQueue();;

    public static CommQueue getInstance() {
        return instance;
    }

    private CommQueue() {
        openChannels = new int[1];
        failedRobots = new HashSet<>();
    }

    public void addFailedRobot(RobotData robot) {
        synchronized (failedRobots) {
            failedRobots.add(robot);
        }
    }

    public List<RobotData> getFailedRobotsAndClean() {
        synchronized (failedRobots) {
            List<RobotData> removed = new ArrayList<>(failedRobots);
            failedRobots.clear();
            return removed;
        }
    }

    public void incrementOpenChannels() {
        synchronized (openChannels) {
            openChannels[0]++;
            if (openChannels[0] == 1)
                openChannels.notifyAll();
        }
    }

    public void decrementOpenChannels() {
        synchronized (openChannels) {
            openChannels[0]--;
            if (openChannels[0] == 0)
                openChannels.notifyAll();
        }
    }

    public void waitForCommEvent() throws InterruptedException {
        waitForCommEventStarted();
        waitForAllChannelsClosed();
    }

    public void waitForAllChannelsClosed() throws InterruptedException {
        synchronized (openChannels) {
            while (openChannels[0] != 0) {
                openChannels.wait();
            }
        }
    }

    public void waitForCommEventStarted() throws InterruptedException {
        synchronized (openChannels) {
            while (openChannels[0] == 0) {
                openChannels.wait();
            }
        }
    }
}
