/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection.lotr.gamelogic;

/**
 *
 * @author matias
 */
public class LotRPlayerState {
    int position;
    int cardsAmount;
    int lifeTokens;
    int sunTokens;
    int ringTokens;
    int shields;
    
    public LotRPlayerState(){
        position=0;
        cardsAmount=0;
        lifeTokens=0;
        sunTokens=0;
        ringTokens=0;
        shields=0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCardsAmount() {
        return cardsAmount;
    }

    public void setCardsAmount(int cardsAmount) {
        this.cardsAmount = cardsAmount;
    }

    public int getLifeTokens() {
        return lifeTokens;
    }

    public void setLifeTokens(int lifeTokens) {
        this.lifeTokens = lifeTokens;
    }

    public int getSunTokens() {
        return sunTokens;
    }

    public void setSunTokens(int sunTokens) {
        this.sunTokens = sunTokens;
    }

    public int getRingTokens() {
        return ringTokens;
    }

    public void setRingTokens(int ringTokens) {
        this.ringTokens = ringTokens;
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }
    
    public void addCards(int amount){
        this.cardsAmount = this.cardsAmount+amount;
    }
    
    public void move(int amount){
        this.position = this.position+amount;
    }
    
    public void changeToken(String token, int amount){
                    if (token.equals("sun")){
                        sunTokens = sunTokens+=amount;
                    }
                    else if (token.equals("life")){
                        lifeTokens = lifeTokens+=amount;
                    }
                    else if (token.equals("ring")){
                        ringTokens = ringTokens+=amount;
                    }
                    else if (token.equals("shield")){
                        shields = shields+=amount;
                    }
    }
    
    public boolean getCondition(String condition){
        
        return false;
    }
    
    @Override
    public String toString() {
        return "LotRPlayerState{" + "position=" + position + ", cardsAmount=" + cardsAmount + ", lifeTokens=" + lifeTokens + ", sunTokens=" + sunTokens + ", ringTokens=" + ringTokens + ", shields=" + shields + '}';
    }
    
    
}
