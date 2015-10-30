/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lotr.socket;


import io.socket.client.*;
import io.socket.backo.*;
import io.socket.hasbinary.*;
import io.socket.parser.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.json.simple.JSONException;
import org.json.simple.JSONObject;

/**
 *
 * @author matias
 */
public class SocketIOConnection {
    
    public SocketIOConnection() throws MalformedURLException, URISyntaxException{
           Socket socket = IO.socket("http://localhost:3000");
           /*
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

              @Override
              public void call(Object... args) {
                socket.emit("foo", "hi");
                socket.disconnect();
              }

            }).on("event", new Emitter.Listener() {

              @Override
              public void call(Object... args) {}

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

              @Override
              public void call(Object... args) {}

            });
            socket.connect();
                   */
    }
    
}
