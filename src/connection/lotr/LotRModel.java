/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import connection.lotr.gamelogic.LotRGameState;
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
import interfaz.MainWindow;
import java.util.ArrayList;
import org.json.simple.JSONValue;
/**
 *
 * @author matias
 */
public class LotRModel extends Model{
    private Hashtable<String, JSONObject> evaluationPolicy;
    private JSONObject IPAPolicy;
    private MainWindow window;
    private LotRDataInput database;
    private String modelName;
    private LotRGameState gameState;
    
    public LotRModel(MainWindow w, LotRDataInput inp, String JSONpath){
        super();
        database=inp;
        window = w;
        modelName=null;
        window.consolePrint("Leyendo modelo de datos cargado...");
        JSONParser parser = new JSONParser();
         try {
            Object obj = parser.parse(new FileReader(JSONpath));
            JSONObject loadedModel = (JSONObject) obj;
            modelName = (String)loadedModel.get("modelName");
            JSONArray policies = (JSONArray) loadedModel.get("actions");
            IPAPolicy = (JSONObject) loadedModel.get("IPA");
            int i = 0;
            evaluationPolicy = new Hashtable<String,JSONObject>();
            //construir hashatble de politicas a analizar
            while (i<policies.size()){
                JSONObject ev = (JSONObject)(policies.get(i));
                evaluationPolicy.put((String)ev.get("name"), ev );
                i++;
            }
            window.consolePrint("Modelo cargado con éxito. \n ---------------------------------------------");
         } catch (Exception e) {
             window.consolePrint("No se encontró modelo de análisis o éste es erróneo. \n ---------------------------------------------");
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

            //Para cada jugador, instancia un nuevo perfil de Symlog hasheado por alias; tener una hash con el par alias - userID
            Hashtable<String, SymlogProfile> partialProfiles = new Hashtable<String, SymlogProfile>();
            Hashtable<String, String> userIDs = new Hashtable<String, String>();
            BasicDBList players = (BasicDBList) game.get("players");
            DBObject[] playersArr = players.toArray(new DBObject[0]);
            //instancio el estado de juego con la configuracion inicial del juego
            gameState = new LotRGameState();
            System.out.println("--------------------- NUEVA PARTIDA: ID "+(String)game.get("gameID")+" ---------------------");
            for(DBObject p : playersArr) {
              partialProfiles.put((String)p.get("alias"), new SymlogProfile());
              userIDs.put((String)p.get("alias"), (String)p.get("userID"));
              gameState.addPlayer((String)p.get("alias"));
            }
            
            if (game.get("configName")!=null){
                DBObject config = database.getConfig((String)game.get("configName"));
                if (config!=null){
                    gameState.loadInitialState(config);
                    //si hay datos sobre el gamestate
                    window.consolePrint("Analizo nueva partida. Game ID: "+game.get("gameID"));
                    window.consolePrint("---------------------------------------------");
                    //evalua cada accion de juego
                    ArrayList<GameActionSchema> gameActions = game.getGameActions();
                    int i=0;
                    while (i<gameActions.size()){
                        LotRGameAction action = (LotRGameAction) gameActions.get(i);
                        //updatear el gamestate
                        gameState.update(action);
                        //analizar la accion si es relevante
                        if (this.isActionRelevant(action)){
                            //evaluar acción   
                            String actionName = (String) action.get("action");
                            JSONObject policy = evaluationPolicy.get(actionName);
                            //si la accion es formato SimpleChoice y existe una acción próxima
                            if ("SimpleChoice".equals(policy.get("format")) && i+1<gameActions.size()){
                                this.evaluateSimpleChoice(action, (LotRGameAction) gameActions.get(i+1), policy, partialProfiles.get((String)action.get("player")), (String)action.get("player"));
                            }
                            else if ("SpontaneousChoice".equals(policy.get("format")) && i+1<gameActions.size()){
                                 this.evaluateSpontaneousChoice(action, policy, partialProfiles.get((String)action.get("player")), (String)action.get("player"));
                            }
                            else if ("PolledChoice".equals(policy.get("format")) && i+1<gameActions.size()){
                                 this.evaluatePolledChoice(action, policy, partialProfiles);
                            }
                        }
                        i++;
                    }
                    //recupero el chat correspondiente a la partida
                    DBObject chat = database.getChatForGame((String)game.get("gameID"));
                    if (chat!=null){
                        //si existe, busco los posibles conflictos y llevo la data a los partial profiles
                        Hashtable<String, LotRIPAConflictTable> tab = this.createIPATables(playersArr);
                        BasicDBList msgs = (BasicDBList)chat.get("chats");

                        //cargo los datos de IPA provistos por el modelo JSON 
                        if (msgs.size()>0 && msgs.size()> (long)IPAPolicy.get("minimumInteractionsTotal")){
                            int chatsAmount = msgs.size();
                            for (Object o : msgs){
                                DBObject msg = (DBObject)o;
                                String alias = (String)msg.get("from");
                                if (msg.get("IPA")!=null){
                                    //posible bad casting

                                    tab.get(alias).addInteractionToConflict((int)msg.get("IPA"));               
                                }
                                tab.get(alias).addInteraction();
                            }
                            //analisis de chats, delegado a table
                            for (String key : tab.keySet()) {
                                    int conflicts = tab.get(key).analyzeChatsForUser(partialProfiles.get(key), IPAPolicy, tab.size(), msgs.size());
                                    window.consolePrint("\nSe han hallado "+conflicts+ " conflictos IPA para el usuario "+key);
                            }
                        }
                        else{
                            window.consolePrint("\nNo se han encontrado chats para esta partida, o su número es insuficiente para proveer datos significativos. \n");
                        }
                    }

                    /*
                    //guardo cada perfil parcial en la base de datos, en la coleccion de usuarios
                    this.savePartialProfiles(partialProfiles, userIDs);
                    //guardo en el campo de la partida en la base de datos que ya analice la partida
                    database.setAnalyzedGame((String)game.get("gameID"), modelName);
                    //imprimo resultado
                    */
                    for (String key : partialProfiles.keySet()) {
                        window.consolePrint(key);
                        window.consolePrint(partialProfiles.get(key).toString());
                    }
                    //window.consolePrint("\nSe ha guardado en la base de datos el análisis.");
                    window.consolePrint("---------------------------------------------");
                }
            }
            else{
                //no hay data sobre la configuracion inicial
                System.out.println("No hay datos sobre la configuracion inicial de la partida");
            }
            
        }
    };
    
