package annotate;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashMap;
import template.Feature;

public class Annotator {
	private static String[] terms = {"operation", "address", "input", "output"};
	private static double failFactor = 0.5;
	public static boolean annotate(Document doc, HashMap<String, Feature> template){
		
		//在网页body标签标注服务名称
		Feature feature = template.get("service");
		doc.body().attr("service", feature.value());
		
		//遍历词典，逐个检索
		int successCount = 0;
		int totalCount = 0;
		for(String term : terms){
			feature = template.get(term);
			if(!feature.isValue()){
				totalCount++;
				String key = feature.clue()[0];
				String tag = feature.clue()[1];
				Elements objs = doc.getElementsByTag(tag);
				for(Element obj : objs){
					if(obj.ownText().toLowerCase().matches(key)){
						obj.parent().addClass(term);
						successCount++;
					}
				}
			}else{
				doc.body().attr(term, feature.value());
			}
		}
		if((double)successCount/totalCount < failFactor){
			return false;
		}else{
			return true;
		}
	}
}
