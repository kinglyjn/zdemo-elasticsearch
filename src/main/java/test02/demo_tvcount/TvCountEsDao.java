package test02.demo_tvcount;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * ElasticSearchUtil
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvCountEsDao {
	private static final String ENCRIPT_ALGORITHM = "MD5";
	private static TransportClient client = null;
	
	/**
     * 获取客户端
     * 
     */
    public static Client getClient() {
    	if (client != null) {
    		return client;
    	} else {
        	synchronized (TvCountEsDao.class) {
    			if (client==null) {
    				try {
    		        	Settings settings = Settings.builder().put("cluster.name", "mycluster").build();
    		        	client = new PreBuiltTransportClient(settings , new ArrayList<Class<? extends Plugin>>(1));
    		        	client.addTransportAddress(new TransportAddress(InetAddress.getByName("hadoop01"), 9300));
    		        	client.addTransportAddress(new TransportAddress(InetAddress.getByName("hadoop02"), 9300));
    		        	client.addTransportAddress(new TransportAddress(InetAddress.getByName("hadoop04"), 9300));
    		        	client.addTransportAddress(new TransportAddress(InetAddress.getByName("hadoop07"), 9300));
    		        	client.addTransportAddress(new TransportAddress(InetAddress.getByName("hadoop08"), 9300));
    		        } catch (UnknownHostException e) {
    		            e.printStackTrace();
    		        }
    			}
    		}
            return client;
        }
    }
    
    
    /**
     * 添加索引
     * 
     */
    public static String addIndex(String index, String type, TVCount tvCount) {
    	System.err.println("TvCountEsDao#addIndex: " + tvCount);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("actor", tvCount.getActor());
        hashMap.put("alias", tvCount.getAlias());
        hashMap.put("description", tvCount.getDescription());
        hashMap.put("director", tvCount.getDirector());
        hashMap.put("tvid", tvCount.getTvid());
        hashMap.put("tvname", tvCount.getTvname());
        hashMap.put("tvtype", tvCount.getTvtype());

        IndexResponse response = getClient().prepareIndex(index, type)
                                            .setId(StringUtils.encrypt(tvCount.getTvid(), ENCRIPT_ALGORITHM))//设置id
                                            .setSource(hashMap)
                                            .execute()
                                            .actionGet();
        return response.getId();
    }
    
    
    /**
     * query base
     * 
     */
    public static Map<String, Object> search(String index, String type, String key) {
    	GetResponse response = getClient().prepareGet(index, type, key).execute().actionGet();
    	return response.getSource();
    }
    
    /**
     * multi match
     * 
     */
    public static List<String> multiMatch(String[] indices, String[] fieldNames, String keyword, int from, int size) {
    	long time1 = System.currentTimeMillis();
    	
    	MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword, fieldNames);
		SearchResponse response = getClient().prepareSearch(indices)
				.setQuery(multiMatchQuery)
				.setFrom(from)
				.setSize(size)
				.get();
		
		SearchHits hits = response.getHits();
		if (hits.totalHits==0) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (SearchHit hit : hits) {
			ids.add(hit.getId());
		}
		
		long time2 = System.currentTimeMillis();
		System.err.println("Elasticsearch查询耗时：" + (time2-time1) + " ms.");
		return ids;
    }
}
