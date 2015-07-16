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
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import data.analyzer.UserSchema;
import data.analyzer.GameSchema;
import data.analyzer.DataInput;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class LotRDataInput implements DataInput{
    
    private DB db;
    
    public LotRDataInput(){

    };
    
    public void connectToSource(){
        MongoClient mongoClient;
        try {
            mongoClient = new MongoClient( "localhost" );
            db = mongoClient.getDB( "test" );
        } catch (UnknownHostException ex) {
            Logger.getLogger(LotRDataInput.class.getName()).log(Level.SEVERE, null, ex);
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
