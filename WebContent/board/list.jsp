<%@page import="java.util.*"%>
<%@page import="Board.*"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<title>리스트</title>
<%
	List<BoardVO> list = (List<BoardVO>) request.getAttribute("postlist");
%>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("button[class=btn_go_inputForm]").click(function() {
			location.href="Board?cmd=inputForm";
		});
	});
</script>
<style type="text/css">
body {
	margin-top: 40px;
	text-align: center;
}
table {
	display: inline-block;
	border: 1px solid aqua;
}
td{background-color: #dddddd; padding: 10px;}
</style>
</head>
<body>
	<button type="button" class="btn_go_inputForm">글쓰기</button><br><br>
	
	<table>
	<tr>
	<td>
	글번호
	</td>
	<td>
	제목
	</td>
	<td>
	작성자
	</td>
	<td>
	작성일
	</td>
	<td>
	히트수
	</td>
	<td>
	파일넘버(현재 구현 X)
	</td>
	</tr>
	
	<%
	for(int i=0;i<list.size();i++){
	BoardVO vo = list.get(i);
	%>
	<tr>
	<td>
	<%=vo.getNum()%> 
	</td>
	<td>
	<%=vo.getTitle()%> 
	</td>
	<td>
	<%=vo.getAuthor()%> 
	</td>
	<td>
	<%=vo.getWdate()%> 
	</td>
	<td>
	<%=vo.getHits()%> 
	</td>
	<td>
	<%=vo.getFilename() %>
	<%-- <%if(vo.getAttnum()==0){ %>
	파일 없음
	<% }else {%>
	<%=vo.getAttnum()%> 
	<% }%> --%>
	</td>
	</tr>
	<% 	
	}
	%>
	</table>
</body>
</html>