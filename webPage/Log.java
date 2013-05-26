package webPage;

import java.io.*;
import java.util.Date;
import java.text.DateFormat;

public class Log {
	private static String logPath = "./log/";
	private static boolean append = true;
	public static void log(String content)throws Exception{
		File dir = new File(logPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String curName = DateFormat.getDateInstance().format(new Date());
		String curTime = DateFormat.getTimeInstance().format(new Date()) + "\r\n";
		File f = new File(logPath + curName + ".log");
		BufferedWriter w = new BufferedWriter(new FileWriter(f, append));
		w.write(curTime);
		w.write(content);
		w.flush();
		w.close();
//		System.out.println(curTime + content);
	}
}
