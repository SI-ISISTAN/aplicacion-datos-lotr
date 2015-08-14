/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
//Imports de MongoDB
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.CommandFailureException;
import com.mongodb.Cursor;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import data.analyzer.ConnectionData;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.bson.Document;
import data.analyzer.UserSchema;
import data.analyzer.GameSchema;
import data.analyzer.DataInput;
import data.analyzer.InvalidInputException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class LotRDataInput extends DataInput{
    
    private DB db;
    
    public LotRDataInput(){

    };
    
    
    public void connectToSourceLocal (String databaseName) throws InvalidInputException{
        MongoClient mongoClient;
        try {
            mongoClient = new MongoClient( "localhost" );
            db = mongoClient.getDB( databaseName );
        } catch (UnknownHostException|IllegalArgumentException ex) {
            throw new InvalidInputException("Datos de conexión inválidos.");
        }
    }
    
    public void connectToSourceRemote (String databaseName, String URIString) throws InvalidInputException{
        MongoClient mongoClient;
        String textUri = URIString;
        
        try{
            MongoClientURI uri = new MongoClientURI(textUri);
            try {
                mongoClient = new MongoClient(uri);
                db = mongoClient.getDB( databaseName );
            } catch (UnknownHostException ex) {
                throw new InvalidInputException("Error de conexión.");
            }
        }
        catch (IllegalArgumentException ex1){
            throw new InvalidInputException("Error de conexión.");
        }
        
    }
    
    public ArrayList<UserSchema> getUsers(){
       DBCollection usersCollection = db.getCollection("users");
       DBCursor users = usersCollection.find();
       DBObject user;
       ArrayList<UserSchema> usersList = new ArrayList<UserSchema>();
       
       try {
            while(users.hasNext()) {
                usersList.add( new LotRUser(users.next()));
            }
        } finally {
            users.close();
        }
       return usersList;
    }
    
    public ArrayList<GameSchema> getGames(){
        DBCollection gamesCollection = db.getCollection("games");
        DBCursor games = gamesCollection.find();
        ArrayList<GameSchema> gamesList = new ArrayList<GameSchema>();
        try {
            while(games.hasNext()) {
                gamesList.add(new LotRGame(games.next()));
            }
        } finally {
            games.close();
        }
        return gamesList;
    }
    
    public void setAnalyzedGame(String gameID, String modelName){
        DBCollection gamesCollection = db.getCollection("games");
        
        BasicDBObject newDocument = new BasicDBObject();
        BasicDBObject newDocument2 = new BasicDBObject();
        newDocument2.append("analyzed", true);
        newDocument2.append("model", modelName);
	newDocument.append("$set", newDocument2);
			
	BasicDBObject searchQuery = new BasicDBObject().append("gameID", gameID);

	gamesCollection.update(searchQuery, newDocument);
        
    }
    
    public void resetAnalysis(){
        DBCollection gamesCollection = db.getCollection("games");
        
        BasicDBObject newDocument = new BasicDBObject();
        BasicDBObject newDocument2 = new BasicDBObject();
        newDocument2.append("analyzed", false);
        newDocument2.append("model", null);
	newDocument.append("$set", newDocument2);
        
        
	DBCursor games = gamesCollection.find();
        try {
            while(games.hasNext()) {
               gamesCollection.update(games.next(), newDocument);
            }
        } finally {
            games.close();
        }
                
	
    }
    
    public ArrayList<GameSchema> getGamesForUser(String userID){
        DBCollection gamesCollection = db.getCollection("games");
        BasicDBObject query = new BasicDBObject("players", new BasicDBObject("$elemMatch", new BasicDBObject("userID", userID)));
        DBCursor games = gamesCollection.find(query);
        ArrayList<GameSchema> gamesList = new ArrayList<GameSchema>();
        try {
            while(games.hasNext()) {
                gamesList.add(new LotRGame(games.next()));
            }
        } finally {
            games.close();
        }
        return gamesList;
    } 
}
