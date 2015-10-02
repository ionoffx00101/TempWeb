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
        System.out.println("Ŭ���̾�Ʈ ���ӵ� wsSession: "+session);
        System.out.println("������ ������ config :"+config);
        //Session:�����ڸ��� �Ѱ��� ������ �����Ǿ� ������ ��ż������� ����
        //�Ѱ��� ���������� �������� ���� ����ؼ� �����ϸ� Session�� ���� �ٸ����� HttpSession �� ������
         
        this.wsSession = session;
        this.config = config;
         
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.ctx = (ServletContext) config.getUserProperties().get(ServletContext.class.getName());
         
        System.out.println("������ ������ ����Ȯ�� httpSession :"+httpSession);
    }
     
    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last)
            throws IOException {
        System.out.println("�ؽ�Ʈ �޽��� ����:"+msg);
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
        System.out.println("Ŭ���̾�Ʈ-->���� ���̳ʸ� ������ ����");
        if (stream == null) {
            stream = session.getBasicRemote().getSendStream();
        }
  
        stream.write(msg);
        stream.flush();
        if (last) {
            stream.close();
            stream = null;
            System.out.println("����-->Ŭ���̾�Ʈ ���̳ʸ� ���ۿϷ�");
        }
    }
  
}