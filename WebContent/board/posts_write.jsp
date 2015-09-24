<%@page import="Board.*"%>
<%@page import="java.sql.Date"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.io.FilenameUtils"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="utf-8"/><!--  자바코드가 아닌 코어태그로 인코딩을 처리함 -->
<% /* 업로드 html에서 폼을 받으면 이곳이 실행이된다  request.get..으로 파일은 받을 수 없다*/
request.setCharacterEncoding("utf-8");
BoardService svc = new BoardService(request,response);
BoardVO vo = new BoardVO();
AttachedVO avo = new AttachedVO();

boolean check= false;

 boolean isMultipart = ServletFileUpload.isMultipartContent(request);
 if (!isMultipart) { //파일 보내야지 실행하겠다 
 }else {
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);
    List items = null;
    String changedFn ="";
    try {
       items = upload.parseRequest(request);
    } catch (FileUploadException e) {
     //out.println("에러 1: "+e);
    } // 위는 파일 업로드에 꼭 필요한 로직이다.
    Iterator itr = items.iterator(); // items 는 List 이므로 리스트 다루는 방법으로 해도 된다
    while (itr.hasNext()) {
      FileItem item = (FileItem) itr.next();
      if (item.isFormField()) { // 파일이 아닌 폼필드에 입력한 내용을 가져옴.
        if(item!=null && item.getFieldName().equals("title")) {
          String title = item.getString("utf-8");//form field 안에 입력한 데이터를 가져옴
          vo.setTitle(title);
        }else if(item!=null && item.getFieldName().equals("author")) {
          String author = item.getString("utf-8");
          vo.setAuthor(author);
        }else if(item!=null && item.getFieldName().equals("content")) {
            String content = item.getString("utf-8");
            vo.setContents(content);
         }
     } else { // 폼 필드가 아니고 파일인 경우
    	 
    try {

       String itemName = item.getName();//로컬 시스템 상의 파일경로 및 파일 이름 포함
       System.out.println(itemName+"123");
       if(itemName==null || itemName.equals("")){} //continue;
       else{  String fileName = FilenameUtils.getName(itemName);// 경로없이 파일이름만 추출함
       // 전송된 파일을 서버에 저장하기 위한 절차
       //String rootPath = getServletContext().getRealPath("/");
       File savedFile = new File("C:/upload/"+fileName); 
       //여기 파일 네임은 진짜 파일 네임  파일 번호를 가지고 진짜 이름을 찾아와야한다.
       if(savedFile.exists()){//파일 중복 검사
    	
    	String orginFn = fileName; //원래 파일
    	changedFn = fileName+" "+new java.util.Date().getTime(); //가짜이름
    	avo.setOriginfn(fileName);
        avo.setSavedfn(changedFn);
    	//두개를 디비에 보관한다.
    	savedFile = new File("C:/upload/"+changedFn); 
       }
       item.write(savedFile);// 지정 경로에 파일을 저장함
       }
       String orginFn = FilenameUtils.getName(itemName);
       Long len = item.getSize();
       avo.setLen(len);
       
     
       
   	   check =svc.filepostsWrite(vo,avo);
   	   
      

    } catch (Exception e) {
       //out.println("서버에 파일 저장중 에러: "+e);
      }
   }
  }
 } 
%>
<%-- {"check":"<%=check%>"} --%>
<% 
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
}%>
