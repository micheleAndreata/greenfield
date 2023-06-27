package AdminServer.Mqtt;

import AdminServer.Beans.PollutionMeasurements;
import Utils.MqttHandler;
import Utils.SharedBeans.RobotMeasure;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Arrays;
import java.util.logging.Logger;

public class MqttSubscriber {
    private static final Logger logger = Logger.getLogger(MqttSubscriber.class.getSimpleName());
    String brokerUrl;
    int qos;
    String[] topics;
    PollutionMeasurements pollutionMeasurements;
    MqttHandler mqttHandler;

    public MqttSubscriber(String brokerUrl, int qos) {
        this.brokerUrl = brokerUrl;
        this.qos = qos;
        this.topics = new String[4];
        for(int i = 0; i < 4; i++) {
            this.topics[i] = "greenfield/pollution/district" + (i+1);
        }
        this.pollutionMeasurements = PollutionMeasurements.getInstance();
    }

    public void initialize() {
        mqttHandler = new MqttHandler(brokerUrl, qos);
        mqttHandler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.warning("Connection lost to " + brokerUrl);
                mqttHandler.tryConnecting();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                RobotMeasure[] robotMeasures = decodeMessage(message);
                logger.info(Arrays.toString(robotMeasures));
                pollutionMeasurements.addMeasurements(robotMeasures);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        mqttHandler.tryConnecting();
        mqttHandler.subscribe(topics);
    }

    public void disconnect() {
        mqttHandler.disconnect();
    }

    public static RobotMeasure[] decodeMessage(MqttMessage message) {
        return (new Gson()).fromJson(message.toString(), RobotMeasure[].class);
    }
}
