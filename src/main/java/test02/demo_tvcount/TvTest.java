package test02.demo_tvcount;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

/**
 * TvTest
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvTest {
	
	/**
	 * mysql test
	 * 
	 */
	@Test
	public void test01() throws SQLException {
		List<TVCount> list = TvCountJdbcDao.queryData("select * from tv.tvcount");
		list.forEach(v -> System.out.println(v));
	}
	
	/**
	 * grabbing data from youku & sink objs to mysql
	 * 
	 */
	@Test
	public void test02() throws Exception {
		List<String> list = new LinkedList<>();
		list.add("http://list.youku.com/show/id_z3a85e68cbc1f11e6bdbb.html");
        list.add("http://list.youku.com/show/id_ze4d19b7a424f11e6b32f.html");
        list.add("http://list.youku.com/show/id_z2a4578e283a211e5b2ad.html");
        list.add("http://list.youku.com/show/id_zfb07f6438e9611e6b16e.html");
        list.add("http://list.youku.com/show/id_z7bd563d8a56811e5be16.html");
        list.add("http://list.youku.com/show/id_z23b897b8296d11e6b522.html");
        list.add("http://list.youku.com/show/id_zd1352cf69a5011e6b9bb.html");
        list.add("http://list.youku.com/show/id_z948f6a06531711e6b32f.html");
        list.add("http://list.youku.com/show/id_zcbffdabe962411de83b1.html");
        list.add("http://list.youku.com/show/id_zc6235132314c11e6abda.html");
        list.add("http://list.youku.com/show/id_za77357b5b53f11e69e06.html");
        list.add("http://list.youku.com/show/id_z96deec12799211e6bdbb.html");
        list.add("http://list.youku.com/show/id_z1c275062779211e0a046.html");
        list.add("http://list.youku.com/show/id_zcbffe9d2962411de83b1.html");
        list.add("http://list.youku.com/show/id_zdfd77502ad3611e68ce4.html");
        list.add("http://list.youku.com/show/id_zcc07361a962411de83b1.html");
        list.add("http://list.youku.com/show/id_ze46186b0f71711e5a080.html");
        list.add("http://list.youku.com/show/id_zdc3eb942cd9511e69c81.html");
        list.add("http://list.youku.com/show/id_z6bb14b2275f611e4b2ad.html");
        list.add("http://list.youku.com/show/id_zcbfb7e6a962411de83b1.html");
        list.add("http://list.youku.com/show/id_zd56886dc86fc11e3a705.html");
        list.add("http://list.youku.com/show/id_z0af300b2ed2c11e3b8b7.html");
        list.add("http://list.youku.com/show/id_z20f07d85951011e6bdbb.html");
        list.add("http://list.youku.com/show/id_z878b8396a09811e69e06.html");
        list.add("http://list.youku.com/show/id_zcbffd26c962411de83b1.html");
		List<TVCount> tvCounts = TvCountGrabberAndParser.grabbingAndParsing(list);
		for (TVCount tvCount : tvCounts) {
			TvCountJdbcDao.insertData(tvCount);
		}
	}
	
	/**
	 * transport data from mysql to hbase and index with es.
	 * 
	 */
	@Test
	public void test03() throws Exception {
		List<TVCount> list = TvCountJdbcDao.queryData("select * from tv.tvcount");
		for (TVCount tvCount : list) {
			TvCountHbaseDao.saveOrUpdate(tvCount);
			TvCountEsDao.addIndex("tv", "tvcount", tvCount);
		}
	}
	
	/**
	 * hbase test
	 * 
	 */
	@Test
	public void test04() throws Exception {
		List<TVCount> list = TvCountHbaseDao.scan();
		list.forEach(v -> System.out.println(v));
	}
	
	
	/**
	 * es test
	 * 
	 */
	@Test
	public void test05() {
		List<String> list = TvCountEsDao.multiMatch(new String[]{"tv"}, new String[] {"tvname"}, "剧集", 0, 10);
		if (list!=null) {
			list.forEach(v -> System.out.println(v));
		}
	}
	
	
	/**
	 * multi match query by es index & get origin data from hbase
	 * 
	 */
	@Test
	public void test06() throws Exception {
		List<TVCount> tvCounts = TvCountQuery.multiMatch(new String[]{"tv"}, new String[]{"tvtype"}, "古装哈哈", 0, 10);
		if (tvCounts != null) {
			tvCounts.forEach(v -> System.out.println(v));
		}
	}
	
}
