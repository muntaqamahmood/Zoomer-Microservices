package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        // TODO
        String[] tokenURL = r.getRequestURI().getPath().split("/");
        if (tokenURL.length != 4) { this.sendStatus(r, 400); return;}
        String passengerUID;
        passengerUID= tokenURL[3];
        if (passengerUID.isEmpty()){
             this.sendStatus(r, 400); return;
        }
        FindIterable<Document> documents;
        try{
            documents = this.dao.getPassengerTrip(passengerUID);
            boolean found = false;
            for(Document d : documents){
                found = true;
            }
            if(!found){
                this.sendStatus(r, 404);
                return;
            }
        }
        catch (Exception exception) {
           this.sendStatus(r, 500); return;
        }

        JSONArray jsonArrayTrips = new JSONArray();
        try{
            JSONObject jsonObject;
            String format;
            for (Document doc : documents) {
                format = doc.toJson();
                jsonObject= new JSONObject(format);
                jsonObject.put("_id", jsonObject.getJSONObject("_id").get("$oid"));
                jsonObject.remove("passenger");
                jsonArrayTrips.put(jsonObject);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        JSONObject response = new JSONObject();
        try{
            response.put("data", jsonArrayTrips);
            this.sendResponse(r, response, 200);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
