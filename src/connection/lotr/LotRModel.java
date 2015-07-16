/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import data.analyzer.GameActionSchema;
import data.analyzer.Model;
import data.analyzer.GameSchema;
import data.analyzer.SymlogProfile;
import java.io.FileReader;
import java.util.Hashtable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 *
 * @author matias
 */
public class LotRModel extends Model{
    
    private JSONObject loadedModel;
    
    public LotRModel(){
        super();
        System.out.println("Test de leer config:");
        JSONParser parser = new JSONParser();
         try {
            Object obj = parser.parse(new FileReader("model.json"));
            loadedModel = (JSONObject) obj;
         } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isActionRelevant(LotRGameAction action){
        //Busco si la accion figura en el modelo cargado
        JSONArray relevant = (JSONArray) loadedModel.get("actions");
        int i = 0;
        boolean found=false;
        while (!found && i<relevant.size()){
            if ( ((String)action.get("action")).equals(((JSONObject)relevant.get(i)).get("name"))){
                found=true;
                System.out.println("Encontre una! Accion: "+ (String)action.get("action"));
            }
            i++;
        }
        return found;
    };
    
    //Chequea si el juego cumple con las caracteristicas minimas
    //para que los datos que contiene resulten relevantes
    public boolean isAnalyzable(GameSchema game){
        return true;
    }
    
    public void evaluateGame(GameSchema g){
        //chequea si el juego es digno de evaluacion
        LotRGame game = (LotRGame)g;
        if (this.isAnalyzable(game)){
            //Para cada jugador, instancia un nuevo perfil de Symlog hasheado por UserID
            Hashtable<String, SymlogProfile> partialProfiles = new Hashtable<String, SymlogProfile>();
            BasicDBList players = (BasicDBList) game.get("players");
            DBObject[] playersArr = players.toArray(new DBObject[0]);
            for(DBObject p : playersArr) {
              partialProfiles.put((String)p.get("userID"), new SymlogProfile());
            }
            System.out.println("Analizo nueva partida.");
            //evalua cada accion de juego
            for (GameActionSchema o : game.getGameActions()){
                LotRGameAction action = (LotRGameAction) o;
                if (this.isActionRelevant(action)){
                    //this.evaluateAction(action, result);
                }
            }
        }
    };
    
    public void evaluateAction(LotRGameAction action, SymlogProfile result){
        String actionName = (String) action.get("action");
        System.out.println(actionName);
    }
}
