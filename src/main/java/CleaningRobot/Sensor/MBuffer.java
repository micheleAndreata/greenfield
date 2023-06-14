package CleaningRobot.Sensor;

import CleaningRobot.CleaningRobot;
import CleaningRobot.Sensor.Simulator.Buffer;
import CleaningRobot.Sensor.Simulator.Measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static Utils.Config.OVERLAP;
import static Utils.Config.WINDOW_SIZE;

public class MBuffer implements Buffer {
    ArrayList<Measurement> measurements;
    volatile boolean hasEnoughData = false;
    private final int windowSize = WINDOW_SIZE;
    private final int overlap = OVERLAP;

    private static final Logger logger = Logger.getLogger(MBuffer.class.getSimpleName());
    public static MBuffer instance = new MBuffer();
    public static MBuffer getInstance() {
        return instance;
    }

    private MBuffer() {
        measurements = new ArrayList<>();
    }

    @Override
    public void addMeasurement(Measurement m) {
        synchronized (this) {
            measurements.add(m);
            hasEnoughData = measurements.size() >= windowSize;
            if (hasEnoughData)
                notify();
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        synchronized (this) {
            while (!hasEnoughData) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.severe("Interrupted while waiting for data");
                    e.printStackTrace();
                }
            }
            List<Measurement> window = new ArrayList<>(measurements.subList(0, windowSize));
            measurements.removeAll(measurements.subList(0, overlap));
            hasEnoughData = false;
            return window;
        }
    }
}
