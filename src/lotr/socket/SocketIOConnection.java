/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr.socket;


import interfaz.MainWindow;
import io.socket.client.*;
import io.socket.backo.*;
import io.socket.emitter.Emitter;
import io.socket.hasbinary.*;
import io.socket.parser.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.json.JSONArray;
//import org.json.simple.JSONException;
import org.json.JSONObject;

/**
 *
 * @author matias
 */
public class SocketIOConnection {
    
    private MainWindow window;
    private ArrayList<String> ongoingGames;
    private Socket socket;
    
    public SocketIOConnection(MainWindow w, String url) throws MalformedURLException, URISyntaxException{
        window=w;
        ongoingGames=new ArrayList<>();

         socket = IO.socket(url);
           
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

              @Override
              public void call(Object... args) {
              }

            }).on("hello message", new Emitter.Listener() {
                
              @Override
              public void call(Object... args) {
                  socket.emit("admin connect");
              }
            }).on("send lobby info", new Emitter.Listener() {
                
              @Override
              public void call(Object... args) {
                  JSONArray games = (JSONArray)(((JSONObject)args[0]).get("games"));
                  //agrego las cosas halladas a la lista
                  ongoingGames=new ArrayList<>();
                  for (int i=0;i<games.length();i++){
                      ongoingGames.add((String)((JSONObject)games.get(i)).get("gameID"));
                  }
                  window.loadGamesLists(ongoingGames);
              }
              //se agrega un nuevo juego a los activos
             }).on("game finished", new Emitter.Listener() {
                
              @Override
              public void call(Object... args) {

                  ongoingGames.add((String)(((JSONObject)args[0]).get("gameID")));
                  window.loadGamesLists(ongoingGames);
              }
              
              }).on("recieve message", new Emitter.Listener() {
                
              @Override
              public void call(Object... args) {
                        window.addChatMessage(((String)((JSONObject)((JSONObject)args[0]).get("player")).get("alias"))+": "+((String)((JSONObject)args[0]).get("msg"))+"\n");
              }
                }).on("admin message", new Emitter.Listener() {
                
              @Override
              public void call(Object... args) {
                        window.addChatMessage("Administrador: "+((String)((JSONObject)args[0]).get("msg"))+"\n");
              }
                
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

              @Override
              public void call(Object... args) {}

            });
            socket.connect();
                   
    }
    
    public void disconnect(){
        socket.disconnect();
    }
    
    public void connectToGame(String gameID){
        socket.emit("admin join game", gameID);
    }
    
    public void refreshLobbyInfo(){
        socket.emit("ask lobby info");
    }
    
    public void disconnectFromGame(String gameID){
        socket.emit("admin leave game", gameID);
    }
    
    public void sendMessage(String gameID, String msg){
        socket.emit("admin message", gameID, msg);
    }
    
}
