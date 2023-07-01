package CleaningRobot.RobotP2P.MechanicHandler;

import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RequestsQueue {
    private static final Logger logger = Logger.getLogger(RequestsQueue.class.getSimpleName());
    private static RequestsQueue instance;

    private final List<MaintenanceRequest> queue;

    private RequestsQueue() {
        queue = new ArrayList<>();
    }

    public synchronized static RequestsQueue getInstance() {
        if (instance == null) {
            instance = new RequestsQueue();
        }
        return instance;
    }

    public synchronized void add(MaintenanceRequest request) {
        queue.add(request);
    }

    public synchronized List<MaintenanceRequest> readAllAndClean() {
        List<MaintenanceRequest> q = new ArrayList<>(queue);
        queue.clear();
        return q;
    }
}
