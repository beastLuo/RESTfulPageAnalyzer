package som;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SomTree {
	public SomTree(){}
	private NodeForSom root = null;
	private SomParser parser = null;
	private String serviceName;
	private String provider;
	private HashMap<String, Integer> textSizeSum = new HashMap<String, Integer>();
	private HashMap<String, Integer> nodeCounter = new HashMap<String, Integer>();
//	public void init(ArrayList<Page> pages)
//	{
//		Iterator<Page> i = pages.iterator();
//		while(i.hasNext())
//		{
//			Page p = i.next();
//			constructSom(p);
//		}
//	}
	public void init(Page p)
	{
		constructSom(p);
	}
	private void constructSom(Page p)
	{
		if(root == null)
			root = new NodeForSom(p.getRoot());
		else root.counterIncre();
		root.setAveTextSize(computeAveTextSize(root.getLabel(), p.getRoot(), root));
		traversePageInPre(root, p.getRoot(), p.getPageName());
	}
	
	private int computeAveTextSize(String label, NodeForDom node, NodeForSom sn)
	{
		if(!nodeCounter.containsKey(label))
		{
			nodeCounter.put(label, 1);
			textSizeSum.put(label, node.getTextSize());
		}
		int counter = nodeCounter.get(label).intValue();
		int sum = textSizeSum.get(label).intValue();
		if(node.getTextSize() != sum/counter)
			sn.setTemplateNode(false);
		counter++;
		sum += node.getTextSize();
		nodeCounter.put(label, counter);
		textSizeSum.put(label, sum);
		return sum/counter;
	}
	
	//�������ÿһ��Page����SomTree
	private void traversePageInPre(NodeForSom som, NodeForDom dom, String pName)
	{
		Iterator<Node> i = dom.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForDom child = (NodeForDom) i.next();
			NodeForSom n;
			//��SomTree���Ѵ��ڸýڵ㣬��Counter++���������һ���½ڵ�
			if(som.containChild(child.getLabel()))
			{
				n = som.getChild(child.getLabel());
				n.counterIncre();
				
				//����ÿ���ڵ��ƽ���ı�����
				n.setAveTextSize(computeAveTextSize(n.getLabel(), child, n));
				
				//���ýڵ������ı����ݣ��򽫸ýڵ�����и��ڵ�ı��hasText��Ϊtrue
				//Ŀ����Ϊ�����ع�SomTreeʱɾ�����ı����ݵ�����
				if(child.isFlag())
				{
					n.appendPage(pName);
					NodeForSom temp = n;
					while(temp != null)
					{
						temp.setHasText(true);
						temp = (NodeForSom)temp.getParent();
					}
				}
			}else{
				n = new NodeForSom(child);
				som.appendChild(n);
				n.setParent(som);
				n.setAveTextSize(computeAveTextSize(n.getLabel(), child, n));
				if(child.isFlag())
				{
					n.appendPage(pName);
					NodeForSom temp = n;
					while(temp != null)
					{
						temp.setHasText(true);
						temp = (NodeForSom)temp.getParent();
					}
				}
			}
			if(child.getChildren() != null && child.getChildren().size() != 0)
			{
				traversePageInPre(n, child, pName);
			}
		}
	}
	
	public String parseThis()throws Exception
	{
		parser = new SomParser();
		return parser.parse(root);
	}
	public NodeForSom getRoot() {
		return root;
	}
	public void setRoot(NodeForSom root) {
		this.root = root;
	}
	public SomParser getParser() {
		return parser;
	}
	public void setParser(SomParser parser) {
		this.parser = parser;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
