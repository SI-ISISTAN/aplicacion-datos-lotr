/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analyzer;

/**
 *
 * @author matias
 */

public class SymlogProfile {
    private int up_down;
    private int positive_negative;
    private int forward_backward;
    
    private int up_down_min;
    private int positive_negative_min;
    private int forward_backward_min;
    
    private int up_down_max;
    private int positive_negative_max;
    private int forward_backward_max;
    
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
    }

    public int getUp_down() {
        return up_down;
    }

    public int getPositive_negative() {
        return positive_negative;
    }

    public int getForward_backward() {
        return forward_backward;
    }

    public void setUp_down(int up_down) {
        this.up_down = up_down;
    }

    public void setPositive_negative(int positive_negative) {
        this.positive_negative = positive_negative;
    }

    public void setForward_backward(int forward_backward) {
        this.forward_backward = forward_backward;
    }

    public int getUp_down_min() {
        return up_down_min;
    }

    public int getPositive_negative_min() {
        return positive_negative_min;
    }

    public int getForward_backward_min() {
        return forward_backward_min;
    }

    public int getUp_down_max() {
        return up_down_max;
    }

    public int getPositive_negative_max() {
        return positive_negative_max;
    }

    public int getForward_backward_max() {
        return forward_backward_max;
    }

    public void setUp_down_min(int up_down_min) {
        this.up_down_min = up_down_min;
    }

    public void setPositive_negative_min(int positive_negative_min) {
        this.positive_negative_min = positive_negative_min;
    }

    public void setForward_backward_min(int forward_backward_min) {
        this.forward_backward_min = forward_backward_min;
    }

    public void setUp_down_max(int up_down_max) {
        this.up_down_max = up_down_max;
    }

    public void setPositive_negative_max(int positive_negative_max) {
        this.positive_negative_max = positive_negative_max;
    }

    public void setForward_backward_max(int forward_backward_max) {
        this.forward_backward_max = forward_backward_max;
    }
    
    public void addToMax(int ud, int pn, int fb){
        this.up_down_max+=ud;
        this.positive_negative_max+=pn;
        this.forward_backward_max+=fb;
    }
    
    public void addToMin(int ud, int pn, int fb){
        this.up_down_min-=ud;
        this.positive_negative_min-=pn;
        this.forward_backward_min-=fb;
    }
    
    public void addToRange(int ud, int pn, int fb){
        this.up_down_max+=ud;
        this.positive_negative_max+=pn;
        this.forward_backward_max+=fb;
        this.up_down_min-=ud;
        this.positive_negative_min-=pn;
        this.forward_backward_min-=fb;
    }
}
