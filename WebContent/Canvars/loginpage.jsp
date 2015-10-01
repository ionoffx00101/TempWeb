<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<% request.setCharacterEncoding("utf-8"); 
session.setAttribute("login", true);
%>
<!DOCTYPE html>
<html>
<head>
<title>로그인</title>
</head>
<body>
<form action="loginCheck.jsp" >
<input type="hidden" name="cmd" value="user_ok">
	id : <input type="text" name ="id" id="id" value="7839"><br> 
	name : <input type="text" name ="name" id="name" value="KING"><br>
	<button type="submit" id="login">로그인</button>
</form>
</body>
</html>