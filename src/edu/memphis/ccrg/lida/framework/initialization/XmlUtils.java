/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * 
 */
package edu.memphis.ccrg.lida.framework.initialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Utilities for reading an XML file.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 * 
 */
public class XmlUtils {

	private static final Logger logger = Logger.getLogger(XmlUtils.class
			.getCanonicalName());

	/**
	 * Validates specified XML file with specified XML schema file
	 * 
	 * @param xmlFile
	 *            name of xml file
	 * @param schemaFile
	 *            name of schema file
	 * @return true if the xml is valid under specified schema.
	 */
	public static boolean validateXmlFile(String xmlFile, String schemaFile) {
		boolean result = false;
		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		// 2. Compile the schema.
		// Here the schema is loaded from a java.io.File, but you could use
		// a java.net.URL or a javax.xml.transform.Source instead.
		// File schemaLocation = new File(schemaFile);
		InputStream is = ClassLoader.getSystemResourceAsStream(schemaFile);

		Schema schema;
		try {
			// schema = factory.newSchema(schemaLocation);
			schema = factory.newSchema(new StreamSource(is));
		} catch (SAXException ex) {
			logger.log(Level.WARNING, "The Schema file is not valid. {0}",
					ex.getMessage());
			ex.printStackTrace();
			return false;
		}

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Parse the document you want to check.
		Source source = new StreamSource(xmlFile);

		// 5. Check the document
		try {
			validator.validate(source);
			logger.log(Level.INFO, xmlFile + " is valid.");
			result = true;
		} catch (SAXException ex) {
			logger.log(Level.WARNING, xmlFile + " is not valid because\n >>>"
					+ ex.getMessage());
		} catch (IOException ex) {
			logger.log(Level.WARNING, xmlFile + " is not a valid file."
					+ ex.getMessage());
		}
		return result;
	}

	/**
	 * Returns text value of first element in specified element with specified
	 * tag. Returns null if specified tag is empty or only has subchildren.
	 * 
	 * @param ele
	 *            Dom element
	 * @param tagName
	 *            name of xml tag
	 * @return text value of element with specified xml tag
	 */
	public static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		List<Element> nl = getChildren(ele, tagName);
		if (nl != null && nl.size() != 0) {
			Element el = (Element) nl.get(0);
			textVal = getValue(el);
			if (textVal != null) {
				textVal = textVal.trim();
				if (textVal.equalsIgnoreCase("")) {
					return null;
				}
			}
		}

