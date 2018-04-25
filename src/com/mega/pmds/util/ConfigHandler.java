package com.mega.pmds.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mega.pmds.RomManipulator;

public class ConfigHandler {
	private static ConfigHandler instance = null;
	private Document names;
	private XPath xpath;
	DocumentBuilder builder;
	
	private ConfigHandler() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			names = builder.parse("names.xml");
			xpath = XPathFactory.newInstance().newXPath();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void reload() throws SAXException, IOException {
		verifyInit();
		instance.names = instance.builder.parse("names.xml");
	}
	
	private static void verifyInit() {
		if(instance==null)
			init();
	}
	
	private static void init() {
		instance = new ConfigHandler();
	}
	
	public static String nameFromTypeAndOffset(LoadTask.Type type, int offset) {
		verifyInit();
		//Select data based on filename
		String fName = ((RomManipulator.getFilename()).split("\\."))[0];
		Element rom = null;
		try {
			rom = (Element)instance.xpath.evaluate("//*[@id='" + fName + "']", instance.names, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rom==null)
			return defaultNameFromTypeAndOffset(type, offset);
		String str = "";
		switch(type) {
			case AREA:
				str = "area";
				break;
			case SCENE_LIST:
				str = "scene";
				break;
			default:
				return "";
		}
		NodeList nameList = rom.getElementsByTagName(str);
		for(int i=0; i<nameList.getLength(); i++) {
			if(nameList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element name = (Element)nameList.item(i);
				if(Integer.parseInt(name.getAttribute("offset"), 16)==offset)
					return name.getAttribute("name");
			}
		}
		return defaultNameFromTypeAndOffset(type, offset);
	}
	
	//Used if the target data is undefined for the specific filename
	private static String defaultNameFromTypeAndOffset(LoadTask.Type type, int offset) {
		Element rom = null;
		try {
			rom = (Element)instance.xpath.evaluate("//*[@id='Unmodified']", instance.names, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = "";
		switch(type) {
			case AREA:
				str = "area";
				break;
			case SCENE_LIST:
				str = "scene";
				break;
			default:
				return "";
		}
		NodeList nameList = rom.getElementsByTagName(str);
		for(int i=0; i<nameList.getLength(); i++) {
			if(nameList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element name = (Element)nameList.item(i);
				if(Integer.parseInt(name.getAttribute("offset"), 16)==offset)
					return name.getAttribute("name");
			}
		}
		return "";
	}
	
	@Deprecated
	public static String nameFromPathAndIndex(String path, int index) {
		verifyInit();
		String[] steps = path.split("/");
		NodeList romList = instance.names.getElementsByTagName("rom");
		Element current = (Element)romList.item(0); 
		
		for(int i=1; i<steps.length; i++) {
			NodeList nodes = current.getChildNodes();
			if(nodes.getLength()==0) {
				return "";
			}
			for(int j=0; j<nodes.getLength(); j++) {
				if(nodes.item(j).getNodeType()!=Node.ELEMENT_NODE){
					continue;
				}
				Element testing = (Element)nodes.item(j);
				System.out.println(testing.getAttribute("name"));
				if(testing.getAttribute("name").equals(steps[i])) {
					current = testing;
					break;
				}else if(j==nodes.getLength()-1)
					return "";
			}
		}
		try {
		}catch(NullPointerException npe) {
			
		}
		Element result = (Element)current.getChildNodes().item(index);
		try {
			return result.getAttribute("name");
		}catch(NullPointerException npe) {
			return "";
		}
	}
}