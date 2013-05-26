package som;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Parser {
	public Parser(){}
	protected abstract void setAttributes(Element e, Node node);
	public String parse(Node root) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder b = factory.newDocumentBuilder();
		Document doc = b.newDocument();
		Element e = doc.createElement(root.getName());
		doc.appendChild(e);
		setAttributes(e, root);
		navChildren(root, e, doc);
		
		return transform(doc);
	}
	
	private void navChildren(Node root, Element node, Document doc)
	{
		ArrayList<Node> children = root.getChildren();
		Iterator<Node> i = children.iterator();
		while(i.hasNext())
		{
			Node child = i.next();
			Element e = doc.createElement(child.getName());
			node.appendChild(e);
			setAttributes(e, child);
			if(child.getChildren().size() != 0)
			{
				navChildren(child, e, doc);
			}
		}
	}
	private String transform(Document doc) throws Exception 
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tr = factory.newTransformer();
		tr.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		tr.transform(new DOMSource(doc), new StreamResult(out));
		return out.toString();
	}
}
