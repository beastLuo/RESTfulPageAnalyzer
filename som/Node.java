package som;

import java.util.ArrayList;

import org.jsoup.nodes.Attributes;

public abstract class Node {
	protected String name;
	protected Attributes attr;
	protected String label;
	protected ArrayList<Node> children;
	protected Node parent;
	protected String id;
	
	public void appendChild(Node child)
	{
		this.children.add(child);
	}
	public void removeChild(Node child)
	{
		this.children.remove(child);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Attributes getAttr() {
		return attr;
	}
	public void setAttr(Attributes attr) {
		this.attr = attr;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public ArrayList<Node> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
