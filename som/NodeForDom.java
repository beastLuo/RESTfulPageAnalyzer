package som;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class NodeForDom extends Node{
	private boolean flag = false;
	private int textSize = 0;
	
	public NodeForDom(){}
	public NodeForDom(Element node)
	{
		this.name = node.tagName().replace("#", "");
		this.attr = node.attributes();
		if(!Pattern.matches("\\s*", node.ownText()) && node.ownText().length() != 0)
		{
			this.flag = true;
			this.textSize = node.ownText().trim().length();
		}
		this.children = new ArrayList<Node>();
		this.parent = null;
		this.id = node.id();
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
}
