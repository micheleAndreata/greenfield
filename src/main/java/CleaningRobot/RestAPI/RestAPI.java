package CleaningRobot.RestAPI;

import Utils.SharedBeans.RobotData;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestAPI {
    private final String serverAddress;
    private final Client client;

    public RestAPI(String serverAddress) {
        this.serverAddress = serverAddress;
        this.client = new Client();
    }

    public ClientResponse addRobot(RobotData robotData) {
        String path = "/robots/add";
        String requestUrl = serverAddress + path;
        String jsonData = new Gson().toJson(robotData);
        return postRequest(client, requestUrl, jsonData);
    }

    public static ClientResponse postRequest(Client client, String url, String data) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").post(ClientResponse.class, data);
        } catch (ClientHandlerException e) {
            return null;
        }
    }
    public static ClientResponse getRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            return null;
        }
    }
    public static ClientResponse deleteRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e) {
            return null;
        }
    }
}
