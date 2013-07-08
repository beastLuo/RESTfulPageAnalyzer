package som;

import java.io.File;
import java.util.ArrayList;

public class ServicePageSet {
	public ServicePageSet(){}
	private static String targetPath;
	private static String serviceName;
	private static String provider;
	private static ArrayList<Page> pageList;

	public static void doInit(String path, SomTree st)throws Exception
	{
		traverse(path, st);
	}
	private static void traverse(String path, SomTree st)throws Exception
	{
		File target = new File(path);
		String[] list = target.list();
		for(String fn : list)
		{
			File f = new File(path + fn);
			if(f.isDirectory())
				traverse(path + fn + "/", st);
			if(!fn.contains(".html") && !fn.contains(".htm")) continue;
			Page p = new Page();
			p.initPage(fn, path + fn);
			st.init(p);
//			Tool.print(fn);
		}
	}
}
