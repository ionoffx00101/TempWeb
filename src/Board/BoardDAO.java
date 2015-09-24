package Board;

import java.sql.*;
import java.util.*;

public class BoardDAO extends AbstractDAO {

	public List<BoardVO> getAllBoard() {
		conn = getConn();
		String sql = "SELECT t1.*, TRUNC((ROWNUM-1)/10)+1 page FROM ( SELECT num,LPAD(' ', (LEVEL-1)*4,'¡¡') || DECODE(LEVEL,1,'','¦Æ ')||title as title,contents,wdate,author,hits,REF,ATTNUM,Filename FROM board START WITH ref='0' CONNECT BY PRIOR Num=Ref order siblings by num) t1";
		try {
			pstmt = conn.prepareStatement(sql);
			// pstmt.setInt(1, page);
			rs = pstmt.executeQuery();
			List<BoardVO> list = new ArrayList<>();
			while (rs.next()) {
				BoardVO vo = new BoardVO();
				vo.setNum(rs.getInt("NUM"));
				vo.setTitle(rs.getString("Title"));
				vo.setContents(rs.getString("Contents"));
				vo.setWdate(rs.getDate("Wdate"));
				vo.setAuthor(rs.getString("Author"));
				vo.setHits(rs.getInt("Hits"));
				vo.setRef(rs.getInt("Ref"));
				vo.setAttnum(rs.getInt("Attnum"));
				vo.setFilename(rs.getString("Filename"));
				list.add(vo);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return null;
	}

	public boolean daoPostsWrite(BoardVO vo) {

		conn = getConn();
		String sql = "insert into board (num,title,contents,wdate,author,hits,REF,ATTNUM) values (board_num_seq.nextval,?,?,SYSDATE,?,?,?,?)";
		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContents());
			pstmt.setString(3, vo.getAuthor());
			pstmt.setInt(4, vo.getHits());
			pstmt.setInt(5, vo.getRef());
			pstmt.setInt(6, vo.getAttnum());
			int rows = pstmt.executeUpdate();
			return rows > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}

		return false;

	}

	public boolean filePostsWrite(BoardVO vo) {

		conn = getConn();
		String sql = "insert into board (num,title,contents,wdate,author,hits,REF,ATTNUM,filename) values (board_num_seq.nextval,?,?,SYSDATE,?,?,?,?,?)";
		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContents());
			pstmt.setString(3, vo.getAuthor());
			pstmt.setInt(4, vo.getHits());
			pstmt.setInt(5, vo.getRef());
			pstmt.setInt(6, vo.getAttnum());
			pstmt.setString(7, vo.getFilename());
			int rows = pstmt.executeUpdate();
			return rows > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}

		return false;

	}

	public List<BoardVO> getPageBoard(int page) {
		conn = getConn();
		String sql = "SELECT * FROM ( SELECT t1.*, TRUNC((ROWNUM-1)/10)+1 page FROM ( SELECT num,LPAD(' ', (LEVEL-1)*4,'¡¡') || DECODE(LEVEL,1,'','¦Æ ')||title as title,contents,wdate,author,hits,REF,ATTNUM,Filename FROM board START WITH ref='0' CONNECT BY PRIOR Num=Ref order siblings by num DESC) t1) WHERE page=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, page);
			rs = pstmt.executeQuery();
			List<BoardVO> list = new ArrayList<>();
			while (rs.next()) {
				BoardVO vo = new BoardVO();
				vo.setNum(rs.getInt("NUM"));
				vo.setTitle(rs.getString("Title"));
				vo.setContents(rs.getString("Contents"));
				vo.setWdate(rs.getDate("Wdate"));
				vo.setAuthor(rs.getString("Author"));
				vo.setHits(rs.getInt("Hits"));
				vo.setRef(rs.getInt("Ref"));
				vo.setAttnum(rs.getInt("Attnum"));
				vo.setFilename(rs.getString("Filename"));
				list.add(vo);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return null;
	}

	public int getTotalCount() {
		conn = getConn();
		String sql = "SELECT count(*) FROM BOARD";
		int totalRows = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				totalRows = rs.getInt(1);
				return totalRows;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return 0;
	}
	
	public BoardVO getpost(int postnum) {
		conn = getConn();
		String sql = "SELECT * FROM board where num=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, postnum);
			rs = pstmt.executeQuery();
			BoardVO vo = new BoardVO();
			if (rs.next()) {
				vo.setNum(rs.getInt("NUM"));
				vo.setTitle(rs.getString("Title"));
				vo.setContents(rs.getString("Contents"));
				vo.setWdate(rs.getDate("Wdate"));
				vo.setAuthor(rs.getString("Author"));
				vo.setHits(rs.getInt("Hits"));
				vo.setRef(rs.getInt("Ref"));
				vo.setAttnum(rs.getInt("Attnum"));
				vo.setFilename(rs.getString("Filename"));
			}
			return vo;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return null;
	}
}
