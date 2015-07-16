/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import data.analyzer.UserSchema;
import com.mongodb.DBObject;
/**
 *
 * @author matias
 */
public class LotRUser implements UserSchema{
    
    private DBObject userData;
    
    public LotRUser(DBObject user){
        userData = user;
    }
    
    public String getKeyAttribute(){
        return (String)((DBObject)(userData.get("local"))).get("userID");
    }
    
    public void put(String at, Object val){
        userData.put(at,val);
    }
    
    public Object get(String at){
        return userData.get(at);
    }
    
}
