package AdminServer;

import AdminServer.PollutionManagement.MqttSubscriber;

import java.util.Locale;
import java.util.Scanner;

public class AdminServer {
    static {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s : %5$s %n");
    }

    public static void main(String[] args) {
        MqttSubscriber sub = new MqttSubscriber("localhost", "1883", "pollution");
        sub.initialize();

        System.out.println("\n ***  Press a random key to exit *** \n");
        Scanner command = new Scanner(System.in);
        command.nextLine();

        sub.disconnect();
    }
}
