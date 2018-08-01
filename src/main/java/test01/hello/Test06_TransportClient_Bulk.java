package test01.hello;

import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test06_TransportClient_Bulk 测试bulk批量操作
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test06_TransportClient_Bulk {
	private static final Logger logger = LogManager.getLogger(Test06_TransportClient_Bulk.class);
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
	
	
	/**
	 * 使用bulk方式批量操作
	 * 以批量添加为例
	 * 
	 */
	@Test
	public void test01() throws Exception {
		BulkResponse response = client.prepareBulk()
				.add(client.prepareIndex("lib12", "user", "4")
						.setSource(XContentFactory.jsonBuilder()
								.startObject()
								.field("name", "zhaosi")
								.field("interests", "i like qima shejian")
								.endObject()))
				.add(client.prepareIndex("lib12", "user", "5")
						.setSource(XContentFactory.jsonBuilder()
								.startObject()
								.field("name", "wangwu")
								.field("interests", "i like haha heihei")
								.endObject()))
				.get();
		
		logger.info(response.status());
		logger.info(response.hasFailures());
	}
}
