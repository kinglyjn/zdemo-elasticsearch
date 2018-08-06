package test02.demo_tvcount;

import java.util.List;

/**
 * TvCountQuery 根据ES索引或Hbase表的主键查询
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvCountQuery {
	
	/**
	 * multi match query by es index & get origin data from hbase
	 * 
	 */
	public static List<TVCount> multiMatch(String[] indices, String[] fields, String keyword, int from, int size) throws Exception {
		List<String> ids = TvCountEsDao.multiMatch(indices, fields, keyword, from, size);
		return TvCountHbaseDao.get(ids);
	}
	
}
