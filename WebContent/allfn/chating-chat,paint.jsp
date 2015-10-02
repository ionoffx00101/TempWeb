<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<%
    Object objList = application.getAttribute("usrList");
	String myid =(String)session.getAttribute("id");
    List<String> usrList = null;
    if(objList!=null) {
    	usrList=(List<String>)objList;
    	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>그리는 곳</title>
<style type="text/css">
input #chat {
	width: 410px
}

#console-container {
	width: 700px;
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
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="application/javascript">
			//로그인 과정이 필요하다
			var clientId = '<%=(String)session.getAttribute("id")%>'; 
			//메세지 보낼때마다 아이디가 심어짐
			
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
	                Console.log({msg:'Error: WebSocket is not supported by this browser.'});
	                return;
	            }
	
	             // 서버에 접속이 되면 자동으로 호출되는 콜백함수
	            Chat.socket.onopen = function () {
	                Console.log({msg:'Info: WebSocket connection opened.'});
	                // 채팅입력창에 메시지를 입력하기 위해 키를 누르면 호출되는 콜백함수
	                 document.getElementById('chat').onkeydown = function(event) {
	                    // 엔터키가 눌린 경우, 서버로 메시지를 전송함
	                    if (event.keyCode == 13) {
	                    	var receiver = $('select[name=receiver]').val();
	                        var message = document.getElementById('chat').value;
	                        Chat.sendMessage({sender:clientId, receiver:receiver, msg:message});
	                        document.getElementById('chat').value = '';
	                    }
	                }; 
	     
	            };
				
	            // 연결이 끊어진 경우에 호출되는 콜백함수
	            Chat.socket.onclose = function () {
	            	// 채팅 입력창 이벤트를 제거함
	                document.getElementById('chat').onkeydown = null;
	                Console.log({msg:'Info: WebSocket closed.'});
	            };
				
	            // 서버로부터 메시지를 받은 경우에 호출되는 콜백함수
	            Chat.socket.onmessage = function (message) {//메세지에 json이 오면 eval를 해주고  그냥 문자열만 온다면 기존처럼 해도된다
	            	
	            	 var jsonObj = eval('('+message.data+')');
	                 if('clear' in jsonObj) {
	                	 receiveRemove();
	                 }
	                 else if('content' in jsonObj) {
	                     var jsonLine = jsonObj.content;
	                     receiveDraw(jsonLine);
	                 }
	                 else if('msg' in jsonObj){
	                	 Console.log(jsonObj);
	                 }
	                 else if('file' in jsonObj){
	                	 saveData(jsonObj);
	                 }

	            };
	        });
	     	// connect() 함수 정의 끝
	     	
	     	// 위에서 정의한 connect() 함수를 호출하여 접속을 시도함
	        Chat.initialize = function() {
	            if (window.location.protocol == 'http:') {//웹브라우저의 주소창이 http냐

	            	Chat.connect('ws://192.168.8.19:8500/TempWeb/websocket/allfn');//서버쪽에 있는 웹소켓에 접근을 하겠다 
	            } else {
	                Chat.connect('wss://' + window.location.host + '/websocket/allfn');
	            }
	        };
	
	        // 서버로 메시지를 전송하고 입력창에서 메시지를 제거함
	        Chat.sendMessage = (function(jsonObj) {
	        	
	        	if (jsonObj != null) {
	                Chat.socket.send(JSON.stringify(jsonObj));
	            }
	        });
  			
  			
	        var Console = {}; // 화면에 메시지를 출력하기 위한 객체 생성
	
	        // log() 함수 정의
	        Console.log = (function(jsonObj) {

	            var console = document.getElementById('console');
	         
	            var p = document.createElement('p');
	            p.style.wordWrap = 'break-word';
	            p.innerHTML = jsonObj.sender+" : "+jsonObj.msg; 
	           /*  p.innerHTML = jsonObj.msg;  */
	            
	           /*  if(jsonObj.clear){receiveRemove(); return;}
	            //receiveDraw(jsonObj.x1,jsonObj.y1,jsonObj.x2,jsonObj.y2); */
	   
	            console.appendChild(p); // 전달된 메시지를 하단에 추가함
	            // 추가된 메시지가 25개를 초과하면 가장 먼저 추가된 메시지를 한개 삭제함
	            while (console.childNodes.length > 25) {//p태그가 25넘어가면 가장 앞의 p를 지운다
	                console.removeChild(console.firstChild);
	            }
	            // 스크롤을 최상단에 있도록 설정함
	            console.scrollTop = console.scrollHeight;
	        });
	        //접속자 명단을 출력하기 위한 객체 생성
	
	        // 위에 정의된 함수(접속시도)를 호출함
	       // Chat.initialize();
		$(function() { // 위에 정의한 모든 함수를 호출할수있지
			Chat.initialize();
		});	

