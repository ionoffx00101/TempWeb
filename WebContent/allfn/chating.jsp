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

			var clientId = '<%=(String)session.getAttribute("id")%>'; 

	        var Chat = {}; 

	        Chat.socket = null; 

	        Chat.connect = (function(host) { 
	            if ('WebSocket' in window) { 
	                Chat.socket = new WebSocket(host); 
	            } else if ('MozWebSocket' in window) { 
	                Chat.socket = new MozWebSocket(host);
	            } else { 
	                Console.log({msg:'Error: WebSocket is not supported by this browser.'});
	                return;
	            }
	
	            Chat.socket.onopen = function () {
	                Console.log({msg:'Info: WebSocket connection opened.'});
	                 document.getElementById('chat').onkeydown = function(event) {
	                    if (event.keyCode == 13) {
	                    	var receiver = $('select[name=receiver]').val();
	                        var message = document.getElementById('chat').value;
	                        Chat.sendMessage({sender:clientId, receiver:receiver, msg:message});
	                        document.getElementById('chat').value = '';
	                    }
	                }; 
	            };
		
	            Chat.socket.onclose = function () {
	                document.getElementById('chat').onkeydown = null;
	                Console.log({msg:'Info: WebSocket closed.'});
	            };
				

				Chat.socket.onmessage = function (evt) {
	            		   
	                       console.log({msg:'수신 message.data:'+evt.data});
	                       if(evt.data instanceof Blob){
	                           console.log({msg:'수신 데이터 타입:Blob'});
	                           saveData(evt.data, 'my-blob.txt');
	                       }else if(evt.data instanceof ArrayBuffer){
	                           console.log({msg:'수신 데이터 타입:ArrayBuffer'});
	                           saveData2(evt.data, 'my-arrayBuffer.txt');
	                       }else {
	                    	   var jsonObj = eval('('+evt.data+')');
	      	            	 
	      	                 if('clear' in jsonObj) {//o
	      	                	 receiveRemove();
	      	                 }
	      	                 else if('content' in jsonObj) {//o
	      	                     var jsonLine = jsonObj.content;
	      	                     receiveDraw(jsonLine);
	      	                 }
	      	                 else if('msg' in jsonObj){//o
	      	                	 Console.log(jsonObj);
	      	                 }
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
//==========================================================v	
		Chat.sendBinary = (function() {
                // Blob, ArrayBuffer 둘 중 한가지 방법으로 전송
                sendFileBlob();
                //sendFileArrayBuffer();
            });
//==========================================================
  			
	        var Console = {}; 
	
	        Console.log = (function(jsonObj) {

	            var console = document.getElementById('console');
	         
	            var p = document.createElement('p');
	            p.style.wordWrap = 'break-word';
	            p.innerHTML = jsonObj.sender+" : "+jsonObj.msg; 
	        
	            console.appendChild(p); 
	            while (console.childNodes.length > 25) {
	                console.removeChild(console.firstChild);
	            }
	         
	            console.scrollTop = console.scrollHeight;
	        });
		$(function() { 
			Chat.initialize();
		});	
		
//==========================================================v	
		 function sendBinary() {
             Chat.sendBinary();
         }
//==========================================================		 
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
//==========================================================v
	
	  function sendBinary() {
                Chat.sendBinary();
            }
             
            // Blob 를 파일에 저장
            function saveData(blob, fileName) {

                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
 
                url = window.URL.createObjectURL(blob);
                a.href = url;
                a.download = fileName;
                a.click();
                window.URL.revokeObjectURL(url);
            };
             
            // ArrayBuffer 를 파일에 저장
            function saveData2(arrayBuffer, fileName) {
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
                var parts = [];
                parts.push(arrayBuffer);
                url = window.URL.createObjectURL(new Blob(parts));
                a.href = url;
                a.download = fileName;
                a.click();
                window.URL.revokeObjectURL(url);
            };
             
           //canvas의 이미지 데이터를 서버로 전송하는 예
           function sendImgArrayBuffer(){
                // Sending canvas ImageData as ArrayBuffer
                var img = canvas_context.getImageData(0, 0, 400, 320);
                var binary = new Uint8Array(img.data.length);
                for (var i = 0; i < img.data.length; i++) {
                  binary[i] = img.data[i];
                }
                Chat.socket.send(binary.buffer);
            };
             
            //파일을 Blob를 서버로 전송함
            function sendFileBlob() {
                // Sending file as Blob
                var file = document.querySelector('input[type="file"]').files[0];
                Chat.socket.send(file);
            }
             
            //파일을 ArrayBuffer를 서버로 전송함
            function sendFileArrayBuffer() {
                var file = document.querySelector('input[type="file"]').files[0];
                var fileReader = new FileReader();
                fileReader.onload = function() {
                    arrayBuffer = this.result;
                    Chat.socket.send(arrayBuffer);
                };
                fileReader.readAsArrayBuffer(file);
            }
	
	
//==========================================================


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