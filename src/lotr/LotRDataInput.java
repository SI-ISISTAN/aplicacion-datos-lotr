/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr;
//Imports de MongoDB
import com.mongodb.BasicDBList;
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
    
     public ArrayList<GameSchema> getUnanalizedGames(){
       ArrayList<GameSchema> allGames = this.getGames();
       ArrayList<GameSchema> unanalyzed = new ArrayList();
       for (GameSchema game: allGames){
           //cast json boolean to string for comparison
          if (!game.isAnalyzed()){
              unanalyzed.add(game);
          }
       }
       return unanalyzed;
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
    
    public void resetAnalysis(String modelName){
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
        
        //Borro los analisis que se hayan llevado a cabo con este modelo
        DBCollection usersCollection = db.getCollection("users");
        DBCursor users = usersCollection.find();
        try {
            while(users.hasNext()) {
               DBObject user = users.next();
               BasicDBObject symlog = (BasicDBObject) user.get("symlog");
               String symlogModel=null;
               if (symlog!=null){
                   symlogModel= (String)symlog.get("model");
               }
               if (symlogModel!=null){
                   if (modelName.equals(symlogModel)){
                       String userID = (String)((BasicDBObject)user.get("local")).get("userID");
                        BasicDBObject match = new BasicDBObject("local", new BasicDBObject("userID", userID));
                        BasicDBObject update2 = new BasicDBObject("symlog", new BasicDBObject());
                        usersCollection.update(match, new BasicDBObject("$unset", update2));
                   }
               }
            }
        } finally {
            users.close();
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
    
    public DBObject getUser(String userID){
        DBCollection usersCollection = db.getCollection("users");
        BasicDBObject subQuery = new BasicDBObject("userID",userID);
        BasicDBObject query = new BasicDBObject("local",subQuery);
        DBObject user = usersCollection.findOne(query);
        return user;
    }
    
    public void updateSymlog(String userID, BasicDBObject newProfile){
        DBCollection usersCollection = db.getCollection("users");
        BasicDBObject subQuery = new BasicDBObject("userID",userID);
        BasicDBObject query = new BasicDBObject("local",subQuery);
        DBObject user = usersCollection.findOne(query);
        usersCollection.update(user, new BasicDBObject("$set" , new BasicDBObject("symlog",newProfile) ));
    }
    
    public DBObject getChatForGame(String gameID){
        DBCollection chatsCollection = db.getCollection("chats");
        BasicDBObject query = new BasicDBObject("gameID", gameID);
        DBObject chat = chatsCollection.findOne(query);
        return chat;
    }
    
    public void updateStats(String userID, BasicDBObject newProfile){
        DBCollection usersCollection = db.getCollection("users");
        BasicDBObject subQuery = new BasicDBObject("userID",userID);
        BasicDBObject query = new BasicDBObject("local",subQuery);
        DBObject user = usersCollection.findOne(query);
        if (user!=null){
            usersCollection.update(user, new BasicDBObject("$set" , new BasicDBObject("stats",newProfile) ));
        }
    }
    
    public ArrayList<String> getChats(){
        DBCollection chatsCollection = db.getCollection("chats");
        DBCursor chats = chatsCollection.find();
        ArrayList<String> chatsList = new ArrayList<String>();
        try {
            while(chats.hasNext()) {
                DBObject c = chats.next();
                BasicDBList msgs = (BasicDBList)c.get("chats");
                if (msgs.size()>0){
                    chatsList.add((String)(c.get("gameID")));
                }
            }
        } finally {
            chats.close();
        }
        return chatsList;
    }
    
    //devuelvo solo los que tienen mas de 1 mensaje
    public ArrayList<BasicDBObject> getChatMessages(String chatID){
        DBCollection chatsCollection = db.getCollection("chats");
        BasicDBObject query = new BasicDBObject("gameID",chatID);
        DBObject chat = chatsCollection.findOne(query);
        ArrayList<BasicDBObject> messageList = new ArrayList<BasicDBObject>();
        BasicDBList messages = (BasicDBList)chat.get("chats");
        for (Object o : messages){
            BasicDBObject ms = (BasicDBObject)o;
            messageList.add(ms);
        }
        return messageList;
    }
    
    public DBObject getConfig(String configName){
        DBCollection configsCollection = db.getCollection("configs");
        DBObject config = configsCollection.findOne();
        BasicDBList configs = (BasicDBList)config.get("configs");
        int i=0;
        while (i<configs.size()){
            if (configs.get(i) != null && configName.equals( ((DBObject)configs.get(i)).get("configName") )){
                return (DBObject)configs.get(i);
            }
            i++;
        }
        return null;
    }
    
    //eeee massomeno. falta setear todo en 0 cuando se termina
    public void saveChatAnalysis (String chatID, ArrayList<BasicDBObject> msgs){
        DBCollection chatsCollection = db.getCollection("chats");
        BasicDBObject query = new BasicDBObject("gameID",chatID);
        DBObject chat = chatsCollection.findOne(query);
        BasicDBList messages = (BasicDBList)chat.get("chats");
        int i=0;
        //como carao hago esto??? no resolvi un carajo
        while (i<messages.size()){
            if (msgs.get(i).get("IPA")!=null && msgs.get(i).get("IPADescription")!=null){
                BasicDBObject query2 = query.append("chats", new BasicDBObject("$elemMatch", new BasicDBObject("_id", msgs.get(i).get("_id"))));
                BasicDBObject newValues = msgs.get(i);
                newValues.append("IPA", msgs.get(i).get("IPA"));
                newValues.append("IPADescription", msgs.get(i).get("IPADescription"));
                
                chatsCollection.update(query2, new BasicDBObject("$set" , new BasicDBObject("chats.$",newValues)));
            }
            else{
                //habria q ver como garcha hacer el unset propiamente
                if (((BasicDBObject)messages.get(i)).get("IPA")!=null && ((BasicDBObject)messages.get(i)).get("IPADescription")!=null){
                    
                    BasicDBObject query2 = query.append("chats", new BasicDBObject("$elemMatch", new BasicDBObject("_id", msgs.get(i).get("_id"))));
                    BasicDBObject newValues = msgs.get(i);
                    newValues.append("IPA", null);
                    newValues.append("IPADescription", null);

                    chatsCollection.update(query2, new BasicDBObject("$set" , new BasicDBObject("chats.$",newValues)));
                }
            }
            
            
            i++;
        }
    }
    
    public ArrayList<DBObject> getAllUsers(){
        ArrayList<DBObject> ret = new ArrayList<DBObject>();
        DBCollection usersCollection = db.getCollection("users");
        DBCursor users = usersCollection.find();
        try {
            while(users.hasNext()) {
                DBObject c = users.next();
                ret.add((BasicDBObject) c);
            }
        } finally {
            users.close();
        }
        return ret;
    }
}
