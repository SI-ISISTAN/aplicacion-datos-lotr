/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import data.analyzer.GameActionSchema;
import data.analyzer.Model;
import data.analyzer.GameSchema;
import data.analyzer.SymlogProfile;
import data.analyzer.UserSchema;
/**
 *
 * @author matias
 */
public class LotRModel extends Model{
    
    //evalua si vale la pena evaluar una acción de juego en función
    //a ciertos criterios, codeados aquí porque son propios del modelo
    public boolean isActionRelevant(LotRGameAction action, UserSchema user){
        if (action.get("player") ==user.getKeyAttribute() || action.get("poll")!=null){
            System.out.println("field check");
            return true;
        }
        else{
            return false;
        }
        
    };
    
    public void evaluateGame(GameSchema g, UserSchema u){
        //evalua cada accion de juego
        SymlogProfile result = new SymlogProfile();
        LotRGame game = (LotRGame)g;
        LotRUser user = (LotRUser)u;
        //asocia el ID del jugador a su alias dentro de la partida
        boolean found=false;
        BasicDBList players = (BasicDBList)(game.gerAttr("players"));

        for (Object o : players){
            DBObject obj = (DBObject)o;
            if (user.getKeyAttribute().equals(obj.get("userID"))){
                found=true;
                user.put("alias", obj.get("alias"));
            }
        }
        
        //si pude asociar un player del juego a un id
        if (found){
            for (GameActionSchema o : game.getGameActions()){
                LotRGameAction action = (LotRGameAction) o;
                if (this.isActionRelevant(action, user)){
                    this.evaluateAction(action, result);
                }
            }
        }
    };
    
    public void evaluateAction(LotRGameAction action, SymlogProfile result){
        String actionName = (String) action.get("action");
        System.out.println(actionName);
    }
}
