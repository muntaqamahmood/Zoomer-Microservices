package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;
import java.sql.ResultSet;

import org.json.JSONObject;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 409, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        String[] fields = {"name", "email", "password"};
        int uid;
        ResultSet resultSet;
        Class<?>[] fieldClasses = {String.class, String.class, String.class};
        String email = null;
        String name = null;
        String password = null;
        try {
            if(!validateFields(deserialized, fields, fieldClasses) ) {
                this.sendStatus(r, 400);
                return;
            }
            else {
                name = deserialized.getString("name");
                email = deserialized.getString("email");
                password = deserialized.getString("password");
            }
            resultSet = this.dao.getUserInfo(email);
            // if user next exists
            if(resultSet.next()){
                // user exists, so statusCode is 403
                this.sendStatus(r, 403); return;
            }
            else {
                try{
                    this.dao.insertUser(name, email, password);
                    this.sendStatus(r, 200); return;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    this.sendStatus(r, 500); return;
                }
            }

        } catch (Exception exception) {
            this.sendStatus(r, 500);
            exception.printStackTrace();
        }

    }
}
