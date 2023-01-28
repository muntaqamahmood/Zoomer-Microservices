package ca.utoronto.utm.mcs;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgresDAO {
	
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //

    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
    }

    public int generateUID() throws SQLException {
        int uid = 0;
        try {
            String query;
            ResultSet resultSet;
            query = "SELECT count(*) FROM Users";
            resultSet = this.st.executeQuery(query);
            if (resultSet.next()) {
                uid = resultSet.getInt("count");
                return uid;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return uid;
    }

    public ResultSet getUserInfo(String email) throws SQLException {
        try{
            String query;
            query = "SELECT uid, email, prefer_name, password, rides, isdriver " +
                    "FROM Users " +
                    "WHERE email = '%s'";
            query = String.format(query, email);
            return this.st.executeQuery(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }

    }

    public int insertUser(String prefer_name, String email, String password) throws SQLException {
        int uid;
        String query;
        uid = generateUID();
        // default values for attr: rides=0 and isdriver=false
        query = "INSERT INTO Users(uid, email, prefer_name, password, rides, isdriver) " +
                "VALUES('%s', '%s', '%s', '%s', 0, false)";
        query = String.format(query, uid, email, prefer_name, password);
        this.st.execute(query);
        ResultSet resultSet = getUserInfo(email);
        if(resultSet.next()) {
            uid = resultSet.getInt("uid");

        }
        return uid;
    }
}
