/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr;
import com.mongodb.BasicDBList;
import data.analyzer.GameSchema;
import com.mongodb.DBObject;
import data.analyzer.GameActionSchema;
import java.util.ArrayList;
import java.util.Arrays;
import lotr.LotRGameAction;
/**
 *
 * @author matias
 */
public class LotRGame implements GameSchema{
    
    private DBObject gameData;
    
    public LotRGame(DBObject game){
        gameData = game;
    }
    
    public  ArrayList<GameActionSchema> getGameActions(){
        ArrayList<GameActionSchema> actions = new ArrayList<GameActionSchema>();
        BasicDBList actionsJson = (BasicDBList)gameData.get("gameActions");
        for (Object o : actionsJson){
            DBObject obj = (DBObject)o;
            LotRGameAction newAction = new LotRGameAction(obj);
            actions.add(newAction);
        }
        return actions;
    };
    
    public Object get(String attr){
        return gameData.get(attr);
    }
    
    public boolean isAnalyzed(){
        if (gameData.get("analyzed")==null || !(Boolean)gameData.get("analyzed")){
              return false;
        }
        else return true;
    }
}
