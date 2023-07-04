package CleaningRobot.RobotP2P.MechanicHandler;

import java.util.logging.Logger;

public class MechanicState {

    private static final Logger logger = Logger.getLogger(MechanicState.class.getSimpleName());
    private volatile boolean inMaintenance = false;
    private final Object lockInMaintenance = new Object();
    private volatile boolean needingMaintenance = false;
    private final Object lockNeedMaintenance = new Object();
    private volatile long requestTimestamp;
    private final Object lockTimestamp = new Object();

    private static MechanicState instance;
    public static MechanicState getInstance() {
        if (instance == null) {
            instance = new MechanicState();
        }
        return instance;
    }

    private MechanicState() {}

    public boolean isInMaintenance() {
        return inMaintenance;
    }

    public boolean isNeedingMaintenance() {
        return needingMaintenance;
    }

    public void needMaintenance() {
        synchronized (lockNeedMaintenance) {
            needingMaintenance = true;
            lockNeedMaintenance.notifyAll();
        }
        synchronized (lockTimestamp) {
            requestTimestamp = System.currentTimeMillis();
        }
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void waitForMaintenanceInterest() throws InterruptedException {
        synchronized (lockNeedMaintenance) {
            while (!needingMaintenance) {
                lockNeedMaintenance.wait();
            }
        }
    }

    public void waitForMaintenanceDisinterest() throws InterruptedException {
        synchronized (lockNeedMaintenance) {
            while (needingMaintenance) {
                lockNeedMaintenance.wait();
            }
        }
    }

    public void waitForMechanicExit() {
        synchronized (lockInMaintenance) {
            while (inMaintenance) {
                try {
                    lockInMaintenance.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace(); //TODO: handle interrupt
                }
            }
        }
    }

    public void enterMechanic() {
        synchronized (lockInMaintenance) {
            inMaintenance = true;
        }
    }

    public void exitMechanic() {
        synchronized (lockInMaintenance) {
            inMaintenance = false;
            lockInMaintenance.notifyAll();
        }
        synchronized (lockNeedMaintenance) {
            needingMaintenance = false;
            lockNeedMaintenance.notifyAll();
        }
    }

}
