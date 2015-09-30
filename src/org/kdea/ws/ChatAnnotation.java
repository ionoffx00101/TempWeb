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
   // AtomicInteger Ŭ������ getAndIncrement()�� ȣ���� ������ ī���͸� 1�� �����ϴ� ����� ������ �ִ�
   private static final AtomicInteger connectionIds = new AtomicInteger(0);

   private static final Map<String,Session> sessionMap = new HashMap<String,Session>();
    
   private final String nickname;
   // Ŭ���̾�Ʈ�� ���� ������ ������ �Ѱ��� Session ��ü�� �����ȴ�.
    
   // Session ��ü�� �÷��ǿ� �����Ͽ� �ΰ� �ش� Ŭ���̾�Ʈ���� �����͸� ������ ������ ����Ѵ�

   private EndpointConfig config;
   private Session wsSession;

   private HttpSession httpSession;
   private ServletContext ctx;
    
   public ChatAnnotation() {
       // Ŭ���̾�Ʈ�� ������ ������ ������������ Thread �� ���� �����Ǵ� ���� Ȯ���� �� �ִ�
       String threadName = "Thread-Name:"+Thread.currentThread().getName();
       // getAndIncrement()�� ī��Ʈ�� 1 �����ϰ� �����Ǳ� ���� ���ڸ� �����Ѵ�
       nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
       System.out.println("������:"+threadName+", "+nickname);
   }

   @OnOpen
   public void start(Session session, EndpointConfig config) {
       System.out.println("Ŭ���̾�Ʈ ���ӵ� "+session);
        
       //Session:�����ڸ��� �Ѱ��� ������ �����Ǿ� ������ ��ż������� ����
       //�Ѱ��� ���������� �������� ���� ����ؼ� �����ϸ� Session�� ���� �ٸ����� HttpSession �� ������
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

   // ���� ���ǰ� ����� Ŭ���̾�Ʈ�κ��� �޽����� ������ ������ ���ο� �����尡 ����Ǿ� incoming()�� ȣ����
   @OnMessage
   public void incoming(String message) {
        
       String threadName = "Thread-Name:"+Thread.currentThread().getName();
       System.out.println("�޽��� ����:"+threadName+", "+nickname);
       System.out.println("httpSession:"+httpSession);

       if(message==null || message.trim().equals("")) return;
        
       String filteredMessage = String.format("%s: %s", nickname, message);
        
       //Guest0�� �޽����� Ư�� Ŭ���̾�Ʈ(Guest2)���Ը� �����ϴ� ���
       if(this.nickname.equals("Guest0")) {
           sendToOne(filteredMessage, sessionMap.get("Guest2"));
       }
       else //���� ���ӵ� ��� Ŭ���̾�Ʈ���� �޽����� �����ϴ� ���
       {
           broadcast(filteredMessage);
       }
   }

   @OnError
   public void onError(Throwable t) throws Throwable {
       System.err.println("����/��������("+nickname+"):Chat Error: " + t.toString());
       sessionMap.remove(this.nickname);
   }

   // Ŭ���̾�Ʈ�κ��� ������ �޽����� Ư�� ������(Session)���Ը� �����Ѵ�
   private void sendToOne(String msg, Session ses) {
       try {
           ses.getBasicRemote().sendText(msg);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
    
   // Ŭ���̾�Ʈ�κ��� ������ �޽����� ��� �����ڿ��� �����Ѵ�
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