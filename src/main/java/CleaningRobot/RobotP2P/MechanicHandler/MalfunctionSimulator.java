package CleaningRobot.RobotP2P.MechanicHandler;

import java.util.Random;
import java.util.logging.Logger;

public class MalfunctionSimulator extends Thread {

    private final Random random = new Random();
    private boolean stopCondition = false;

    private static final Logger logger = Logger.getLogger(MalfunctionSimulator.class.getSimpleName());

    @Override
    public synchronized void start() {
        super.start();
        stopCondition = false;
    }

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                if (random.nextDouble() < 0.1 && !MechanicState.getInstance().isNeedingMaintenance()) {
                    logger.info("Malfunction! Needing maintenance");
                    MechanicState.getInstance().needMaintenance();
                    MechanicState.getInstance().waitForMaintenanceDisinterest();
                }
                Thread.sleep(10000);
            }
            catch (InterruptedException e) {
                stopCondition = true;
                break;
            }
        }
    }
}
