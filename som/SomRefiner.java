package som;

import java.util.ArrayList;
import java.util.Iterator;

public class SomRefiner {
	public SomRefiner(){}
	private int distThreshhold = 5;
	private int countThreshhold = 1;
	private SomTree st;
	public SomTree refineSom(SomTree tree)
	{
		this.st = tree;
		refineLeafNode();
		//refineInCounter();
		return st;
	}
	
	private void refineLeafNode()
	{
		NodeForSom root = st.getRoot();
		Iterator<Node> i = root.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForSom child = (NodeForSom)i.next();
			traverseFirstInPre(child, i);
		}
	}
	private void traverseFirstInPre(NodeForSom root, Iterator<Node> ite)
	{
		Iterator<Node> i = root.getChildren().iterator();
		if(!root.hasText())
			ite.remove();
		while(i.hasNext())
		{
			NodeForSom child = (NodeForSom)i.next();
			//当前节点为下列标签时，后序遍历将该节点的所有子孙合并到该节点上
			if(child.getName().equals("h1") || child.getName().equals("h2")|| child.getName().equals("h3")
					|| child.getName().equals("h4")|| child.getName().equals("h5")|| child.getName().equals("h6")
					|| child.getName().equals("ul")|| child.getName().equals("table")|| child.getName().equals("p")
					|| child.getName().equals("pre")|| child.getName().equals("ol")|| child.getName().equals("dl"))
			{
				Iterator<Node> j = child.getChildren().iterator();
				while(j.hasNext())
				{
					NodeForSom node = (NodeForSom) j.next();
					traverseFirstInPos(node, 0, j);
				}
			}
			traverseFirstInPre((NodeForSom)child, i);
		}
	}
	private void traverseFirstInPos(NodeForSom root, int depth, Iterator<Node> rootPtr)
	{
		depth++;
		ArrayList<Node> children = root.getChildren();
		Iterator<Node> i = children.iterator();
		while(i.hasNext())
		{
			traverseFirstInPos((NodeForSom)i.next(), depth, i);
		}
		if(root.getChildren().size() == 0)
		{
			NodeForSom p = (NodeForSom)root.getParent();
			p.appendPageList(root.getPageList());
			rootPtr.remove();
		}
	}
	private void refineInCounter()
	{
		NodeForSom root = st.getRoot();
		Iterator<Node> i = root.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForSom child = (NodeForSom)i.next();
			traverseSecondInPos(child, i);
		}
	}
	private void traverseSecondInPos(NodeForSom root, Iterator<Node> rootPtr)
	{
		Iterator<Node> i = root.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForSom child = (NodeForSom)i.next();
			traverseSecondInPos(child, i);
		}
		if(root.getCounter() < countThreshhold)
		{
			NodeForSom p = (NodeForSom)root.getParent();
			p.appendPageList(root.getPageList());
			//p.setCounter(p.getCounter() + root.getCounter());
			rootPtr.remove();
		}
	}
}

