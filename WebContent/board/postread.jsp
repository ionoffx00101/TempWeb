<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@ page import="Board.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- core를 c란 이름으로 쓰겠다 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>글 상세보기</title>
<%
	BoardVO vo = (BoardVO) request.getAttribute("selectpost");
%>
<c:set var="vo" value="<%=vo%>" scope="request"></c:set>
</head>
<body>
<table>
<tr>
<td>제목
</td>
<td>
<input type="text" readonly value="${vo.title}"> 
</td>
</tr>
<tr>
<td>작성자
</td>
<td>
<input type="text" readonly value="${vo.author}"> 
</td>
</tr>
<tr>
<td>내용
</td>
<td>
<textarea rows="5" cols="30" readonly="readonly" >${vo.contents}</textarea>
</td>
</tr>
<tr>
<td>첨부파일
</td>
<td>
<a href="Board?cmd=filedownload&postnum=${vo.num}&filenum=${vo.attnum}">${vo.filename}</a>
</td>
</tr>
</table><br>
<a href="Board?cmd=list"><button>목록보기</button></a>
</body>
</html>