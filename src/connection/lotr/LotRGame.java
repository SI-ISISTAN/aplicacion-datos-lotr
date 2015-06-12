/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import data.analyzer.GameSchema;
import com.mongodb.DBObject;
/**
 *
 * @author matias
 */
public class LotRGame implements GameSchema{
    
    private DBObject gameData;
    
    public LotRGame(DBObject game){
        gameData = game;
    }
    
    
}
