/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;

import data.analyzer.GameActionSchema;
import com.mongodb.DBObject;
/**
 *
 * @author matias
 */
public class LotRGameAction implements GameActionSchema{
    
    private DBObject action;
    
    public LotRGameAction (DBObject act){
        action=act;
    }
    
    public Object get(String attr){
        return action.get(attr);
    }
    
}
