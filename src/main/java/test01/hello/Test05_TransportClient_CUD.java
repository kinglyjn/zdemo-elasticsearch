package test01.hello;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test05_TransportClient_CUD 增删改测试
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test05_TransportClient_CUD {
	private static final Logger logger = LogManager.getLogger(Test05_TransportClient_CUD.class);
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
	 * 添加文档
	 * 
	 */
	@Test
	public void test01() throws IOException {
		XContentBuilder doc = XContentFactory.jsonBuilder()
									.startObject()
									.field("id", 1)
									.field("title", "Java设计模式之装饰模式")
									.field("content", "在不必要改变原类文件和使用继承的情况下，动态地扩展一个对象的功能。")
									.field("postdate", "2017-09-09")
									.field("url", "http://www.page.keyllo.com")
									.endObject();
		// 若文档不存在则创建，若存在则更新
		IndexResponse response = client.prepareIndex("lib19", "blog", "1").setSource(doc).get();
		logger.info(response.status());
		logger.info(response.getResult());
	}
	
	
	/**
	 * 删除文档
	 * 
	 */
	@Test
	public void test02() throws IOException {
		DeleteResponse response = client.prepareDelete("lib19", "blog", "1").get();
		logger.info(response.status());
		logger.info(response.getResult());
	}
	
	
	/**
	 * 将查询到的文档删除
	 * 在索引lib19和lib2中查询标题中含有”工厂“的文档，并将其删除
	 * 
	 */
	@Test
	public void test03() {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
			.newRequestBuilder(client)
			.filter(QueryBuilders.matchQuery("title", "工厂"))
			.source("lib19", "lib2") //指定所在的索引
			.get();
		logger.info(response.getStatus());
		logger.info(response.getDeleted()); //删除文档的个数
	}
	
	
	/**
	 * 更新文档
	 * 
	 */
	@Test
	public void test04() throws Exception {
		UpdateRequest request = new UpdateRequest();
		request.index("lib19")
				.type("blog")
				.id("1")
				.doc( XContentFactory.jsonBuilder()
						.startObject()
						.field("title", "Java设计模式之装饰模式-修改值")
						.endObject() );
		UpdateResponse response = client.update(request).get();
		logger.info(response.status());
		logger.info(response.getResult());
	}
	
	
	/**
	 * upsert更新
	 * 文档不存在则插入，存在则覆盖更新
	 * 
	 */
	@Test
	public void test05() throws Exception {
		IndexRequest indexRequest = new IndexRequest("lib19", "blog", "2")
										.source( XContentFactory.jsonBuilder()
												.startObject()
												.field("id", 1)
												.field("title", "工厂模式")
												.field("content", "静态工厂，动态工厂等等")
												.field("postdate", "2017-10-09")
												.field("url", "http://www.page.keyllo.com/blog")
												.endObject() );
		UpdateRequest updateRequest = new UpdateRequest()
										.index("lib19")
										.type("blog")
										.id("2")
										.doc( XContentFactory.jsonBuilder()
												.startObject()
												.field("title", "Java设计模式之装饰模式-修改值2")
												.endObject() )
										.upsert(indexRequest);
				
		UpdateResponse response = client.update(updateRequest).get();
		logger.info(response.status());
		logger.info(response.getResult());
	}
}
