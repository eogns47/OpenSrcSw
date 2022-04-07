package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class searcher {

	private String input_file;
	private String query;
	
	public searcher(String file,String query) throws Exception {
		this.input_file = file;
		this.query=query;
		
	}

	public void calcsim22() throws Exception {
		
		File file1 = new File(input_file); //index.post 읽어오기
		File file2 = new File("./collection.xml"); //collection.xml 읽어오기
		
		HashMap hashQuery =new HashMap();  //Query의 해쉬맵 생성
	
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		for( int k=0; k<kl.size(); k++) {
			Keyword kwrd = kl.get(k);
			hashQuery.put(kwrd.getString(), kwrd.getCnt());    //Query의 형태소 분석 후 해쉬맵에 넣기.
		}
		
		FileInputStream fileStream = new FileInputStream(input_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		HashMap hashmap2 = (HashMap) object;
		
		Iterator<String> it = hashQuery.keySet().iterator();  // 질의(query)를 읽기 위한 객체 생성
		

		int i=0,j,k;

		double Q_id0=0; // 유사도
		double Q_id1=0; // 유사도
		double Q_id2=0; // 유사도
		double Q_id3=0; // 유사도
		double Q_id4=0; // 유사도
		String tfidf1[]=new String[kl.size()];
		String tfidf2[]=new String[kl.size()];
		String tfidf3[]=new String[kl.size()];
		String tfidf4[]=new String[kl.size()];
		String tfidf0[]=new String[kl.size()];
		String tfidf[]=new String[kl.size()];
		try {
		while (it.hasNext()) {
			String key = it.next();
			String value = String.valueOf( hashmap2.get(key));
			String value2 = String.valueOf( hashQuery.get(key)); // query의 가중치
			String split[]=value.split("\\s");
			tfidf0[i]=(split[2]); //한 단어에 대해 각각의 문서의 가중치를 배열에 넣어준다.
			tfidf1[i]=(split[4]);  
			tfidf2[i]=(split[6]);  
			tfidf3[i]=(split[8]); 
			tfidf4[i]=(split[10]); 
			tfidf[i]=value2;		           		
			i++;
			}
		
		
		for(i=0; i<kl.size();i++) {
			Q_id0+=Double.parseDouble(tfidf[i])*Double.parseDouble(tfidf0[i]);
			Q_id1+=Double.parseDouble(tfidf[i])*Double.parseDouble(tfidf1[i]);
			Q_id2+=Double.parseDouble(tfidf[i])*Double.parseDouble(tfidf2[i]);
			Q_id3+=Double.parseDouble(tfidf[i])*Double.parseDouble(tfidf3[i]);
			Q_id4+=Double.parseDouble(tfidf[i])*Double.parseDouble(tfidf4[i]);
			
		}
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("검색된 문서가 없습니다");
		}
		

		double a,b0,b1,b2,b3,b4;        //분모 값을 넣어줄 변수 생성후 초기화
		a=0;
		b0=0;
		b1=0;
		b2=0;
		b3=0;
		b4=0;
		
		
		for(k=0; k<kl.size();k++) {
			a += Math.pow(Double.parseDouble(tfidf[k]), 2);       //코사인 유사도 분모의 query 부분
		}
		for(j=0; j<kl.size();j++) {
			b0 += Math.pow(Double.parseDouble(tfidf0[j]), 2);     //0번 문서에서의 코사인 유사도 분모
			b1 += Math.pow(Double.parseDouble(tfidf1[j]), 2);
			b2 += Math.pow(Double.parseDouble(tfidf2[j]), 2);
			b3 += Math.pow(Double.parseDouble(tfidf3[j]), 2);
			b4 += Math.pow(Double.parseDouble(tfidf4[j]), 2);		
		}
		
		double denom0= Math.sqrt(a)*Math.sqrt(b0);                 //각각 유사도의 분모 
		double denom1= Math.sqrt(a)*Math.sqrt(b1);
		double denom2= Math.sqrt(a)*Math.sqrt(b2);
		double denom3= Math.sqrt(a)*Math.sqrt(b3);
		double denom4= Math.sqrt(a)*Math.sqrt(b4); //
		
		double cosSim0 = denom0==0 ? 0 :Q_id0 / denom0; 
		double cosSim1 = denom1==0 ? 0 :Q_id1 / denom1;
		double cosSim2 = denom2==0 ? 0 :Q_id2 / denom2;
		double cosSim3 = denom3==0 ? 0 :Q_id3 / denom3;
		double cosSim4 = denom4==0 ? 0 :Q_id4 / denom4;
		
	

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document document = docBuilder.parse(file2);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("doc");
		org.w3c.dom.Document docu = docBuilder.newDocument();
		
		String titles[]=new String[5];
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			String titleData=eElement.getElementsByTagName("title").item(0).getTextContent();
			titles[temp]=titleData;
		}
		
		HashMap<String,Double> map= new HashMap();

		map.put(titles[0], cosSim0);   
		map.put(titles[1], cosSim1);   
		map.put(titles[2], cosSim2);   
		map.put(titles[3], cosSim3);   
		map.put(titles[4], cosSim4);   


		
		
		List<String> keySet = new ArrayList<>(map.keySet());
		Collections.sort(keySet,(o1,o2)->(map.get(o2).compareTo(map.get(o1))));
		int e=0;
		
		
		for(String key : keySet) {
			if(map.get(key)!=0.0) {
			System.out.printf("Title : %s, query와의 유사도: %.2f \n",key,map.get(key));
			
			}
			e++;
			if(e==3)
				break;
	}
	}

			
		
		
	
		
		
	

		
		


	}

