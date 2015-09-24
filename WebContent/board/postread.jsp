<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@ page import="Board.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- core�� c�� �̸����� ���ڴ� -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>�� �󼼺���</title>
<%
	BoardVO vo = (BoardVO) request.getAttribute("selectpost");
%>
<c:set var="vo" value="<%=vo%>" scope="request"></c:set>
</head>
<body>
<table>
<tr>
<td>����
</td>
<td>
<input type="text" readonly value="${vo.title}"> 
</td>
</tr>
<tr>
<td>�ۼ���
</td>
<td>
<input type="text" readonly value="${vo.author}"> 
</td>
</tr>
<tr>
<td>����
</td>
<td>
<textarea rows="5" cols="30" readonly="readonly" >${vo.contents}</textarea>
</td>
</tr>
<tr>
<td>÷������
</td>
<td>
<a href="Board?cmd=filedownload&postnum=${vo.num}&filenum=${vo.attnum}">${vo.filename}</a>
</td>
</tr>
</table><br>
<a href="Board?cmd=list"><button>��Ϻ���</button></a>
</body>
</html>