package test02.demo_tvcount;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * tvcount 数据爬取及解析
 * TvCountGrabberAndParser
 * @author kinglyjn
 * @date 2018年8月6日
 *
 */
public class TvCountGrabberAndParser {
	
	/**
	 * grabbingAndParsing
	 * 
	 */
	public static List<TVCount> grabbingAndParsing(List<String> urls) throws Exception {
		if (urls==null) {
			return null;
		}
		List<TVCount> tvCounts = new LinkedList<>();
		for (String urlString : urls) {
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			
			String html = IOUtils.toString(is, "utf-8");
			Element ele0 = Jsoup.parseBodyFragment(html).select(".yk-content .fix").get(0);
			System.out.println("---------------------------------");
			
			// tvid 
			String tvid = urlString.split("id_")[1].replace(".html", "");
			System.out.println("tvid=" + tvid);
			
			// pic
			Element ele1 = ele0.select(".p-thumb img").get(0);
			String pic = ele1.attr("src");
			System.out.println("pic" + pic);
			
			// tvname 
			Elements ele2 = ele0.select(".p-base ul .p-title");
			String tvname = ele2.text().replace("剧集：", "");
			System.out.println("tvname=" + tvname);
			
			// director
			Element ele3 = ele0.select(".p-base ul li:contains(导演：)").get(0);
			String director = ele3.text().replace("导演：", "");
			System.out.println("director=" + director);
			
			// actor  
			Elements ele4 = ele0.select(".p-base ul .p-performer");
			String actor = ele4.attr("title");
			System.out.println("actor=" + actor);
			
			// allnumber   
			Element ele5 = ele0.select(".p-base ul .p-renew").get(0);
			String allnumber = ele5.text();
			System.out.println("allnumber=" + allnumber);
			
			// tvtype  
			Element ele6 = ele0.select(".p-base ul li:contains(类型：)").get(0);
			String tvtype = ele6.text().replace("类型：", "");
			System.out.println("tvtype=" + tvtype);
			
			// description 
			Element ele7 = ele0.select(".p-base ul .p-intro .intro-more").get(0);
			String description = ele7.text();
			System.out.println("description=" + description);
			
			// alias  
			Elements ele8 = ele0.select(".p-base ul .p-alias");
			String alias = ele8.attr("title");
			System.out.println("alias=" + alias);
			
			// tvshow  
			Element ele9 = ele0.select(".p-base ul li:contains(总播放数：)").get(0);
			String tvshow = ele9.text().replace("总播放数：", "").replaceAll(",", "");
			System.out.println("tvshow=" + tvshow);
			
			// present 
			Element ele10 = ele0.select(".p-base ul li:contains(优酷开播：)").get(0);
			String present = ele10.text().replace("优酷开播：", "");
			System.out.println("present=" + present);
			
			// score  
			Elements ele11 = ele0.select(".p-base ul .p-score .star-num");
			String score = ele11.text();
			System.out.println("score=" + score);
			
			// zone 
			Element ele12 = ele0.select(".p-base ul li:contains(地区：)").get(0);
			String zone = ele12.text().replace("地区：", "");
			System.out.println("zone=" + zone);
			
			// commentnumber
			Element ele13 = ele0.select(".p-base ul li:contains(评论：)").get(0);
			String commentnumber = ele13.text().replace("评论：", "").replaceAll(",", "");
			System.out.println("commentnumber=" + commentnumber);
			
			// supportnumber
			Element ele14 = ele0.select(".p-base ul li:contains(顶：)").get(0);
			String supportnumber = ele14.text().replace("顶：", "").replaceAll(",", "");
			System.out.println("supportnumber=" + supportnumber);
			
			TVCount tvCount = new TVCount(tvname, director, actor, 
					allnumber, tvtype, description, 
					tvid, alias, tvshow, 
					present, score, zone, 
					commentnumber, supportnumber, pic);
			tvCounts.add(tvCount);
			is.close();
		}
		return tvCounts;
	}
	
}
