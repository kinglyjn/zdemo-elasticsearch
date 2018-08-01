package test01.hello;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.missing.InternalMissing;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test04_TransportClient_Aggs_Query 测试聚合查询
 * @author kinglyjn
 * @date 2018年8月1日
 *
 */
public class Test04_TransportClient_Aggs_Query {
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
	 * aggs max/min/avg/sum/cardinality
	 * 
	 */
	@Test
	public void test01() {
		//AggregationBuilder aggs = AggregationBuilders.max("max_of_age").field("age");
		//AggregationBuilder aggs = AggregationBuilders.min("min_of_age").field("age");
		//AggregationBuilder aggs = AggregationBuilders.avg("avg_of_age").field("age");
		//SumAggregationBuilder aggs = AggregationBuilders.sum("sum_of_age").field("age");
		CardinalityAggregationBuilder aggs = AggregationBuilders.cardinality("cardinality_of_age").field("age");
		
		SearchResponse response = client.prepareSearch("lib3")
				.addAggregation(aggs)
				.get();
		
		//Max max = response.getAggregations().get("max_of_age");
		//double result = max.getValue();
		//Min min = response.getAggregations().get("min_of_age");
		//double result = min.getValue();
		//Avg avg = response.getAggregations().get("avg_of_age");
		//double result = avg.getValue();
		//Sum sum = response.getAggregations().get("sum_of_age");
		//double result = sum.getValue();
		Cardinality cardinality = response.getAggregations().get("cardinality_of_age");
		long result = cardinality.getValue();
		
		logger.info(result);
	}
	
	
	/**
	 * terms aggs 
	 * 分组聚合，根据用户的年龄字段进行分组
	 * 
	 */
	@Test
	public void test02() {
		TermsAggregationBuilder aggs = AggregationBuilders.terms("group_of_age").field("age").size(10);
		SearchResponse response = client.prepareSearch("lib3")
									.addAggregation(aggs)
									.get();
		//LongTerms terms = response.getAggregations().get("group_of_age");
		//List<Bucket> buckets = terms.getBuckets();
		Terms terms = response.getAggregations().get("group_of_age");
		List<? extends Terms.Bucket> buckets = terms.getBuckets();
		
		for (Terms.Bucket bucket : buckets) {
			Object key = bucket.getKey();
			long docCount = bucket.getDocCount();
			logger.info("key={},doc_count={}", key,docCount);
		}
	}
	
	
	/**
	 * filter aggs
	 * filters aggs
	 * 过滤聚合，指定一个或多个过滤条件，把满足所有对应过滤条件的结果分到同一个组里面
	 * 
	 */
	@Test
	public void test03() {
		/*
		FilterAggregationBuilder aggs = AggregationBuilders.filter("interests_contains_music", QueryBuilders.termQuery("interests", "music"));
		SearchResponse response = client.prepareSearch("lib3")
									.addAggregation(aggs)
									.execute()
									.actionGet();
		Filter filter = response.getAggregations().get("interests_contains_music");
		logger.info("name={}, doc_count={}", filter.getName(), filter.getDocCount());
		*/
		
		FiltersAggregationBuilder aggs = AggregationBuilders.filters("my_filters", 
				new FiltersAggregator.KeyedFilter("mymusic", QueryBuilders.termQuery("interests", "music")),
				new FiltersAggregator.KeyedFilter("mysleeping", QueryBuilders.termQuery("interests", "sleeping")));
		//FiltersAggregationBuilder aggs = AggregationBuilders.filters("my_filters", 
		//		QueryBuilders.termQuery("interests", "music"),
		//		QueryBuilders.termQuery("interests", "sleeping"));
		
		SearchResponse response = client.prepareSearch("lib3")
									.addAggregation(aggs)
									.execute()
									.actionGet();
		Filters filters = response.getAggregations().get("my_filters");
		List<? extends Filters.Bucket> buckets = filters.getBuckets();
		for (Filters.Bucket bucket : buckets) {
			Object key = bucket.getKey();
			long docCount = bucket.getDocCount();
			logger.info("key={},doc_count={}", key,docCount);
			//key=mymusic,doc_count=3
			//key=mysleeping,doc_count=1
		}
	}
	
	
	/**
	 * range aggs
	 * 范围聚合，将满足相应范围的结果分到同一个组里面
	 * 
	 */
	@Test
	public void test04() {
		RangeAggregationBuilder aggs = AggregationBuilders.range("range_of_age").field("age")
										.addUnboundedTo(20) 	// -inf ~ 20
										.addRange(20, 25)		// 20 ~ 25
										.addRange(25, 30)		// 25 ~ 30
										.addUnboundedFrom(30);	// 30 ~ +inf
		SearchResponse response = client.prepareSearch("lib3")
										.addAggregation(aggs)
										.execute()
										.actionGet();
		Range range = response.getAggregations().get("range_of_age");
		List<? extends Range.Bucket> buckets = range.getBuckets();
		for (Range.Bucket bucket : buckets) {
			Object key = bucket.getKey();
			long docCount = bucket.getDocCount();
			logger.info("key={},doc_count={}", key,docCount);
			//key=*-20.0,doc_count=0
			//key=20.0-25.0,doc_count=1
			//key=25.0-30.0,doc_count=2
			//key=30.0-*,doc_count=1
		}				
	}
	
	
	/**
	 * missing aggs
	 * 用于统计某个字段的值为空的文档个数（注意不能用在已分词的数据上，如text）
	 * 
	 */
	@Test
	public void test05() {
		MissingAggregationBuilder aggs = AggregationBuilders.missing("missing_of_age").field("age");
		SearchResponse response = client.prepareSearch("lib3")
										.addAggregation(aggs)
										.execute()
										.actionGet();
		InternalMissing missing = response.getAggregations().get("missing_of_age");
		logger.info("name={}, doc_count={}", missing.getName(), missing.getDocCount());
		//name=missing_of_age, doc_count=3
	}
	
	
	/**
	 * 稍复杂的分组
	 * 对于那些有音乐兴趣的用户按照年龄进行分组，之后再对每一组的年龄求平均值，按照平均年龄进行排序
	 * 
	 */
	@Test
	public void test06() {
		// init query
		MatchQueryBuilder query = QueryBuilders.matchQuery("interests", "music");
		// init aggs
		TermsAggregationBuilder aggs = AggregationBuilders.terms("group_of_age").field("age").size(3);
		AvgAggregationBuilder subAggs = AggregationBuilders.avg("avg_of_grouped_age").field("age");
		aggs.subAggregation(subAggs);
		aggs.order(BucketOrder.aggregation("avg_of_grouped_age", false)); //根据分组后的平均年龄倒序输出
		
		// get response
		SearchResponse response = client.prepareSearch("lib3")
				.setQuery(query)
				.setSize(0) //不输出hits命中文档内容
				.addAggregation(aggs)
				.get();
		
		// parse to result
		LongTerms groupOfAge = response.getAggregations().get("group_of_age");
		List<Bucket> buckets = groupOfAge.getBuckets();
		for (Bucket bucket : buckets) {
			Object key = bucket.getKey();
			long docCount = bucket.getDocCount();
			
			InternalAggregation internalAggs = (InternalAggregation) bucket.getAggregations().asMap().get("avg_of_grouped_age");
			Double avg_of_grouped_age = (Double) internalAggs.getProperty("value");
			logger.info("key={},doc_count={}; avg_of_grouped_age={}", key,docCount,avg_of_grouped_age);
		}
		/*
		 {
		  "took": 1,
  		  "timed_out": false,
  		  "_shards": {...}
		  "hits": {...},
		  "aggregations": {
		    "group_of_age": {
		      "doc_count_error_upper_bound": 0,
		      "sum_other_doc_count": 0,
		      "buckets": [
		        {
		          "key": 23,
		          "doc_count": 1,
		          "avg_of_grouped_age": {
		            "value": 23
		          }
		        },
		        {
		          "key": 28,
		          "doc_count": 1,
		          "avg_of_grouped_age": {
		            "value": 28
		          }
		        },
		        {
		          "key": 30,
		          "doc_count": 1,
		          "avg_of_grouped_age": {
		            "value": 30
		          }
		        }
		      ]
		    }
		  }
		 }
		 */
	}
}
