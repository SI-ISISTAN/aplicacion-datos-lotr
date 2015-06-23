/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import com.mongodb.BasicDBList;
import data.analyzer.GameSchema;
import com.mongodb.DBObject;
import data.analyzer.GameActionSchema;
import java.util.ArrayList;
import java.util.Arrays;
import connection.lotr.LotRGameAction;
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
    
    public Object gerAttr(String at){
        return gameData.get(at);
    }
}
