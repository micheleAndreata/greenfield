package CleaningRobot.RobotP2P.MechanicHandler;

import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;
import Utils.SharedBeans.RobotList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PendingRequests {
    private static final Logger logger = Logger.getLogger(PendingRequests.class.getSimpleName());
    private static PendingRequests instance;

    private final List<MaintenanceRequest> queue;

    private PendingRequests() {
        queue = new ArrayList<>();
    }

    public static PendingRequests getInstance() {
        if (instance == null) {
            instance = new PendingRequests();
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

    public static boolean isStillPending(MaintenanceRequest request) {
        return RobotList.getInstance().contains(request.getRobotID());
    }
}
