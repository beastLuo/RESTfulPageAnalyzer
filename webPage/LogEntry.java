package webPage;

public class LogEntry {
	private String fileName = "";		//文件名
	private String filePath = "";		//文件路径
	private String matchedItems = "";	//匹配项，记录标注成功的项
	private String unmatchedItems = "";  //不匹配项，记录标注失败的项
	
	public LogEntry(String fn){
		fileName = fn;
	}
	
	public LogEntry(String fn, String fp){
		fileName = fn;
		filePath = fp;
	}
	
	public void setName(String fn){
		fileName = fn;
	} 
	
	public void setPath(String fp){
		filePath = fp;
	}
	
	public String fileName(){
		return fileName;
	}
	
	public String filePath(){
		return filePath;
	}
	
	public void addMatched(String item){
		if(matchedItems.equals("")){
			matchedItems = item;
		}else{
			matchedItems = matchedItems.concat(" # " + item);
		}
	}
	
	public void addUnmatched(String item){
		unmatchedItems.concat(" # " + item);
	}
	
	public String matchedItems(){
		return matchedItems;
	}
	
	public String unmatchedItems(){
		return unmatchedItems;
	}
	
	public void log() throws Exception{
		if(matchedItems.equals("")) 
			return;
		String out = "fileName: \t" + fileName + "\r\n";
		out = out.concat("filePath: \t" + filePath + "\r\n");
		out = out.concat("matchedItems: \t" + matchedItems + "\r\n");
		out = out.concat("=====================================================================================\r\n");
		//out.concat("unmatchedItems: \t" + unmatchedItems + "\n");
		Log.log(out);
	}
}
