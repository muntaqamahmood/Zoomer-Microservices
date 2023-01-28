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

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"uid", "radius"};
        Class<?> fieldClasses[] = {String.class, Integer.class};
        String uid = null;
        Integer radius = null;
        if (validateFields(body, fields, fieldClasses)) {
            uid = body.getString("uid");
            radius = body.getInt("radius");
        }else{
            this.sendStatus(r, 400);
            return;
        }

        String nearbyURL = "http://locationmicroservice:8000/location/nearbyDriver/%s?radius=%d";
        nearbyURL = String.format(nearbyURL, uid, radius);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(nearbyURL))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try{
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(res.statusCode() != 200){
                this.sendStatus(r, res.statusCode());
                return;
            }
            JSONObject res2 = new JSONObject(res.body());
            JSONObject obj = new JSONObject();
            obj.put("data",  res2.getJSONObject("data").names());
            this.sendResponse(r, obj, 200);
            return;
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
