package AdminServer.PollutionManagement;

import Utils.Beans.PollutionMeasurements;
import Utils.Beans.RobotMeasure;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class MqttSubscriber {
    private static final Logger logger = Logger.getLogger(MqttSubscriber.class.getSimpleName());
    String brokerUrl;
    int qos;
    String[] topics;
    String clientId;
    PollutionMeasurements pollutionMeasurements;
    MqttClient mqttClient;

    public MqttSubscriber(String brokerUrl, int qos) {
        this.brokerUrl = brokerUrl;
        this.qos = qos;
        this.topics = new String[4];
        for(int i = 0; i < 4; i++) {
            this.topics[i] = "greenfield/pollution/district" + (i+1);
        }
        this.pollutionMeasurements = PollutionMeasurements.getInstance();
        clientId = MqttClient.generateClientId();
    }

    public void initialize() {
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
                    pollutionMeasurements.addMeasurements(decodeMessage(message));
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
            for (String topic : topics) {
                mqttClient.subscribe(topic, qos);
                logger.info("Connected to topic " + topic);
            }
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

    public static RobotMeasure[] decodeMessage(MqttMessage message) {
        return (new Gson()).fromJson(message.toString(), RobotMeasure[].class);
    }
}
