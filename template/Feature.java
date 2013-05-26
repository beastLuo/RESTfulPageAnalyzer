package template;

public class Feature {
	private String value = "";
	private String[] clue = {"",""};
	private boolean isValue;
	public Feature(String v){
		this.value = v;
		this.isValue = true;
	}
	public Feature(String k, String t){
		this.clue[0] = k;
		this.clue[1] = t;
		this.isValue = false;
	}
	public boolean isValue(){
		return isValue;
	}
	public String value(){
		return value;
	}
	public String[] clue(){
		return clue;
	}
}
