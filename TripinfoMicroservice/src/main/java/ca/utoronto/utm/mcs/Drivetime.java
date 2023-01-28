package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        String locationNavURL = "http://locationmicroservice:8000/location/navigation/:%s?passengerUid=%s";
        String[] token;
        String tripID;
        String driver, passenger;
        token = r.getRequestURI().getPath().split("/");
        if (token[0].contains("GET") && token.length == 4) {
            tripID = token[3];
            if (tripID.isEmpty() || !ObjectId.isValid(tripID)) {
                this.sendStatus(r, 400);
                return;
            }
        } else {
            this.sendStatus(r, 400);
            return;
        }

        try{
            if(this.dao.getTripById(tripID)==null) {
                this.sendStatus(r, 404); return;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.sendStatus(r, 500); return;
        }
        ArrayList<String> tripArray;
        try{
            ObjectId tripObj = new ObjectId(tripID);
            tripArray = this.dao.getTripDetails(tripObj);
            passenger = tripArray.get(0);
            driver = tripArray.get(1);
            locationNavURL = String.format(locationNavURL, driver, passenger);
            System.out.println(locationNavURL);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.sendStatus(r, 404); return;
        }
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(locationNavURL))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try{
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode()!= 200){
                this.sendStatus(r, httpResponse.statusCode()); return;
            }
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject response = new JSONObject(httpResponse.body());
            JSONObject data = response.getJSONObject("data");
            jsonObject.put("arrival_time", data.getInt("total_time"));
            jsonObject1.put("data", jsonObject);
            this.sendResponse(r, jsonObject1, 200);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
