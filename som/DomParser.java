package som;

import org.w3c.dom.Element;

public class DomParser extends Parser{
	public DomParser(){}
	protected void setAttributes(Element e, Node node)
	{
		NodeForDom n = (NodeForDom) node;
		e.setAttribute("label", n.label);
		e.setAttribute("flag", n.isFlag()?"true":"false");
	}
}
