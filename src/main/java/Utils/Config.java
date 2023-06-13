package Utils;

public class Config {
    public static final int WINDOW_SIZE = 8;
    public static final double OVERLAP_FACTOR = 0.5;
    public static final int OVERLAP = (int) (WINDOW_SIZE * OVERLAP_FACTOR);
    public static final String BROKER_ADDRESS = "tcp://localhost:1883";
    public static final int QOS = 2;
}
