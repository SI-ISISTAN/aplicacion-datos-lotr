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
import data.analyzer.UserSchema;
import java.util.ArrayList;
/**
 *
 * @author matias
 */
public class LotRModel extends Model{
    private Hashtable<String, JSONObject> evaluationPolicy;
    
    public LotRModel(){
        super();
        System.out.println("Test de leer config:");
        JSONParser parser = new JSONParser();
         try {
            Object obj = parser.parse(new FileReader("model.json"));
            JSONObject loadedModel = (JSONObject) obj;
            
            JSONArray policies = (JSONArray) loadedModel.get("actions");
            int i = 0;
            evaluationPolicy = new Hashtable<String,JSONObject>();
            //construir hashatble de politicas a analizar
            while (i<policies.size()){
                JSONObject ev = (JSONObject)(policies.get(i));
                evaluationPolicy.put((String)ev.get("name"), ev );
                i++;
            }
            
         } catch (Exception e) {
             System.out.println("No se encontró modelo de análisis o éste es erróneo");
            e.printStackTrace();
        }
    }
    
    public boolean isActionRelevant(LotRGameAction action){
        //Busco si la accion figura en el modelo cargado
        boolean found=false;
        for (String key : evaluationPolicy.keySet()) {
            if ( ((String)action.get("action")).equals(((JSONObject)evaluationPolicy.get(key)).get("name"))){
                found=true;
            }
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
              partialProfiles.put((String)p.get("alias"), new SymlogProfile());
            }
            System.out.println("Analizo nueva partida.");
            //evalua cada accion de juego
            ArrayList<GameActionSchema> gameActions = game.getGameActions();
            int i=0;
            while (i<gameActions.size()){
                LotRGameAction action = (LotRGameAction) gameActions.get(i);
                if (this.isActionRelevant(action)){
                    //evaluar acción   
                    String actionName = (String) action.get("action");
                    JSONObject policy = evaluationPolicy.get(actionName);
                    //si la accion es formato SimpleChoice y existe una acción próxima
                    if ("SimpleChoice".equals(policy.get("format")) && i+1<gameActions.size()){
                        this.evaluateSimpleChoice(action, (LotRGameAction) gameActions.get(i+1), policy, partialProfiles.get((String)action.get("player")));
                    }                 
                }
                i++;
            }
            //imprimo resultado
            for (String key : partialProfiles.keySet()) {
                System.out.println(key);
                System.out.println(partialProfiles.get(key).toString());
            }
        }
    };
    
    public void evaluateSimpleChoice(LotRGameAction action, LotRGameAction nextAction, JSONObject policy, SymlogProfile result){
        JSONArray choices = (JSONArray) policy.get("choices");
        int i =0;
        boolean found=false;
        while (i<choices.size() && !found){
           JSONObject choice = (JSONObject)choices.get(i);
           //si encuentro la accion en las politicas
           if ( ((String)nextAction.get("action")).equals((choice).get("action"))){
                JSONArray values = (JSONArray)choice.get("result");
                result.addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                this.setRange(choices, result);
                found=true;
           }
           i++;
        }
        if (!found){
            JSONArray values = (JSONArray)(((JSONObject)choices.get(choices.size()-1)).get("result"));
            result.addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
            this.setRange(choices, result);
        }
    }
    
    public void setRange(JSONArray choices, SymlogProfile profile){
        //agregar al rango para calculo de acciones consideradas
                int j=0;
                long maxUD = -99999;
                long maxPN = -99999;
                long maxFB= -99999;
                long minUD = 99999;
                long minPN = 99999;
                long minFB= 99999;
                while (j<choices.size()){
                    JSONArray othvalues = (JSONArray)((JSONObject)choices.get(j)).get("result");
                    if ((long)othvalues.get(0) > maxUD){
                        maxUD = (long)othvalues.get(0);
                    }
                    if ((long)othvalues.get(0) < minUD){
                        minUD = (long)othvalues.get(0);
                    }
                    if ((long)othvalues.get(1) > maxPN){
                        maxPN = (long)othvalues.get(1);
                    }
                    if ((long)othvalues.get(1) < minPN){
                        minPN = (long)othvalues.get(1);
                    }
                    if ((long)othvalues.get(2) > maxFB){
                        maxFB = (long)othvalues.get(2);
                    }
                    if ((long)othvalues.get(2) < minFB){
                        minFB = (long)othvalues.get(2);
                    }
                    
                    j++;     
                }
            profile.addToMin(minUD, minPN, minFB);
            profile.addToMax(maxUD, maxPN, maxFB);
    }
    
    public void evaluateAction(LotRGameAction action, SymlogProfile result){
        
    }
};
