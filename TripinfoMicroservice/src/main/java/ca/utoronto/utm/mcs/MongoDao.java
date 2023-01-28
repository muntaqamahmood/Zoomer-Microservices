package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	public MongoDao() {
        // TODO:
        // Connect to the mongodb database and create the database and collection.
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String uri = "mongodb://%s:%s@" + addr + ":27017";
		String username = "root";
		String password = "123456";
		String dbName = "trip";
		uri = String.format(uri, username, password);
		try {
			MongoClient mongoClient = MongoClients.create(uri);
			MongoDatabase database = mongoClient.getDatabase(dbName);
			this.collection = database.getCollection(dbName);
			System.out.println("Mongodb set up");
			System.out.println(this.collection);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public String postTrip(String driver, String passenger, int startTime) throws JSONException {
		Document doc = new Document();
		doc.put("driver", driver);
		doc.put("passenger", passenger);
		doc.put("startTime", startTime);
		try{
			System.out.println(3);
			this.collection.insertOne(doc);
			System.out.println(4);
			String id = String.valueOf(doc.getObjectId("_id"));
			System.out.println(5);
			return id;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public boolean patchTrip(String id, double distance, int endTime, int timeElapsed, int discount, double totalCost, double driverPayout){
		Document doc = new Document();
		doc.put("distance", distance);
		doc.put("endTime", endTime);
		doc.put("timeElapsed", timeElapsed);
		doc.put("totalCost", totalCost);
		doc.put("discount", discount);
		doc.put("driverPayout", driverPayout);
		ObjectId objId = new ObjectId(id);
		try{
			FindIterable<Document> documents = this.getTripById(id);
			boolean b = false;
			for(Document d : documents){
				b = true;
			}
			if(!b) return false;
		}catch (Exception e) {
			throw e;
		}
		try {
			this.collection.updateOne(Filters.eq("_id", objId), new Document("$set", doc));
			return true;
		}catch (Exception e) {
			throw e;
		}
	}

	public FindIterable<Document> getTripById(String id) {
		FindIterable<Document> documents;
		ObjectId objId = new ObjectId(id);
		try{
			documents = this.collection.find(Filters.eq("_id",objId));
			return documents;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getTripDetails(ObjectId tripId) {
		FindIterable<Document> documents;
		ArrayList<String> tripArr = new ArrayList<>();
		try{
			documents = this.collection.find(Filters.eq("_id", tripId));
			while (documents.iterator().hasNext()){
				Document document = (Document) documents;
				tripArr.add(document.getString("passenger"));
				tripArr.add(document.getString("driver"));
			}
			return tripArr;
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	// *** implement database operations here *** //
	public FindIterable<Document> getPassengerTrip(String uid) {
		FindIterable<Document> documents;
		try{

			documents = this.collection.find(Filters.eq("passenger", uid));
			return documents;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
	public FindIterable<Document> getDriverTrip(String uid) {
		FindIterable<Document> documents;
		try{
			documents = this.collection.find(Filters.eq("driver", uid));
			return documents;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public FindIterable<Document> printAll() {
		FindIterable<Document> documents;
		System.out.println("---------------------------");
		System.out.println("PRINT ALL DOCUMENTS FROM MONGODB");
		try{
			documents = this.collection.find();
			for(Document d : documents){
				System.out.println(d.toString());
			}
			System.out.println("---------------------------");
			return documents;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

}
