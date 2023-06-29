package CleaningRobot.RobotP2P.Mechanic;

import CleaningRobot.CleaningRobot;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;
import CleaningRobot.RobotP2P.Communications.Communications;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;

import java.util.List;

public class MechanicHandler extends Thread{
    private final CleaningRobot robot;

    private boolean stopCondition = false;

    public MechanicHandler(CleaningRobot robot){
        this.robot = robot;
    }

    @Override
    public void run(){
        while(!stopCondition) {
            try {
                MechanicState.getInstance().waitForMaintenanceInterest();

                Communications.broadcastRequestMaintenance(robot.getRobotData());
                RobotsAnswers.getInstance().waitForAllAnswers();

                MechanicState.getInstance().enterMechanic();
                robot.sleep();
                MechanicState.getInstance().exitMechanic();

                RobotList robotList = RobotList.getInstance();
                List<MaintenanceRequest> requests = RequestsQueue.getInstance().readAllAndClean();
                for (MaintenanceRequest request : requests) {
                    Communications.freeMechanic(robot.getRobotData(), robotList.getRobot(request.getRobotID()));
                }
            }
            catch (InterruptedException e) {
                stopCondition = true;
                break;
            }
        }
    }
}
