/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

/**
 * Abstract class for model used to evaluate game actions and ellaborate Symlog Profile.
 * @author matias
 */
public abstract class Model {
    
    
    public abstract void evaluateGame(GameSchema game);
    
    public abstract void calculateMetrics();
    
}
