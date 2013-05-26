package semanticSegmentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import som.Node;
import som.NodeForSom;
import som.SomTree;

public class Distiller {
	public Distiller(){}
	private NodeForSom headerTree;
	public NodeForSom doDistill(SomTree st)
	{
		NodeForSom root = st.getRoot();
		headerTree = new NodeForSom();
		headerTree.copyNode(root);
		traverseInPos(root, headerTree);
		return headerTree;
	}
	private boolean traverseInPos(NodeForSom root, NodeForSom hParent)
	{
		boolean flag = false;
		Iterator<Node> i = root.getChildren().iterator();
		while(i.hasNext())
		{
			NodeForSom child = (NodeForSom)i.next();
			NodeForSom hChild = new NodeForSom();
			hChild.copyNode(child);
			if(child.getChildren().size() != 0)
			{
				if(traverseInPos(child, hChild) == true)
				{
					hParent.appendChild(hChild);
					hChild.setParent(hParent);
					flag = true;
				}
			}else{
				String tag = child.getName();
				if(tag.equals("h1") || tag.equals("h2") || tag.equals("h3") || 
						tag.equals("h4") || tag.equals("h5") || tag.equals("h6"))
				{
					if(!child.isTemplateNode()){
						continue;
					}
					hParent.appendChild(hChild);
					hChild.setParent(hParent);
					flag = true;
				}
			}
		}
		return flag;
	}
	public NodeForSom getHeaderTree() {
		return headerTree;
	}
	public void setHeaderTree(NodeForSom headerTree) {
		this.headerTree = headerTree;
	}
	
}
