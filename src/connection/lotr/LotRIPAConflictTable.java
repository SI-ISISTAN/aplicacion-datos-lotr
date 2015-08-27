/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;

import data.analyzer.SymlogProfile;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author matias
 */
public class LotRIPAConflictTable {
    ArrayList<LotRIPAConflictSchema> table;
    long total_interactions;
    
    public LotRIPAConflictTable(){
        table = new ArrayList<LotRIPAConflictSchema>();
              //Agrego conflictos e índices
                  table.add(new LotRIPAConflictSchema(1, 0, 0.05));
                  table.add(new LotRIPAConflictSchema(2, 0.03, 0.14));
                  table.add(new LotRIPAConflictSchema(3, 0.06, 0.2));
                  table.add(new LotRIPAConflictSchema(4, 0.04, 0.11));
                  table.add(new LotRIPAConflictSchema(5, 0.21, 0.4));
                  table.add(new LotRIPAConflictSchema(6, 0.14, 0.3));
                  table.add(new LotRIPAConflictSchema(7, 0.02, 0.11));
                  table.add(new LotRIPAConflictSchema(8, 0.01, 0.11));
                  table.add(new LotRIPAConflictSchema(9, 0.01, 0.09));
                  table.add(new LotRIPAConflictSchema(10, 0.03, 0.13));
                  table.add(new LotRIPAConflictSchema(11, 0.01, 0.1));
                  table.add(new LotRIPAConflictSchema(12, 0, 0.07));
                  //habilidades no declaradas formalmente
                  table.add(new LotRIPAConflictSchema(13, 0, 1));
                  table.add(new LotRIPAConflictSchema(14, 0, 1));
                  table.add(new LotRIPAConflictSchema(15, 0, 1));
                  table.add(new LotRIPAConflictSchema(16, 0, 1));
        total_interactions=0;
    }
    
    public void addInteractionToConflict(int number){
        table.get(number-1).addInteraction();
    }
    
    public void addInteraction(){
        total_interactions++;
    }
    
    //queda delegado aca el análisis (ver cómo carajo)
    public void analyzeChatsForUser(SymlogProfile profile){
        if (total_interactions>0){
            System.out.println("Lllego o dia q peleabas con o de patias");
            for (LotRIPAConflictSchema cs : table){
                if (cs.getInteractions()>0){
                    double ratio = (double)cs.getInteractions()/(double)total_interactions;
                    System.out.println(cs.getInteractions());
                    System.out.println(total_interactions);
                    if (ratio< cs.getRange_min() || ratio> cs.getRange_max()){
                        System.out.println("Conflicto detectado! En habilidad "+cs.getCode()+"!");
                        System.out.println("Ratio encontrado: "+ratio);
                        System.out.println("Para limites: "+cs.getRange_min()+" y "+cs.getRange_max());
                    }
                    else{
                        System.out.println("Valores apropidados! En habilidad "+cs.getCode()+"!");
                        System.out.println("Ratio encontrado: "+ratio);
                        System.out.println("Para limites: "+cs.getRange_min()+" y "+cs.getRange_max());
                    }
                }
            }
        }
    }

    public long getTotal_interactions() {
        return total_interactions;
    }
    
    
}

