package CleaningRobot;

import CleaningRobot.Mqtt.MqttPublisher;
import CleaningRobot.RestAPI.RestAPI;
import CleaningRobot.RobotP2P.BotNetServiceGrpc;
import CleaningRobot.RobotP2P.GrpcServices.BotNetServiceImpl;
import CleaningRobot.RobotP2P.RobotP2P;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import CleaningRobot.Sensor.SensorListener;
import CleaningRobot.Sensor.MBuffer;
import CleaningRobot.Sensor.Simulator.PM10Simulator;
import com.sun.jersey.api.client.ClientResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Logger;

import static Utils.Config.BROKER_ADDRESS;
import static Utils.Config.QOS;

public class CleaningRobot {
    RobotData robotData;
    RobotList robotList;
    RestAPI restAPI;
    PM10Simulator sensor;
    SensorListener sensorListener;
    MqttPublisher mqttPublisher;
    RobotP2P robotP2P;
    private static final Logger logger = Logger.getLogger(CleaningRobot.class.getSimpleName());
    static {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s : %5$s %n");
    }
    public static void main(String[] args) throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Insert robot ID: ");
        String robotID = inFromUser.readLine();

        System.out.println("Insert port for grpc: ");
        int ownGrpcPort = Integer.parseInt(inFromUser.readLine());
        //int ownGrpcPort = 1337;

        //System.out.println("Insert server address: ");
        //String serverAddress = inFromUser.readLine();
        String serverAddress = "http://localhost:1337";
        String ownIpAddress = "localhost";

        CleaningRobot cleaningRobot = new CleaningRobot(robotID, ownGrpcPort, ownIpAddress, serverAddress);

        // TODO: handle return boolean values
        if (!cleaningRobot.register())
            return;
        cleaningRobot.startSensor();
        cleaningRobot.startP2P();
        cleaningRobot.presentToOthers();
        //cleaningRobot.startPublishingData();

        System.out.println("\n*** Press enter to stop Robot ***\n");
        inFromUser.readLine();

        cleaningRobot.stop();
    }

    public CleaningRobot(String robotID, int ownGrpcPort, String ownIpAddress, String serverAddress) {
        robotData = new RobotData(robotID, ownGrpcPort, ownIpAddress);
        restAPI = new RestAPI(serverAddress);
        sensor = new PM10Simulator(MBuffer.getInstance());
        sensorListener = new SensorListener();
    }

    public void stop() {
        sensor.stopMeGently();
        sensorListener.stopMeGently();
        mqttPublisher.stopMeGently();
    }

    public void startSensor() {
        sensor.start();
        sensorListener.start();
        logger.info("Sensor started");
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
        robotList = RobotList.getInstance();
        robotList.setRobots(response.getEntity(RobotList.class).getList());
        robotData = robotList.getRobot(robotData);
        robotP2P = new RobotP2P(robotData);
        logger.info("Robot " + robotData.getRobotID() + " registered");
        return true;
    }

    public void startPublishingData() {
        mqttPublisher = new MqttPublisher(robotData.getDistrict(), robotData.getRobotID(), BROKER_ADDRESS, QOS);
        mqttPublisher.start();
        logger.info("Started publishing data");
    }

    public void startP2P() {
        robotP2P.start();
    }

    public void presentToOthers() {
        robotP2P.presentToOthers();
    }
}
