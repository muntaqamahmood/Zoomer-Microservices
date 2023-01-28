package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import java.util.*;

import static ca.utoronto.utm.mcs.Utils.isNumeric;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] urlArr = r.getRequestURI().getPath().split("/");
        String query = r.getRequestURI().getQuery();
        if(query == null || query.length() == 0 || urlArr.length != 4 || query.contains("&")){
            this.sendStatus(r, 400);
            return;
        }

        String[] entry = query.split("=");
        String uid = urlArr[3];
        String radius = entry[1];

        if(uid == null || radius == null || uid.length() == 0 || radius.length() == 0 || !isNumeric(uid) || !isNumeric(radius)){
            this.sendStatus(r, 400);
            return;
        }
        Double userLong = null;
        Double userLat = null;
        try{
            Result res = this.dao.getUserLocationByUid(uid);
            if(!res.hasNext()){
                this.sendStatus(r, 404);
                return;
            }
            Record rec = res.next();
            userLong = rec.get("n.longitude").asDouble();
            userLat = rec.get("n.latitude").asDouble();
            if(userLong == null|| userLat == null){
                this.sendStatus(r, 404);
                return;
            }
            Result res2 = this.dao.getDriversInRadius(userLong, userLat, radius);
            JSONObject drivers = new JSONObject();
            boolean found = false;
            for (Record rec2 : res2.list()) {
                Double dLongitude = rec2.get("n.longitude").asDouble();
                Double dLatitude = rec2.get("n.latitude").asDouble();
                String dStreet = rec2.get("n.street").asString();
                String dId = rec2.get("n.uid").asString();
                if(dId.equals(uid)) continue;
                found = true;
                JSONObject driver = new JSONObject();
                driver.put("longitude", dLongitude);
                driver.put("latitude", dLatitude);
                driver.put("street", dStreet);
                drivers.put(dId, driver);
            }
            if(!found){
                this.sendStatus(r, 404);
                return;
            }
            JSONObject response = new JSONObject();
            response.put("data", drivers);
            this.sendResponse(r, response, 200);
        }catch(Exception e){
            System.out.println("Error with nearby server");
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
    }
}
