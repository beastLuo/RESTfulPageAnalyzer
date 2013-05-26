package som;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Element;

public class SomParser extends Parser{
	public SomParser(){}
	protected void setAttributes(Element e, Node node)
	{
		NodeForSom n = (NodeForSom) node;
		e.setAttribute("label", n.label);
		e.setAttribute("counter", String.valueOf(n.getCounter()));
		e.setAttribute("aveTextSize", String.valueOf(n.getAveTextSize()));
		e.setAttribute("isTemplate", String.valueOf(n.isTemplateNode()));
//		e.setAttribute("hasText", String.valueOf(n.hasText()));
		if(!n.getId().equals("")) e.setAttribute("id", n.getId());
		if(n.getCounter() < 10)
			e.setTextContent(getPageList(n));
	}
	private String getPageList(NodeForSom n)
	{
		HashMap<String, Integer> names = n.getPageList();
		Iterator<String> i = names.keySet().iterator();
		String content = "";
		while(i.hasNext())
		{
			content += i.next() + "<br/>";
		}
		return content;
	}
}