    //Evaluar accion de formato elección simple
    public void evaluateSimpleChoice(LotRGameAction action, LotRGameAction nextAction, JSONObject policy, SymlogProfile result, String player){
        JSONArray choices = (JSONArray) policy.get("choices");
        int i =0;
        boolean found=false;
        while (i<choices.size() && !found){
           JSONObject choice = (JSONObject)choices.get(i);
           //si encuentro la accion en las politicas
           if ( ((String)nextAction.get("action")).equals((choice).get("action"))){
                    JSONArray values = this.getSymlogValues((JSONArray)choice.get("result"), player);
                    result.addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                    this.setRange(choices, result, player);
                found=true;
           }
           i++;
        }
        if (!found){
                JSONArray values = this.getSymlogValues((JSONArray)(((JSONObject)choices.get(choices.size()-1)).get("result")), player);
                result.addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                this.setRange(choices, result, player);
        }
        result.sumInteraction();
    }
    
    //Evluar accion de formato elección espontánea
    public void evaluateSpontaneousChoice(LotRGameAction action,  JSONObject policy, SymlogProfile result, String player){
        JSONArray fieldLocation = (JSONArray) policy.get("field");
        
        int j=0;
        JSONObject o = (JSONObject)JSONValue.parse(action.getDBObject().toString());
        String value = null;
        
        while (j<fieldLocation.size()){
            if (o!= null){ 
                try{
                    o = (JSONObject)o.get((String)fieldLocation.get(j));
                }
                catch (ClassCastException e){
                    value = (String)o.get((String)fieldLocation.get(j));
                }         
                j++;
            }
        }
        //Si se encontro el valor
        if (value!=null){
            JSONArray choices = (JSONArray) policy.get("choices");
            int i =0;
            boolean found=false;
            while (i<choices.size() && !found){
               JSONObject choice = (JSONObject)choices.get(i);
               //si encuentro la accion en las politicas
               if ( value.equals((choice).get("action"))){
                        JSONArray values = this.getSymlogValues((JSONArray)choice.get("result"), player);
                        result.addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                        result.addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                    found=true;
               }
               i++;
            }
            result.sumInteraction();
        }
    }
    
