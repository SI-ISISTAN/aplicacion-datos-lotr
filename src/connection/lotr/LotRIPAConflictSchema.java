/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr;

/**
 *
 * @author matias
 */
public class LotRIPAConflictSchema {
    long code;
    long interactions;
    double range_max;
    double range_min;
    
    public LotRIPAConflictSchema(){
        code=0;
        interactions =0;
        range_max = 0;
        range_min = 0;
    }
    
     public LotRIPAConflictSchema(long cod, double min, double max){
        code=cod;
        interactions =0;
        range_max = max;
        range_min = min;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public void setInteractions(long interactions) {
        this.interactions = interactions;
    }

    public void setRange_max(double range_max) {
        this.range_max = range_max;
    }

    public void setRange_min(double range_min) {
        this.range_min = range_min;
    }
     
     public void addInteraction(){
         interactions++;
     }

    public long getCode() {
        return code;
    }

    public long getInteractions() {
        return interactions;
    }

    public double getRange_max() {
        return range_max;
    }

    public double getRange_min() {
        return range_min;
    }
     
     
}
