package org.kdea.ws;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//JSR-356 ServerEndpoint
@ServerEndpoint(value = "/websocket/chat", configurator=ServletAwareConfig.class)
public class ChatAnnotation {

   private static final String GUEST_PREFIX = "Guest";
   // AtomicInteger 클래스는 getAndIncrement()를 호출할 때마다 카운터를 1씩 증가하는 기능을 가지고 있다
   private static final AtomicInteger connectionIds = new AtomicInteger(0);

   private static final Map<String,Session> sessionMap = new HashMap<String,Session>();
    
   private final String nickname;
   // 클라이언트가 새로 접속할 때마다 한개의 Session 객체가 생성된다.
    
   // Session 객체를 컬렉션에 보관하여 두고 해당 클라이언트에게 데이터를 전송할 때마다 사용한다

   private EndpointConfig config;
   private Session wsSession;

   private HttpSession httpSession;
   private ServletContext ctx;
    
   public ChatAnnotation() {
       // 클라이언트가 접속할 때마다 서버측에서는 Thread 가 새로 생성되는 것을 확인할 수 있다
       String threadName = "Thread-Name:"+Thread.currentThread().getName();
       // getAndIncrement()은 카운트를 1 증가하고 증가되기 전의 숫자를 리턴한다
       nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
       System.out.println("생성자:"+threadName+", "+nickname);
   }

   @OnOpen
   public void start(Session session, EndpointConfig config) {
       System.out.println("클라이언트 접속됨 "+session);
        
       //Session:접속자마다 한개의 세션이 생성되어 데이터 통신수단으로 사용됨
       //한개의 브라우저에서 여러개의 탭을 사용해서 접속하면 Session은 서로 다르지만 HttpSession 은 동일함
       this.wsSession = session;
       this.config = config;
        
       this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
       this.ctx = (ServletContext) config.getUserProperties().get(ServletContext.class.getName());

       sessionMap.put(nickname, session);
        
       String message = String.format("* %s %s", nickname, "has joined.");
       broadcast(message);
   }

   @OnClose
   public void end() {
       sessionMap.remove(this.wsSession);
       String message = String.format("* %s %s", nickname, "has disconnected.");
       broadcast(message);
   }

   // 현재 세션과 연결된 클라이언트로부터 메시지가 도착할 때마다 새로운 쓰레드가 실행되어 incoming()을 호출함
   @OnMessage
   public void incoming(String message) {
        
       String threadName = "Thread-Name:"+Thread.currentThread().getName();
       System.out.println("메시지 도착:"+threadName+", "+nickname);
       System.out.println("httpSession:"+httpSession);

       if(message==null || message.trim().equals("")) return;
        
       String filteredMessage = String.format("%s: %s", nickname, message);
        
       //Guest0의 메시지는 특정 클라이언트(Guest2)에게만 전달하는 경우
       if(this.nickname.equals("Guest0")) {
           sendToOne(filteredMessage, sessionMap.get("Guest2"));
       }
       else //현재 접속된 모든 클라이언트에게 메시지를 전달하는 경우
       {
           broadcast(filteredMessage);
       }
   }

   @OnError
   public void onError(Throwable t) throws Throwable {
       System.err.println("오류/세션제거("+nickname+"):Chat Error: " + t.toString());
       sessionMap.remove(this.nickname);
   }

   // 클라이언트로부터 도착한 메시지를 특정 수신자(Session)에게만 전달한다
   private void sendToOne(String msg, Session ses) {
       try {
           ses.getBasicRemote().sendText(msg);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
    
   // 클라이언트로부터 도착한 메시지를 모든 접속자에게 전송한다
   private void broadcast(String msg) {
        
       Set<String> keys = sessionMap.keySet();
       Iterator<String> it = keys.iterator();
       while(it.hasNext()){
           String key = it.next();
           Session s = sessionMap.get(key);
           try{
               s.getBasicRemote().sendText(msg);
           }catch(IOException e) {
               sessionMap.remove(key);
               try {
                   s.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
               String message = String.format("* %s %s",key, "has been disconnected.");
               broadcast(message);
           }
       }
   }
}