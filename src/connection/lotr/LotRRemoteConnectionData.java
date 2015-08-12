package connection.lotr;

import data.analyzer.ConnectionData;
import data.analyzer.DataInput;
import data.analyzer.InvalidInputException;

/**
 *
 * @author matias
 */
public class LotRRemoteConnectionData implements ConnectionData{
    
    private String databaseName;
    private String databaseURI;
    
    public LotRRemoteConnectionData(String dbname, String dburi){
        databaseName=dbname;
        databaseURI = dburi;
    }
    
    public void connect(DataInput input) throws InvalidInputException{
        LotRDataInput inp = (LotRDataInput)input;
        try{
            inp.connectToSourceRemote(databaseName, databaseURI);
        }
        catch (InvalidInputException ex){
            throw ex;
        }
    }
    
}