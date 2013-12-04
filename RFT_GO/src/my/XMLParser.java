package my;

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
	/**
	 * A paraméterül kapott elérésiúton lévõ fájlból Field objektum listát alkot.
	 * 
	 * **/
	public List<Field> parse(String file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//dbf.setValidating(true);
		DocumentBuilder parser;
		List<Field> list=new ArrayList<Field>();
		try {
			parser = dbf.newDocumentBuilder();
			Document doc = parser.parse(file);
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++){			
				NodeList nl2 =nl.item(i).getChildNodes();
					for (int j = 1; j < nl2.getLength(); j=j+2) {
						Element ftag = (Element) nl2.item(j);	
						String id = ftag.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();		
						String desc = ftag.getElementsByTagName("descrpition").item(0).getFirstChild().getNodeValue();
						String comm = ftag.getElementsByTagName("command").item(0).getFirstChild().getNodeValue();
						list.add(new Field(Integer.parseInt(id),desc,comm));							
					}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	/* példa futás törlendõ
	public static void main(String[] args) {
		XMLParser p=new XMLParser();
		List<Field> fields=p.parse("Fields.xml");
		List<Field> luckyCards=p.parse("LuckyCards.xml");
	}
	*/
}
