package CleaningRobot;

import CleaningRobot.Mqtt.MqttPublisher;
import Utils.Beans.RobotData;
import Utils.Beans.RobotList;
import CleaningRobot.Mqtt.SensorListener;
import CleaningRobot.Sensor.MBuffer;
import CleaningRobot.Sensor.Simulator.PM10Simulator;
import com.sun.jersey.api.client.ClientResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

import static Utils.Config.BROKER_ADDRESS;
import static Utils.Config.QOS;

public class CleaningRobot {
    RobotData robotData;
    ArrayList<RobotData> robots;
    RestAPI restAPI;
    PM10Simulator sensor;
    SensorListener sensorListener;
    MqttPublisher mqttPublisher;
    private static final Logger logger = Logger.getLogger(CleaningRobot.class.getSimpleName());
    static {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s : %5$s %n");
    }
    public static void main(String[] args) throws IOException {
        CleaningRobot cleaningRobot = new CleaningRobot("RID2", 1337, "localhost");

        // TODO: handle return boolean values
        cleaningRobot.register();
        cleaningRobot.startSensor();
        cleaningRobot.presentToOthers();
        cleaningRobot.startPublishingData();

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n*** Press enter to stop Robot ***\n");
        inFromUser.readLine();

        cleaningRobot.stop();
    }

    public CleaningRobot(String robotID, int port, String ipAddress) {
        robotData = new RobotData(robotID, port, ipAddress);
        restAPI = new RestAPI(ipAddress);
        sensor = new PM10Simulator(MBuffer.getInstance());
        sensorListener = new SensorListener();
    }

    public void startSensor() {
        sensor.start();
        sensorListener.start();
    }

    public void stop() {
        sensor.stopMeGently();
        sensorListener.stopMeGently();
        mqttPublisher.stopMeGently();
    }

    public boolean register() {
        ClientResponse response = restAPI.addRobot(robotData);
        if (response == null) {
            logger.severe("Server not available");
            return false;
        }
        if (response.getStatus() != 200) {
            logger.severe("Server responded with: " + response.getStatus() + " " + response.getEntity(String.class));
            return false;
        }
        robots = response.getEntity(RobotList.class).getList();
        robotData = robots.get(robots.indexOf(robotData));
        return true;
    }

    public void startPublishingData() {
        mqttPublisher = new MqttPublisher(robotData.getDistrict(), robotData.getRobotID(), BROKER_ADDRESS, QOS);
        mqttPublisher.start();
    }

    public boolean presentToOthers() {
        return true;
    }
}
