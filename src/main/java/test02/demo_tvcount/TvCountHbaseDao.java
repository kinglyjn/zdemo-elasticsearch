package test02.demo_tvcount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * TvCountHbaseDao
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvCountHbaseDao {
	private static final String ENCRIPT_ALGORITHM = "MD5";
	
	private static final String TABLE_NAME = "tv:tvcount";
	private static final byte[] FAMILY = Bytes.toBytes("tvinfo");
	private static final byte[] COLUMN_TVID = Bytes.toBytes("tvid");
	private static final byte[] COLUMN_TVNAME = Bytes.toBytes("tvname");
	private static final byte[] COLUMN_DIRECTOR = Bytes.toBytes("director");
	private static final byte[] COLUMN_ACTOR = Bytes.toBytes("actor");
	private static final byte[] COLUMN_ALLNUMBER = Bytes.toBytes("allnumber");
	private static final byte[] COLUMN_TVTYPE = Bytes.toBytes("tvtype");
	private static final byte[] COLUMN_DESCRIPTION = Bytes.toBytes("description");
	private static final byte[] COLUMN_ALIAS = Bytes.toBytes("alias");
	private static final byte[] COLUMN_TVSHOW = Bytes.toBytes("tvshow");
	private static final byte[] COLUMN_PRESENT = Bytes.toBytes("present");
	private static final byte[] COLUMN_SCORE = Bytes.toBytes("score");
	private static final byte[] COLUMN_ZONE = Bytes.toBytes("zone");
	private static final byte[] COLUMN_COMMENTNUMBER = Bytes.toBytes("commentnumber");
	private static final byte[] COLUMN_SUPPORTNUMBER = Bytes.toBytes("supportnumber");
	private static final byte[] COLUMN_PIC = Bytes.toBytes("pic");
	
	
	/**
	 * 初始化put
	 * 
	 */
	private static Put initPut(TVCount tvCount) {
		String rowkey = StringUtils.encrypt(tvCount.getTvid(), "MD5");
		Put put = new Put(Bytes.toBytes(rowkey));
		
		put.addColumn(FAMILY, COLUMN_TVID, Bytes.toBytes(tvCount.getTvid()));
		put.addColumn(FAMILY, COLUMN_TVNAME, Bytes.toBytes(tvCount.getTvname()));
		put.addColumn(FAMILY, COLUMN_DIRECTOR, Bytes.toBytes(tvCount.getDirector()));
		put.addColumn(FAMILY, COLUMN_ACTOR, Bytes.toBytes(tvCount.getActor()));
		put.addColumn(FAMILY, COLUMN_ALLNUMBER, Bytes.toBytes(tvCount.getAllnumber()));
		put.addColumn(FAMILY, COLUMN_TVTYPE, Bytes.toBytes(tvCount.getTvtype()));
		put.addColumn(FAMILY, COLUMN_DESCRIPTION, Bytes.toBytes(tvCount.getDescription()));
		put.addColumn(FAMILY, COLUMN_ALIAS, Bytes.toBytes(tvCount.getAllnumber()));
		put.addColumn(FAMILY, COLUMN_TVSHOW, Bytes.toBytes(tvCount.getTvshow()));
		put.addColumn(FAMILY, COLUMN_PRESENT, Bytes.toBytes(tvCount.getPresent()));
		put.addColumn(FAMILY, COLUMN_SCORE, Bytes.toBytes(tvCount.getScore()));
		put.addColumn(FAMILY, COLUMN_ZONE, Bytes.toBytes(tvCount.getZone()));
		put.addColumn(FAMILY, COLUMN_COMMENTNUMBER, Bytes.toBytes(tvCount.getCommentnumber()));
		put.addColumn(FAMILY, COLUMN_SUPPORTNUMBER, Bytes.toBytes(tvCount.getSupportnumber()));
		put.addColumn(FAMILY, COLUMN_PIC, Bytes.toBytes(tvCount.getPic()));
		
		return put;
	}
	
	/**
	 * 初始化 pojo
	 * 
	 */
	private static TVCount initTvCount(Result result) {
		if (result.isEmpty()) {
			return null;
		}
		String tvid = Bytes.toString(result.getValue(FAMILY, COLUMN_TVID));
		String tvname = Bytes.toString(result.getValue(FAMILY, COLUMN_TVNAME));
		String director = Bytes.toString(result.getValue(FAMILY, COLUMN_DIRECTOR));
		String actor = Bytes.toString(result.getValue(FAMILY, COLUMN_ACTOR));
		String allnumber = Bytes.toString(result.getValue(FAMILY, COLUMN_ALLNUMBER));
		String tvtype = Bytes.toString(result.getValue(FAMILY, COLUMN_TVTYPE));
		String description = Bytes.toString(result.getValue(FAMILY, COLUMN_DESCRIPTION));
		String alias = Bytes.toString(result.getValue(FAMILY, COLUMN_ALIAS));
		String tvshow = Bytes.toString(result.getValue(FAMILY, COLUMN_TVSHOW));
		String present = Bytes.toString(result.getValue(FAMILY, COLUMN_PRESENT));
		String score = Bytes.toString(result.getValue(FAMILY, COLUMN_SCORE));
		String zone = Bytes.toString(result.getValue(FAMILY, COLUMN_ZONE));
		String commentnumber = Bytes.toString(result.getValue(FAMILY, COLUMN_COMMENTNUMBER));
		String supportnumber = Bytes.toString(result.getValue(FAMILY, COLUMN_SUPPORTNUMBER));
		String pic = Bytes.toString(result.getValue(FAMILY, COLUMN_PIC));
		return new TVCount(tvname, director, actor, 
				allnumber, tvtype, description, 
				tvid, alias, tvshow, 
				present, score, zone, 
				commentnumber, supportnumber, pic);
	}
	
	
	/**
	 * 增
	 * 
	 */
	public static void saveOrUpdate(TVCount tvCount) throws Exception {
		System.err.println("TvCountHbaseDao#saveOrUpdate: " + tvCount);
		Put put = initPut(tvCount);
		HbaseTableUtils.putOne(TABLE_NAME, put);
	}
	
	public static void saveOrUpdate(List<TVCount> tvCounts) throws Exception {
		System.err.println("TvCountHbaseDao#saveOrUpdate: " + tvCounts);
		List<Put> puts = new ArrayList<Put>();
		for (TVCount tvCount : tvCounts) {
			Put put = initPut(tvCount);
			puts.add(put);
		}
		HbaseTableUtils.putList(TABLE_NAME, puts);
	}
	
	/**
	 * 删
	 * 
	 */
	public static void delete(TVCount tvCount) throws Exception {
		System.err.println("TvCountHbaseDao#delete: " + tvCount);
		String rowkey = StringUtils.encrypt(tvCount.getTvid(), ENCRIPT_ALGORITHM);
		Delete delete = new Delete(Bytes.toBytes(rowkey));
		HbaseTableUtils.deleteRow(TABLE_NAME, delete);
	}
	
	/**
	 * 查
	 * 
	 */
	public static List<TVCount> get(List<String> rowkeys) throws Exception {
		long time1 = System.currentTimeMillis();
		
		if (rowkeys==null) {
			return null;
		}
		List<Get> gets = new LinkedList<Get>();
		for (String rowkey : rowkeys) {
			gets.add(new Get(Bytes.toBytes(rowkey)));
		}
		Result[] results = HbaseTableUtils.get(TABLE_NAME, gets);
		List<TVCount> tvCounts = new LinkedList<>();
		for (Result result : results) {
			TVCount tvCount = initTvCount(result);
			tvCounts.add(tvCount);
		}
		
		long time2 = System.currentTimeMillis();
		System.err.println("Hbase查询耗时：" + (time2-time1) + " ms.");
		return tvCounts;
	}
	public static List<TVCount> scan() throws Exception {
		Scan scan = new Scan();
		ResultScanner scanner = HbaseTableUtils.scan(TABLE_NAME, scan);
		List<TVCount> list = new ArrayList<TVCount>();
		for (Result result : scanner) {
			TVCount tvCount = initTvCount(result);
			list.add(tvCount);
		}
		return list;
	}
}
