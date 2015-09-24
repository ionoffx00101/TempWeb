package Board;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BoardService {
	
	private boolean DEBUG = true;
	private boolean leftMore;
	private boolean rightMore;
	private int pg = 1;
	private static int rowsPerPage = 5;
	private static int numsPerPage = 3;
	private static int totalPages;
	
	private HttpServletRequest request;
	private HttpSession session;
	private HttpServletResponse response;
	
	public BoardService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;

	}
	//lnk
	public String list(){
		BoardDAO dao = new BoardDAO();
		
		//List<BoardVO> postlist = dao.getAllBoard();
		
		String sPage = request.getParameter("page");
		if(sPage!=null) {
			pg = Integer.valueOf(sPage);
		}

		List<BoardVO> postlist = dao.getPageBoard(pg);
		//request.setAttribute("list", list);
		NavigationVO nvo = getNavVO();
		request.setAttribute("nvo", nvo);
		
		request.setAttribute("postlist", postlist);
		
		return "/board/list.jsp";
	}
	public String readpost(){
		
		BoardDAO dao = new BoardDAO();
		

		int postnum = Integer.parseInt(request.getParameter("postnum"));
		
		
		BoardVO selectpost = dao.getpost(postnum);
		
		request.setAttribute("selectpost", selectpost);
		
		return "/board/postread.jsp";
	}
	
	public String inputForm(){
		
		return "/board/inputForm.jsp";
	}
	public String lnkpostWrite(){

		return "/board/posts_write.jsp";
	}
	// logic
	public boolean postsWrite(){
		BoardDAO dao = new BoardDAO();
		BoardVO vo = new BoardVO();
		
		vo.setTitle(request.getParameter("title"));
		vo.setAuthor(request.getParameter("author"));
		vo.setContents(request.getParameter("content"));
		//file

		boolean check=dao.daoPostsWrite(vo);
		
		return check;
	}
	
	public boolean filepostsWrite(BoardVO vo,AttachedVO avo){
		BoardDAO dao = new BoardDAO();
		AttachedDAO adao = new AttachedDAO();
		
	    boolean fileok = adao.addFile(avo);

		int filenum=0;
		filenum = adao.getFilenum(avo.getSavedfn());
		
		boolean check=false;
		if(filenum>0){
		vo.setAttnum(filenum);
		vo.setFilename(avo.getOriginfn());
		//file
		}
		else{
			vo.setAttnum(0);
			vo.setFilename(null);
		}
		
		check=dao.filePostsWrite(vo);

		return check;
	}
	
	public String filedownload(){
		int postnum = Integer.parseInt(request.getParameter("postnum"));
		int filenum = Integer.parseInt(request.getParameter("filenum"));
		
		//���ϴٿ�ε� ����
		response.setContentType("application/octet-stream;charset=utf-8");
	
		   AttachedDAO dao = new AttachedDAO();
		   AttachedVO vo = dao.getFilename(filenum);
		   
		   String fileName = vo.getOriginfn();
		   String chfileName = vo.getSavedfn();
		   
		    System.out.println(fileName);
		    System.out.println(chfileName);
		   //�Ʒ�ó�� attachment �� ����ϸ� �������� ������ �ٿ�ε� â�� ���� ���ϸ��� �����ش�.
		   String utffileName = null;
		try {
			utffileName = new String(fileName.getBytes("utf-8"),"8859_1");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   response.setHeader("Content-Disposition", "attachment;filename="+utffileName+";");

		   ServletOutputStream sos = null;
		   try{
		    sos = response.getOutputStream();
		   }catch(Exception e){e.printStackTrace();}

		   //������ ���� ��Ʈ���� ���� �������� ����Ʈ �����͸� �������ָ� �ȴ�.
		   FileInputStream fio = null;
		   //chfileName = new String(chfileName.getBytes("8859_1"),"utf-8");
		   if(chfileName!=null){
			   fileName=chfileName;
		   }

		   File inFile = new File("C:/upload/"+fileName);
		   byte[] buf = null;
		   if(inFile.exists()) {
		      int len = (int)inFile.length();
		      buf = new byte[len];
		   }
		   try {
			fio = new FileInputStream("C:/upload/"+fileName);
		
		   fio.read(buf);
		   sos.write(buf);
		   sos.flush();
		   fio.close();
		   sos.close();
		   } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		//�ٽ� �������� ������
		BoardDAO bdao = new BoardDAO();
		BoardVO selectpost = bdao.getpost(postnum);
		request.setAttribute("selectpost", selectpost);
		
		return "/board/postread.jsp";
	}
	
 	private NavigationVO getNavVO() {
		NavigationVO nvo = new NavigationVO();
		nvo.setCurrPage(pg);
		nvo.setLinks(getNavLinks(pg, rowsPerPage, numsPerPage));
		nvo.setLeftMore(leftMore);
		nvo.setRightMore(rightMore);
		nvo.setTotalPage(totalPages);
		return nvo;
	}
	
	private int[] getNavLinks(int page, int rowsPerPage, int numsPerPage) {
		//System.out.printf("page=%d, RPP=%d, NPP=%d \n",page, rowsPerPage, numsPerPage);
		BoardDAO dao = new BoardDAO();
        
        int totalRows = dao.getTotalCount();
        
        totalPages = (totalRows-1)/rowsPerPage+1;
        //System.out.printf("%d�྿ �������� ��:%d \n", rowsPerPage, totalPages);
         
        int tmp = (page-1)/numsPerPage+1; // ���° ��ũ�׷쿡 ���ϴ°�?
        //System.out.printf("%d��° ��ũ �׷�",tmp);
        int end = tmp*numsPerPage;
        int start = (tmp-1)*numsPerPage+1;
         
        if(start==1) leftMore = false; // << ���� �̵� ��¿���
        else leftMore = true;
        if(end>=totalPages) {         // >> ������ �̵� ��¿���
            end = totalPages;
            rightMore = false;
        }else rightMore = true;
        //System.out.printf("START:%d~END:%d \n",start, end);
         
        int[] links = new int[end-start+1];
        for(int num=start,i=0;num<=end;num++,i++) {
            links[i] = num;
        }
        if(DEBUG) printLinks(links);
        return links;
    }

	// ����� ��¿�
    private void printLinks(int[] links) {
        if(leftMore);//System.out.print("<< ");
        for(int i=0;i<links.length;i++) {
           // System.out.printf("%d ", links[i]);
        }
        if(rightMore) ;//System.out.print(" >>");
       // System.out.println();
    }
}
