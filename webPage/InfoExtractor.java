package webPage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.soap.Node;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class InfoExtractor {
	private String _path;
	private String _key;
	public InfoExtractor(String p, String k){
		this._path = p;
		this._key = k;
	}
	
	public void start(){
//		try{
//			Document doc = Jsoup.parse(new File(this._path), "utf-8");
//			System.out.println(Pattern.matches("^[/w/s/p{Punct}]*$", doc.getElementById("Main").text().toString()));
//		}
//		catch(IOException e){
//			e.printStackTrace();
//		}
		Element target = getTarget();
		Document nf = new Document("");
		String ot = target.ownText();
		String nc = "";
		if(ot != null && ot.length() > 0){
			String[] t = ot.split(" ");
			List<org.jsoup.nodes.Node> children = target.childNodes();
			Iterator<org.jsoup.nodes.Node> i = children.iterator();
			while(i.hasNext()){
				org.jsoup.nodes.Node child = i.next();
				if(child instanceof TextNode && i.hasNext()){
					String k = child.toString();
					String v = i.next().toString();
					//set.put(k, v);
					System.out.println(k + "\n" + v + "\n");
					Element nt = new Element(Tag.valueOf("div"), "");
					nt.append(k + v);
					nf.appendChild(nt);
				}
			}
		}
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("output.html")));
			w.write(nf.toString());
			w.flush();
			w.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private Element getTarget(){
		try{
			Document doc = Jsoup.parse(new File(this._path), "utf-8");
			Elements keyTags = doc.getElementsByClass(this._key);
			if(keyTags.size() != 1) return null;
			Element keyTag = keyTags.get(0);
			while(!isTarget(keyTag)){
				keyTag = keyTag.parent();
			}
			return keyTag;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean isTarget(Element tag){
		int cond = 0;
		ArrayList<String[]> set = new ArrayList<String[]>();
		String[] mWords = {"method", "api"};
		String[] dWords = {"description", "introduction"};
		String[] aWords = {"argument", "parameter"};
		String[] rWords = {"response", "result"};
		String[] eWords = {"error codes", "status codes"};
		
		set.add(mWords);
		set.add(dWords);
		set.add(aWords);
		set.add(rWords);
		set.add(eWords);
		
		for(String[] words : set){
			for(String word : words){
				if(tag.text().contains(word)){
					cond++;
					break;
				}
			}
		}
		if(cond >= 3)return true;
		return false;
	}
}
