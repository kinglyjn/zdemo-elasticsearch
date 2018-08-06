package test02.demo_tvcount;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

/**
 * HbaseUtils
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class HbaseUtils {
	private static final Configuration HBASE_CONF;
	private static Connection CONNECTION = null;
	
	static {
		HBASE_CONF = HBaseConfiguration.create();
		HBASE_CONF.addResource(HbaseUtils.class.getResource("/hbase/core-site.xml"));
		HBASE_CONF.addResource(HbaseUtils.class.getResource("/hbase/hbase-site.xml"));
		HBASE_CONF.addResource(HbaseUtils.class.getResource("/hbase/hdfs-site.xml"));
	}
	
	
	/**
	 * 获取全局配置
	 */
	public static Configuration getConfiguration() {
		return HBASE_CONF;
	}
	
	/**
	 * 获取全局CONNECTION
	 * @throws IOException 
	 */
	public static Connection getConnection() throws IOException {
		if (CONNECTION != null) {
			return CONNECTION;
		}
		synchronized (HbaseUtils.class) {
			if (CONNECTION==null) {
				synchronized (HbaseUtils.class) {
					CONNECTION = ConnectionFactory.createConnection(HBASE_CONF);
				}
			}
		}
		return CONNECTION;
	}
	
	/**
	 * 获取Admin
	 * @throws IOException 
	 */
	public static Admin getAdmin() throws IOException {
		return getConnection().getAdmin();
	}
	
	/**
	 * 获取Table
	 * @throws IOException 
	 */
	public static Table getTable(String tableName) throws IOException {
		return getConnection().getTable(TableName.valueOf(tableName));
	}
	
	/**
	 * 关闭Admin
	 * @throws IOException 
	 */
	public static void closeAdmin(Admin admin) throws IOException {
		if (admin != null) {
			admin.close();
		}
	}
	
	/**
	 * 关闭Table
	 * @throws IOException 
	 */
	public static void closeTable(Table table) throws IOException {
		if (table != null) {
			table.close();
		}
	}
}
