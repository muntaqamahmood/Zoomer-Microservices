package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {

        String params[] = r.getRequestURI().toString().split("/");

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = { "endTime", "timeElapsed", "discount", "totalCost", "driverPayout"};
        Class<?> fieldClasses[] = {Integer.class, Integer.class, Integer.class, Double.class, Double.class};
        double distance = 0;
        Integer endTime = null;
        Integer timeElapsed = null;
        Integer discount = null;
        Double totalCost = null;
        Double driverPayout = null;
        if (validateFields(body, fields, fieldClasses)) {
            endTime = body.getInt("endTime");
            timeElapsed = body.getInt("timeElapsed");
            discount = body.getInt("discount");
            totalCost = body.getDouble("totalCost");
            driverPayout = body.getDouble("driverPayout");
        }else{
            this.sendStatus(r, 400);
            return;
        }
        String fields1[] = {"distance"};
        Class<?> fieldClasses1[] = {Integer.class};
        String fields2[] = {"distance"};
        Class<?> fieldClasses2[] = {Double.class};
        if(validateFields(body, fields1, fieldClasses1)){
            distance = body.getInt("distance") + 0.0;
        }else if(validateFields(body, fields2, fieldClasses2)){
            distance = body.getDouble("distance");
        }else{
            this.sendStatus(r, 400);
            return;
        }
        String tripId = params[2];
        try{
            int status;
            if(this.dao.patchTrip(tripId, distance, endTime, timeElapsed, discount, totalCost, driverPayout)) status = 200;
            else status = 404;
            this.sendStatus(r, status);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
