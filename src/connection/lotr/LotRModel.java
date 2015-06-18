/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import data.analyzer.GameActionSchema;
import data.analyzer.Model;
import data.analyzer.GameSchema;
import data.analyzer.SymlogProfile;
/**
 *
 * @author matias
 */
public class LotRModel extends Model{
    
    public boolean isActionRelevant(LotRGameAction action){
        return true;
    };
    
    public void evaluateGame(GameSchema game){
        //evalua cada accion de juego
        SymlogProfile result = new SymlogProfile();
        for (GameActionSchema o : game.getGameActions()){
            LotRGameAction action = (LotRGameAction) o;
            if (this.isActionRelevant(action)){
                this.evaluateAction(action, result);
            }
        }
    };
    
    public void evaluateAction(LotRGameAction action, SymlogProfile result){
        String actionName = (String) action.get("action");
        System.out.println(actionName);
    }
}
