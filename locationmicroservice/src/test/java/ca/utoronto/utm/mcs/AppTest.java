package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class.
 */

public class AppTest {

    HttpClient globalClient = HttpClient.newHttpClient();
    final static String locationURL = "http://0.0.0.0:8000/location/";

    @Test
    public void getNearbyDriverPass() throws IOException, InterruptedException {
        String responseBody = "{\"uid\": \"1\", \"is_driver\": false}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("user")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(responseBody))
                .build();
        globalClient.send(request, HttpResponse.BodyHandlers.ofString());

        globalClient = HttpClient.newHttpClient();
        responseBody = "{\"longitude\": 57.17, \"latitude\": 61.89," +
                " \"street\": \"Passenger Road\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("passenger")))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(responseBody))
                .build();
        globalClient.send(request, HttpResponse.BodyHandlers.ofString());

        globalClient = HttpClient.newHttpClient();
        responseBody = "{\"uid\": \"2\", \"is_driver\": true}";
        request = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("user")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(responseBody))
                .build();
        globalClient.send(request, HttpResponse.BodyHandlers.ofString());

        globalClient = HttpClient.newHttpClient();
        responseBody = "{\"longitude\": 57.71, \"latitude\": 62.03," +
                " \"street\": \"Driver Avenue\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("driver")))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(responseBody))
                .build();
        globalClient.send(request, HttpResponse.BodyHandlers.ofString());

        String format = locationURL.concat("nearbyDriver/1?radius=1000");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(format))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
    }

    @Test
    public void getNearbyDriverFail() throws IOException, InterruptedException {
        String format = locationURL.concat("nearbyDriver/passenger");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(format))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getNavigationPass() throws JSONException, IOException, InterruptedException {
        //make driver
        JSONObject driver = new JSONObject();
        driver.put("uid", 1);
        driver.put("is_driver", true);
        globalClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("user")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(driver.toString()))
                .build();
        globalClient.send(request, HttpResponse.BodyHandlers.ofString());

        // driver's street
        JSONObject dStreet = new JSONObject();
        dStreet.put("latitude", 4.1118);
        dStreet.put("longitude", 4.1119);
        dStreet.put("street", "Driver's Road");

        String dURI = locationURL.concat("1");
        String dObj = "{\"street\":\"Driver's Road\",\"latitude\":4.1118,\"longitude\":4.1119}";
        globalClient = HttpClient.newHttpClient();
        HttpRequest patchReq = HttpRequest.newBuilder()
                .uri(URI.create(dURI))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(dObj))
                .build();
        globalClient.send(patchReq, HttpResponse.BodyHandlers.ofString());

        //make passenger
        JSONObject passenger = new JSONObject();
        passenger.put("uid", 2);
        passenger.put("is_driver", false);
        globalClient = HttpClient.newHttpClient();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("user")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(passenger.toString()))
                .build();
        globalClient.send(request2, HttpResponse.BodyHandlers.ofString());

        //passenger's Street
        JSONObject pStreet = new JSONObject();
        pStreet.put("latitude", 4.1117);
        pStreet.put("longitude", 4.1118);
        pStreet.put("street", "Passenger's Road");

        String passURI = locationURL.concat("2");
        String passObj = "{\"street\":\"Passenger's Road\",\"latitude\":4.1117,\"longitude\":4.1118}";
        globalClient = HttpClient.newHttpClient();
        HttpRequest patchReq2 = HttpRequest.newBuilder()
                .uri(URI.create(passURI))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(passObj))
                .build();
        HttpResponse<String> re = globalClient.send(patchReq2, HttpResponse.BodyHandlers.ofString());
        System.out.println(re.toString());

        // make roads
        JSONObject road1 = new JSONObject();
        road1.put("roadName", "Driver's Road");
        road1.put("hasTraffic", false);
        globalClient = HttpClient.newHttpClient();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("road")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(road1.toString()))
                .build();
        HttpResponse<String> r = globalClient.send(request3, HttpResponse.BodyHandlers.ofString());
        System.out.println(r.toString());

        JSONObject road2 = new JSONObject();
        road2.put("roadName", "Passenger's Road");
        road2.put("hasTraffic", true);
        globalClient = HttpClient.newHttpClient();
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("road")))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(road2.toString()))
                .build();
        HttpResponse<String> resp = globalClient.send(request4, HttpResponse.BodyHandlers.ofString());
        System.out.println(resp.toString());

        //connect 2 roads
        JSONObject connect = new JSONObject();
        connect.put("roadName1", "Driver's Road");
        connect.put("roadName2", "Passenger's Road");
        connect.put("hasTraffic", true);
        connect.put("time", 35);
        globalClient = HttpClient.newHttpClient();
        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("hasRoute")))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(connect.toString()))
                .build();
        HttpResponse<String> response = globalClient.send(postReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.toString());


        // final request
        globalClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(locationURL.concat("navigation/1?passengerUid=2")))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> httpResponse = globalClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.toString());
        assertEquals(200, httpResponse.statusCode());

    }

    @Test
    public void getNavigationFail404() throws IOException, InterruptedException {
        String format = locationURL.concat("navigation/driverDNE?passengerUid=DNE");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(format))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getNavigationFail400() throws IOException, InterruptedException {
        String format = locationURL.concat("navigation/?passengerUid=");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(format))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
