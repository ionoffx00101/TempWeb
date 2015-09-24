<%@page import="java.util.*"%>
<%@page import="Board.*"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- core를 c란 이름으로 쓰겠다 -->
<!DOCTYPE html>
<html>
<head>
<title>리스트</title>
<%
	List<BoardVO> list = (List<BoardVO>) request.getAttribute("postlist");
NavigationVO nvo = (NavigationVO) request.getAttribute("nvo");
%>
<c:set var="list" value="<%=list%>" scope="page"></c:set>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("button[class=btn_go_inputForm]").click(function() {
			location.href="Board?cmd=inputForm";
		});
		$('button[class=btn_Remove]').on('click',function(){
			var idx = $(this).attr('data-idx');
			$.ajax({
				type:'post',
				url:"Board?cmd=lnkpostListRemove",
				dataType:'json',
				data:{'idx':idx},
				success:function(res){
					if(res.remove){
						alert("ㅇㅋ");
					location.href="Board?cmd=list";
					}
					else{
						alert("에러 갔다오고");
					}
				},
				error:function(err){
					alert("에러");
				}
			});
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

a:link {text-decoration: none; color: #333333;}
a:visited {text-decoration: none; color: #333333;}
a:active {text-decoration: none; color: #333333;}
a:hover {text-decoration: none; color: blue;}
img {vertical-align: middle;}
.lefttd{text-align: left;width: 500px; }
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
	파일이름
	</td>
	<td>
	수정
	</td>
	<td>
	삭제
	</td>
	</tr>
	
	<c:forEach var="vo" items="${list}" begin="0" end="<%=list.size()%>" step="1" varStatus="status">
	<tr>
	<td>
	${vo.num} 
	</td>
	<td class="lefttd">
	<a href="Board?cmd=readpost&postnum=${vo.num}">${vo.title}</a>
	</td>
	<td>
	${vo.author} 
	</td>
	<td>
	${vo.wdate} 
	</td>
	<td>
	${vo.hits} 
	</td>
	<td>
	${vo.filename} 
	</td>
	<td>
	<button type ="button" class="btn_Update" data-idx="${status.index}">수정</button>
	</td>
	<td>
	<button class="btn_Remove" data-idx="${status.index}">삭제</button>
	</td>
	</tr>
</c:forEach>
	
	<%-- <%
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
	 <%if(vo.getFilename()==null){ %>
	파일 없음
	<% }else {%>
	<%=vo.getFilename() %>
	<% }%> 
	</td>
	</tr>
	<%if(vo.getAttnum()==0){ %>
	파일 없음
	<% }else {%>
	<%=vo.getAttnum()%> 
	<% }%>
	<% 	
	}
	%> --%>
	</table>
	<br>
	
	<%
		int[] nums = nvo.getLinks();
		if (nvo.isLeftMore()) {
	%>
	<!-- 왼쪽 이동 링크 -->
	<a href="Board?cmd=list&page=<%=nums[0] - 1%>"><img src="images/monotone_arrow_left_small.png"  width='30' ></a>
	<%
		}

		for (int i = 0; i < nums.length; i++) { // 네비게이션 링크
			int num = nums[i];
			if (nvo.getCurrPage() == num) {
	%>
	[
	<span style='color: red; font-size: 1.5em;'><%=num%></span>]
	<%
		} else {
	%>
	[
	<a href="Board?cmd=list&page=<%=num%>"><%=num%></a>]
	<%
		}
		}

		if (nvo.isRightMore()) {
	%>
	<!-- 오른쪽 이동 링크 -->
	<a href="Board?cmd=list&page=<%=nums[nums.length - 1] + 1%>"><img src="images/monotone_arrow_right.png" width='30' ></a>
	<%
		}
	%>
	
</body>
</html>