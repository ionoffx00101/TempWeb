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


// 톰캣이 제공하는 서버api를 이용해서 만든 웹소켓
//우리가 만든클래스지만 서버쪽 소켓이 된다
@ServerEndpoint(value = "/websocket/chatbasic",configurator=ServletAwareConfig.class) //클라이언트가 접속할 때 사용될 URI
//@ServerEndpoint(value = "/websocket/chat") //클라이언트가 접속할 때 사용될 URI
public class ChatAnnotation {

    private static final String GUEST_PREFIX = "Guest";
    // AtomicInteger 클래스는 getAndIncrement()를 호출할 때마다 카운터를 1씩 증가하는 기능을 가지고 있다
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    // CopyOnWriteArraySet 을 사용하면 컬렉션에 저장된 객체를 좀더 간편하게 추출할 수 있다
    // 예를 들어, toArray()메소드를 통해 쉽게 Object[] 형의 데이터를 추출할 수 있다.
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<ChatAnnotation>();

    private final String nickname;
    // 클라이언트가 새로 접속할 때마다 한개의 Session 객체가 생성된다.
    // Session 객체를 컬렉션에 보관하여 두고 해당 클라이언트에게 데이터를 전송할 때마다 사용한다
    private Session session;
//이용자를 식별하기위해 세션을 사용한다.
    public ChatAnnotation() {
    	// 클라이언트가 접속할 때마다 서버측에서는 Thread 가 새로 생성되는 것을 확인할 수 있다 그리고 새로 생성된 쓰레드이름을 출력한다..
    	String threadName = "Thread-Name:"+Thread.currentThread().getName();
    	// getAndIncrement()은 카운트를 1 증가하고 증가된 숫자를 리턴한다
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement(); //게스트1 ..게스트2를 만들기 위해서
        System.out.println(threadName+", "+nickname);
    }
    //새로 추가
    private static  Map<String,Session> sessionMap = new HashMap<>();
    private HttpSession httpSession;

    @OnOpen //커넥션이 처음 열리면= 이용자가 처음접속하면 돌아간다
    public void start(Session session,EndpointConfig config) {
    	System.out.println("클라이언트 접속됨 "+session);
    	// 접속자마다 한개의 세션이 생성되어 데이터 통신수단으로 사용됨
        this.session = session;
        //connections.add(this);
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String userId = (String)httpSession.getAttribute("id");
        System.out.println("접속한 클라이언트 id"+userId);
        
        sessionMap.put(userId,session);
        Object objList = httpSession.getServletContext().getAttribute("usrList");
        if(objList==null){//채팅방에 들어온 최초사람에게 이 게 생김
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

    // 현재 세션과 연결된 클라이언트로부터 메시지가 도착할 때마다 새로운 쓰레드가 실행되어 incoming()을 호출함
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

    // 현재 세션으로부터 도착한 메시지를 모든 접속자에게 전송한다
   private void broadcast(String msg) {
    	Iterator<ChatAnnotation> ss = connections.iterator();
        for (int i=0;i<connections.size();i++) {
        	ChatAnnotation client = ss.next();
            try {
                synchronized (client) { //동시접속을 막음 
                    // 서버에 접속 중인 모든 이용자에게 메지지를 전송한다
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IllegalStateException ise){
            	// 특정 클라이언트에게 현재 메시지 보내기 작업 중인 경우에 동시에 쓰기작업을 요청하면 오류 발생함
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
							Thread.sleep(100); // 메시지 보내기 작업을 마치도록 기다려준다
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
            	// 메시지 전송 중에 오류가 발생(클라이언트 퇴장을 의미함)하면 해당 클라이언트를 서버에서 제거한다
                System.err.println("Chat Error: Failed to send message to client:"+e);
                connections.remove(client);//나간 클라이언트 세션 삭제
                try {
                	// 접속 종료
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                // 한 클라이언트의 퇴장을 모든 이용자에게 알린다
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