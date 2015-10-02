package org.kdea.java.pureDraw;

import java.io.IOException;
import java.util.*;
 
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
import org.json.simple.*;
import org.json.simple.parser.*;
 
@ServerEndpoint(value = "/websocket/puredraw", configurator=ServletAwareConfig.class) //Ŭ���̾�Ʈ�� ������ �� ���� URI
public class ChatAnnotation {
 
    private static Map<String, Session> sessionMap = new HashMap<>();
 
    private HttpSession httpSession;
 
    public ChatAnnotation() {
    }
 
 
    @OnOpen
    public void start(Session session, EndpointConfig config) {
   
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String userId = (String)httpSession.getAttribute("id");
        System.out.println("������ Ŭ���̾�Ʈ ID:"+userId);
         
        sessionMap.put(userId, session);
        Object objList = httpSession.getServletContext().getAttribute("usrList");
        if(objList==null) {
            List<String> usrList = new ArrayList<>();
            httpSession.getServletContext().setAttribute("usrList", usrList);
            objList = usrList;
        }
        List<String> usrList = (List<String>) objList;
        usrList.add(userId);
    }
 
 
    @OnClose
    public void end() {
        String usrId = (String)httpSession.getAttribute("id");
        sessionMap.remove(usrId);
         
        Object objList = httpSession.getServletContext().getAttribute("usrList");
        List<String> usrList = (List<String>) objList;
        usrList.remove(usrId);
    }
 
    // ���� ���ǰ� ����� Ŭ���̾�Ʈ�κ��� �޽����� ������ ������ ���ο� �����尡 ����Ǿ� incoming()�� ȣ����
    @OnMessage
    public void incoming(String message) {
 
        if(message==null || message.trim().equals("")) return;
 
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObj = (JSONObject)jsonParser.parse(message);
            String sender = (String)jsonObj.get("sender");
            String receiver = (String)jsonObj.get("receiver");
             
            try {
                sessionMap.get(receiver).getBasicRemote().sendText(message);
                sessionMap.get(sender).getBasicRemote().sendText(message);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
 
     
    @OnError
    public void onError(Throwable t) throws Throwable {
        System.err.println("Chat Error: " + t.toString());
    }
}