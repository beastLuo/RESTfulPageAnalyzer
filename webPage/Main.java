package webPage;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import semanticSegmentation.Segmentor;

import template.TemplateParser;
import template.TemplateSelector;


public class Main {

	/**
	 * @param args
	 */
	
	private static void traverse(String path){
		File dir = new File(path);
		String[] list = dir.list();
		for(String fn : list){
			File f = new File(path + fn);
			if(f.isDirectory()){
				traverse(path + fn + "/");
			}else{
				if(!fn.contains(".htm") && !fn.contains(".html")){
					continue;
				}
				String resultPath = path.replace("./test/", "./result/");
				File resultDir = new File(resultPath);
				if(!resultDir.exists()){
					resultDir.mkdirs();
				}
				File result = new File(resultPath + fn);
				try{
					Segmentor.transform(f, result);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		try{
 			traverse("./test/");
 			Segmentor.printFailure();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
}

