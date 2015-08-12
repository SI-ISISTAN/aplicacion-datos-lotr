/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;

import data.analyzer.ConnectionData;
import data.analyzer.DataInput;
import data.analyzer.InvalidInputException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class LotRLocalConnectionData implements ConnectionData{
    
    private String databaseName;
    
    public LotRLocalConnectionData(String dbname){
        databaseName=dbname;
    }
    
    public void connect(DataInput input) throws InvalidInputException{
        LotRDataInput inp = (LotRDataInput)input;
        try {
            inp.connectToSourceLocal(databaseName);
        } catch (InvalidInputException ex) {
           throw ex;
        }
    }
    
}
