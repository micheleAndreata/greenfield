package CleaningRobot.RobotP2P.MechanicHandler;

import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;
import CleaningRobot.RobotP2P.Communications.Communications;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;

import java.util.List;
import java.util.logging.Logger;

public class MechanicHandler extends Thread{
    private final RobotData robotData;

    private boolean stopCondition = false;

    private static final Logger logger = Logger.getLogger(MechanicHandler.class.getSimpleName());

    public MechanicHandler(RobotData robotData){
        this.robotData = robotData;
    }

    @Override
    public void run(){
        while(!stopCondition && !Thread.currentThread().isInterrupted()) {
            try {
                logger.info("Waiting for maintenance interest...");
                MechanicState.getInstance().waitForMaintenanceInterest();

                logger.info("Requesting maintenance. RobotList size: " + RobotList.getInstance().size());
                Communications.broadcastRequestMaintenance(robotData);
                RobotsAnswers.getInstance().waitForAllAnswers();

                MechanicState.getInstance().enterMechanic();
                logger.info("Inside mechanic");
                Thread.sleep(10000);

                MechanicState.getInstance().exitMechanic();

                logger.info("Outside mechanic");
                freeMechanic();
            }
            catch (InterruptedException e){
                logger.warning("Stopping forcefully");
                stopCondition = true;
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void freeMechanic() {
        RobotList robotList = RobotList.getInstance();
        List<MaintenanceRequest> requests = PendingRequests.getInstance().readAllAndClean();
        for (MaintenanceRequest request : requests) {
            if (PendingRequests.isStillPending(request))
                Communications.freeMechanic(robotData, robotList.getRobot(request.getRobotID()));
        }
    }

    public void stopMeGently() {
        stopCondition = true;
        MechanicState.getInstance().needMaintenance();
    }
}