/* canvas1 내거 드로잉함 */
var cnt =0;
var ctx = null;
var x1=y1=x2=y2=0;
var isDrag =false;

$(function() {
 	var $canvas = $('canvas').eq(0);
	ctx = $canvas[0].getContext("2d"); 
	
	$canvas.on('mousedown',function(evt){
		x1 = evt.pageX - this.offsetLeft;
		y1 = evt.pageY - this.offsetTop;
		 
		isDrag =true;
	});
	
	$canvas.on('mouseup',function(evt){
		isDrag=false;
		
	});
	
	$canvas.on('mousemove',function(evt){
		if(isDrag){
			x2 = evt.pageX - this.offsetLeft;
			y2 = evt.pageY - this.offsetTop;
			
			var receiver = $('select[name=receiver]').val();
            var msg = {sender:clientId, receiver:receiver};
            msg.content = {x1:x1, y1:y1, x2:x2, y2:y2};
 
            Chat.sendMessage(msg);
              
			drawLine(x1,y1,x2,y2);
			//sendmessage
			x1=x2;
			y1=y2;    
		}
	});
	$('#cleanAll').on('click',function(evt){
		
		var receiver = $('select[name=receiver]').val();
        Chat.sendMessage({sender:clientId, receiver:receiver, clear:true});

		ctx.clearRect(0, 0, canvas1.width, canvas1.height);
		
	});
});
function drawLine(x1,y1,x2,y2) {
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.strokeStyle = 1;
    ctx.lineWidth = 'black';
    ctx.stroke();
    ctx.closePath();
}

/* 캔버스 2 남의 것이 그려짐 */
var cnt_t =0;
var ctx_t = null;
var x1_t=y1_t=x2_t=y2_t=0;
var isDrag_t =false;

$(function() {
 	var $canvas_t = $('canvas').eq(1);
	ctx_t = $canvas_t[0].getContext("2d"); 
	
});
function receiveRemove() {
	ctx_t.clearRect(0, 0, canvas2.width, canvas2.height);
}
function receiveDraw(jsonLine) {
	
	ctx_t.beginPath();
    ctx_t.moveTo(jsonLine.x1, jsonLine.y1);
    ctx_t.lineTo(jsonLine.x2, jsonLine.y2);
    ctx_t.strokeStyle = 1;
   	ctx_t.lineWidth = 'black';
    ctx_t.stroke();
    ctx_t.closePath();
}

// 파일 저장
function sendBinary() {
    Chat.sendBinary();
}

Chat.sendBinary = (function() {
    // Blob, ArrayBuffer 둘 중 한가지 방법으로 전송
    sendFileBlob();
    //sendFileArrayBuffer();
});
// Blob 를 파일에 저장
function saveData(jsonObj) {
	
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";

    url = window.URL.createObjectURL(jsonObj.file);
    a.href = url;
    a.download = jsonObj.fileName;
    a.click();
    window.URL.revokeObjectURL(url);
};
 
 /* 파일 전송 */
//파일을 Blob를 서버로 전송함
function sendFileBlob() {
    // Sending file as Blob
    var file = document.querySelector('input[type="file"]').files[0];
    
    var receiver = $('select[name=receiver]').val();
    Chat.sendMessage({sender:clientId, receiver:receiver, file:file,filename:filename});
    
    //Chat.socket.send(file);
}

//파일박스에서 선택된 파일의 이름을 구하는 방법
var filename = '';
function onChange(files){
filename = files[0].name;
alert('선택변경:'+files[0].name);
}
</script>

</head>
<body>
내 이름 : <%=myid %> <br>
상대방 이름 :  <select name="receiver"> 
		<% // 사람이 들어올때마다 갱신하려면 웹소켓을 이용해야한다
		if(usrList!=null){
			for(int i=0;i<usrList.size();i++){
				%>
				<option><%=usrList.get(i) %></option>
				<%
			}
		}
		%>
		</select>
 <br>
 <br>
<!--  채팅 창 -->
	<div id="console-container">
			<div id="console" />
	</div>
		<!-- 파일 선택 -->
		<input type="file" onchange="onChange(this.files);">
		<input type="button" value="바이너리 데이터 전송" onclick="sendBinary();">
		<!-- 채팅입력 -->
		<p>
			<input type="text" placeholder="type and press enter to chat"
				id="chat" />
		</p>
	<br>
<!-- 캔버스 영역 -->
	<button id='cleanAll'>지우개</button>
 	<br><br>
	<canvas id="canvas1" width="250" height="250" style="border: 1px solid #cccccc; "></canvas>&lt;보내는거 
	받는거&rsaquo;
	<canvas id="canvas2" width="250" height="250" style="border: 1px solid #cccccc; "></canvas>
	
</body>
</html>