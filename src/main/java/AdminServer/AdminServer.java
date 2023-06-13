package AdminServer;

import Utils.Beans.PollutionMeasurements;
import AdminServer.PollutionManagement.MqttSubscriber;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Logger;

import static Utils.Config.BROKER_ADDRESS;
import static Utils.Config.QOS;

public class AdminServer {
    private static final Logger logger = Logger.getLogger(AdminServer.class.getSimpleName());
    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    static {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s : %5$s %n");
    }

    public static void main(String[] args) throws IOException {
        MqttSubscriber sub = new MqttSubscriber(BROKER_ADDRESS, QOS);
        sub.initialize();

        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
        server.start();

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n*** Press enter to stop AdminServer ***\n");
        inFromUser.readLine();


        logger.info("Shutting down AdminServer");
        server.stop(0);
        sub.disconnect();
        logger.info("AdminServer stopped");
    }
}
