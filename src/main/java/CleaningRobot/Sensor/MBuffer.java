package CleaningRobot.Sensor;

import CleaningRobot.Sensor.Simulator.Buffer;
import CleaningRobot.Sensor.Simulator.Measurement;

import java.util.ArrayList;
import java.util.List;

import static Utils.Config.OVERLAP;
import static Utils.Config.WINDOW_SIZE;

public class MBuffer implements Buffer {
    ArrayList<Measurement> measurements;
    volatile boolean hasEnoughData = false;

    public static final MBuffer instance = new MBuffer();
    public static MBuffer getInstance() {
        return instance;
    }

    private MBuffer() {
        measurements = new ArrayList<>();
    }

    @Override
    public synchronized void addMeasurement(Measurement m) {
        measurements.add(m);
        hasEnoughData = measurements.size() >= WINDOW_SIZE;
        if (hasEnoughData)
            this.notifyAll();
    }

    @Override
    public synchronized List<Measurement> readAllAndClean() {
        while (!hasEnoughData) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                hasEnoughData = false;
                return null;
            }
        }
        List<Measurement> window = new ArrayList<>(measurements.subList(0, WINDOW_SIZE));
        measurements.removeAll(measurements.subList(0, OVERLAP));
        hasEnoughData = false;
        return window;
    }
}
