package webPage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GeneExtractor {
	private ArrayList<String> clKeys;
	private ArrayList<String> idKeys;
	private ArrayList<Integer> wCl;
	private ArrayList<Integer> wId;
	private String path;
	private int thresh;
	private int fnum;
	
	public GeneExtractor(String p){
		this.path = p;
		
		clKeys = new ArrayList<String>(0);
		idKeys = new ArrayList<String>(0);
		wCl = new ArrayList<Integer>(0);
		wId = new ArrayList<Integer>(0);
		
		thresh = 50;
		fnum = 0;
	}
	
	public void start(){
		count();
		//parseResult("result_flickr.txt");
		//cut();
		saveRecord();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(Calendar.getInstance().getTime()));
	}
	
	private void cut(){
		Iterator<Integer> i = wId.iterator();
		Iterator<String> j = idKeys.iterator();
		while(i.hasNext()){
			float f = i.next().floatValue();
			String s = j.next();
			if(f >= fnum *0.8){
				j.remove();
				i.remove();
			}
		}
		i = wCl.iterator();
		j = clKeys.iterator();
		while(i.hasNext()){
			float f = i.next().floatValue();
			String s = j.next();
			if(f >= fnum *0.8){
				j.remove();
				i.remove();
			}
		}
	}
	
	private void count(){
		File dir = new File(path);
		String[] list = dir.list();
		try{
			//Traverse all files in directory
			for(String fn : list){
				++fnum;
				System.out.println(String.valueOf(fnum));
				Document doc = Jsoup.parse(new File(path + fn), "utf-8");
				Elements tags = doc.body().getElementsByTag("div");
				//Traverse all tags that have id and class
				for(Element tag : tags){
					//get class of tag
					Set<String> cls = tag.classNames();
					if(cls != null && !cls.isEmpty()){
						for(String cl : cls){
							if(cl == null || cl.length() <= 0)
								continue;
							if(clKeys.contains(cl)){
								int i = clKeys.indexOf(cl);
								int n = wCl.get(i).intValue();
								wCl.set(i, ++n);
							}
							else{
								clKeys.add(cl);
								wCl.add(new Integer(1));
							}
						}
					}
					//get id of tag
					String id = tag.id();
					if(id != null && id.length() <= 0) continue;
					if(idKeys.contains(id)){
						int i = idKeys.indexOf(id);
						int n = wId.get(i).intValue();
						wId.set(i, ++n);
					}
					else{
						idKeys.add(id);
						wId.add(new Integer(1));
					}

				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
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
					wCl.add(new Integer(Integer.parseInt(pair[2])));
					//System.out.println(pair[1]);
				}
				if(pair[0].equals("id")){
					idKeys.add(pair[1]);
					wId.add(new Integer(Integer.parseInt(pair[2])));
					//System.out.println(pair[1]);
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	
	private void saveRecord(){
		String out = "";
		for(int i = 0; i < wCl.size(); ++i){
			int n = wCl.get(i).intValue();
			if(n >= thresh){
				String cl = clKeys.get(i);
				//if(cl.toLowerCase().contains("method"))
				out += "class\t" +  cl + "\t" + String.valueOf(n) + "\r\n";
			}
		}
		for(int i = 0; i < wId.size(); ++i){
			int n = wId.get(i).intValue();
			if(n >= thresh){
				String id = idKeys.get(i);
				//if(id.toLowerCase().contains("method"))
				out += "id\t" + id + "\t" + String.valueOf(n) + "\r\n";
			}
		}
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("result.txt")));
			w.write(out);
			w.flush();
			w.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
