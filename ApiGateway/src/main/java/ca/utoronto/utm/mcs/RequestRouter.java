package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;

import static java.time.temporal.ChronoUnit.SECONDS;

public class RequestRouter implements HttpHandler {
	
    /**
     * You may add and/or initialize attributes here if you 
     * need.
     */
	public RequestRouter() {
	}

	@Override
	public void handle(HttpExchange r) throws IOException {
		String[] uriArray = r.getRequestURI().getPath().split("/");
		String url = "";
		switch(uriArray[1]) {
			case "location":
				url = "http://locationmicroservice:8000"+ r.getRequestURI();
				break;
			case "user":
				url = "http://usermicroservice:8000"+ r.getRequestURI();
				break;
			case "trip":
				url = "http://tripinfomicroservice:8000"+ r.getRequestURI();
			default:
		}
		try {
			HttpClient client = HttpClient.newHttpClient();
			InputStream iStream = r.getRequestBody();
			String body = new String(iStream.readAllBytes(), StandardCharsets.UTF_8);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.timeout(Duration.of(10, SECONDS))
					.header("Content-Type", "application/json")
					.method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(body))
					.build();
			HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
			r.sendResponseHeaders(res.statusCode(), res.body().length());
			this.writeOutputStream(r, res.body());
		}
		catch (Exception e){
			JSONObject json = new JSONObject();
			try {
				json.put("status", "INTERNAL SERVER ERROR");
			} catch (JSONException ex) {
				throw new RuntimeException(ex);
			}
			String res = json.toString();
			r.sendResponseHeaders(500, res.length());
			this.writeOutputStream(r, res);
		}
	}

	public void writeOutputStream(HttpExchange r, String response) throws IOException {
		OutputStream os = r.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
