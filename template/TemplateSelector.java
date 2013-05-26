package template;

import org.w3c.dom.Document;
import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class TemplateSelector {
	private static String templatePath = "./template/";
	
	public static Document getTemplate(String name) throws Exception{
		File dir = new File(templatePath);
		String[] list = dir.list();
		for(String fn : list){
			if(fn.contains(name)){
				File f = new File(templatePath+fn);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(f);
				return doc;
			}
		}
		return null;
	}
	
	public static String[] getList(){
		File dir = new File(templatePath);
		return dir.list();
	}
}