    //Evaluar accion de formato con poll
    public void evaluatePolledChoice(LotRGameAction action,  JSONObject policy, Hashtable<String, SymlogProfile> partialProfiles){
        JSONObject obj = (JSONObject)JSONValue.parse(action.getDBObject().toString());
        JSONObject poll = (JSONObject)obj.get("poll");
        boolean agreement = false;
        if (poll!=null){
            //obtengo la informacion sobre si la poll termino en agreement o no
            if (poll.get("agreement") != null && (boolean)poll.get("agreement")==true){
                agreement=true;
            }
            
            //analizar la decision segun parámetros forward-backward
            JSONArray votes= (JSONArray) poll.get("votes");
            int v=0;
            if (votes.size()>2){    //esat comparacion solo tiene sentido si hay mas de 2 votantes
                while (v<votes.size()){
                    String user = (String)((JSONObject)votes.get(v)).get("alias");
                    if ((boolean)((JSONObject)votes.get(v)).get("agree") != agreement){
                        
                            JSONArray values = this.getSymlogValues((JSONArray)((JSONObject)policy.get("forward_backward")).get("disagree"),user);
                            partialProfiles.get(user).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));     
                        //partialProfiles.get(user).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                    }
                    else{
                            JSONArray values = this.getSymlogValues((JSONArray)((JSONObject)policy.get("forward_backward")).get("agree"), user);
                            partialProfiles.get(user).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                        //partialProfiles.get(user).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                    }
                    //ESTO HAY QUE REVISARLO+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    JSONArray choices = new JSONArray();
                    choices.add( this.getSymlogValues((JSONArray)((JSONObject)policy.get("forward_backward")).get("disagree"),user));
                    choices.add(this.getSymlogValues((JSONArray)((JSONObject)policy.get("forward_backward")).get("agree"),user));
                    
                    this.setRangePoll(choices, partialProfiles.get(user));
                    //ESTO HAY QUE REVISARLO+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    partialProfiles.get(user).sumInteraction();
                    v++;
                }
            }
            
            //analizar la decision segun parámetros up-down
            
            //obtengo quienes son los jugadores q participan activamente en la decision
            JSONArray fieldLocation = (JSONArray) policy.get("chosen");  
            JSONArray sacrifices = (JSONArray)poll.get("actions");
            if (sacrifices!=null){   
                ArrayList<String> chosen = new ArrayList<String>();
                int i=0;
                while (i<sacrifices.size()){
                    
                    Object o= sacrifices.get(i);
                    this.recursiveFieldSearch(fieldLocation, 0, fieldLocation.size(), o, chosen);
                   
                i++;
                }
                            
                //analizo segun up-down
                    JSONObject up_down_policy = (JSONObject)policy.get("up_down");
                    int votenum=0;
                    while (votenum<votes.size()){
                        JSONObject vote = (JSONObject)votes.get(votenum);
                        String voter = (String)vote.get("alias");
                        int c=0;
                        boolean found=false;
                        while (!found && c<chosen.size()){
                            if (voter.equals(chosen.get(c))){
                                found=true;
                            }
                            c++;
                        }
                        if (found){
                            
                            if ((boolean)vote.get("agree")){
                                JSONArray values= this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("self")).get("agree"),voter);
                                partialProfiles.get(voter).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                                //partialProfiles.get(voter).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                            }
                            else{
                                JSONArray values = this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("self")).get("disagree"), voter);
                                partialProfiles.get(voter).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                                //partialProfiles.get(voter).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                            }
                            //ESTO HAY QUE REVISARLO+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            JSONArray choices = new JSONArray();
                            choices.add( this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("self")).get("disagree"),voter));
                            choices.add( this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("self")).get("agree"),voter));
                            this.setRangePoll(choices, partialProfiles.get(voter));
                        }
                        else{
                            if ((boolean)vote.get("agree")){
                                JSONArray values = this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("others")).get("agree"),voter);
                                partialProfiles.get(voter).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                                partialProfiles.get(voter).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                            }
                            else{
                                JSONArray values = this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("others")).get("disagree"),voter);
                                partialProfiles.get(voter).addValues((long)values.get(0), (long)values.get(1), (long)values.get(2));
                                partialProfiles.get(voter).addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                            }
                            //ESTO HAY QUE REVISARLO+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            JSONArray choices = new JSONArray();
                            choices.add( this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("others")).get("disagree"),voter));
                            choices.add( this.getSymlogValues((JSONArray)((JSONObject)up_down_policy.get("others")).get("agree"),voter));
                            this.setRangePoll(choices, partialProfiles.get(voter));
                        }
                        partialProfiles.get(voter).sumInteraction();
                        votenum++;
                    }              
            }              
        }
    }
    
    public void setRange(JSONArray choices, SymlogProfile profile, String player){
        //agregar al rango para calculo de acciones consideradas
                int j=0;
                long maxUD = -99999;
                long maxPN = -99999;
                long maxFB= -99999;
                long minUD = 99999;
                long minPN = 99999;
                long minFB= 99999;
                while (j<choices.size()){
                    //tengo que agregar el analisis de posibilidades
                    JSONArray othvalues = this.getSymlogValues((JSONArray)((JSONObject)choices.get(j)).get("result"), player);
                    
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
    
    public void setRangePoll(JSONArray choices, SymlogProfile profile){
        //agregar al rango para calculo de acciones consideradas
                int j=0;
                long maxUD = -99999;
                long maxPN = -99999;
                long maxFB= -99999;
                long minUD = 99999;
                long minPN = 99999;
                long minFB= 99999;
                while (j<choices.size()){
                    JSONArray othvalues = (JSONArray)(choices.get(j));
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
    
    public void recursiveFieldSearch(JSONArray fieldLocation, int index, int limit, Object o, ArrayList<String> chosen){
        if (index<limit-1){
            if (o!= null){
                    if (o instanceof JSONArray){
                        int it=0;
                        JSONArray arr = ((JSONArray)o);
                        while (it<arr.size()){
                            this.recursiveFieldSearch(fieldLocation,index+1,limit,arr.get(it),chosen);
                            it++;
                        }
                    }
                    else if (o instanceof JSONObject){
                        JSONObject ob = ((JSONObject)o);
                        this.recursiveFieldSearch(fieldLocation,index+1,limit,ob,chosen);
                    }
            }
        }
        else if (index==limit-1){
            String value = (String)((JSONObject)o).get((String)fieldLocation.get(index));
            chosen.add(value);
        }
    }
    
    public void savePartialProfiles(Hashtable<String,SymlogProfile> partialProfiles, Hashtable<String,String> userIDs){
        for (String key : partialProfiles.keySet()){
            DBObject user =database.getUser(userIDs.get(key));
            if (user!=null){
                BasicDBObject symlog = (BasicDBObject)user.get("symlog");
                SymlogProfile partial = partialProfiles.get(key);
                ArrayList<Double> normalized = partialProfiles.get(key).getNormalizedProfile();
                boolean addNewModel = false;
                if (symlog!=null){
                    if (modelName.equals(symlog.get("model"))){
                        //Construyo el perfil agregando las interacciones que tengo
                        //Revisar esta pija
                        double up_down = (double)symlog.get("up_down");
                        double positive_negative = (double)symlog.get("positive_negative");
                        double forward_backward = (double)symlog.get("forward_backward");
                        long interactions = (long)symlog.get("interactions");
                        //po que???
                        if (interactions>0 && partial.getInteractions()>0){ 
                            long totalInteractions = interactions+partial.getInteractions();
                            double new_ud = ((double)(up_down*interactions)/totalInteractions + (double)(normalized.get(0)*partial.getInteractions())/totalInteractions);
                            double new_pn = ((double)(positive_negative*interactions)/totalInteractions  + (double)(normalized.get(1)*partial.getInteractions())/totalInteractions );
                            double new_fb = ((double)(forward_backward*interactions)/totalInteractions  + (double)(normalized.get(2)*partial.getInteractions())/totalInteractions );
                            //Agrego un control para evitar error de redondeo en operaciones con double
                            if (new_ud<0){
                                new_ud=0;
                            }
                            if (new_ud>1){
                                new_ud=1;
                            }
                            if (new_pn<0){
                                new_pn=0;
                            }
                            if (new_pn>1){
                                new_pn=1;
                            }
                            if (new_fb<0){
                                new_fb=0;
                            }
                            if (new_fb>1){
                                new_fb=1;
                            }
                            //Actualizar los valores en la db
                            BasicDBObject document = new BasicDBObject();
                            document.put("model", modelName);
                            document.put("up_down", new_ud);
                            document.put("positive_negative", new_pn);
                            document.put("forward_backward", new_fb);
                            document.put("interactions", totalInteractions);
                            database.updateSymlog(userIDs.get(key),document);
                        }
                    }
                    else{
                        addNewModel=true;
                    }
                }
                else{
                    addNewModel=true;
                }
                if (addNewModel){
                    if (partial.getInteractions()>0){
                        BasicDBObject document = new BasicDBObject();
                        document.put("model", modelName);
                        document.put("up_down", ((double)normalized.get(0)));
                        document.put("positive_negative", (double)normalized.get(1));
                        document.put("forward_backward", (double)normalized.get(2));
                        document.put("interactions", partial.getInteractions());
                        database.updateSymlog(userIDs.get(key),document);
                    }
                }
            }
            else{
                window.consolePrint("No se ha encontrado en la base de datos al user de ID "+userIDs.get(key));
            }
        }
    }

    public String getModelName() {
        return modelName;
    }

    private Hashtable<String, LotRIPAConflictTable> createIPATables(DBObject[] players){
        Hashtable<String, LotRIPAConflictTable> ret = new Hashtable();
        for(DBObject p : players) {
              LotRIPAConflictTable conflicts = new LotRIPAConflictTable();
              ret.put((String)p.get("alias"), conflicts);
            }
        return ret;
    }
    
    //le paso el array que contiene objetos par {"conditions", "values"}
    public JSONArray getSymlogValues (JSONArray values, String player){
        JSONArray result = null;
        int i=0;
        boolean found = false;
        //itero por los pares condition/values
        while (!found && i<values.size()){
            //si el array conditions es nulo, es incondicional
            if (((JSONObject)values.get(i)).get("conditions") == null){
                
                result = (JSONArray)((JSONObject)values.get(i)).get("values");
                found=true;
            }
            else{
                //si el array conditions es no nulo
                JSONArray conditions = (JSONArray)((JSONObject)values.get(i)).get("conditions");
                int j=0;
                boolean allconditions=true;
                while (j<conditions.size()){
                    String condition = (String)((JSONObject)conditions.get(j)).get("condition");
                    //si se dan todos lso terminos de la condicion, se la acepta como valor de retorno
                    if (!"other".equals(condition)){
                        if (!gameState.getCondition((JSONObject)conditions.get(j), player)){
                                allconditions=false;
                        }
                    }
                    else{
                            //si llegue al punto de evaluar "others", que va ultimo y esta solo, es porque las demas condiciones fallaron; tiro el valor
                            result = (JSONArray)((JSONObject)values.get(i)).get("values");
                            found=true;
                    }
                    j++;
                }
                if (allconditions && !found){
                    
                    result = (JSONArray)((JSONObject)values.get(i)).get("values");
                    found=true;
                }
            }
            i++;
        }
        return result;
    }

};
