package webPage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class StructExtract {
	private String targetPath = "./test/";
	private String resultPath = "./result/";
	public StructExtract()
	{
	}
	public void go()
	{
		traverse(targetPath);
	}
	public void traverse(String path)
	{
		File root = new File(path);
		String[] list = root.list();
		if(list == null || list.length == 0) return;
		for(String fn : list)
		{
			File f = new File(path + fn);
			if(f.isDirectory()) traverse(f.getPath() + "/");
			if(!fn.contains(".html") && !fn.contains(".htm")) continue;
			analyzeFile(f);
		}
	}
	private Element wrap(String html, int hNum)
	{
		if(hNum > 6) return null;
		Document doc = new Document("");
		Elements hs = Jsoup.parse(html).getElementsByTag("h" + String.valueOf(hNum));
		if(hs == null || hs.size() == 0) return wrap(html, ++hNum);
		Element div;
		for(Element h : hs)
		{
			List<Element> sibs = h.parent().children();
			int sIndex = h.elementSiblingIndex().intValue();
			div = new Element(Tag.valueOf("div"), "");
			div.appendChild(h);
			for(int i = sIndex+1; i < sibs.size(); i++)
			{
				Element sib = sibs.get(i);
				if(sib.tagName() == h.tagName()) break;
				div.appendChild(sib);
			}
			doc.append(div.toString());
		}
		return doc;
	}
	
	private void analyzeFile(File f)
	{
		try{
			Document doc = new Document("");
			Elements h1s = Jsoup.parse(f, "utf-8").getElementsByTag("h1");
			Element divh1;
			for(Element h1 : h1s)
			{
				List<Element> sibs = h1.parent().children();
				int sIndex = h1.elementSiblingIndex().intValue();
				divh1 = new Element(Tag.valueOf("div"), "");
				divh1.appendChild(h1);
				Element div = new Element(Tag.valueOf("div"), "");
				for(int i = sIndex+1; i < sibs.size(); i++){
					Element sib = sibs.get(i);
					if(sib.tagName() == "h1") break;
					Whitelist wl = new Whitelist();
					String temp = Jsoup.clean(sib.toString(),wl.basic().addTags("table").addTags("tbody").addTags("tr")
							.addTags("td").addTags("h2").addTags("h3").addTags("h4").addTags("h5").addTags("h6"));
					div.append(temp);
				}
				Element result = wrap(div.html(), 2);
				if(result != null) divh1.appendChild(result);
				doc.append(divh1.toString());
			}
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(resultPath + f.getName())));
			w.write(Jsoup.parse(doc.toString()).toString());
			w.flush();
			w.close();
			System.out.println(f.getPath());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
