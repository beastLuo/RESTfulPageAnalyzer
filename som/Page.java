package som;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	public Page(){}
	private String pageName;
	private String filePath;
	private NodeForDom root;
	private DomParser parser;
	
	public void initPage(String name, String path) throws Exception
	{
		this.pageName = Pattern.compile("\\W+").matcher(name).replaceAll(" ");
		this.filePath = path;
		process();
	}
	public String parserThis() throws Exception
	{
		parser = new DomParser();
		return parser.parse(root);
	}
	
	private void process() throws Exception
	{
		File input = new File(filePath);
		Document doc;
		try{
			doc = Jsoup.parse(input, "utf-8");
		}catch(Exception e){
			throw e;
		}
		root = new NodeForDom(doc);
		root.setLabel(doc.tagName().replace("#", ""));
		NodeForDom body = new NodeForDom(doc.body());
		body.setParent(root);
		root.appendChild(body);
		String label = doc.body().tagName() + "/" + getAttrNames(doc.body()) + "#" + body.getParent().getLabel();
		body.setLabel(label);
		initDom(body, doc.body());
		root.setTextSize(traverseInPosForTextSize(root));
	}
	
	//后序遍历计算各个内部节点的TextSize
	private int traverseInPosForTextSize(NodeForDom dom)
	{
		int sum = 0;
		Iterator<Node> i = dom.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForDom child = (NodeForDom)i.next();
			sum += traverseInPosForTextSize(child);
		}
		sum += dom.getTextSize();
		dom.setTextSize(sum);
		return sum;
	}
	
	//返回属性名的字符串
	private String getAttrNames(Element node)
	{
		Attributes attrs = node.attributes();
		String attrNames = "";
		if(!node.id().equals(""))
		{
			attrNames = "id:" + node.id();
		}else{
			Iterator<Attribute> i = attrs.iterator();
			while(i.hasNext())
			{
				Attribute attr = i.next();
				attrNames += attr.getKey() + "/";
			}
			if(attrNames.contains("/"))
				attrNames = attrNames.substring(0, attrNames.lastIndexOf("/"));
		}
		return attrNames;
	}
	
	//先序建树，用两个哈希表对label进行查重
	private void initDom(NodeForDom parent, Element node)
	{
		HashMap<String, Integer> labels = new HashMap<String, Integer>();
		HashMap<String, NodeForDom> firstNodes = new HashMap<String, NodeForDom>();
		Elements children = node.children();
		Iterator<Element> i = children.iterator();
		while(i.hasNext())
		{
			Element child = i.next();
			NodeForDom c = new NodeForDom(child);
			c.setParent(parent);
			parent.appendChild(c);
			String label = child.tagName() + "/" + getAttrNames(child) + "#" + parent.getLabel();
			if(!labels.containsKey(label))
			{
				c.setLabel(label);
				labels.put(label, new Integer(1));
				firstNodes.put(label, c);
			}else{
				int count = labels.get(label).intValue();
				if(count == 1)
				{
					NodeForDom first = firstNodes.get(label);
					first.setLabel(String.valueOf(count) + "." + first.getLabel());
					c.setLabel(String.valueOf(++count) + "." + label);
					labels.put(label, new Integer(count));
				}else{
					c.setLabel(String.valueOf(++count) + "." + label);
					labels.put(label, new Integer(count));
				}
			}
			if(child.children() != null && child.children().size() != 0)
			{
				initDom(c, child);
			}
		}
	}
	
	public NodeForDom getRoot() {
		return root;
	}

	public void setRoot(NodeForDom root) {
		this.root = root;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
