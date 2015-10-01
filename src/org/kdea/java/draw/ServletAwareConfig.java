package org.kdea.java.draw;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
      
public class ServletAwareConfig extends ServerEndpointConfig.Configurator {
 
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
         
        HttpSession session = (HttpSession) request.getHttpSession();
        //ServletContext ctx = session.getServletContext();
 
        config.getUserProperties().put(HttpSession.class.getName(), session);
       // config.getUserProperties().put(ServletContext.class.getName(), ctx);
    }
}