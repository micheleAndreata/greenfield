package AdminServer.PollutionManagement;

import org.eclipse.paho.client.mqttv3.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MqttSubscriber {
    private static final Logger logger = Logger.getLogger(MqttSubscriber.class.getSimpleName());
    String brokerAddress;
    String brokerPort;
    String topic;
    String clientId;
    MqttClient mqttClient;

    public MqttSubscriber(String brokerAddress, String brokerPort, String topic) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.topic = topic;
        clientId = MqttClient.generateClientId();
    }

    public void initialize() {
        String brokerUrl = "tcp://" + brokerAddress + ":" + brokerPort;
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.log(Level.WARNING, "Connection lost to {0}", brokerUrl);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    logger.log(Level.INFO, "Message received from {0}", topic);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.log(Level.INFO, "Connecting to {0}", brokerUrl);
            mqttClient.connect(connOpts);
            logger.log(Level.INFO, "Connected to {0}", brokerUrl);
            mqttClient.subscribe(topic, 2);
            logger.log(Level.INFO, "Connected to topic {0}", topic);
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
            me.printStackTrace();
        }
    }
}
