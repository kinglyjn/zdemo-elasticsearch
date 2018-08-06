package test02.demo_tvcount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JdbcUtil
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvCountJdbcDao {
	private static String DRIVER = "com.mysql.jdbc.Driver";
	private static String URL = "jdbc:mysql://192.168.1.96:3306/tv?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";
	private static String USERNAME = "zhangqingli";
	private static String PASSWORD = "qweasd";

	/**
	 * 连接数据库
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		if (conn == null) {
			try {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	/**
	 * 插入数据
	 * 
	 */
	public static void insertData(TVCount tvCount) throws SQLException {

		String sql = "insert into tv.tvcount(tvname,director,actor,allnumber,tvtype,description,tvid,alias,tvshow,present,score,zone,commentnumber,supportnumber,pic) "
				+ "values ('" + tvCount.getTvname() + "','" + tvCount.getDirector() + "','" + tvCount.getActor() + "','" + tvCount.getAllnumber() + "','" + tvCount.getTvtype() + "','"
				+ tvCount.getDescription() + "','" + tvCount.getTvid() + "','" + tvCount.getAlias() + "','" + tvCount.getTvshow() + "','" + tvCount.getPresent() + "','" + tvCount.getScore() + "','"
				+ tvCount.getZone() + "','" + tvCount.getCommentnumber() + "','" + tvCount.getSupportnumber() + "','" + tvCount.getPic() + "')";
		System.out.println(sql);

		Connection conn = getConnection();
		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			stmt.close();
			conn.close();
		}
	}

	/**
	 * 查询数据
	 * 
	 */
	public static List<TVCount> queryData(String sql) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = null;
		ArrayList<TVCount> arrayList = new ArrayList<TVCount>();
		try {
			stmt = conn.createStatement();
			ResultSet set = stmt.executeQuery(sql);

			while (set.next()) {
				TVCount tv = new TVCount();
				tv.setTvid(set.getString("tvid"));
				tv.setTvname(set.getString("tvname"));
				tv.setDirector(set.getString("director"));
				tv.setActor(set.getString("actor"));
				tv.setAllnumber(set.getString("allnumber"));
				tv.setTvtype(set.getString("tvtype"));
				tv.setDescription(set.getString("description"));
				tv.setAlias(set.getString("alias"));
				tv.setTvshow(set.getString("tvshow"));
				tv.setPresent(set.getString("present"));
				tv.setScore(set.getString("score"));
				tv.setZone(set.getString("zone"));
				tv.setCommentnumber(set.getString("commentnumber"));
				tv.setSupportnumber(set.getString("supportnumber"));
				tv.setPic(set.getString("pic"));
				arrayList.add(tv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			stmt.close();
			conn.close();
		}
		return arrayList;
	}
}
