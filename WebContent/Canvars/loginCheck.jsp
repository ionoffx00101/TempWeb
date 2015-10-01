<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String id=request.getParameter("id");
System.out.println("id"+id);
session.setAttribute("id", id);

%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<a href="NetDraw.jsp"> 그림그리기</a>
</body>
</html>