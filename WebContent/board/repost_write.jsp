<%@page import="Board.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="utf-8"/><!--  자바코드가 아닌 코어태그로 인코딩을 처리함 -->
<% /* 업로드 html에서 폼을 받으면 이곳이 실행이된다  request.get..으로 파일은 받을 수 없다*/
request.setCharacterEncoding("utf-8");
BoardService svc = new BoardService(request,response);
BoardVO vo = new BoardVO();
boolean check = true;
String title=(String)request.getAttribute("title");

System.out.println("여기"+title);

System.out.println(check);
%>
{"check":"<%=check%>"}
<%-- <% 
if(check){
	%>
	<script type="text/javascript">
alert("저장완료");
location.href="Board?cmd=list";
</script>
	<%
}else{
%><script type="text/javascript">
alert("저장실패");
location.href="Board?cmd=inputForm";
</script>
	<%
}%> --%>
