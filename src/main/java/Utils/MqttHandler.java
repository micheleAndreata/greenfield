package Utils;

import org.eclipse.paho.client.mqttv3.*;

import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class MqttHandler {

    private static final Logger logger = Logger.getLogger(MqttHandler.class.getSimpleName());
    private static final int MAX_CONN_RETRIES = 10;

    final int[] connRetries = new int[1];

    MqttClient mqttClient;
    String brokerUrl;
    int qos;

    public MqttHandler(String brokerUrl, int qos) {
        try {
            this.mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId());
        } catch (MqttException e) {
            logger.severe("An error occurred while initializing mqttSubscriber");
            throw new RuntimeException(e);
        }
        ;
        this.brokerUrl = brokerUrl;
        this.qos = qos;
    }

    public void tryConnecting() {
        int retries;
        synchronized (connRetries) {
            retries = connRetries[0];
        }
        if(retries < MAX_CONN_RETRIES) {
            try {
                if (!mqttClient.isConnected())
                    connect();
            }
            catch (MqttException e) {
                logger.info("failed to connect to mqtt broker, trying to reconnect...");
                synchronized (connRetries) {
                    connRetries[0]++;
                }
                try {
                    sleep(1500);
                } catch (InterruptedException ex) {
                    logger.severe("interrupted while waiting for connection");
                    return;
                }
                tryConnecting();
            }
        }
        else {
            throw new RuntimeException("No response from mqtt broker. Shutting down");
        }
    }

    public void connect() throws MqttException {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        mqttClient.connect(connOpts);
        logger.info("Connected to broker " + brokerUrl);
        synchronized (connRetries) {
            connRetries[0] = 0;
        }
    }

    public void subscribe(String[] topics) {
        for (String topic : topics) {
            try {
                mqttClient.subscribe(topic, qos);
            } catch (MqttException e) {
                logger.severe("An error occurred while subscribing to topic " + topic);
                throw new RuntimeException(e);
            }
            logger.info("Subscribed to topic " + topic);
        }
    }

    public void disconnect() {
        try {
            if (mqttClient.isConnected())
                mqttClient.disconnect();
        }
        catch (MqttException me) {
            logger.severe("Exception while disconnecting " + me.getMessage());
            me.printStackTrace();
        }
    }

    public void initializeSubscriber(MqttCallback mqttCallback) {
        mqttClient.setCallback(mqttCallback);
    }

    public void publish(String topic, MqttMessage message) {
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            logger.severe("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
