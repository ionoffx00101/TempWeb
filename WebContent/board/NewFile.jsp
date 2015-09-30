
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="Board.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.io.FilenameUtils"%>
<%
 request.setCharacterEncoding("utf-8");
BoardService svc = new BoardService(request,response);
BoardVO vo = new BoardVO();
AttachedVO avo = new AttachedVO();
boolean check= false;


 boolean isMultipart = ServletFileUpload.isMultipartContent(request);
 if (!isMultipart) {
 }else {
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);
    List<FileItem> items = null;
    try {
       items = upload.parseRequest(request);
    } catch (FileUploadException e) {
        //out.println("에러 1: "+e);
    }
    for(int i=0;i<items.size();i++) {
        FileItem item = (FileItem)items.get(i);
        if (item.isFormField()) { // 파일이 아닌 폼필드에 입력한 내용을 가져옴.
            if(item!=null && item.getFieldName().equals("title")) {
              String title = item.getString("utf-8");//form field 안에 입력한 데이터를 가져옴
              vo.setTitle(title);
              //out.println("전송자:"+name+"<br>"); 
            }else if(item!=null && item.getFieldName().equals("author")) {
              String author = item.getString("utf-8");
              //out.println("파일에 대한 설명:"+desc+"<br>");
              vo.setAuthor(author);
            }else if(item!=null && item.getFieldName().equals("content")) {
                String content = item.getString("utf-8");
                //out.println("파일에 대한 설명:"+desc+"<br>");
                vo.setContents(content);
              }
         } else { // 폼 필드가 아니고 파일인 경우
            try {
               String itemName = item.getName();//로컬 시스템 상의 파일경로 및 파일 이름 포함
 
               if(itemName==null || itemName.equals("")) continue;
               String fileName = FilenameUtils.getName(itemName);// 경로없이 파일이름만 추출함
               // 전송된 파일을 서버에 저장하기 위한 절차
               //String rootPath = getServletContext().getRealPath("/");
               File savedFile = new File("c:/upload/"+fileName); 
               item.write(savedFile);// 지정 경로에 파일을 저장함
                    
               avo.setOriginfn(fileName);
               check =svc.filepostsWrite(vo,avo);
               
               //out.println("<tr><td><b>파일저장 경로:</b></td></tr><tr><td><b>"+savedFile+"</td></tr>");
               //out.println("<tr><td><b><a href=\"DownloadServlet?file="+fileName+"\">"+fileName+"</a></td></tr>");
               
            } catch (Exception e) {
               //out.println("서버에 파일 저장중 에러: "+e);
            }
        } // end of else
    }
 } // end of else
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
}
%>