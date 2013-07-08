package webPage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class Crawler {
	
	private String seed;
	private String site;
	private String name;
	
	public Crawler(String s, String _name, String _site){
		this.seed = s;
		this.name = _name;
		this.site = _site;
	}
	
	public void start(){
		try{
			Document doc = Jsoup.connect(seed).timeout(5000).get();
			String path = "./" + name + "/";
			File dir = new File(path);
			if(!dir.exists())
				dir.mkdirs();
			doc = Jsoup.parse(doc.toString(), site);
			Elements links = doc.select("a[href]");
			long count = 0;
			for(Element link : links){
				++count;
				File f = new File(path + String.valueOf(count) + ".html");
				savePage(link, f);
				Tool.print(String.valueOf(count));
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void savePage(Element link, File f){
		try{
			String add = link.attr("abs:href");
			Document temp = Jsoup.connect(add).timeout(10000).get();
			String content = Jsoup.clean(temp.toString(), Whitelist.relaxed().addAttributes("div", "class", "id"));
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			w.write(content);
			w.flush();
			w.close();
			Tool.print(add);
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	
	public static boolean isUrl (String pInput) { 
        if(pInput == null){ 
            return false; 
        } 
        String regEx = "^(http|https|ftp)//://([a-zA-Z0-9//.//-]+(//:[a-zA-" 
            + "Z0-9//.&%//$//-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{" 
            + "2}|[1-9]{1}[0-9]{1}|[1-9])//.(25[0-5]|2[0-4][0-9]|[0-1]{1}" 
            + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)//.(25[0-5]|2[0-4][0-9]|" 
            + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)//.(25[0-5]|2[0-" 
            + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0" 
            + "-9//-]+//.)*[a-zA-Z0-9//-]+//.[a-zA-Z]{2,4})(//:[0-9]+)?(/" 
            + "[^/][a-zA-Z0-9//.//,//?//'///////+&%//$//=~_//-@]*)*$"; 
        Pattern p = Pattern.compile(regEx); 
        Matcher matcher = p.matcher(pInput); 
        return matcher.matches(); 
    }


}
