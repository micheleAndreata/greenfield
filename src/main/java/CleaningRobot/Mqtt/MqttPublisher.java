package CleaningRobot.Mqtt;

import CleaningRobot.Sensor.Simulator.Measurement;
import Utils.SharedBeans.RobotMeasure;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.logging.Logger;

public class MqttPublisher extends Thread{
    private final String topic;
    private final String robotID;
    private final String brokerUrl;
    private final int qos;
    private MqttClient mqttClient;
    private volatile boolean stopCondition = false;
    private static final Logger logger = Logger.getLogger(MqttPublisher.class.getSimpleName());

    public MqttPublisher(int district, String robotID, String brokerUrl, int qos) {
        this.topic = "greenfield/pollution/district" + district;
        this.robotID = robotID;
        this.brokerUrl = brokerUrl;
        this.qos = qos;
        initializeMqttClient();
    }

    @Override
    public void run() {
        while (!stopCondition) {
            // TODO: maybe another option to busy waiting?
            try {
                sleep(15000); //use wait?
            } catch (InterruptedException e) {
                logger.severe("Exception: " + e.getMessage());
                e.printStackTrace();
            }
            sendData();
        }
        try {
            if (mqttClient.isConnected())
                mqttClient.disconnect();
        } catch (MqttException e) {
            // TODO: handle this exception
            logger.severe("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopMeGently() {
        stopCondition = true;
    }

    private void initializeMqttClient() {
        try {
            mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);
            logger.info("Connected to " + brokerUrl);
            logger.info("Publishing to " + topic);
        } catch (MqttException e) {
            // TODO: handle this exception
            logger.severe("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendData() {
        AveragesBuffer averagesBuffer = AveragesBuffer.getInstance();
        List<Measurement> averages = averagesBuffer.readAllAndClean();
        String payload = new Gson().toJson(wrapAverages(averages));
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            // TODO: handle this exception
            logger.severe("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public RobotMeasure[] wrapAverages(List<Measurement> averages) {
        RobotMeasure[] measures = new RobotMeasure[averages.size()];
        for (int i = 0; i < averages.size(); i++) {
            measures[i] = new RobotMeasure(robotID, averages.get(i).getValue(), averages.get(i).getTimestamp());
        }
        return measures;
    }
}
