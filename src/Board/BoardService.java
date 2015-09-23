package Board;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BoardService {
	
	private HttpServletRequest request;
	private HttpSession session;
	private HttpServletResponse response;
	
	public BoardService(HttpServletRequest request) {
		this.request = request;

	}
	//lnk
	public String list(){
		BoardDAO dao = new BoardDAO();
		
		List<BoardVO> postlist = dao.getAllBoard();
	
		request.setAttribute("postlist", postlist);
		
		return "/board/list.jsp";
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
		vo.setFilename(avo.getOriginfn());
		//file
		boolean check=dao.filePostsWrite(vo);
		
		return check;
	}
}
