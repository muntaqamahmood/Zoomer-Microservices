package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    HttpClient client = HttpClient.newHttpClient();
    @Test
    public void exampleTest() {
        assertTrue(true);
    }

    public void populate() throws JSONException, IOException, InterruptedException {
        JSONObject driver1 = new JSONObject();
        driver1.put("uid", "100");
        driver1.put("is_driver", true);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8000/location/user"))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(driver1.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
        JSONObject driver2 = new JSONObject();
        driver2.put("uid", "101");
        driver2.put("is_driver", true);
        HttpRequest req2 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8000/location/user"))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(driver2.toString()))
                .build();
        client.send(req2, HttpResponse.BodyHandlers.ofString());
    }
    @Test
    public void tripRequestPass() throws JSONException, IOException, InterruptedException {
        populate();
        JSONObject obj = new JSONObject();
        obj.put("uid", "100");
        obj.put("radius", 20);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/request"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void tripRequestFail() throws JSONException, IOException, InterruptedException {
        JSONObject obj = new JSONObject();
        obj.put("wack", 123);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/request"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, res.statusCode());
    }

    @Test
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException {
        populate();
        JSONObject obj = new JSONObject();
        obj.put("driver", "100");
        obj.put("passenger", "101");
        obj.put("startTime", 1669349669);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
        populate();
        JSONObject obj = new JSONObject();
        obj.put("weetardo", "wacky");
        obj.put("startTime", "yeet");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, res.statusCode());
    }

    @Test
    public void patchTripPass() throws JSONException, IOException, InterruptedException{
        populate();
        JSONObject obj = new JSONObject();
        obj.put("driver", "100");
        obj.put("passenger", "101");
        obj.put("startTime", 1669349669);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        JSONObject resObj = new JSONObject(res.body());
        JSONObject resObj2 = resObj.getJSONObject("data");
        String tripId = resObj2.getString("_id");

        String data = "{\n" +
                "    \"distance\" : 87,\n" +
                "    \"endTime\" : 1669349672,\n" +
                "    \"timeElapsed\": 3,\n" +
                "    \"discount\": 5,\n" +
                "    \"totalCost\": 55.9,\n" +
                "    \"driverPayout\": 47.5\n" +
                "}";
        JSONObject obj3 = new JSONObject(data);
        HttpRequest req3 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/" + tripId))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(obj3.toString()))
                .build();
        HttpResponse<String> res2 = client.send(req3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res2.statusCode());
    }

    @Test
    public void patchTripFail() throws JSONException, IOException, InterruptedException{
        JSONObject obj3 = new JSONObject();
        obj3.put("wacky doodle", 123);
        HttpRequest req3 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/1234ideclareathumbwar"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(obj3.toString()))
                .build();
        HttpResponse<String> res2 = client.send(req3, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, res2.statusCode());
    }

    @Test
    public void tripsForPassengerPass() throws IOException, InterruptedException, JSONException {
//        populate();
        JSONObject obj = new JSONObject();
        obj.put("driver", "100");
        obj.put("passenger", "101");
        obj.put("startTime", 1669349669);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/passenger/101"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void tripsForPassengerFail400() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/passenger/"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void tripsForPassengerFail404() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/passenger/DNE"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void tripsForDriverPass() throws JSONException, IOException, InterruptedException {
        JSONObject obj = new JSONObject();
        obj.put("driver", "100");
        obj.put("passenger", "101");
        obj.put("startTime", 1669349669);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/driver/100"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void tripsForDriverFail400() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/driver/"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void tripsForDriverFail404() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/driver/DNE"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void driverTimePass() throws JSONException, IOException, InterruptedException {
        JSONObject obj = new JSONObject();
        obj.put("driver", "100");
        obj.put("passenger", "101");
        obj.put("startTime", 1669349669);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/confirm"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        
        
        String tripID = new JSONObject(httpResponse.body()).getJSONObject("data").getString("_id");
        System.out.println(tripID);
        String formatURI = String.format("http://0.0.0.0:8002/trip/driverTime/" + tripID);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(formatURI))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> httpResponse1 = httpClient.send(httpRequest1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200 , httpResponse1.statusCode());

    }

    @Test
    public void driverTimeFail400() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/driverTime/"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void driverTimeFail404() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8002/trip/driverTime/DNE"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

}