		return textVal;
	}

	/**
	 * Returns Integer value of first element inside specified element with
	 * specified tag or else null.
	 * 
	 * @param ele
	 *            Dom element
	 * @param tagName
	 *            name of xml tag
	 * @return Integer value of first element with specified xml tag or null if
	 *         no such value can be parsed
	 * 
	 */
	public static Integer getIntegerValue(Element ele, String tagName) {
		Integer i = null;
		try {
			i = Integer.parseInt(getTextValue(ele, tagName));
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "Cannot parse int value in tag {0}",
					tagName);
		}
		return i;
	}
	
	/**
	 * Returns whether specified {@link Element} contains a child node with specified tagName
	 * @param ele {@link Element}
	 * @param tagName tag name
	 * @return true if 
	 */
	public static boolean containsTag(Element ele, String tagName){
		return getChildren(ele, tagName).size() != 0;
	}

	/**
	 * Returns a Boolean with value true if the first element inside specified
	 * element has specified tag contains 'true' ignoring case. Returns false
	 * otherwise.
	 * 
	 * @param ele
	 *            Dom element
	 * @param tagName
	 *            name of xml tag
	 * @return boolean value of element with specified xml tag
	 */
	public static boolean getBooleanValue(Element ele, String tagName) {
		return Boolean.parseBoolean(getTextValue(ele, tagName));
	}

	/**
	 * Reads and creates a Properties from specified Element
	 * 
	 * @param moduleElement
	 *            Dom element
	 * @return Properties
	 */
	public static Map<String, Object> getParams(Element moduleElement) {
		Map<String, Object> prop = new HashMap<String, Object>();
		List<Element> nl = getChildren(moduleElement, "param");
		if (nl != null) {
			for (Element param : nl) {
				String name = param.getAttribute("name");
				String value = (getValue(param)).trim();
				prop.put(name, value);
			}
		}
		return prop;
	}

	/**
	 * Reads typed parameters from xml element and returns them in a Map where
	 * the key is the parameter's String name and the value is the value of the parameter.
	 * Finds all tags named "param".  Those tags should have two attributes, name and type. 
	 * Valid parameters types are "int", "double", "boolean", and "string".
	 * If the value is not compatible with the specified type then value will be null, except
	 * in the case of a Boolean with an incompatible value, in which case the value will be False.
	 * If a parameter's value is not specified or empty then the value will be null. 
	 * 
	 * @param moduleElement
	 *            Dom {@link Element}
	 * @return parameters indexed by name.
	 */
	public static Map<String, Object> getTypedParams(Element moduleElement) {
		Map<String, Object> prop = new HashMap<String, Object>();
		List<Element> nl = getChildren(moduleElement, "param");
		if (nl != null) {
			for (Element param : nl) {
				String name = param.getAttribute("name");
				String type = param.getAttribute("type");
				String sValue = getValue(param);
				Object value = sValue;
				if (sValue != null) {

					if (type == null || "string".equalsIgnoreCase(type)) {
						value = sValue;
					} else if ("int".equalsIgnoreCase(type)) {
						try {
							value = Integer.parseInt(sValue);
						} catch (NumberFormatException e) {
							value = null;
							logger.log(Level.FINE, e.toString(), TaskManager
									.getCurrentTick());
						}
					} else if ("double".equalsIgnoreCase(type)) {
						try {
							value = Double.parseDouble(sValue);
						} catch (NumberFormatException e) {
							value = null;
							logger.log(Level.FINE, e.toString(), TaskManager
									.getCurrentTick());
						}
					} else if ("boolean".equalsIgnoreCase(type)) {
						value = Boolean.parseBoolean(sValue);
					}
				}
				prop.put(name, value);
			}
		}
		return prop;
	}

	/**
	 * Returns the first child of specified Element with specified name.
	 * 
	 * @param parent
	 *            an {@link Element}
	 * @param name
	 *            name of child specified
	 * @return child or null
	 */
	public static Element getChild(Element parent, String name) {
		for (Node child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child instanceof Element && name.equals(child.getNodeName())) {
				return (Element) child;
			}
		}
		return null;
	}

	/**
	 * Returns String value of first child found that is a {@link Text}. or null
	 * if the Text is empty
	 * 
	 * @param parent
	 *            Element
	 * @return child's string value.
	 */
	public static String getValue(Element parent) {
		for (Node child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child instanceof Text) {
				String value = child.getNodeValue().trim();
				if (value.equalsIgnoreCase("")) {
					return null;
				} else {
					return value;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the values of the children of the element with specified name.
	 * 
	 * @param element
	 *            Parent {@link Element}
	 * @param name
	 *            specified tag name
	 * @return values of children
	 */
	public static List<String> getChildrenValues(Element element, String name) {
		List<String> vals = new ArrayList<String>();
		List<Element> nl = getChildren(element, name);
		if (nl != null) {
			for (Element el : nl) {
				String value = getValue(el);
				vals.add(value);
			}
		}
		return vals;
	}

	/**
	 * Returns all children with specified name.
	 * 
	 * @param parent
	 *            {@link Element}
	 * @param name
	 *            name of sought children
	 * @return list of all children found.
	 */
	public static List<Element> getChildren(Element parent, String name) {
		List<Element> nl = new ArrayList<Element>();
		for (Node child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child instanceof Element && name.equals(child.getNodeName())) {
				nl.add((Element) child);
			}
		}
		return nl;
	}

	/**
	 * Returns the Elements with name childName, in the group groupName inside
	 * the specified Element e.  groupName must be a direct child of e.  
	 * 
	 * @param e
	 *            {@link Element}
	 * @param groupName
	 *            name of the group
	 * @param childName
	 *            name of children Elements returned
	 * @return List of child {@link Element}s
	 */
	public static List<Element> getChildrenInGroup(Element e,
			String groupName, String childName) {
		List<Element> children = new ArrayList<Element>();
		Element groupElement = getChild(e, groupName);
		if (groupElement != null) {
			children = getChildren(groupElement, childName);
		}
		return children;
	}

	/**
	 * Verifies and parses specified xml file into a {@link Document}.
	 * 
	 * @param fileName
	 *            the name of the file to parse
	 * @param schemaFilePath
	 *            path to the schema file
	 * @return the DOM {@link Document} of the file fileName or null if the xml
	 *         file is not valid
	 */
	public static Document parseXmlFile(String fileName, String schemaFilePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document dom = null;
		try {
			db = dbf.newDocumentBuilder();
			if (validateXmlFile(fileName, schemaFilePath)) {
				// parse using builder to get DOM representation of the XML file
				dom = db.parse(fileName);
			} else {
				logger.log(Level.WARNING, "Xml file invalid, file: " + fileName
						+ " was not parsed");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return dom;
	}

	/**
	 * Parses a String containing xml data into a dom {@link Document}
	 * 
	 * @param xml
	 *            the string with xml data
	 * @return a dom {@link Document}
	 */
	public static Document parseXmlString(String xml) {
		Document dom = null;
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return dom;
	}
}
