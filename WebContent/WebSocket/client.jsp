<%@ page contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ page import="java.util.*" %>
    <%
    Object objList = application.getAttribute("usrList");
    List<String> usrList = null;
    if(objList!=null) usrList=(List<String>)objList;
    %>
<!DOCTYPE html>
<html>
<head>
<title>Apache Tomcat WebSocket Examples: Chat</title>
<style type="text/css">
input#chat {
	width: 410px
}

#console-container {
	width: 400px;
}

#console {
	border: 1px solid #CCCCCC;
	border-right-color: #999999;
	border-bottom-color: #999999;
	height: 170px;
	overflow-y: scroll;
	padding: 5px;
	width: 100%;
}

#console p {
	padding: 0;
	margin: 0;
}
</style>
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="application/javascript">
	

	        var Chat = {}; //빈오브젝트..json오브젝트..

	        
	        Chat.socket = null; //자바스크립트 오브젝트에는 내가 맘대로 속성을 넣을수있다.

	        // connect() 함수 정의
	        Chat.connect = (function(host) { // 브라우저에 따라 객체를 다른곳에서 받아야한다 윈도우 내에 있어서 윈도우에 그게 있는지 확인하고 있으면 불러온다. 표준완성이 덜되서 그렇다.
	        	//나중에 웹표준이 되면 한개만 불러와도 된다.. 지금은 어떤브라우저에서도 되려면 이렇게 해야한다. 이런걸 크로우브라우징코드라고 한다.
	            // 서버에 접속시도
	            if ('WebSocket' in window) { //익스랑 크롬에서는 websocket으로 말한다
	                Chat.socket = new WebSocket(host); //자바스크립트에 내장되어있는 웹소켓 api를 쓴다.
	            } else if ('MozWebSocket' in window) { // 모질라를 기반으로 한 브라우저는 mozwebsocket라고 부른다
	                Chat.socket = new MozWebSocket(host);
	            } else { //위에 둘다없으면 접속실패로 이게 된다.
	                Console.log('Error: WebSocket is not supported by this browser.');
	                return;
	            }
	
	             // 서버에 접속이 되면 자동으로 호출되는 콜백함수
	            Chat.socket.onopen = function () {
	                Console.log('Info: WebSocket connection opened.');
	                // 채팅입력창에 메시지를 입력하기 위해 키를 누르면 호출되는 콜백함수
	                document.getElementById('chat').onkeydown = function(event) {
	                    // 엔터키가 눌린 경우, 서버로 메시지를 전송함
	                    if (event.keyCode == 13) {
	                        Chat.sendMessage();
	                    }
	                };
	     
	            };
				
	            // 연결이 끊어진 경우에 호출되는 콜백함수
	            Chat.socket.onclose = function () {
	            	// 채팅 입력창 이벤트를 제거함
	                document.getElementById('chat').onkeydown = null;
	                Console.log('Info: WebSocket closed.');
	            };
				
	            // 서버로부터 메시지를 받은 경우에 호출되는 콜백함수
	            Chat.socket.onmessage = function (message) {
	            	// 수신된 메시지를 화면에 출력함
	                Console.log(message.data); 
	            };
	            //서버에 새 사람이 로그인을 했을때 호출되는 콜백함수.
	            
	            
	        });
	     	// connect() 함수 정의 끝
	     	
	     	// 위에서 정의한 connect() 함수를 호출하여 접속을 시도함
	        Chat.initialize = function() {
	            if (window.location.protocol == 'http:') {//웹브라우저의 주소창이 http냐
	                //Chat.connect('ws://' + window.location.host + '/websocket/chat');
	            //ws://ip주소:포트번호/프로젝트명...
	            //192.168.8.19:8500
	            	Chat.connect('ws://192.168.8.19:8500/TempWeb/websocket/chatbasic');//서버쪽에 있는 웹소켓에 접근을 하겠다 
	            	//	Chat.connect('ws://192.168.8.19:8500/TempWeb/websocket/chat'); //원하는 서버 주소를 넣어야함
	            } else {
	                Chat.connect('wss://' + window.location.host + '/websocket/chatbasic');
	            }
	        };
	
	        // 서버로 메시지를 전송하고 입력창에서 메시지를 제거함
	        Chat.sendMessage = (function() {
	        	var receiver = $('select[name=receiver]').val();
	            var message = document.getElementById('chat').value;
	            var msg = {sender:sender,receiver:receiver,message:message}
	            if (message != '') {
	                Chat.socket.send(message);
	                document.getElementById('chat').value = '';
	            }
	            //var selectMember = document.getElementById('member').value;
	            var selectMember = $('input:checkbox[name="loginmember"]').val(); //선택된 거 체크
	            //메세지랑 선택된 거 json으로 보내주면 될듯
	            //선택된 사람 없으면 all
	           //alert(selectMember);
	        });
	        
	   
	
	        var Console = {}; // 화면에 메시지를 출력하기 위한 객체 생성
	
	        // log() 함수 정의
	        Console.log = (function(message) {
	            var console = document.getElementById('console');
	            alert(message+"<기겅 ㄱ");
	            var p = document.createElement('p');
	           // var jsonObj = eval('('+받은 json문자열+')');
	            p.style.wordWrap = 'break-word';//단어단위로 쪼개라
	            p.innerHTML = message; //제이쿼리로는 p.html('message')
	            console.appendChild(p); // 전달된 메시지를 하단에 추가함
	            // 추가된 메시지가 25개를 초과하면 가장 먼저 추가된 메시지를 한개 삭제함
	            while (console.childNodes.length > 25) {
	                console.removeChild(console.firstChild);
	            }
	            // 스크롤을 최상단에 있도록 설정함
	            console.scrollTop = console.scrollHeight;
	        });
	        //접속자 명단을 출력하기 위한 객체 생성
	
	        // 위에 정의된 함수(접속시도)를 호출함
	       // Chat.initialize();
	$(function() {
		Chat.initialize();
	});

	    
</script>
</head>
<body>

	<div>
		<div id="console-container">
			<div id="console" />
		</div>
		<p>
			<input type="text" placeholder="type and press enter to chat"
				id="chat" />
		</p>
		<select name="receiver">
		<%
		if(usrList!=null){
			for(int i=0;i<usrList.size();i++){
				%>
				<option><%=usrList.get(i) %></option>
				<%
			}
		}
		%>
		</select>
	</div>
</body>
</html>