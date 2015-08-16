/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

import java.util.ArrayList;

/**
 *
 * @author matias
 */

public class SymlogProfile {
    private long up_down;
    private long positive_negative;
    private long forward_backward;
    
    private long up_down_min;
    private long positive_negative_min;
    private long forward_backward_min;
    
    private long up_down_max;
    private long positive_negative_max;
    private long forward_backward_max;
    
    private long interactions;
    
    public SymlogProfile(){
        up_down=0;
        positive_negative=0;
        forward_backward=0;
        up_down_min=0;
        positive_negative_min=0;
        forward_backward_min=0;
        up_down_max=0;
        positive_negative_max=0;
        forward_backward_max=0;
        interactions=0;
    }

    public long getUp_down() {
        return up_down;
    }

    public long getPositive_negative() {
        return positive_negative;
    }

    public long getForward_backward() {
        return forward_backward;
    }

    public void setUp_down(long up_down) {
        this.up_down = up_down;
    }

    public void setPositive_negative(long positive_negative) {
        this.positive_negative = positive_negative;
    }

    public void setForward_backward(long forward_backward) {
        this.forward_backward = forward_backward;
    }

    public long getUp_down_min() {
        return up_down_min;
    }

    public long getPositive_negative_min() {
        return positive_negative_min;
    }

    public long getForward_backward_min() {
        return forward_backward_min;
    }

    public long getUp_down_max() {
        return up_down_max;
    }

    public long getPositive_negative_max() {
        return positive_negative_max;
    }

    public long getForward_backward_max() {
        return forward_backward_max;
    }

    public void setUp_down_min(long up_down_min) {
        this.up_down_min = up_down_min;
    }

    public void setPositive_negative_min(long positive_negative_min) {
        this.positive_negative_min = positive_negative_min;
    }

    public void setForward_backward_min(long forward_backward_min) {
        this.forward_backward_min = forward_backward_min;
    }

    public void setUp_down_max(long up_down_max) {
        this.up_down_max = up_down_max;
    }

    public void setPositive_negative_max(long positive_negative_max) {
        this.positive_negative_max = positive_negative_max;
    }

    public void setForward_backward_max(long forward_backward_max) {
        this.forward_backward_max = forward_backward_max;
    }
    
    public void addToMax(long ud, long pn, long fb){
        this.up_down_max+=ud;
        this.positive_negative_max+=pn;
        this.forward_backward_max+=fb;
    }
    
    public void addToMin(long ud, long pn, long fb){
        this.up_down_min+=ud;
        this.positive_negative_min+=pn;
        this.forward_backward_min+=fb;
    }
    
    public void addToRange(long ud, long pn, long fb){
        this.up_down_max+=ud;
        this.positive_negative_max+=pn;
        this.forward_backward_max+=fb;
        this.up_down_min-=ud;
        this.positive_negative_min-=pn;
        this.forward_backward_min-=fb;
    }
    
    public void addValues(long ud, long pn, long fb){
        this.up_down+=ud;
        this.positive_negative+=pn;
        this.forward_backward+=fb;
    }
    
    public void sumInteraction(){
        interactions=interactions+1;
    }
    
    public ArrayList<Double> getNormalizedProfile(){
        ArrayList<Double> n = new ArrayList<Double>();
        float mediaUD = this.up_down_max-this.up_down_min;
        if (mediaUD!=0){
            n.add(0, (double)(this.up_down-this.up_down_min)/mediaUD);
        }
        else{
            n.add(0, (double)0);
        }
        float mediaPN = this.positive_negative_max-this.positive_negative_min;
        if (mediaPN!=0){
        n.add(1, (double)(this.positive_negative-(this.positive_negative_min))/mediaPN);
        }
        else{
            n.add(1, (double)0);
        }
        float mediaFB = this.forward_backward_max-this.forward_backward_min;
        if (mediaFB!=0){
        n.add(2, (double)(this.forward_backward-(this.forward_backward_min))/mediaFB);
        }
        else{
            n.add(2, (double)0);
        }
        return n;
    }
    
    public void addToRangeSingle(long ud, long pn, long fb){
        if (ud>0){
            up_down_max+=ud;
        }
        else if (ud<0){
            up_down_min+=ud;
        }
        if (pn>0){
            positive_negative_max+=pn;
        }
        else if (pn<0){
            positive_negative_min+=pn;
        }
        if (fb>0){
            forward_backward_max+=fb;
        }
        else if (fb<0){
            forward_backward_min+=fb;
        }
    }

    public long getInteractions() {
        return interactions;
    }
    
    @Override
     public String toString() {
        String r = "UD: "+ this.up_down+", PN:"+this.positive_negative+", FB:"+this.forward_backward+"\n";
        String concat = r.concat("UDM: "+ this.up_down_max+", PNM:"+this.positive_negative_max+", FBM:"+this.forward_backward_max+"\n");
        String concat1 = concat.concat("UDm: "+ this.up_down_min+", PNm:"+this.positive_negative_min+", FBm:"+this.forward_backward_min+"\n");
        ArrayList<Double> normalized = this.getNormalizedProfile();
        String concat2 = concat1.concat("Perfil normalizado (0 minimo, 1 máximo): UD: "+ normalized.get(0) +", PN:"+normalized.get(1)+", FB:"+normalized.get(2)+"\n");
        String concat3 = concat2.concat("Interacciones: "+this.interactions+"\n");
        return concat3;
    }
}
