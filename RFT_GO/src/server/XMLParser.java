package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLParser {
	
	
	private List<?> parse(String fileName){		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		List<Object> list=new ArrayList<>();
		try {
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(fileName);
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++){			
				NodeList nl2 =nl.item(i).getChildNodes();
					for (int j = 1; j < nl2.getLength(); j=j+2) {
						Element ftag = (Element) nl2.item(j);	
						String id = ftag.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();		
						String desc = ftag.getElementsByTagName("descrpition").item(0).getFirstChild().getNodeValue();
						String comm = ftag.getElementsByTagName("command").item(0).getFirstChild().getNodeValue();
						if (nl.item(i).getNodeName().equals("LuckyCards")) {
							list.add(new LuckyCard(Integer.parseInt(id),desc,comm));
						}else if (nl.item(i).getNodeName().equals("Fields")) {
							list.add(new Field(Integer.parseInt(id),desc,comm));
						}
													
					}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return list;
		
	}

	@SuppressWarnings("unchecked")
	public List<LuckyCard> parseLuckyCards(String file){
		return (List<LuckyCard>) parse(file);	
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> parseField(String file){
		return (List<Field>) parse(file);
	}
}
