package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String bodyStr = Utils.convert(r.getRequestBody());
        try {
            JSONObject body = new JSONObject(bodyStr);
            String fields[] = {"driver", "passenger", "startTime"};
            Class<?> fieldClasses[] = {String.class, String.class, Integer.class};
            String driver = null;
            String passenger = null;
            Integer startTime = null;
            if (validateFields(body, fields, fieldClasses)) {
                driver = body.getString("driver");
                passenger = body.getString("passenger");
                startTime = body.getInt("startTime");
            }else{
                this.sendStatus(r, 400);
                return;
            }
            String id = this.dao.postTrip(driver, passenger, startTime);
            JSONObject obj = new JSONObject();
            JSONObject obj2 = new JSONObject();
            obj2.put("_id", id);
            obj.put("data", obj2);
            this.sendResponse(r, obj, 200);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
