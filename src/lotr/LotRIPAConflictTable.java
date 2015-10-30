/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr;

import data.analyzer.IPAConflictSchema;
import data.analyzer.SymlogProfile;
import java.util.ArrayList;
import java.util.Hashtable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author matias
 */
public class LotRIPAConflictTable {
    ArrayList<IPAConflictSchema> table;
    long total_interactions;
    
    public LotRIPAConflictTable(){
        table = new ArrayList<IPAConflictSchema>();
              //Agrego conflictos e índices
              //Esto no estaría nada mal cargarlo desde el JSON
                  table.add(new IPAConflictSchema(1, 0, 0.05));
                  table.add(new IPAConflictSchema(2, 0.03, 0.14));
                  table.add(new IPAConflictSchema(3, 0.06, 0.2));
                  table.add(new IPAConflictSchema(4, 0.04, 0.11));
                  table.add(new IPAConflictSchema(5, 0.21, 0.4));
                  table.add(new IPAConflictSchema(6, 0.14, 0.3));
                  table.add(new IPAConflictSchema(7, 0.02, 0.11));
                  table.add(new IPAConflictSchema(8, 0.01, 0.11));
                  table.add(new IPAConflictSchema(9, 0.01, 0.09));
                  table.add(new IPAConflictSchema(10, 0.03, 0.13));
                  table.add(new IPAConflictSchema(11, 0.01, 0.1));
                  table.add(new IPAConflictSchema(12, 0, 0.07));
                  //habilidades no declaradas formalmente
                  table.add(new IPAConflictSchema(13, 0, 1));
                  table.add(new IPAConflictSchema(14, 0, 1));
                  table.add(new IPAConflictSchema(15, 0, 1));
                  table.add(new IPAConflictSchema(16, 0, 1));
        total_interactions=0;
    }
    
    public void addInteractionToConflict(int number){
        table.get(number-1).addInteraction();
    }
    
    public void addInteraction(){
        total_interactions++;
    }
    
    //queda delegado aca el análisis (ver cómo carajo)
    public int analyzeChatsForUser(SymlogProfile profile, JSONObject IPAPolicy, int tabSize, int msgAmount){
        int conflicts=0;
        if (total_interactions>0){
            for (IPAConflictSchema cs : table){
                if (cs.getInteractions()>0){
                    double ratio = (double)cs.getInteractions()/(double)total_interactions;
                    if (ratio< cs.getRange_min() || ratio> cs.getRange_max()){
                        //Tengo evidencia de conflicto.
                        //chequeo que la cantidad de interacciones para el usuario este en lso rangos aceptables
                        //declarados en el modelo JSON
                        double int_index = (double)this.getTotal_interactions() / (double)msgAmount;
                        double ipu_min = (double) IPAPolicy.get("minIPUFactor");
                        double ipu_max = (double)IPAPolicy.get("maxIPUFactor");
                        double int_average = 1 / (double)tabSize;
                        //conflicto confirmado
                        if (int_index > int_average*ipu_min && int_index < int_average*ipu_max ){
                            String code = "C".concat(Long.toString(cs.getCode())) ;
                            JSONArray values = (JSONArray)IPAPolicy.get(code);
                            if (values!=null){
                                conflicts++;
                                profile.addToRangeSingle((long)values.get(0), (long)values.get(1), (long)values.get(2));
                                profile.sumInteraction();
                            }
                        }
                        
                    }
                }
            }
        }
        return conflicts;
    }

    public long getTotal_interactions() {
        return total_interactions;
    }
    
    
}

