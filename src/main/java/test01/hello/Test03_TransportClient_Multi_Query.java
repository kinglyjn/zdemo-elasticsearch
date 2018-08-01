package test01.hello;

import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test03_TransportClient_Multi_Query 测试组合查询
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test03_TransportClient_Multi_Query {
	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	private TransportClient client;
	
	/**
	 * 创建并返回客户端
	 * 注意 TransportClient 将会在es7.0中标志为废弃，在es8.0中删除，请使用更高版本的 REST Client 代替
	 * 
	 */
	@Before
	public void getTransportClient() {
		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
		client = new PreBuiltTransportClient(settings, new ArrayList<Class<? extends Plugin>>(2));
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
	 * bool 组合查询
	 * 
	 */
	@Test
	public void test01() {
		BoolQueryBuilder query = QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery("interests", "music"))
						.mustNot(QueryBuilders.termQuery("name", "wangwu"))
						.should(QueryBuilders.rangeQuery("birthday").from("1990-05-05").format("yyyy-MM-dd"));
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(30)
				.addSort(SortBuilders.fieldSort("age").order(SortOrder.DESC))
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			hit.getSourceAsMap().forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
}
