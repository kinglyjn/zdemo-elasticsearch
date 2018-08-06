package test02.demo_tvcount;

import java.util.List;

/**
 * 数据导入HBase并向ElasticSearch添加索引
 * DataAndIndexUtil
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class DataAndIndexUtil {
	
	public void transportDataFromMysqlToHbaseAndElasticSearch() throws Exception {
		String sql = "select * from tvcount";
        List<TVCount> list = TvCountJdbcDao.queryData(sql);
    	
    	for (TVCount tvCount : list) {
    		TvCountHbaseDao.saveOrUpdate(tvCount);
    		TvCountEsDao.addIndex("tv", "tvcount", tvCount);
		}
	}
}
