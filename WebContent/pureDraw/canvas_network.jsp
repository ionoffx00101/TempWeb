<%@page import="java.util.*"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    Object objList = application.getAttribute("usrList");
    List<String> usrList = null;
    if(objList!=null) usrList = (List<String>) objList;
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>네트워크 캔바스 예제</title>
<script type="text/javascript" src="jquery-2.1.4.min.js"></script>
<style type="text/css">
    body { margin:0px 0px; text-align: center; }
    canvas { display:inline-block; border: 1px solid black; }
</style>
<script type="text/javascript">
var clientId = '<%=(String) session.getAttribute("id")%>';
var Chat = {};
 
Chat.socket = null;
 
// connect() 함수 정의
Chat.connect = (function(host) {
    // 서버에 접속시도
    if ('WebSocket' in window) {
        Chat.socket = new WebSocket(host);
    } else if ('MozWebSocket' in window) {
        Chat.socket = new MozWebSocket(host);
    } else {
        Console.log('Error: WebSocket is not supported by this browser.');
        return;
    }
 
     // 서버에 접속이 되면 호출되는 콜백함수
    Chat.socket.onopen = function () {
        console.log('Info: WebSocket connection opened.');
    };
     
    // 연결이 끊어진 경우에 호출되는 콜백함수
    Chat.socket.onclose = function () {
        console.log('Info: WebSocket closed.');
    };
     
    // 서버로부터 메시지를 받은 경우에 호출되는 콜백함수
    Chat.socket.onmessage = function (message) {
        var jsonObj = eval('('+message.data+')');
        if('clear' in jsonObj) {
            clearCanvas();
        }
        else if('content' in jsonObj) {
            var jsonLine = jsonObj.content;
            drawLine(jsonLine);
        }
    };
});
    // connect() 함수 정의 끝
     
    // 위에서 정의한 connect() 함수를 호출하여 접속을 시도함
Chat.initialize = function() {
    if (window.location.protocol == 'http:') {
        //Chat.connect('ws://' + window.location.host + '/websocket/chat');
        Chat.connect('ws://192.168.8.19:8500/TempWeb/websocket/puredraw');
    } else {
        Chat.connect('wss://' + window.location.host + '/websocket/puredraw');
    }
};
 
// 서버로 메시지를 전송하고 입력창에서 메시지를 제거함
Chat.sendMessage = (function(jsonObj) {
 
    if (jsonObj != null) {
        Chat.socket.send(JSON.stringify(jsonObj));
    }
});
 
</script>
 
<script type="text/javascript">
var cnt = 0;
var ctx = null;
var x1=y1=x2=y2=0;
var isDrag = false;
var ptArr = new Array();
var timer = null;
var jsonStr = '';
 
$(function(){
    var $canvas = $('canvas').eq(0);
    ctx = $canvas[0].getContext("2d");
     
    $canvas.on('mousedown', function(evt){
        x1 = evt.pageX - this.offsetLeft;
        y1 = evt.pageY - this.offsetTop;
 
        isDrag = true;
    });
     
    $canvas.on('mouseup', function(evt){
        isDrag = false;
        if(ptArr.length>0) {
            Chat.sendMessage(ptArr);
            ptArr.splice(0,ptArr.length);
        }
    });
 
    $canvas.on('mousemove', function(evt){
        if(isDrag) {
            x2 = evt.pageX-this.offsetLeft;
            y2 = evt.pageY-this.offsetTop;
             
            var receiver = $('select[name=receiver]').val();
            var msg = {sender:clientId, receiver:receiver};
            msg.content = {x1:x1, y1:y1, x2:x2, y2:y2};
 
            Chat.sendMessage(msg);
             
            x1 = x2;
            y1 = y2;
        }
    });
     
    $('#btnClear').eq(0).on('click',function(){
        var receiver = $('select[name=receiver]').val();
        Chat.sendMessage({sender:clientId, receiver:receiver, clear:true});
    });
     
    Chat.initialize();
});
 
function drawLine(jsonLine) {
 
    ctx.strokeStyle = "#ff5533";
    ctx.lineJoin = "round";
    ctx.lineWidth = 5;
 
    ctx.beginPath();
 
    ctx.moveTo(jsonLine.x1, jsonLine.y1);
 
    ctx.lineTo(jsonLine.x2, jsonLine.y2);
     
    ctx.closePath();
    ctx.stroke();
}
 
function clearCanvas() {
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
}
 
</script>
</head>
<body>
    대화상대 선택<select id="receiver" name="receiver" style="vertical-align:top;" >
    <% 
        if(usrList!=null) {
            for(int i=0;i<usrList.size();i++) { %>
                <option><%=usrList.get(i)%></option>
    <%       } 
        }
    %>
    </select><br>
<canvas width="600" height="480"></canvas>
<button type="button" id='btnClear' style="vertical-align: top;">캔바스 지우기</button>
</body>
</html>