<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="Board.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- core를 c란 이름으로 쓰겠다 -->
<% 
BoardVO parentvo = new BoardVO();
parentvo = (BoardVO)request.getAttribute("repostparent");

%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="utf-8"/><!--  자바코드가 아닌 코어태그로 인코딩을 처리함 -->
<c:set var="parent" value="<%=parentvo%>" scope="request"></c:set>
<!DOCTYPE html>
<html>
<head>
<title>답글달기</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>
	<form id="form" action="Board?">
		<input type="hidden" name="cmd" value="rePostWrite">
		<input type="hidden" name="num" value="${parent.num}">
		<input type="hidden" name="title" value="re : ${parent.title}">
		
		<h2>re : ${parent.title}</h2><br>
		
		글쓴이 : <input type="text" name="author"><br>
		내용 :<textarea rows="3" cols="50" placeholder="이곳에 입력" name="contents"></textarea>
		
		<br>
		
		<button type ="submit" class="btn_write">답글쓰기</button>
		<a href="Board?cmd=readpost&postnum=${parent.num}"><button type ="button" class="btn_cancel">취소</button></a>
		<a href="Board?cmd=readpost&postnum=${parent.num}"><button type ="button" class="btn_cancel">원래글 보기</button></a>
		<a href="Board?cmd=list"><button type ="button" class="btn_list">글목록</button></a>
	</form>
</body>
</html>