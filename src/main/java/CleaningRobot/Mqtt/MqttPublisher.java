package CleaningRobot.Mqtt;

import CleaningRobot.Sensor.Simulator.Measurement;
import Utils.MqttHandler;
import Utils.SharedBeans.RobotMeasure;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.List;
import java.util.logging.Logger;

public class MqttPublisher extends Thread{
    private final String topic;
    private final String robotID;
    private final String brokerUrl;
    private final int qos;
    private MqttHandler mqttHandler;
    private volatile boolean stopCondition = false;
    private static final Logger logger = Logger.getLogger(MqttPublisher.class.getSimpleName());

    public MqttPublisher(int district, String robotID, String brokerUrl, int qos) {
        this.topic = "greenfield/pollution/district" + district;
        this.robotID = robotID;
        this.brokerUrl = brokerUrl;
        this.qos = qos;
        initialize();
    }

    @Override
    public void run() {
        while (!stopCondition) {
            // TODO: maybe another option to busy waiting?
            try {
                sleep(15000); //use wait?
            } catch (InterruptedException e) {
                break;
            }
            sendData(); // TODO: need to handle waiting for reconnection before sending data
        }
        disconnect();
    }

    private void initialize() {
        mqttHandler = new MqttHandler(brokerUrl, qos);
        mqttHandler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                logger.warning("Connection lost to " + brokerUrl);
                mqttHandler.tryConnecting();
            }
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {}
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
        mqttHandler.tryConnecting();
    }

    private void sendData() {
        AveragesBuffer averagesBuffer = AveragesBuffer.getInstance();
        List<Measurement> averages = averagesBuffer.readAllAndClean();
        String payload = new Gson().toJson(wrapAverages(averages));
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);

        mqttHandler.publish(topic, message);
    }

    public void disconnect() {
        mqttHandler.disconnect();
    }

    public RobotMeasure[] wrapAverages(List<Measurement> averages) {
        RobotMeasure[] measures = new RobotMeasure[averages.size()];
        for (int i = 0; i < averages.size(); i++) {
            measures[i] = new RobotMeasure(robotID, averages.get(i).getValue(), averages.get(i).getTimestamp());
        }
        return measures;
    }
}
