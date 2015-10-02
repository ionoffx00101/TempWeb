<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
         
        <script type="application/javascript">
 
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
                    Console.log('Info: WebSocket connection opened.\n'+Chat.socket.extensions);
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
                Chat.socket.onmessage = function (evt) {
 
                    console.log('수신 message.data:'+evt.data);
                    if(evt.data instanceof Blob){
                        console.log('수신 데이터 타입:Blob');
                        saveData(evt.data, 'my-blob.txt');
                    }else if(evt.data instanceof ArrayBuffer){
                        console.log('수신 데이터 타입:ArrayBuffer');
                        saveData2(evt.data, 'my-arrayBuffer.txt');
                    }else {
                        Console.log(evt.data);
                        return;
                    }
                };
            });
            // connect() 함수 정의 끝
             
            // 위에서 정의한 connect() 함수를 호출하여 접속을 시도함
            Chat.initialize = function() {
                if (window.location.protocol == 'http:') {
                    //Chat.connect('ws://' + window.location.host + '/websocket/chat');
                    Chat.connect('ws://192.168.8.19:8500/TempWeb/websocket/echoStreamAnnotation');
                } else {
                    Chat.connect('wss://' + window.location.host + '/websocket/echoStreamAnnotation');
                }
            };
     
            // 서버로 메시지를 전송하고 입력창에서 메시지를 제거함
            Chat.sendMessage = (function() {
 
                var message = document.getElementById('chat').value;
                if (message != '') {
                    Chat.socket.send(message);
                    document.getElementById('chat').value = '';
                }
            });
             
            // 서버로 바이너리 데이터를 전송
            Chat.sendBinary = (function() {
                // Blob, ArrayBuffer 둘 중 한가지 방법으로 전송
                sendFileBlob();
                //sendFileArrayBuffer();
            });
 
            var Console = {}; // 화면에 메시지를 출력하기 위한 객체 생성
     
            // log() 함수 정의
            Console.log = (function(message) {
                var console = document.getElementById('console');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.innerHTML = message;
                console.appendChild(p); // 전달된 메시지를 하단에 추가함
                // 추가된 메시지가 25개를 초과하면 가장 먼저 추가된 메시지를 한개 삭제함
                while (console.childNodes.length > 25) {
                    console.removeChild(console.firstChild);
                }
                // 스크롤을 최상단에 있도록 설정함
                console.scrollTop = console.scrollHeight;
            });
     
            // 위에 정의된 함수(접속시도)를 호출함
            Chat.initialize();
             
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
// 파일박스에서 선택된 파일의 이름을 구하는 방법
var filename = '';
 function onChange(files){
     filename = files[0].name;
     alert('선택변경:'+files[0].name);
 }
        </script>
    </head>
    <body ><p>
    <div>
        <p>
            <input type="text" placeholder="type and press enter to chat" id="chat" />
        </p>
        <div id="console-container">
            <div id="console"></div>
        </div>
    </div>
    <input type="file" onchange="onChange(this.files);">
    <input type="button" value="바이너리 데이터 전송" onclick="sendBinary();">
    </body>
</html>