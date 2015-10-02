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

// ��Ĺ�� �����ϴ� ����api�� �̿��ؼ� ���� ������
//�츮�� ����Ŭ�������� ������ ������ �ȴ�
@ServerEndpoint(value = "/websocket/allfn", configurator = ServletAwareConfig.class) // Ŭ���̾�Ʈ�� ������ �� ���� URI ServletAwareConfig�� �������Ѵ�
// @ServerEndpoint(value = "/websocket/chat") //Ŭ���̾�Ʈ�� ������ �� ���� URI
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
 
	@OnOpen // Ŀ�ؼ��� ó�� ������= �̿��ڰ� ó�������ϸ� ���ư��� ������ ������
	public void start(Session session, EndpointConfig config) {

		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.ctx = (ServletContext) config.getUserProperties().get(ServletContext.class.getName());
		this.wsSession = session;
	    this.config = config;
	    
		String userId = (String) httpSession.getAttribute("id");
		System.out.println("������ Ŭ���̾�Ʈ id" + userId);

		sessionMap.put(userId, session);

		Object objList = httpSession.getServletContext().getAttribute("usrList");
		if (objList == null) {// ä�ù濡 ���� ���ʻ������ �� �� ����
			List<String> usrList = new ArrayList<>();
			httpSession.getServletContext().setAttribute("usrList", usrList);
			//usrList.add("all"); <��ü���� ������ ����� ����� ���� ���� �ʿ� �־ ������ �̰� �־���
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

	// ���� ���ǰ� ����� Ŭ���̾�Ʈ�κ��� �޽����� ������ ������ ���ο� �����尡 ����Ǿ� incoming()�� ȣ����

	@OnMessage
	    public void echoTextMessage(Session session, String msg, boolean last)
	            throws IOException {
/*	        System.out.println("�ؽ�Ʈ �޽��� ����:"+msg);
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
		
	        System.out.println("Ŭ���̾�Ʈ-->���� ���̳ʸ� ������ ����");
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
	            System.out.println("����-->Ŭ���̾�Ʈ ���̳ʸ� ���ۿϷ�");
	        }*/
	        
	        
	        
	 }
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Chat Error: " + t.toString());
	}

}