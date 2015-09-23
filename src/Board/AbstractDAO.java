package Board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public abstract class AbstractDAO {
	protected Connection conn;
	protected PreparedStatement pstmt;
	protected ResultSet rs;

	public Connection getConn() {
		
		Context initCtx;
		DataSource ds;
		Connection conn = null;
		try {
			initCtx = new InitialContext();
			ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/MyDataSource");//data source는 커넥션 풀에서 커넥션을 가져다줄수있다
			//System.err.println("DS :"+ds.toString());
			conn = ds.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		return conn;
	}

	public void closeAll() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
