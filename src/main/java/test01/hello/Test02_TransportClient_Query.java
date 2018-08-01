package test01.hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test02_TransportClient_Query
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test02_TransportClient_Query {
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
	 * 根据_index、_type、_id定位获取对应的文档
	 * 
	 */
	@Test
	public void test01() throws UnknownHostException {
		//GetResponse response = client.prepareGet("lib3", "user", "1").execute().actionGet();
		//logger.info(response.getSourceAsString());
		
		//TypeQueryBuilder query = QueryBuilders.typeQuery("user");
		IdsQueryBuilder query = QueryBuilders.idsQuery().addIds("1","2","3");
		SearchResponse response = client.prepareSearch("lib3")
									.setQuery(query)
									.get();
		SearchHits hits = response.getHits(); //获取命中的文档
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			hit.getSourceAsMap().forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
	/**
	 * mget (multi get 一次定位获取多个文档)
	 * 
	 */
	@Test
	public void test02() {
		MultiGetResponse multiGetResponse = client.prepareMultiGet()
				.add("lib3", "user", "1", "2", "3")  // 如果中间某个文档不存在则忽略，继续查询下一个对应的文档
				.add("lib19", "blog", "1", "2")
				.get();
		for (MultiGetItemResponse item : multiGetResponse) {
			GetResponse response = item.getResponse();
			if (response!=null && response.isExists()) {
				logger.info(response.getSourceAsString());
			}
		}
	}
	
	
	/**
	 * term
	 * terms
	 * constant_score
	 * 
	 */
	@Test
	public void test03() {
		//TermQueryBuilder query = QueryBuilders.termQuery("age", 23);
		//TermsQueryBuilder query = QueryBuilders.termsQuery("interests", "sleeping", "music");
		CommonTermsQueryBuilder query = QueryBuilders.commonTermsQuery("interests", "music").analyzer("ik_max_word");
		
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(30) //默认最多查询出10条记录，这里设置为30
				.get();
		SearchHits hits = response.getHits(); //获取命中的文档
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
	/**
	 * match
	 * match_all
	 * multi_match
	 * match_phrase
	 * match_phrase_prefix
	 * 
	 */
	@Test
	public void test04() {
		//MatchQueryBuilder query = QueryBuilders.matchQuery("interests", "music");
		//MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
		//MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery("sleeping", "address", "interests");
		//MatchPhraseQueryBuilder query = QueryBuilders.matchPhraseQuery("interests", "music and playing piano");
		MatchPhrasePrefixQueryBuilder query = QueryBuilders.matchPhrasePrefixQuery("interests", "sleepin");
		
		SearchResponse response = client.prepareSearch("lib3", "lib4")
				.setQuery(query)
				.setSize(30)
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
	/**
	 * range
	 * 
	 */
	@Test
	public void test05() {
		//FieldSortBuilder sort = SortBuilders.fieldSort("birthday").order(SortOrder.DESC);
		//RangeQueryBuilder query = QueryBuilders.rangeQuery("birthday").from("1990-05-05").to("1990-06-09").format("yyyy-MM-dd"); //结果包含上下限
		RangeQueryBuilder query = QueryBuilders.rangeQuery("birthday").gte("1990-05-05").lte("1990-06-09").format("yyyy-MM-dd");
		
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				//.addSort(sort)
				.setSize(30)
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
	/**
	 * wildcard
	 * 
	 */
	@Test
	public void test06() {
		WildcardQueryBuilder query = QueryBuilders.wildcardQuery("interests", "sl*p??g");
		
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(30) 
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	
	/**
	 * fuzzy
	 * 
	 */
	@Test
	public void test07() {
		FuzzyQueryBuilder query = QueryBuilders.fuzzyQuery("interests", "slepig");
		
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(30) 
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
	/**
	 * querystring
	 * 
	 */
	@Test
	public void test08() {
		QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("+music -sleeping +zhangsan"); //空格表示AND，逗号表示OR（查询将在所有字段进行匹配）
		
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(30) 
				.get();
		SearchHits hits = response.getHits();
		for (SearchHit hit : hits) {
			System.err.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.getSourceAsMap();
			map.forEach((k,v) -> logger.info("{}:{}", k,v));
		}
	}
	
}
