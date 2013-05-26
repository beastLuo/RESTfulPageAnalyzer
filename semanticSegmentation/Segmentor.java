package semanticSegmentation;
import java.io.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.*;
import org.jsoup.select.Elements;

import template.TemplateParser;
import template.TemplateSelector;
import webPage.Log;

import annotate.Annotator;

import java.util.Iterator;

public class Segmentor {
	private static long failCount = 0;
	//读取网页，进行处理并输出，主要步骤分为：
	//1. 预处理 preprocess()
	//2. 匹配模板 matchTemplate()
	//3. 提取主要内容 extract()
	//4. 分块 divideInLayer()
	//5. 标注 Annotator.annotate()
	public static void transform(File source, File result)throws Exception{
		Document doc = Jsoup.parse(source, "utf-8");
		preprocess(doc);
		String title = doc.title().toLowerCase();
		String matchedName = matchTemplate(title);
		if(matchedName.equals("")){
			Log.log("Error: Template Matching Failed.\r\nFile:" + source.getPath() + "\r\n");
			failCount++;
			return;
		}
		doc = Jsoup.parse(Jsoup.clean(doc.toString(), Whitelist.relaxed()));
		doc = extract(doc);
		
		//通过统计Header类型不能准确地得出Header嵌套的层数
//		int count = 0;
//		for(int i = 1; i <= 6; i++){
//			if(doc.getElementsByTag("h"+String.valueOf(i)).size() != 0){
//				count++;
//			}
//		}
//		for(int i = 2; i <= count; i++){
//			divideInLayer(i, doc);
//		}
		
		//检测分块中是否含有嵌套的Header，若有则进一步分块，直到所有Header分块完毕
		int count = 2;
		while(hasHeaderInLayerN(count, doc)){
			divideInLayer(count, doc);
			count++;
		}
		
		doc = Jsoup.parse(doc.toString());
		
		boolean success = Annotator.annotate(doc, TemplateParser.parseTemplate(TemplateSelector.getTemplate(matchedName)));
		if(!success){
			Log.log("Error: Annotation Failed, Template does not actually match this page.\r\nFile:" + source.getPath() + "\r\n");
			failCount++;
			return;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));
		bw.write(doc.toString());
		bw.flush();
		bw.close();
		System.out.println(result.getPath());
	}
	
	//打印处理失败的条目数
	public static void printFailure(){
		System.out.println("failCount:" + String.valueOf(failCount));
	}
	
	//预处理函数，处理Header嵌套的情况，例如：h1标签被若干个div标签包含。
	//处理方法是把嵌套去除，由于嵌套一般是为了改变标签的表现，因此去除后不会影响结构
	private static void preprocess(Document doc){
		int hNum;
		for(hNum = 1; hNum <= 6; hNum++){
			distillHeader(doc, "h"+String.valueOf(hNum));
		}
	}
	
	//提取被嵌套的Header，将其取代嵌套的根节点
	private static void distillHeader(Document doc, String header){
		Elements headers = doc.getElementsByTag(header);
		if(headers == null || headers.size() == 0){
			return;
		}
		for(Element h: headers){
			if(h.nextElementSibling() == null){
				Element parent = h.parent();
				while(parent.nextElementSibling() == null){
					parent = parent.parent();
				}
				parent.replaceWith(h);
			}
		}
	}
	
	//将网页的title信息和模板文件名进行比较，返回匹配的文件名
	private static String matchTemplate(String title){
		String[] templateList = TemplateSelector.getList();
		String matchedName = "";
		for(String fn : templateList){
			String[] names = fn.split("\\.")[0].split("#");
			for(String name : names){
				if(title.contains(name)){
					matchedName = name;
				}
			}
		}
		return matchedName;
	}
	
	//对源网页进行提取，创建第1级分块
	private static Document extract(Document doc){
		Document newDoc = new Document("");
		traverseInPreFirst(doc, newDoc);
		return newDoc;
	}
	
	//先序遍历创建第1级分块，进行破坏式内容提取：仅提取第1级Header标签以及标签之后的内容，忽略其它内容，会改变网页结构
	private static void traverseInPreFirst(Element parent, Element body){
		Elements children = parent.children();
		if(children == null || children.size() == 0){
			return;
		}
		for(Element child : children){
			//检测到Header标签时，进行分块操作
			if(child.tagName().equals("h1") || child.tagName().equals("h2") || child.tagName().equals("h3")
					|| child.tagName().equals("h4") || child.tagName().equals("h5") || child.tagName().equals("h6")){
				Element div = new Element(Tag.valueOf("div"), "").attr("class", "layer1");
				String curHeaderName = child.tagName();
				div.appendChild(child.clone().addClass("header1"));
				Element nextSib = child.nextElementSibling();
				//遍历当前Header标签层的兄弟节点
				while(nextSib != null){
					//遍历兄弟节点过程中遇到另一个同级的Header标签，则保存现有的分块，创建一个新的分块
					if(nextSib.tagName().equals(curHeaderName)){
						body.appendChild(div.clone());
						div = new Element(Tag.valueOf("div"), "").attr("class", "layer1");
					}
					div.appendChild(nextSib.clone());
					nextSib = nextSib.nextElementSibling();
				}
				body.appendChild(div);
				return;
			}else{
				traverseInPreFirst(child, body);
			}
		}
	}
	
	//先序遍历创建第n级分块，在原结构上进行改造，将第n级Header标签和之后的内容整合到一个div标签中
	private static void traverseInPre(Element parent, int layerN){
		Elements children = parent.children();
		if(children == null || children.size() == 0){
			return;
		}
		for(Element child : children){
			if(child.tagName().equals("h1") || child.tagName().equals("h2") || child.tagName().equals("h3")
					|| child.tagName().equals("h4") || child.tagName().equals("h5") || child.tagName().equals("h6")){
				Element div = new Element(Tag.valueOf("div"), "").attr("class", "layer"+String.valueOf(layerN));
				Element curHeader = child;
				div.appendChild(child.clone().addClass("header"+String.valueOf(layerN)));
				Element nextSib = child.nextElementSibling();
				Element curSib;
				while(nextSib != null){
					if(nextSib.tagName().equals(curHeader.tagName())){
						curHeader.replaceWith(div);
						div = new Element(Tag.valueOf("div"), "").attr("class", "layer"+String.valueOf(layerN));
						curHeader = nextSib;
						div.appendChild(nextSib.clone());
						nextSib = nextSib.nextElementSibling();
					}else{
						div.appendChild(nextSib.clone());
						curSib = nextSib;
						nextSib = nextSib.nextElementSibling();
						curSib.remove();
					}
				}
				curHeader.replaceWith(div);
				return;
			}else{
				traverseInPre(child, layerN);
			}
		}
	}
	
	//判断是否需要继续进行迭代分块，依据是在最深一级的分块中是否含有未分块的Header
	private static boolean hasHeaderInLayerN(int layerN, Document doc){
		Elements divs = doc.getElementsByClass("layer"+String.valueOf(layerN-1));
		for(Element div : divs){
			Elements tags = div.select("h1, h2, h3, h4, h5, h6");
			for(Element tag : tags){
				if(!tag.className().contains("header")){
					return true;
				}
			}
		}
		return false;
	}
	
	//迭代分块函数，获取class=layer(n-1)的所有分块，进行第n级分块操作
	private static void divideInLayer(int layerN, Document doc){
		Elements divs = doc.getElementsByClass("layer"+String.valueOf(layerN-1));
		for(Element div : divs){
			//重写第n-1级分块，改写成Header标签+Div标签的双标签结构
			Element newDiv = new Element(div.tag(), "").attr("class", div.className());
			Iterator<Element> i  = div.children().iterator();
			newDiv.appendChild(i.next().clone());
			Element layerNBody = new Element(Tag.valueOf("div"), "").attr("class", "bodyLayer"+String.valueOf(layerN-1));
			newDiv.appendChild(layerNBody);
			while(i.hasNext()){
				layerNBody.appendChild(i.next().clone());
			}
			traverseInPre(layerNBody, layerN);
			//覆盖重构之后的第n-1级分块
			div.replaceWith(newDiv);
		}
	}
}
