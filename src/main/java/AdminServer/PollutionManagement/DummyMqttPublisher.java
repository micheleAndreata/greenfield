package AdminServer.PollutionManagement;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DummyMqttPublisher {
    private static final Logger logger = Logger.getLogger(DummyMqttPublisher.class.getSimpleName());

    public static void main(String[] args) {
        String brokerAddress = "localhost";
        String brokerPort = "1883";
        String topic = "pollution";
        String clientId = MqttClient.generateClientId();
        int qos = 2;

        String brokerUrl = "tcp://" + brokerAddress + ":" + brokerPort;
        try(MqttClient mqttClient = new MqttClient(brokerUrl, clientId)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);

            logger.log(Level.INFO, "Connected to {0}", brokerUrl);
            for(int i = 0; i < 10; i++) {
                String payload = String.valueOf(0 + (Math.random() * 10));
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(qos);
                mqttClient.publish(topic, message);
            }
            logger.log(Level.INFO, "Published to {0}", topic);

            if (mqttClient.isConnected())
                mqttClient.disconnect();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
