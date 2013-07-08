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
import webPage.Tool;
import webPage.LogEntry;

import annotate.Annotator;

import java.util.Iterator;

public class Segmentor {
	private static long failCount = 0;
	private static long total = 0;
	//��ȡ��ҳ�����д����������Ҫ�����Ϊ��
	//1. Ԥ���� preprocess()
	//2. ƥ��ģ�� matchTemplate()
	//3. ��ȡ��Ҫ���� extract()
	//4. �ֿ� divideInLayer()
	//5. ��ע Annotator.annotate()
	public static void transform(File source, File result)throws Exception{
		total++;
		Document doc = Jsoup.parse(source, "utf-8");
		preprocess(doc);
		String title = doc.title().toLowerCase();
		String matchedName = "amazon";//matchTemplate(title);
		if(matchedName.equals("")){
//			Log.log("Error: Template Matching Failed.\r\nFile:" + source.getPath() + "\r\n");
			failCount++;
			return;
		}
		doc = Jsoup.parse(Jsoup.clean(doc.toString(), Whitelist.relaxed()));
		doc = extract(doc);
		
		//���ֿ����Ƿ���Ƕ�׵�Header���������һ���ֿ飬ֱ������Header�ֿ����
		int count = 2;
		while(hasHeaderInLayerN(count, doc)){
			divideInLayer(count, doc);
			count++;
		}
		
		doc = Jsoup.parse(doc.toString());
		
		LogEntry entry = new LogEntry(source.getName(), source.getAbsolutePath());
		boolean success = Annotator.annotate(doc, TemplateParser.parseTemplate(TemplateSelector.getTemplate(matchedName)), entry);
		entry.log();
		if(!success){
//			Log.log("Error: Annotation Failed, Template does not actually match this page.\r\nFile:" + source.getPath() + "\r\n");
			failCount++;
			return;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));
		bw.write(doc.toString());
		bw.flush();
		bw.close();
		Tool.print(result.getPath());
	}
	
	//��ӡ����ʧ�ܵ���Ŀ��
	public static void printFailure(){
		Tool.print("total:" + String.valueOf(total));
		Tool.print("failCount:" + String.valueOf(failCount));
	}
	
	//Ԥ������������HeaderǶ�׵���������磺h1��ǩ�����ɸ�div��ǩ������
	//�������ǰ�Ƕ��ȥ��������Ƕ��һ����Ϊ�˸ı��ǩ�ı��֣����ȥ���󲻻�Ӱ��ṹ
	private static void preprocess(Document doc){
		int hNum;
		for(hNum = 1; hNum <= 6; hNum++){
			distillHeader(doc, "h"+String.valueOf(hNum));
		}
	}
	
	//��ȡ��Ƕ�׵�Header������ȡ��Ƕ�׵ĸ��ڵ�
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
	
	//����ҳ��title��Ϣ��ģ���ļ������бȽϣ�����ƥ����ļ���
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
	
	//��Դ��ҳ������ȡ��������1���ֿ�
	private static Document extract(Document doc){
		Document newDoc = new Document("");
		traverseInPreFirst(doc, newDoc);
		return newDoc;
	}
	
	//�������������1���ֿ飬�����ƻ�ʽ������ȡ������ȡ��1��Header��ǩ�Լ���ǩ֮������ݣ������������ݣ���ı���ҳ�ṹ
	private static void traverseInPreFirst(Element parent, Element body){
		Elements children = parent.children();
		if(children == null || children.size() == 0){
			return;
		}
		for(Element child : children){
			//��⵽Header��ǩʱ�����зֿ����
			if(child.tagName().equals("h1") || child.tagName().equals("h2") || child.tagName().equals("h3")
					|| child.tagName().equals("h4") || child.tagName().equals("h5") || child.tagName().equals("h6")){
				Element div = new Element(Tag.valueOf("div"), "").attr("class", "layer1");
				String curHeaderName = child.tagName();
				div.appendChild(child.clone().addClass("header1"));
				Element nextSib = child.nextElementSibling();
				//������ǰHeader��ǩ����ֵܽڵ�
				while(nextSib != null){
					//�����ֵܽڵ������������һ��ͬ����Header��ǩ���򱣴����еķֿ飬����һ���µķֿ�
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
	
	//�������������n���ֿ飬��ԭ�ṹ�Ͻ��и��죬����n��Header��ǩ��֮����������ϵ�һ��div��ǩ��
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
	
	//�ж��Ƿ���Ҫ�������е����ֿ飬������������һ���ķֿ����Ƿ���δ�ֿ��Header
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
	
	//�����ֿ麯������ȡclass=layer(n-1)�����зֿ飬���е�n���ֿ����
	private static void divideInLayer(int layerN, Document doc){
		Elements divs = doc.getElementsByClass("layer"+String.valueOf(layerN-1));
		for(Element div : divs){
			//��д��n-1���ֿ飬��д��Header��ǩ+Div��ǩ��˫��ǩ�ṹ
			Element newDiv = new Element(div.tag(), "").attr("class", div.className());
			Iterator<Element> i  = div.children().iterator();
			newDiv.appendChild(i.next().clone());
			Element layerNBody = new Element(Tag.valueOf("div"), "").attr("class", "bodyLayer"+String.valueOf(layerN-1));
			newDiv.appendChild(layerNBody);
			while(i.hasNext()){
				layerNBody.appendChild(i.next().clone());
			}
			traverseInPre(layerNBody, layerN);
			//�����ع�֮��ĵ�n-1���ֿ�
			div.replaceWith(newDiv);
		}
	}
}
