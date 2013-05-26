package template;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class TemplateParser {
	public static HashMap<String, Feature> parseTemplate(Document template){
		Element service = (Element)template.getElementsByTagName("service").item(0);
		HashMap<String, Feature> table = new HashMap<String, Feature>();
		table.put(service.getTagName(), new Feature(service.getAttribute("value")));
		
		NodeList children = service.getChildNodes();
		for(int i = 0; i < children.getLength(); i++){
			if(children.item(i) instanceof Element){
				Element child = (Element)children.item(i);
				if(child.hasAttribute("value")){
					table.put(child.getTagName(), new Feature(child.getAttribute("value")));
				}else{
					table.put(child.getTagName(), new Feature(child.getAttribute("key"), child.getAttribute("tag")));
				}
			}
		}
		return table;
	}
}
