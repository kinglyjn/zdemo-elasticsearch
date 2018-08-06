package test02.demo_tvcount;

import java.io.IOException;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HbaseNamespaceUtils
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class HbaseNamespaceUtils {
	private static final Logger LOGGER = LogManager.getLogger(HbaseTableUtils.class);
	
	/**
	 * 创建表空间
	 * @throws IOException 
	 */
	public static void createNamespace(String namespaceName) throws IOException {
		Admin admin = null;
		try {
			admin = HbaseUtils.getAdmin();
			boolean namespaceExists = false;
			NamespaceDescriptor[] nsds = admin.listNamespaceDescriptors();
			for (NamespaceDescriptor nsd : nsds) {
				if (namespaceName.equals(nsd.getName())) {
					namespaceExists = true;
					break;
				}
			}
			if (namespaceExists) {
				LOGGER.error("表空间已经存在");
				return;
			}
			NamespaceDescriptor desc = NamespaceDescriptor.create(namespaceName).build();
			admin.createNamespace(desc);
		} catch (IOException e) {
			LOGGER.error("创建表空间失败");
			throw e;
		} finally {
			admin.close();
		}
	}
	
	
	/**
	 * 删除表空间
	 * @throws IOException 
	 */
	public static void dropNamespace(String namespaceName) throws IOException {
		Admin admin = null;
		try {
			admin = HbaseUtils.getAdmin();
			boolean namespaceExists = false;
			NamespaceDescriptor[] nsds = admin.listNamespaceDescriptors();
			for (NamespaceDescriptor nsd : nsds) {
				if (namespaceName.equals(nsd.getName())) {
					namespaceExists = true;
					break;
				}
			}
			if (!namespaceExists) {
				LOGGER.info("表空间不存在");
				return;
			}
			TableName[] tableNames = admin.listTableNamesByNamespace(namespaceName);
			if (tableNames!=null && tableNames.length!=0) {
				LOGGER.info("表空间中存在表，不能删除");
				return;
			}
			admin.deleteNamespace(namespaceName);
		} catch (IOException e) {
			LOGGER.error("删除表空间失败");
			e.printStackTrace();
		} finally {
			admin.close();
		}
	}
}
