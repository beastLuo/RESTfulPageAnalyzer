package webPage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RESTFilter {
	private String _path;
	private ArrayList<String> clKeys;
	private ArrayList<String> idKeys;
	
	public RESTFilter(String p){
		this._path = p;
		clKeys = new ArrayList<String>();
		idKeys = new ArrayList<String>();
	}
	
	public void start(){
		parseResult("result.txt");
		filter("MethodDescription");
	}
	
	private void parseResult(String s){
		File f = new File(s);
		try{
			BufferedReader r = new BufferedReader(new FileReader(f));
			String l;
			while((l = r.readLine()) != null){
				String[] pair = l.split("\t");
				if(pair[0].equals("class")){
					clKeys.add(pair[1]);
					//System.out.println(pair[1]);
				}
				if(pair[0].equals("id")){
					idKeys.add(pair[1]);
					//System.out.println(pair[1]);
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	
	private void filter(String target){
		File dir = new File(this._path);
		String[] list = dir.list();
		long count = 0;
		try{
			File result = new File("./result/");
			if(!result.exists()) result.mkdirs();
			for(String fn : list){
				File f = new File(this._path + fn);
				Document doc = Jsoup.parse(f, "utf-8");
				Elements goal = doc.getElementsByClass(target);
				if(goal == null || goal.size() == 0) continue;
				BufferedReader r = new BufferedReader(new FileReader(f));
				BufferedWriter w = new BufferedWriter(new FileWriter(new File(result.getPath() + "/" + fn)));
				String l;
				while((l = r.readLine()) != null){
					w.write(l);
				}
				r.close();
				w.flush();
				w.close();
				++count;
			}
			System.out.println(String.valueOf(count));
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
}
