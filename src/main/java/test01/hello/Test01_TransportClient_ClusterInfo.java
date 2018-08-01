package test01.hello;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test01_TransportClient_ClusterInfo 获取集群的信息
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test01_TransportClient_ClusterInfo {
	private static final Logger logger = LogManager.getLogger(Test01_TransportClient_ClusterInfo.class);
	private TransportClient client;
	
	@Before
	public void getTransportClient() {
		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
		client = new PreBuiltTransportClient(settings , new ArrayList<Class<? extends Plugin>>(1));
		try {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName("nimbusz"), 9300));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@After
	public void closeTransportClient() {
		client.close();
	}
	
	
	@Test
	public void test01() {
		ClusterHealthResponse clusterHealth = client.admin().cluster().prepareHealth().get();
		
		// 集群的名称
		logger.info("cluster name: {}", clusterHealth.getClusterName());
		// 集群中节点的个数
		logger.info("number of nodes: {}", clusterHealth.getNumberOfNodes());
		// 集群中数据节点的个数
		logger.info("number of data nodes: {}", clusterHealth.getNumberOfDataNodes());
		
		// 每个索引的信息
		Collection<ClusterIndexHealth> indexHealths = clusterHealth.getIndices().values();
		for (ClusterIndexHealth indexHealth : indexHealths) {
			System.err.println("-------------------");
			
			// 索引名
			logger.info("index name: {}", indexHealth.getIndex());
			// 索引分片个数
			logger.info("number of shards: {}", indexHealth.getNumberOfShards());
			// 索引状态
			logger.info("status: {}", indexHealth.getStatus());
		}
		
	}
	
}
