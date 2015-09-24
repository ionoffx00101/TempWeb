<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>글쓰기</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="http://malsup.github.com/jquery.form.js"></script> 
<script type="text/javascript">
	 
	$(it);
	function it() {
		$('button[class=btn_write]').on('click', function() {
			var options = { 
			  beforeSend: function() 
			    {alert("보내기전");
			    },
			    uploadProgress: function(event, position, total, percentComplete) 
			    {
			    	alert("업로드");
			    },
			    success: function() 
			    {
			       alert("ㅇㅋ");
			    },
			    complete: function(response) 
			    {
			    	alert("컴플");
			    },
			    error: function()
			    {
			    	alert("에러");
			    }
			}; // end of options
			     $("#uploadForm").ajaxForm(options);
		});
		
	} 
</script>
</head>
<body>
<form id="uploadForm" action="Board?cmd=lnkpostWrite" method="post" enctype="multipart/form-data">
   <!-- <input type="hidden" name="cmd" value="lnkpostWrite"> -->
   제목 : <input type="text" name="title" required="required"><br>
   글쓴이 : <input type="text" name="author"><br>
   내용 : <textarea rows="3" cols="50" placeholder="이곳에 입력" name="content"></textarea><br>
     파일 : <input type="file" name="file"><br>
     
     <input type="submit" value="등록"><br>
</form><br>
<a href="Board?cmd=list"><button>목록보기</button></a>

</body>
</html>