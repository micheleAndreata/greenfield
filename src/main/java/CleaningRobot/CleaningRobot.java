package CleaningRobot;

import CleaningRobot.Mqtt.MqttPublisher;
import CleaningRobot.RestAPI.RestAPI;
import CleaningRobot.RobotP2P.RobotP2P;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import CleaningRobot.Sensor.SensorListener;
import CleaningRobot.Sensor.MBuffer;
import CleaningRobot.Sensor.Simulator.PM10Simulator;
import com.sun.jersey.api.client.ClientResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.logging.Logger;

import static Utils.Config.BROKER_ADDRESS;
import static Utils.Config.QOS;

public class CleaningRobot {
    RobotData robotData;
    RobotList robotList;
    String serverAddress;
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

        String serverAddress = "http://localhost:1337";

        CleaningRobot cleaningRobot = new CleaningRobot(serverAddress);

        cleaningRobot.startSensor();
        cleaningRobot.startP2P();
        cleaningRobot.presentToOthers();
        cleaningRobot.startPublishingData();

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n*** Press enter to stop Robot ***\n");
        inFromUser.readLine();

        logger.info("Stopping robot");
        cleaningRobot.stop();
        logger.info("Robot stopped");
    }

    public CleaningRobot(String serverAddress) throws IOException {
        this.serverAddress = serverAddress;
        this.restAPI = new RestAPI(serverAddress);
        this.sensor = new PM10Simulator(MBuffer.getInstance());
        this.sensorListener = new SensorListener();

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Insert robot ID: ");
        String robotID = inFromUser.readLine();

        System.out.print("Insert port for grpc: ");
        int ownGrpcPort = Integer.parseInt(inFromUser.readLine());
        while(!isTcpPortAvailable(ownGrpcPort)) {
            System.out.print("Port not available. Insert another port for grpc: ");
            ownGrpcPort = Integer.parseInt(inFromUser.readLine());
        }

        String ownIpAddress = "localhost";

        this.robotData = new RobotData(robotID, ownGrpcPort, ownIpAddress);
        int status = register();
        while (status != 0) {
            if (status == -1)
                throw new RuntimeException("A problem occurred while registering");
            System.out.print("ID already taken. Insert another robot ID: ");
            this.robotData.setRobotID(inFromUser.readLine());
            status = register();
        }
    }

    private int register() {
        ClientResponse response = restAPI.addRobot(robotData);
        if (response == null) {
            logger.severe("Server not available");
            return -1;
        }
        if (response.getStatus() == 400) {
            return -2;
        }
        if (response.getStatus() != 200) {
            logger.severe("Server responded with: " + response.getStatus() + " " + response.getEntity(String.class));
            return -1;
        }
        robotList = RobotList.getInstance();
        robotList.setRobots(response.getEntity(RobotList.class).getList());
        robotData = robotList.getRobot(robotData);
        logger.info("Robot " + robotData.getRobotID() + " registered");
        return 0;
    }

    public void stop() {
        sensor.stopMeGently();
        sensorListener.interrupt();
        mqttPublisher.interrupt();
        robotP2P.stop();
    }

    private void startSensor() {
        sensor.start();
        sensorListener.start();
        logger.info("Sensor started");
    }

    private void startPublishingData() {
        mqttPublisher = new MqttPublisher(robotData.getDistrict(), robotData.getRobotID(), BROKER_ADDRESS, QOS);
        mqttPublisher.start();
        logger.info("Started publishing data to mqtt broker");
    }

    private void startP2P() {
        robotP2P = new RobotP2P(robotData, serverAddress);
        robotP2P.start();
    }

    private void presentToOthers() {
        robotP2P.presentToOthers();
    }

    public RobotData getRobotData() {
        return robotData;
    }

    public void sleep() throws InterruptedException {
        Thread.sleep(10000);
    }

    public static boolean isTcpPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
