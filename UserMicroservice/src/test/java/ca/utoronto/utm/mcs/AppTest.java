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

    @Test
    public void userLoginPass() throws JSONException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject obj = new JSONObject();
        obj.put("name", "sampleName");
        obj.put("email", "sampleEmail");
        obj.put("password", "samplePassword");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
        JSONObject obj2 = new JSONObject();
        obj2.put("email", "sampleEmail");
        obj2.put("password", "samplePassword");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/login"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj2.toString()))
                .build();
        HttpResponse<String> res = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void userLoginFail() throws JSONException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject obj = new JSONObject();
        obj.put("john cena test", "wack");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
        JSONObject obj2 = new JSONObject();
        obj2.put("email", "wrongEmail");
        obj2.put("weetart", "wrongPassword");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/login"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj2.toString()))
                .build();
        HttpResponse<String> res = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, res.statusCode());
    }

    // TAs please note this test can only pass once on a fresh DB
    // as a user cannot register with same credentials twice
    @Test
    public void userRegisterPass() throws IOException, InterruptedException, JSONException {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject obj = new JSONObject();
        obj.put("name", "nameTest");
        obj.put("email", "emailTest2");
        obj.put("password", "passwordTest");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void userRegisterFail() throws JSONException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject obj = new JSONObject();
        obj.put("email", 123);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
