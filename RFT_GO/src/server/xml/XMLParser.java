package server.xml;

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

import server.Field;
import server.LuckyCard;


/**
 * Osztály az XML feldolgozáshoz.
 * 
 * @author Ölveti József
 *
 */
public class XMLParser {
	/**
	 * Metódus XML dokumentumok feldolgozására.
	 * 
	 * @param fileName a dokumentum neve
	 * @return objektum lista, mely tartalmazhat tetszőleges objektumokat is
	 */
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

	/**
	 * Metódus LuckyCard objektumokat leíró XML dokumentumok feldolgozásához.
	 * 
	 * @param fileName a dokumentum neve
	 * @return LuckyCard objektumok listája
	 */
	@SuppressWarnings("unchecked")
	public List<LuckyCard> parseLuckyCards(String fileName){
		return (List<LuckyCard>) parse(fileName);	
	}
	
	/**
	 * Metódus Field objektumokat leíró XML dokumentumok feldolgozásához.
	 * 
	 * @param fileName a dokumentum neve
	 * @return Field objektumok listája
	 */
	@SuppressWarnings("unchecked")
	public List<Field> parseFields(String fileName){
		return (List<Field>) parse(fileName);	
	}
}
