package org.kdea.java.websocket;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ChatService {
	
	private HttpServletRequest request;
	private HttpSession session;
	private HttpServletResponse response;
	
	public ChatService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public String lnkclient(){
	
		return "/WebSocket/client.jsp";
	}
}
