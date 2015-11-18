/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr;

/**
 *
 * @author matias
 */
public class StatProfile {
    private int games;
    private int won;
    private int points;
    private int chats;
    private int deaths;
    
    public StatProfile(){
        games=0;
        won=0;
        points=0;
        chats=0;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getChats() {
        return chats;
    }

    public void setChats(int chats) {
        this.chats = chats;
    }
    
    public void addGame(){
        this.games++;
    }
    
    public void addWon(){
        this.won++;
    }
    
    public void addPoints(int p){
        this.points+=p;
    }
    
     public void addChat(){
        this.chats++;
    }

    @Override
    public String toString() {
        return "StatProfile{" + "games=" + games + ", won=" + won + ", points=" + points + ", chats=" + chats + '}';
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int s) {
        this.deaths = s;
    }
     
    public void addDeath(){
        this.deaths++;
    }
     
     
}
