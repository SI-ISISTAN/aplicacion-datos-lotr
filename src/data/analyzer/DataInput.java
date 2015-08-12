/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author matias
 */
public abstract class DataInput {
    
    public void connectToSource(ConnectionData data) throws InvalidInputException{
        try{
            data.connect(this);
        }
        catch (InvalidInputException ex){
            throw ex;
        }
    }
    
    public abstract ArrayList<UserSchema> getUsers();
    
    public abstract ArrayList<GameSchema> getGames();
    
    public abstract ArrayList<GameSchema> getGamesForUser(String userID);
    
}
