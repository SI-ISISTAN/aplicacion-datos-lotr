/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr.gamelogic;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import connection.lotr.LotRGame;
import connection.lotr.LotRGameAction;
import java.util.Hashtable;

/**
 *
 * @author matias
 */
public class LotRGameState {
    int sauronPosition;
    int locationNumber;
    int locationsAmount=0;
    
    Hashtable<String, LotRPlayerState> players;
    DBObject config;
    
    public LotRGameState(){
        sauronPosition=0;
        locationNumber=0;
        locationsAmount=0;
        players = new Hashtable<String, LotRPlayerState>();
        config=null;
    }
    
    public void addPlayer(String alias){
        players.put(alias, new LotRPlayerState());
    }
    
    public void loadInitialState(DBObject config){
       this.config=config;
       sauronPosition = (int)config.get("sauronPosition");
       locationsAmount = ((BasicDBList)(config.get("locations"))).size();
    }

    @Override
    public String toString() {
        String ret = "LotRGameState{" + "sauronPosition=" + sauronPosition + ", locationNumber=" + locationNumber + ", locationsAmount=" + locationsAmount + ", players: \n" ;
        for (String key : players.keySet()){
                    ret+=players.get(key).toString();
        }
        return ret;
    }
    
    public void update (LotRGameAction action){
        String name = (String)action.get("action");
        DBObject data = (DBObject)action.get("data");
        //habra que hacer seguimiento de turnos?
        switch(name){
            
            case "DealHobbitCards":
                if (data.get("player")==null){
                    for (String key : players.keySet()){
                        players.get(key).addCards((int)data.get("amount"));
                    }
                }
                else{
                    players.get((String)data.get("player")).addCards((int)data.get("amount"));
                }
                break;
            case "DealFeatureCards":
                //ver que onda
                //PO LE MI CO
                break;
            case "PlayerDiscard":
                //ir carta por carta y no contar las especiales
                //esto es cuanto menos, polémico
                BasicDBList discards = (BasicDBList)data.get("discard");
                int discarded = 0;
                for (Object o : discards){
                    DBObject card = (DBObject)o;
                    if ("Generic".equals(card.get("type"))){
                        discarded++;
                    }
                }
                players.get((String)data.get("player")).removeCards(discarded);
                break;
            case "MovePlayer":
                players.get((String)data.get("player")).move((int)data.get("amount"));
                break;
            case "MoveSauron":
                this.sauronPosition+=(int)data.get("amount");
                break;    
            case "PlayerGiveCards":
                players.get((String)data.get("to")).addCards(((BasicDBList)data.get("cards")).size());
                players.get((String)data.get("from")).removeCards(((BasicDBList)data.get("cards")).size());
                break;      
            case "ChangeTokens":
                if (data.get("token")!=null){
                    String token = (String)data.get("token");
                    players.get((String)data.get("alias")).changeToken(token, (int)data.get("amount"));
                }
                break; 
            case "PlayCard":
                players.get((String)data.get("player")).removeCards(1);
                break;
            case "PlaySpecialCard":
                //si no las cuento no las descuento
                break;
        }
        System.out.println(this.toString());
        System.out.println(players.size());
    }
    
    
}