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
public interface DataInput {
    
    public abstract void connectToSource();
    
    public abstract ArrayList<UserSchema> getUsers();
    
    public abstract ArrayList<GameSchema> getGamesForUser(String userID);
    
}
