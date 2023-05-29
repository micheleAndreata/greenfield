package AdminServer.PollutionManagement;

import AdminServer.Beans.PollutionMeasurements;
import AdminServer.Beans.RobotMeasure;
import org.eclipse.paho.client.mqttv3.*;

import java.util.StringTokenizer;
import java.util.logging.Logger;

public class MqttSubscriber {
    private static final Logger logger = Logger.getLogger(MqttSubscriber.class.getSimpleName());
    String brokerAddress;
    String brokerPort;
    String topic;
    String clientId;
    PollutionMeasurements pollutionMeasurements;
    MqttClient mqttClient;

    public MqttSubscriber(String brokerAddress, String brokerPort, String topic,
                          PollutionMeasurements pollutionMeasurements) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.topic = topic;
        this.pollutionMeasurements = pollutionMeasurements;
        clientId = MqttClient.generateClientId();
    }

    public void initialize() {
        String brokerUrl = "tcp://" + brokerAddress + ":" + brokerPort;
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.warning("Connection lost to " + brokerUrl);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    logger.info( "Message received from "+ topic +", message: " + message.toString());
                    pollutionMeasurements.addMeasurement(decodeMessage(message));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.info("Connecting to " + brokerUrl);
            mqttClient.connect(connOpts);
            logger.info("Connected to " + brokerUrl);
            mqttClient.subscribe(topic, 2);
            logger.info("Connected to topic " + topic);
        }
        catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
        }
        catch (MqttException me) {
            logger.severe("Exception while disconnecting " + me.getMessage());
            me.printStackTrace();
            logger.info("");
        }
    }

    public static RobotMeasure decodeMessage(MqttMessage message) {
        StringTokenizer tokenizer = new StringTokenizer(message.toString(), ":");
        return new RobotMeasure(tokenizer.nextToken(),
                Double.parseDouble(tokenizer.nextToken()),
                Long.parseLong(tokenizer.nextToken()));
    }
}
