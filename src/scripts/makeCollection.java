package scripts;

import java.io.File;
import java.io.FileOutputStream;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;





public class makeCollection {
	private String data_path;
	private String output_file = "./collection.xml";
	
	public static File[] makeFileList(String path) {
		File dir= new File(path);
		return dir.listFiles();
	}
	public makeCollection(String path) throws Exception {
		this.data_path = path;
		File[] file=makeFileList(data_path); 
		int i;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document docu = docBuilder.newDocument();
		Element docs = docu.createElement("docs");
		docu.appendChild(docs);
		
		for(i=0;i<file.length;i++) {
			Document html= Jsoup.parse(file[i],"UTF-8"); 
			String titledata = html.title();
			String bodyData= html.body().text(); 
			Element doc= docu.createElement("doc");
			docs.appendChild(doc);
			doc.setAttribute("id", String.valueOf(i));
			Element title=docu.createElement("title");
			title.appendChild(docu.createTextNode(titledata));
			doc.appendChild(title);
			Element body=docu.createElement("body");
			body.appendChild(docu.createTextNode(bodyData));
			doc.appendChild(body);
			}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		DOMSource source = new DOMSource(docu);
		StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));
		transformer.transform(source,result);
		}
	
		public void makeXml(){
		System.out.println( "2주차 과제 실행" );
		}
}