package scripts;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class makeKeyword {
	private String input_file;
	private String output_file = "./index.xml";

	public static String arrayJoin(String glue, String array[]) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			result += array[i];
			if (i < array.length - 1)
				result += glue;
		}
		return result;
	}

	public makeKeyword(String file) throws Exception {
		this.input_file = file;
		File file1 = new File(input_file);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document document = docBuilder.parse(file1);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("doc");
		org.w3c.dom.Document docu = docBuilder.newDocument();
		Element docs = docu.createElement("docs");
		docu.appendChild(docs);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			String titleData=eElement.getElementsByTagName("title").item(0).getTextContent();
			String bodyData= eElement.getElementsByTagName("body").item(0).getTextContent();
			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(bodyData, true);
			String a[] = new String[kl.size()];
			for( int k=0; k<kl.size(); k++) {
				Keyword kwrd = kl.get(k);
				a[k]=kwrd.getString() + ":" + kwrd.getCnt();
			}
			String newdata=arrayJoin("#", a);
			Element doc= docu.createElement("doc");
			docs.appendChild(doc);
			doc.setAttribute("id", String.valueOf(temp));
			Element title=docu.createElement("title");
			title.appendChild(docu.createTextNode(titleData));
			doc.appendChild(title);
			Element body=docu.createElement("body");
			body.appendChild(docu.createTextNode(newdata));
			doc.appendChild(body);					
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		DOMSource source = new DOMSource(docu);
		StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));
		transformer.transform(source,result);
		
		
	}
	public void convertXml(){
		System . out . println ( "3주차 과제 실행" );
		}
}