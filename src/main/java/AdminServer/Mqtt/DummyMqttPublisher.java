package AdminServer.Mqtt;

import Utils.Beans.RobotMeasure;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class DummyMqttPublisher {
    static {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s : %5$s %n");
    }
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

            logger.info("Connected to " + brokerUrl);
            logger.info("Publishing to " + topic);
            for (int j=0; j<5; j++) {
                String robotID = "RID" + j;
                for(int i = 0; i < 30; i++) {
                    double value = Math.random() * 100;
                    long timestamp = new Date().getTime();
                    String payload = new Gson().toJson(new RobotMeasure(robotID, value, timestamp));
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    message.setQos(qos);
                    mqttClient.publish(topic, message);
                    //Thread.sleep(500);
                }
            }
            logger.info("Published to " + topic);

            if (mqttClient.isConnected())
                mqttClient.disconnect();
        }
        catch (MqttException e) {
            logger.severe("Exception: " + e.getMessage());
            e.printStackTrace();
            logger.info("");
        }
    }
}
