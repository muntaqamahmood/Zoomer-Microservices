package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        String[] token, urlParams;
        String driverUid, passengerUid, passenger_road = null, driver_road = null;

        if (r.getRequestURI().getQuery() == null) {
            this.sendStatus(r, 400);
            return;
        }

        Map<String, String> paramsMap = new HashMap<>();
        try {
            urlParams = r.getRequestURI().getQuery().split("&");
            for (String urlParam : urlParams) {
                String key = urlParam.split("=")[0];
                String value = urlParam.split("=")[1];
                paramsMap.put(key, value);
            }
        } catch (Exception e) {
            this.sendStatus(r, 400);
            return;
        }

        token = r.getRequestURI().getPath().split("/");
        if (paramsMap.containsKey("passengerUid")) {
            if (urlParams.length != 1 || token.length != 4) {
                this.sendStatus(r, 400); return;
            }
        }
        Result res_navRoute, res_pUid, res_dUid;
        try {
            driverUid = token[3];
            passengerUid = paramsMap.get("passengerUid");
            res_pUid = this.dao.getUserLocationByUid(passengerUid);
            for (Record record : res_pUid.list()) {
                passenger_road = record.get("n.street").toString();
            }
            if (passenger_road == null) { this.sendStatus(r, 404); return;}

            res_dUid = this.dao.getUserLocationByUid(driverUid);
            for (Record record : res_dUid.list()) {
                driver_road = record.get("n.street").toString();
            }
            if (driver_road == null) { this.sendStatus(r, 404); return; }
            res_navRoute = this.dao.getNavigationRoute(driver_road, passenger_road);
            if (res_navRoute.hasNext()) {
                JSONObject data = new JSONObject();
                for (Result it = res_navRoute; it.hasNext(); ) {
                    Record record = it.next();
                    Value totalTime = record.get("totalTime");
                    data.put("total_time",  totalTime);
                    Path path = record.get("p").asPath();
                    List<Integer> times = new ArrayList<>();
                    times.add(0);
                    for(Path.Segment road : path){
                        int time = road.relationship().get("travel_time").asInt();
                        times.add(time);
                    }
                    List<JSONObject> route = new ArrayList<>();
                    int count = 0;
                    for(Node n : path.nodes()){
                        JSONObject obj = new JSONObject();
                        obj.put("street", n.get("name").asString());
                        obj.put("has_traffic", n.get("has_traffic").asBoolean());
                        obj.put("time", times.get(count));
                        route.add(obj);
                        count++;
                    }
                    data.put("route", route);
                }
                JSONObject response = new JSONObject();
                response.put("data", data);
                this.sendResponse(r, response, 200);
            }
            else {
                this.sendStatus(r, 404); return;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
