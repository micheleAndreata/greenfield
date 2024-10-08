package CleaningRobot.RobotP2P.Communications;

import CleaningRobot.RestAPI.RestAPI;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import com.sun.jersey.api.client.ClientResponse;

import java.util.List;
import java.util.logging.Logger;

public class ErrorHandler extends Thread {
    private volatile boolean stopCondition = false;

    private final RestAPI restAPI;

    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getSimpleName());

    public ErrorHandler(String serverAddress) {
        restAPI = new RestAPI(serverAddress);
    }

    @Override
    public void run() {
        while(!stopCondition && !Thread.currentThread().isInterrupted()) {
            try {
                CommQueue.getInstance().waitForCommEvent();
            } catch (InterruptedException e) {
                stopCondition = true;
                Thread.currentThread().interrupt();
                return;
            }
            handleErrors();
        }
    }

    private void handleErrors() {
        List<RobotData> failedRobots = CommQueue.getInstance().getFailedRobotsAndClean();
        if (failedRobots.isEmpty()) {
            return;
        }
        List<RobotData> remainingRobots = RobotList.getInstance().getList();
        remainingRobots.removeAll(failedRobots);
        for(RobotData failedRobot : failedRobots) {
            Communications.broadcastGoodbye(failedRobot, remainingRobots);

            ClientResponse response = restAPI.removeRobot(failedRobot);
            if (response == null) {
                logger.severe("Server not available");
                throw new RuntimeException("Server is not available");
            }
            if (response.getStatus() != 200) {
                logger.severe("Server responded with: " + response.getStatus() + " " + response.getEntity(String.class));
            }
        }
    }
}
