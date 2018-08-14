package test02.demo_tvcount;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * HbaseOperations
 * @author kinglyjn
 * @date 2018年8月14日
 *
 */
public class HbaseOperations implements Serializable {
	private static final long serialVersionUID = 1L;
	static Connection connection;
	static Configuration conf;
	HTable htable;
	
	/**
	 * 获取hbase连接
	 * 
	 */
	static {
		try {
			// 设置zk连接
			Configuration conf = HBaseConfiguration.create();
			conf.set("hbase.zookeeper.quorum", "bd117,bd118,bd119");
			conf.setInt("hbase.zookeeper.property.clientPort", 2181);
			// 创建hbase链接
			connection = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			System.err.println("创建Connection实例时发生异常！");
		}
				
	}
	
	
	/**
	 * HbaseOperations 构造函数
	 * 
	 */
	public HbaseOperations(String tableName) {
		try {
			// 初始化htable
			htable= (HTable) connection.getTable(TableName.valueOf(tableName));
		} catch (Exception e) {
			System.err.println("创建HTable实例时发生异常！");
		}
	}

	
	/**
	 * 创建表
	 * 表名、列族、版本(0x00180009)、存活时间
	 * 
	 */
	public void createTable(List<String> columnFamilies) throws Exception {
		Admin admin = null;
		try {
			String tableName = Bytes.toString(htable.getTableName());
			
			admin = connection.getAdmin();
			TableName tb = TableName.valueOf(tableName);
			if (admin.tableExists(tb)) {
				throw new RuntimeException("表已经存在！");
			}
			
			HTableDescriptor desc = new HTableDescriptor(tb);
			for (String columnFamily : columnFamilies) {
				HColumnDescriptor hcolumnDescriptor = new HColumnDescriptor(columnFamily);
				hcolumnDescriptor.setBlockCacheEnabled(true); //默认开启读缓存
				hcolumnDescriptor.setInMemory(true); //
				desc.addFamily(hcolumnDescriptor);
			}
			admin.createTable(desc);
		} catch (Exception e) {
			System.err.println("创建表失败");
			throw e;
		} finally {
			admin.close();
		}
	}
	
	
	/**
	 * insert数据
	 * 
	 */
	public void insert(Map<String,Map<String,Object>> record, String rowId) {
		try {
			Put put = new Put(Bytes.toBytes(rowId));
			
			Set<String> columnFamilies = record.keySet();
			for (String columnFamily : columnFamilies) {
				Set<String> columns = record.get(columnFamily).keySet();
				for (String column : columns) {
					put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnFamily), 
							Bytes.toBytes(record.get(columnFamily).get(column).toString()));
				}
			}
			htable.put(put);
		} catch (Exception e) {
			throw new RuntimeException("插入记录发生异常！");
		}
	}

	
	 /*
	 * main
	 * 
	 */
	public static void main(String[] args) throws Exception {
		List<String> columnFamilies = Arrays.asList("cf1", "cf2");
		Map<String, Map<String, Object>> record = new HashMap<>();
		HashMap<String, Object> cf1 = new HashMap<>();
		HashMap<String, Object> cf2 = new HashMap<>();
		cf1.put("aa", 1);
		cf2.put("bb", 2);
		record.put("cf1", cf1);
		record.put("cf2", cf2);
		HbaseOperations operation = new HbaseOperations("test01:t1");
		operation.createTable(columnFamilies);
	}
}
