/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

/**
 * Abstract model for user object for which a symlog profile will be made.
 * @author matias
 */
public interface UserSchema {
    
    public abstract String getKeyAttribute();
    
}
