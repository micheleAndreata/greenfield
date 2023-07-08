package Utils;

import org.eclipse.paho.client.mqttv3.*;

import java.util.logging.Logger;


public class MqttHandler {

    private static final Logger logger = Logger.getLogger(MqttHandler.class.getSimpleName());

    MqttClient mqttClient;
    String brokerUrl;
    int qos;

    public MqttHandler(String brokerUrl, int qos) {
        try {
            this.mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId(), null);
        } catch (MqttException e) {
            logger.severe("An error occurred while initializing mqttSubscriber");
            throw new RuntimeException(e);
        }

        this.brokerUrl = brokerUrl;
        this.qos = qos;
    }

    public void connect() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setMaxReconnectDelay(3000);
        try {
            mqttClient.connect(connOpts);
        } catch (MqttException e) {
            logger.severe("An error occurred while connecting to broker " + brokerUrl);
            e.printStackTrace();
        }
        logger.info("Connected to broker " + brokerUrl);
    }

    public void subscribe(String[] topics) {
        for (String topic : topics) {
            try {
                mqttClient.subscribe(topic, qos);
            } catch (MqttException e) {
                logger.severe("An error occurred while subscribing to topic " + topic);
                e.printStackTrace();
            }
            logger.info("Subscribed to topic " + topic);
        }
    }

    public void disconnect() {
        try {
            if (mqttClient.isConnected())
                mqttClient.disconnect();
            mqttClient.close();
        }
        catch (MqttException me) {
            logger.severe("Exception while disconnecting " + me.getMessage());
            me.printStackTrace();
        }
    }

    public void disconnectForcibly() {
        try {
            mqttClient.disconnectForcibly();
            mqttClient.close(true);
        } catch (MqttException me) {
            logger.severe("Exception while disconnecting " + me.getMessage());
            me.printStackTrace();
        }
    }

    public void setCallback(MqttCallback mqttCallback) {
        mqttClient.setCallback(mqttCallback);
    }

    public void publish(String topic, MqttMessage message) throws MqttException {
        mqttClient.publish(topic, message);
    }
}
