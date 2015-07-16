/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

import java.util.ArrayList;

/**
 * Abstract model for container for game actions and other data.
 * @author matias
 */
public interface GameSchema {
    
    public abstract ArrayList<GameActionSchema> getGameActions();
    
    public abstract boolean isAnalyzed();
    
}
