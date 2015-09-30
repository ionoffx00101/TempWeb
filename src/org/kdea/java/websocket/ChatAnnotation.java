package org.kdea.java.websocket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.kdea.ws.ServletAwareConfig;


// ��Ĺ�� �����ϴ� ����api�� �̿��ؼ� ���� ������
//�츮�� ����Ŭ�������� ������ ������ �ȴ�
@ServerEndpoint(value = "/websocket/chatbasic",configurator=ServletAwareConfig.class) //Ŭ���̾�Ʈ�� ������ �� ���� URI
//@ServerEndpoint(value = "/websocket/chat") //Ŭ���̾�Ʈ�� ������ �� ���� URI
public class ChatAnnotation {

    private static final String GUEST_PREFIX = "Guest";
    // AtomicInteger Ŭ������ getAndIncrement()�� ȣ���� ������ ī���͸� 1�� �����ϴ� ����� ������ �ִ�
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    // CopyOnWriteArraySet �� ����ϸ� �÷��ǿ� ����� ��ü�� ���� �����ϰ� ������ �� �ִ�
    // ���� ���, toArray()�޼ҵ带 ���� ���� Object[] ���� �����͸� ������ �� �ִ�.
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<ChatAnnotation>();

    private final String nickname;
    // Ŭ���̾�Ʈ�� ���� ������ ������ �Ѱ��� Session ��ü�� �����ȴ�.
    // Session ��ü�� �÷��ǿ� �����Ͽ� �ΰ� �ش� Ŭ���̾�Ʈ���� �����͸� ������ ������ ����Ѵ�
    private Session session;
//�̿��ڸ� �ĺ��ϱ����� ������ ����Ѵ�.
    public ChatAnnotation() {
    	// Ŭ���̾�Ʈ�� ������ ������ ������������ Thread �� ���� �����Ǵ� ���� Ȯ���� �� �ִ� �׸��� ���� ������ �������̸��� ����Ѵ�..
    	String threadName = "Thread-Name:"+Thread.currentThread().getName();
    	// getAndIncrement()�� ī��Ʈ�� 1 �����ϰ� ������ ���ڸ� �����Ѵ�
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement(); //�Խ�Ʈ1 ..�Խ�Ʈ2�� ����� ���ؼ�
        System.out.println(threadName+", "+nickname);
    }
    //���� �߰�
    private static  Map<String,Session> sessionMap = new HashMap<>();
    private HttpSession httpSession;

    @OnOpen //Ŀ�ؼ��� ó�� ������= �̿��ڰ� ó�������ϸ� ���ư���
    public void start(Session session,EndpointConfig config) {
    	System.out.println("Ŭ���̾�Ʈ ���ӵ� "+session);
    	// �����ڸ��� �Ѱ��� ������ �����Ǿ� ������ ��ż������� ����
        this.session = session;
        //connections.add(this);
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String userId = (String)httpSession.getAttribute("id");
        System.out.println("������ Ŭ���̾�Ʈ id"+userId);
        
        sessionMap.put(userId,session);
        Object objList = httpSession.getServletContext().getAttribute("usrList");
        if(objList==null){//ä�ù濡 ���� ���ʻ������ �� �� ����
        	List<String> usrList = new ArrayList<>();
        	httpSession.getServletContext().setAttribute("usrList",usrList);
        	objList = usrList;
        }
        List<String> usrList =(List<String>) objList;
        usrList.add(userId);
        
       /* String message = String.format("* %s %s", nickname, "has joined.");
        String jsonStr = 
        		"{" +
					"\"nickname\":"+nickname+",\"msg\":\"has joined.\",\"select\":\"Guest1\"" +
        		"}";
       //broadcast(message);
        broadcast(jsonStr);*/
        
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s", nickname, "has disconnected.");
        String jsonStr = 
        		"{" +
					"\"nickname\":"+nickname+",\"msg\":\"has disconnected.\",\"select\":\"Guest1\"" +
        		"}";
        broadcast(jsonStr);
    }

    // ���� ���ǰ� ����� Ŭ���̾�Ʈ�κ��� �޽����� ������ ������ ���ο� �����尡 ����Ǿ� incoming()�� ȣ����
    @OnMessage
    public void incoming(String message) {
    	
    	String threadName = "Thread-Name:"+Thread.currentThread().getName();
    	System.out.println(threadName+", "+nickname);
        if(message==null || message.trim().equals("")) return;
        
        JSONParser jsonParser = new JSONParser();
        try{
        JSONObject jsonObj = (JSONObject)jsonParser.parse(message);
        String sender = (String) jsonObj.get("sender");
        String receiver = (String) jsonObj.get("receiver");
        try{
        	sessionMap.get(receiver).getBasicRemote().sendText(message);
        	return;
        }catch(IOException e){
        	e.printStackTrace();
        }
        }catch(ParseException e){
        	e.printStackTrace();
        }
        
        
 /*       String filteredMessage = String.format("%s: %s", nickname, message);
        String jsonStr = 
        		"{" +
					"\"nickname\":"+nickname+",\"msg\":"+message+",\"select\":\"Guest1\"" +
        		"}";
        broadcast(jsonStr);*/
    }

    
    @OnError
    public void onError(Throwable t) throws Throwable {
        System.err.println("Chat Error: " + t.toString());
    }

    // ���� �������κ��� ������ �޽����� ��� �����ڿ��� �����Ѵ�
   private void broadcast(String msg) {
    	Iterator<ChatAnnotation> ss = connections.iterator();
        for (int i=0;i<connections.size();i++) {
        	ChatAnnotation client = ss.next();
            try {
                synchronized (client) { //���������� ���� 
                    // ������ ���� ���� ��� �̿��ڿ��� �������� �����Ѵ�
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IllegalStateException ise){
            	// Ư�� Ŭ���̾�Ʈ���� ���� �޽��� ������ �۾� ���� ��쿡 ���ÿ� �����۾��� ��û�ϸ� ���� �߻���
            	if(ise.getMessage().indexOf("[TEXT_FULL_WRITING]")!=-1) {
            		new Thread() {
            			@Override
            			public void run() {
		            		while(true) {
		            			try{
		            				client.session.getBasicRemote().sendText(msg);
		            				break;
		            			}catch(IllegalStateException _ise){
		            				try {
							Thread.sleep(100); // �޽��� ������ �۾��� ��ġ���� ��ٷ��ش�
							} catch (InterruptedException e) {}
		            			}
		            			catch(IOException ioe){
		            				ioe.printStackTrace();
		            			}
		            		}
            			}
            		}.start();
            	}
            } catch (Exception e) {
            	// �޽��� ���� �߿� ������ �߻�(Ŭ���̾�Ʈ ������ �ǹ���)�ϸ� �ش� Ŭ���̾�Ʈ�� �������� �����Ѵ�
                System.err.println("Chat Error: Failed to send message to client:"+e);
                connections.remove(client);//���� Ŭ���̾�Ʈ ���� ����
                try {
                	// ���� ����
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                // �� Ŭ���̾�Ʈ�� ������ ��� �̿��ڿ��� �˸���
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                String jsonStr = 
                		"{" +
        					"\"nickname\":"+nickname+",\"msg\":\"has been disconnected.\",\"select\":\"Guest1\"" +
                		"}";
                broadcast(message);
            }
        }
    }

}