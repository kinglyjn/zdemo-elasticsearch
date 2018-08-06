package test02.demo_tvcount;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HbaseTableUtils
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class HbaseTableUtils {
	private static final Logger LOGGER = LogManager.getLogger(HbaseTableUtils.class);
	
	/**
	 * 创建表
	 * 表名、列族、版本(0x00180009)、存活时间
	 */
	public static void createTable(String tableName, String columnFamily, 
			Integer minVersions, Integer maxVersions,Integer timeToLive) throws Exception {
		Admin admin = null;
		try {
			admin = HbaseUtils.getAdmin();
			TableName tb = TableName.valueOf(tableName);
			if (admin.tableExists(tb)) {
				LOGGER.info("表已经存在");
				admin.close();
			    return;
			} 
			HTableDescriptor desc = new HTableDescriptor(tb);
			HColumnDescriptor hcolumnDescriptor = new HColumnDescriptor(columnFamily);
			hcolumnDescriptor.setBlockCacheEnabled(true); //默认开启读缓存
			hcolumnDescriptor.setInMemory(true); //
			if (minVersions!=null && maxVersions!=null && minVersions>=0 && maxVersions>0) {
				hcolumnDescriptor.setMinVersions(minVersions);
				hcolumnDescriptor.setMaxVersions(maxVersions);
			}
			if (timeToLive != null ) {
				hcolumnDescriptor.setTimeToLive(timeToLive);
			}
			desc.addFamily(hcolumnDescriptor);
			admin.createTable(desc);
		} catch (Exception e) {
			LOGGER.info("创建表失败");
			throw e;
		} finally {
			admin.close();
		}
	}
	

	/**
	 * 删除表
	 */
	public static void dropTable(String tableName) throws Exception {
		Admin admin = null;
		try {
			admin = HbaseUtils.getAdmin();
			TableName tb = TableName.valueOf(tableName);
			if (!admin.tableExists(tb)) {
				LOGGER.info("表不存在");
				admin.close();
			    return;
			} 
			if (!admin.isTableDisabled(tb)) {
				admin.disableTable(tb);
			}
			admin.deleteTable(tb);
		} catch (Exception e) {
			LOGGER.error("删除表失败");
			throw e;
		} finally {
			admin.close();
		}
	} 
	
	
	/**
	 * putOne数据
	 * @throws IOException 
	 */
	public static void putOne(String tableName, Put put) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.put(put);
		} catch (Exception e) {
			LOGGER.error("putOne失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	/**
	 * putList数据
	 * @throws IOException 
	 */
	public static void putList(String tableName, List<Put> puts) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.put(puts);
		} catch (Exception e) {
			LOGGER.error("putOne失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	
	/**
	 * 检查更新
	 * @throws IOException 
	 */
	public static void checkAndPut(String tableName, String rowkey, String columnFamily, 
			String column, CompareOp compareOp, byte[] value, Put put) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.checkAndPut(Bytes.toBytes(rowkey), Bytes.toBytes(columnFamily), Bytes.toBytes(column), compareOp, value, put);
		} catch (Exception e) {
			LOGGER.error("checkAndPut失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	/**
	 * 递增计数器
	 * 注意：列的类型必须为long类型
	 */
	public static Long increment(String tableName, String rowkey, String columnFamily, String column, Long step) throws Exception {
		Table table = null;
		Long increCount = null;
		try {
			table = HbaseUtils.getTable(tableName);
			increCount = table.incrementColumnValue(Bytes.toBytes(rowkey), Bytes.toBytes(columnFamily), Bytes.toBytes(column), step); 
		} catch (Exception e) {
			LOGGER.error("increment失败");
			throw e;
		} finally {
			table.close();
		}
		return increCount;
	}
	
	/**
	 * 删除数据
	 * @throws IOException 
	 */
	public static void deleteRow(String tableName, Delete delete) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.delete(delete);
		} catch (Exception e) {
			LOGGER.error("deleteRow失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	/**
	 * 删除多行数据
	 * @throws IOException 
	 */
	public static void deleteRows(String tableName, List<Delete> deletes) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.delete(deletes);
		} catch (Exception e) {
			LOGGER.error("deleteRows失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	/**
	 * 检查删除数据
	 * @throws IOException 
	 */
	public static void checkAndDelete(String tableName, String rowkey, String columnFamily, 
			String column, CompareOp compareOp, byte[] value, Delete delete) throws Exception {
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			table.checkAndDelete(Bytes.toBytes(rowkey), Bytes.toBytes(columnFamily), Bytes.toBytes(column), compareOp, value, delete);
		} catch (Exception e) {
			LOGGER.error("checkAndDelete失败");
			throw e;
		} finally {
			table.close();
		}
	}
	
	/**
	 * 判断数据是否存在
	 * @throws IOException 
	 */
	public static Boolean exists(String tableName, String rowkey) throws Exception {
		Boolean exists = null;
		Table table = null;
		try {
			table = HbaseUtils.getTable(tableName);
			Get get = new Get(Bytes.toBytes(rowkey));
			exists = table.exists(get);
		} catch (IOException e) {
			LOGGER.error("exists出现错误");
			throw e;
		} finally {
			table.close();
		}
		return exists;
	}
	
	/**
	 * get数据
	 * @throws IOException 
	 */
	public static Result[] get(String tableName, List<Get> gets) throws Exception {
		Table table = null;
		Result[] results = null;
		try {
			table = HbaseUtils.getTable(tableName);
			results = table.get(gets);
			
			//get
			/*Get get = new Get(Bytes.toBytes("1,199,912"));
			Result result = table.get(get);
			get.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("name"));
			get.setTimeRange(minStamp, maxStamp);
			get.setTimeStamp(timestamp)
			Cell[] rawCells = result.rawCells();
			for (Cell cell : rawCells) {
				String columnFamily = Bytes.toString(CellUtil.cloneFamily(cell)); //注意需要类型匹配，不然Bytes转化的可能是错误类型的数据
				String column = Bytes.toString(CellUtil.cloneQualifier(cell));
				String value = Bytes.toString(CellUtil.cloneValue(cell));
				System.out.println(columnFamily + ":" + column + " -> " + value);
			}*/
		} catch (IOException e) {
			LOGGER.error("get数据失败");
			throw e;
		} finally {
			table.close();
		}
		return results;
	}
	
	
	/**
	 * scan数据
	 * @throws IOException 
	 */
	public static ResultScanner scan(String tableName, Scan scan) throws Exception {
		Table table = null;
		ResultScanner scanner = null;
		try {
			table = HbaseUtils.getTable(tableName);
			//scan
			/*Scan scan = new Scan();
			scan.setRaw(false); 		//原生扫描（能够扫描到KEEP_DELETED_CELLS的记录）
			scan.setCaching(1000); 	//设置缓存行的大小（服务器每次返回的行数）
			scan.setBatch(10); 		//设置批量列的大小（服务器每次返回的列数）
			Filter rf = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("1,000"))); //百万级记录filter耗时24s
			scan.setFilter(rf);
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {
				...
			}*/
			scanner = table.getScanner(scan);
		} catch (Exception e) {
			LOGGER.error("scan失败");
			throw e;
		} finally {
			table.close();
		}
		return scanner;
	}
	
	/**
	 * 查询行数
	 */
	public static Long rowCount(String tableName) throws Exception {
		Table table = null;
		long rowCount = 0;
		try {
			table = HbaseUtils.getTable(tableName);
			Scan scan = new Scan();
			scan.setFilter(new FirstKeyOnlyFilter());
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {  
	            rowCount += result.size();  
	        }  
		} catch (Exception e) {
			LOGGER.error("scan失败");
			throw e;
		} finally {
			table.close();
		}
		return rowCount;
	}
}
