package org.kdea.java.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
  
 
 
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
@ServerEndpoint(value="/websocket/echoStreamAnnotation", configurator=ServletAwareConfig.class)
public class EchoStreamAnnotation {
  
    Writer writer;
    OutputStream stream;
    private EndpointConfig config;
    private Session wsSession;
 
    private HttpSession httpSession;
    private ServletContext ctx;
     
    @OnOpen
    public void start(Session session, EndpointConfig config) {
        System.out.println("클라이언트 접속됨 wsSession: "+session);
        System.out.println("웹소켓 서버측 config :"+config);
        //Session:접속자마다 한개의 세션이 생성되어 데이터 통신수단으로 사용됨
        //한개의 브라우저에서 여러개의 탭을 사용해서 접속하면 Session은 서로 다르지만 HttpSession 은 동일함
         
        this.wsSession = session;
        this.config = config;
         
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.ctx = (ServletContext) config.getUserProperties().get(ServletContext.class.getName());
         
        System.out.println("웹소켓 서버측 세션확인 httpSession :"+httpSession);
    }
     
    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last)
            throws IOException {
        System.out.println("텍스트 메시지 도착:"+msg);
        if (writer == null) {
            writer = session.getBasicRemote().getSendWriter();
        }
        writer.write(msg);
        writer.flush();
        if (last) {
            writer.close();
            writer = null;
        }
    }
  
  
    @OnMessage
    public void echoBinaryMessage(byte[] msg, Session session, boolean last)
            throws IOException {
        System.out.println("클라이언트-->서버 바이너리 데이터 도착");
        if (stream == null) {
            stream = session.getBasicRemote().getSendStream();
        }
  
        stream.write(msg);
        stream.flush();
        if (last) {
            stream.close();
            stream = null;
            System.out.println("서버-->클라이언트 바이너리 전송완료");
        }
    }
  
}