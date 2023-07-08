package AdminClient;

import Utils.SharedBeans.RobotData;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminClient {
    private final String serverAddress;
    private final Client client;
    private final BufferedReader inFromUser;

    public static void main(String[] args) throws IOException {
        AdminClient adminClient = new AdminClient("http://localhost:1337");
        adminClient.start();
    }

    public AdminClient(String serverAddress) {
        this.serverAddress = serverAddress;
        this.client = new Client();
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() throws IOException {
        System.out.println("write exit to stop adminClient.");
        System.out.println("enter 1 to get robot list. enter 2 to get robot average. enter 3 to get robots average");
        String service;

        while(true) {
            System.out.print("Select service: ");
            service = inFromUser.readLine();

            switch (service) {
                case "1":
                    handleGetRobots();
                    break;
                case "2":
                    handleGetRobotAverage();
                    break;
                case "3":
                    handleGetRobotsAverage();
                    break;
                case "exit":
                    break;
                default:
                    System.out.println("invalid input.");
            }
        }
    }

    private void handleGetRobotsAverage() throws IOException {
        System.out.println("*** Get Robots Average Service ***");
        boolean invalidInput;
        long t1 = 0;
        long t2 = Long.MAX_VALUE;

        do {
            invalidInput = false;
            System.out.print("--- enter first timestamp: ");
            try {
                t1 = Long.parseLong(inFromUser.readLine());
            } catch (NumberFormatException e) {
                System.out.println("invalid input");
                invalidInput = true;
            }
        } while (invalidInput);

        do {
            invalidInput = false;
            System.out.print("--- enter second timestamp: ");
            try {
                t2 = Long.parseLong(inFromUser.readLine());
            } catch (NumberFormatException e) {
                System.out.println("--- * Invalid input *");
                invalidInput = true;
            }
        } while (invalidInput);

        ClientResponse response = getRobotsAverage(t1, t2);
        if (response == null) {
            System.out.println("--- * Server unavailable *");
            return;
        }
        if (response.getStatus() != 200) {
            System.out.println("--- Error: " + response.getStatus() + " " + response.getEntity(String.class));
            return;
        }
        System.out.println("--- Response: " + response.getEntity(String.class));
    }

    private void handleGetRobotAverage() throws IOException {
        System.out.println("*** Get Robot Average Service ***");
        boolean invalidInput;
        String robotID;
        int n = 0;

        System.out.print("--- enter robot id: ");
        robotID = inFromUser.readLine();

        do {
            invalidInput = false;
            System.out.print("--- enter how many measurements: ");
            try {
                n = Integer.parseInt(inFromUser.readLine());
            } catch (NumberFormatException e) {
                System.out.println("--- * invalid input *");
                invalidInput = true;
            }
        } while (invalidInput);

        ClientResponse response = getRobotAverage(robotID, n);
        if (response == null) {
            System.out.println("--- * Server unavailable *");
            return;
        }
        if (response.getStatus() != 200) {
            System.out.println("--- Error: " + response.getStatus() + " " + response.getEntity(String.class));
            return;
        }
        System.out.println("--- Response: " + response.getEntity(String.class));
    }

    public void handleGetRobots() {
        System.out.println("*** Get robots Service ***");
        ClientResponse response = getRobots();
        if (response == null) {
            System.out.println("--- * Server unavailable *");
            return;
        }
        if (response.getStatus() != 200) {
            System.out.println("--- Error: " + response.getStatus() + " " + response.getEntity(String.class));
            return;
        }
        RobotData[] robotList = (new Gson()).fromJson(response.getEntity(String.class), RobotData[].class);
        for (RobotData robot : robotList) {
            System.out.println(robot.toString());
        }
    }

    public ClientResponse getRobots() {
        String url = serverAddress + "/robots";
        return getRequest(client, url);
    }
    public ClientResponse getRobotAverage(String robotID, int n) {
        String url = serverAddress + "/stats/robot-average/" + robotID + ":" + n;
        return getRequest(client, url);
    }
    public ClientResponse getRobotsAverage(long t1, long t2) {
        String url = serverAddress + "/stats/robots-average/" + t1 + ":" + t2;
        return getRequest(client, url);
    }

    public static ClientResponse getRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            return null;
        }
    }
}
