package org.kdea.java.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

// ��Ĺ�� �����ϴ� ����api�� �̿��ؼ� ���� ������
//�츮�� ����Ŭ�������� ������ ������ �ȴ�
@ServerEndpoint(value = "/websocket/chatbasic", configurator = ServletAwareConfig.class) // Ŭ���̾�Ʈ�� ������ �� ���� URI ServletAwareConfig�� �������Ѵ�
// @ServerEndpoint(value = "/websocket/chat") //Ŭ���̾�Ʈ�� ������ �� ���� URI
public class ChatAnnotation {

	private static Map<String, Session> sessionMap = new HashMap<>();
	private HttpSession httpSession;

	@OnOpen // Ŀ�ؼ��� ó�� ������= �̿��ڰ� ó�������ϸ� ���ư��� ������ ������
	public void start(Session session, EndpointConfig config) {

		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		String userId = (String) httpSession.getAttribute("id");
		System.out.println("������ Ŭ���̾�Ʈ id" + userId);

		sessionMap.put(userId, session);

		Object objList = httpSession.getServletContext().getAttribute("usrList");
		if (objList == null) {// ä�ù濡 ���� ���ʻ������ �� �� ����
			List<String> usrList = new ArrayList<>();
			httpSession.getServletContext().setAttribute("usrList", usrList);
			usrList.add("all");
			objList = usrList;
		}
		List<String> usrList = (List<String>) objList;
		usrList.add(userId);

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
	public void incoming(String message) {

		String threadName = "Thread-Name:" + Thread.currentThread().getName();

		if (message == null || message.trim().equals("")){
			return;
		}

		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObj = (JSONObject) jsonParser.parse(message);
			String sender = (String) jsonObj.get("sender");
			String receiver = (String) jsonObj.get("receiver");
			String strmsg = (String) jsonObj.get("content");

		/*	if(receiver.equals("all")){
				sessionMap.get(receiver).getBasicRemote().sendText(message);
				���� ������ ��ü���� ������ �� �� �ִ�
			}*/
			try {
				sessionMap.get(receiver).getBasicRemote().sendText(message);
				
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