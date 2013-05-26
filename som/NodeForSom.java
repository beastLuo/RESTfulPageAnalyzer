package som;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NodeForSom extends NodeForDom{
	public NodeForSom(){}
	public NodeForSom(Node node)
	{
		this.name = node.name;
		this.label = node.label;
		this.attr = node.attr;
		this.children = new ArrayList<Node>();
		this.parent = null;
		this.counter = 1;
		this.id = node.id;
	}
	
	public void copyNode(NodeForSom node)
	{
		this.name = node.name;
		this.label = node.label;
		this.attr = node.attr;
		this.children = new ArrayList<Node>();
		this.parent = null;
		this.counter = node.counter;
		this.id = node.id;
		this.aveTextSize = node.aveTextSize; 
		this.pageList = node.pageList;
		this.isTemplateNode = node.isTemplateNode;
	}
	private int counter;
	private HashMap<String, Integer> pageList = new HashMap<String, Integer>();
	private boolean hasText = false;
	private int aveTextSize = 0;
	private boolean isTemplateNode = true;
	
	public boolean containChild(String label)
	{
		Iterator<Node> i = children.iterator();
		while(i.hasNext())
		{
			Node n = i.next();
			if(n.label.equals(label))
				return true;
		}
		return false;
	}
	public NodeForSom getChild(String label)
	{
		Iterator<Node> i = children.iterator();
		while(i.hasNext())
		{
			Node n = i.next();
			if(n.label.equals(label))
				return (NodeForSom)n;
		}
		return null;
	}
	public void counterIncre()
	{
		this.counter++;
	}
	public void appendPageList(HashMap<String, Integer> list)
	{
		this.pageList.putAll(list);
	}
	public void appendPage(String p)
	{
		if(this.pageList.containsKey(p))
		{
			Integer v = pageList.get(p);
			pageList.put(p, v.intValue()+1);
		}
		else
			pageList.put(p, 1);
	}
	public void removePage(String p)
	{
		this.pageList.remove(p);
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public HashMap<String, Integer> getPageList() {
		return pageList;
	}
	public void setPageList(HashMap<String, Integer> pageList) {
		this.pageList = pageList;
	}
	public boolean hasText() {
		return hasText;
	}
	public void setHasText(boolean hasText) {
		this.hasText = hasText;
	}
	public int getAveTextSize() {
		return aveTextSize;
	}
	public void setAveTextSize(int aveTextSize) {
		this.aveTextSize = aveTextSize;
	}
	public boolean isTemplateNode() {
		return isTemplateNode;
	}
	public void setTemplateNode(boolean isTemplateNode) {
		this.isTemplateNode = isTemplateNode;
	}
	
}
