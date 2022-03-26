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

public class indexer {

	@SuppressWarnings({ "rawtypes", "unchecked", "nls" })

	private String input_file;
	private String output_file = "./index.post";

	public indexer(String file) throws Exception {
		this.input_file = file;
		File file1 = new File(input_file);

	
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(file1);

		
		document.getDocumentElement().normalize();

		NodeList bodyList = document.getElementsByTagName("body");
		int b = bodyList.getLength();
		
		String str[] = new String[b];

		FileOutputStream fileStream = new FileOutputStream(output_file);

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
		HashMap[] hashmap = new HashMap[5];
		HashMap allhash = new HashMap();
		for (int i = 0; i < hashmap.length; i++)
			hashmap[i] = new HashMap();

		for (int i = 0; i < b; i++) {

			str[i] = bodyList.item(i).getTextContent(); 

			String wrd[] = str[i].split("#");

			for (int j = 0; j < wrd.length ; j++) {
				String wrd_cnt[] = wrd[j].split(":"); 
				hashmap[i].put(wrd_cnt[0], wrd_cnt[1]); 
				allhash.put(wrd_cnt[0], wrd_cnt[1]);
			}

		}
		Iterator<String> keyvalue = allhash.keySet().iterator();
		 
		while (keyvalue.hasNext()) {
			String key = keyvalue.next();
			for (int i = 0; i < 5; i++) {
				int n=0;
				for (int k = 0; k < 5; k++) {
					
					String cnt = (String) hashmap[k].get(key); 
					if (cnt == null)
						cnt = "0.0"; 
					double cnts = Double.parseDouble(cnt);
					if (cnts != 0.0)
						n++;
				}
				String cnt = (String) hashmap[i].get(key); 
				if (cnt == null)
					cnt = "0.0"; 
				double cnts = Double.parseDouble(cnt); 
				String value = (String) allhash.get(key);
				double tfidf = cnts * Math.log(b / n);
				if(i==0)
					value="";
				String value_tfidf = value + " " + Integer.toString(i)+ " " + Math.round(tfidf*100)/100.0;
				allhash.put(key, value_tfidf);
			}
		}
	
		objectOutputStream.writeObject(allhash);
		objectOutputStream.close();

		FileInputStream fileStream2 = new FileInputStream(output_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream2);
		Object object = objectInputStream.readObject();
		objectInputStream.close();

		System.out.println("읽어온 객체의 type->"+object.getClass());
		HashMap hashmap2 = (HashMap) object;
		Iterator<String> it = hashmap2.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			String value = (String) hashmap2.get(key);
			System.out.println(key + "->" + value);
		}

	}



	public void indexerXml() {
		System.out.println("4주차 과제 실행");
	}

}
