package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }
        JSONObject json = new JSONObject(Utils.convert(r.getRequestBody()));
        String[] field = {"email", "password"};
        Class<?>[] bodyTypes = {String.class, String.class};
        String email = "";
        String password = "";
        if (validateFields(json, field, bodyTypes)) {
            email = json.getString("email");
            password = json.getString("password");
        }  else {
            this.sendStatus(r, 400);
            return;
        }
        try{
            ResultSet res = dao.getUserInfo(email);
            if(res.next()){
                String password2 = res.getString(4);
                int status = password.equals(password2) ? 200 : 403;
                this.sendStatus(r, status);
                return;
            }else{
                this.sendStatus(r, 404);
            }
        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
    }
}
