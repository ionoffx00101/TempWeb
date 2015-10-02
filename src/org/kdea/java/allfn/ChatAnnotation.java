package org.kdea.java.allfn;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.*;
import org.json.simple.parser.*;

// 톰캣이 제공하는 서버api를 이용해서 만든 웹소켓
//우리가 만든클래스지만 서버쪽 소켓이 된다
@ServerEndpoint(value = "/websocket/allfn", configurator = ServletAwareConfig.class) // 클라이언트가 접속할 때 사용될 URI ServletAwareConfig는 만들어야한다
// @ServerEndpoint(value = "/websocket/chat") //클라이언트가 접속할 때 사용될 URI
public class ChatAnnotation {
	
	Writer writer;
	OutputStream stream;
	private EndpointConfig config;
	private Session wsSession;
	private ServletContext ctx;
	
	private static Map<String, Session> sessionMap = new HashMap<>();
	private HttpSession httpSession;
	
	StringBuffer sb = new StringBuffer();
	String sender,receiver,fname;
 
	@OnOpen // 커넥션이 처음 열리면= 이용자가 처음접속하면 돌아간다 소켓이 열리면
	public void start(Session session, EndpointConfig config) {

		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.ctx = (ServletContext) config.getUserProperties().get(ServletContext.class.getName());
		this.wsSession = session;
	    this.config = config;
	    
		String userId = (String) httpSession.getAttribute("id");
		System.out.println("접속한 클라이언트 id" + userId);

		sessionMap.put(userId, session);

		Object objList = httpSession.getServletContext().getAttribute("usrList");
		if (objList == null) {// 채팅방에 들어온 최초사람에게 이 게 생김
			List<String> usrList = new ArrayList<>();
			httpSession.getServletContext().setAttribute("usrList", usrList);
			//usrList.add("all"); <전체한테 보내기 기능을 만들기 위해 여기 맵에 넣어서 맨위에 이걸 넣어줌
			objList = usrList;
		}
		List<String> usrList = (List<String>) objList;
		
		usrList.add(userId);   
		broadcast(userId,usrList);  
	}

	private void broadcast(String sender, List<String> usrList) {
		Set<String> set = sessionMap.keySet();
		
		Iterator<String> it = set.iterator();
		
		while (it.hasNext()){
			String usrid = it.next();
			if(usrid.equals(sender)){continue;}
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArr = new JSONArray();
			jsonArr.addAll(0, usrList);
			jsonObj.put("usrList", usrList);
			/*
			Writer writer = Session*/
			
			
		}
		
	}

	@OnClose
	public void end() {
		String usrId = (String) httpSession.getAttribute("id");
		sessionMap.remove(usrId);

		Object objList = httpSession.getServletContext().getAttribute("usrList");
		List<String> usrList = (List<String>) objList;
		usrList.remove(usrId);
	}

	// 현재 세션과 연결된 클라이언트로부터 메시지가 도착할 때마다 새로운 쓰레드가 실행되어 incoming()을 호출함

	@OnMessage
	    public void echoTextMessage(Session session, String msg, boolean last)
	            throws IOException {
/*	        System.out.println("텍스트 메시지 도착:"+msg);
	        if (writer == null) {
	            writer = session.getBasicRemote().getSendWriter();
	        }
	        writer.write(msg);
	        writer.flush();
	        if (last) {
	            writer.close();
	            writer = null;
	        }
	        
	    	if (msg == null || msg.trim().equals("")){
				return;
			}*/
			sb.append(msg);
			if(last){
			JSONParser jsonParser = new JSONParser();
			try {
				JSONObject jsonObj = (JSONObject) jsonParser.parse(msg);
				String sender = (String) jsonObj.get("sender");
				receiver = (String) jsonObj.get("receiver");
		
				try {
					//sessionMap.get(sender).getBasicRemote().sendText(msg);
					sessionMap.get(receiver).getBasicRemote().sendText(msg);     
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
			}
	    }
	  
	  
	@OnMessage
	    public void echoBinaryMessage(byte[] msg, Session session, boolean last)
	            throws IOException {
		
	        System.out.println("클라이언트-->서버 바이너리 데이터 도착");
	        if (stream == null) {
	            stream = session.getBasicRemote().getSendStream();
	        }
	        try {
				sessionMap.get(receiver).getBasicRemote().sendObject(msg);
			} catch (EncodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
	      /*  stream.write(msg);
	        stream.flush();
	        if (last) {
	            stream.close();
	            stream = null;
	            System.out.println("서버-->클라이언트 바이너리 전송완료");
	        }*/
	        
	        
	        
	 }
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Chat Error: " + t.toString());
	}

}