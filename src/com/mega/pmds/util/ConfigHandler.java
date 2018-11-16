package com.mega.pmds.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
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
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	
	private ConfigHandler() {
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setSchema(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(new File("nameSchema.xsd")));
			builder = factory.newDocumentBuilder();
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
			case SCENE_DATA:
				str = "call";
				break;
			case INTERACTION:
				str = "interaction";
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
			case SCENE_DATA:
				str = "call";
				break;
			case INTERACTION:
				str = "action";
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
	
	public static int getMapDefPointers(int offset) {
		Element rom = null;
		try {
			rom = (Element)instance.xpath.evaluate("//*[@id='Unmodified']", instance.names, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList areaList = rom.getElementsByTagName("area");
		for(int i=0; i<areaList.getLength(); i++) {
			if(areaList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element area = (Element)areaList.item(i);
				if(Integer.parseInt(area.getAttribute("offset"), 16)==offset) {
					if(area.hasAttribute("map"))
						return Integer.parseInt(area.getAttribute("map"), 16);
					else
						return 0;
				}
			}
		}
		return 0;
	}
	
	public static int getMapDefType(int offset) {
		Element rom = null;
		try {
			rom = (Element)instance.xpath.evaluate("//*[@id='Unmodified']", instance.names, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList areaList = rom.getElementsByTagName("area");
		for(int i=0; i<areaList.getLength(); i++) {
			if(areaList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element area = (Element)areaList.item(i);
				if(Integer.parseInt(area.getAttribute("offset"), 16)==offset) {
					if(area.hasAttribute("type"))
						return Integer.parseInt(area.getAttribute("type"));
					else
						return 0;
				}
			}
		}
		return 0;
	}
	
	public static String getParts(int offset) {
		Element rom = null;
		try {
			rom = (Element)instance.xpath.evaluate("//*[@id='Unmodified']", instance.names, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList areaList = rom.getElementsByTagName("area");
		for(int i=0; i<areaList.getLength(); i++) {
			if(areaList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element area = (Element)areaList.item(i);
				if(Integer.parseInt(area.getAttribute("offset"), 16)==offset) {
					if(area.hasAttribute("parts"))
						return area.getAttribute("parts");
					else
						return null;
				}
			}
		}
		return null;
	}
}