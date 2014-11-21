/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/** 
 * Loads the factoriesData.xml file which configures the factories of the
 * framework i.e. what strategies are used by the objects created by the
 * factory, the types of node, links, and {@link FrameworkTask} that can be created as well.
 * 
 * @author Javier Snaider
 * 
 */
public class FactoriesDataXmlLoader {

	private static final String FACTORY_DATA_PROPERTY_NAME = "lida.elementfactory.data";
	private static final String DEFAULT_XML_FILE_PATH = "configs/factoriesData.xml";
	private static final String DEFAULT_SCHEMA_FILE_PATH = "edu/memphis/ccrg/lida/framework/initialization/config/LidaFactories.xsd";
	private static final Logger logger = Logger
			.getLogger(FactoriesDataXmlLoader.class.getCanonicalName());

	private static ElementFactory nfactory = ElementFactory.getInstance();

	/**
	 * Loads {@link ElementFactory} with object types specified in
	 * {@link Properties}
	 * 
	 * @param properties
	 *            {@link Properties}
	 */
	public static void loadFactoriesData(Properties properties) {
		String fileName = properties.getProperty(FACTORY_DATA_PROPERTY_NAME,
				DEFAULT_XML_FILE_PATH);
		Document dom = XmlUtils
				.parseXmlFile(fileName, DEFAULT_SCHEMA_FILE_PATH);
		parseDocument(dom);
	}

	/**
	 * Parses the xml document creating the elements for {@link ElementFactory}
	 * @param dom the xml dom Document
	 */
	static void parseDocument(Document dom) {
		if(dom == null){
			logger.log(Level.SEVERE, "Document dom was null. Factory data will not be loaded.");
			return;
		}
		
		// get the root element
		Element docEle = dom.getDocumentElement();
		Map<String, StrategyDef> strategies = getStrategies(docEle);
		Map<String, LinkableDef> nodes = getLinkables(docEle, "nodes", "node",
				strategies);
		Map<String, LinkableDef> links = getLinkables(docEle, "links", "link",
				strategies);
		Map<String, FrameworkTaskDef> tasks = getTasks(docEle, strategies);
		fillNodes(nodes);
		fillLinks(links);
		fillStrategies(strategies);
		fillTasks(tasks);
	}

	private static void fillNodes(Map<String, LinkableDef> nodes) {
		for (LinkableDef ld : nodes.values()) {
			nfactory.addNodeType(ld);
		}
	}

	private static void fillLinks(Map<String, LinkableDef> links) {
		for (LinkableDef ld : links.values()) {
			nfactory.addLinkType(ld);
		}
	}

	private static void fillStrategies(Map<String, StrategyDef> strategies) {
		for (StrategyDef sd : strategies.values()) {
			if (sd.getType().equalsIgnoreCase("decay")) {
				nfactory.addDecayStrategy(sd.getName(), sd);
			} else if (sd.getType().equalsIgnoreCase("excite")) {
				nfactory.addExciteStrategy(sd.getName(), sd);
			}
			nfactory.addStrategy(sd.getName(), sd);
		}
	}

	private static void fillTasks(Map<String, FrameworkTaskDef> tasks) {
		for (FrameworkTaskDef cd : tasks.values()) {
			nfactory.addFrameworkTaskType(cd);
		}
	}

	/**
	 * Reads in and creates all {@link StrategyDef}s specified in {@link Element}
	 * @param element Dom element
	 * @return a Map with the {@link StrategyDef} indexed by name
	 */
	static Map<String, StrategyDef> getStrategies(Element element) {
		Map<String, StrategyDef> strat = new HashMap<String, StrategyDef>();
		List<Element> list = XmlUtils.getChildrenInGroup(element,
				"strategies", "strategy");
		if (list != null && list.size() > 0) {
			for (Element e : list) {
				StrategyDef strategy = getStrategyDef(e);
				strat.put(strategy.getName(), strategy);
			}
		}
		return strat;
	}

	/**
	 * @param e Dom element
	 * @return the {@link Strategy} definition
	 */
	static StrategyDef getStrategyDef(Element e) {
		StrategyDef strategy = new StrategyDef();
		String className = XmlUtils.getTextValue(e, "class");
		String name = e.getAttribute("name");
		String type = e.getAttribute("type");
		boolean fweight = Boolean.parseBoolean(e.getAttribute("flyweight"));
		Map<String, Object> params = XmlUtils.getTypedParams(e);

		strategy.setClassName(className.trim());
		strategy.setName(name.trim());
		strategy.setType(type.trim());
		strategy.setFlyWeight(fweight);
		strategy.setParams(params);

		return strategy;
	}

	/**
	 * Reads in and creates all {@link LinkableDef}s specified in
	 * {@link Element}
	 * 
	 * @param element
	 *            Dom element
	 * @param groupName
	 *            the name of the group containing {@link LinkableDef} data
	 * @param childName
	 *            the name of the children containing {@link LinkableDef} data
	 * @param strategies
	 *            Map with {@link StrategyDef} indexed by name
	 * @return a Map of {@link LinkableDef} indexed by name
	 */
	static Map<String, LinkableDef> getLinkables(Element element, String groupName,
			String childName, Map<String, StrategyDef> strategies) {
		Map<String, LinkableDef> linkables = new HashMap<String, LinkableDef>();
		List<Element> list = XmlUtils.getChildrenInGroup(element, groupName,
				childName);
		if (list != null && list.size() > 0) {
			for (Element e : list) {
				LinkableDef linkable = getLinkable(e, strategies);
				linkables.put(linkable.getName(), linkable);
			}
		}
		return linkables;
	}

	/**
	 * @param e Dom element
	 * @param strategies
	 *            Map with {@link StrategyDef} indexed by name
	 * @return the {@link Linkable} definition
	 */
	static LinkableDef getLinkable(Element e, Map<String, StrategyDef> strategies) {
		LinkableDef node = new LinkableDef();
		String className = XmlUtils.getTextValue(e, "class");
		String name = e.getAttribute("name");
		Map<String, String> strat = new HashMap<String, String>();
		List<String> list = XmlUtils.getChildrenValues(e, "defaultstrategy");
		checkStrategies(list, strategies);
		for (String s : list) {
			StrategyDef bd = strategies.get(s);
			String type = bd.getType();
			if (strat.containsKey(type)) {
				logger.log(Level.WARNING, "Cannot add strategy {1} a strategy of type {2} already exists", 
						new Object[]{TaskManager.getCurrentTick(), s, type});
			} else {
				strat.put(type, s);
			}
		}

		Map<String, Object> params = XmlUtils.getTypedParams(e);
		node.setClassName(className.trim());
		node.setName(name.trim());
		node.setParams(params);
		node.setDefaultStrategies(strat);
		return node;
	}

	/**
	 * Reads in and creates all {@link FrameworkTaskDef}s specified in
	 * {@link Element}
	 * 
	 * @param element
	 *            Dom element
	 * @param strategies
	 *            Map with {@link StrategyDef} indexed by name
	 * @return a Map of {@link FrameworkTaskDef} indexed by name
	 */
	static Map<String, FrameworkTaskDef> getTasks(Element element,
			Map<String, StrategyDef> strategies) {
		Map<String, FrameworkTaskDef> tasks = new HashMap<String, FrameworkTaskDef>();
		List<Element> list = XmlUtils.getChildrenInGroup(element, "tasks",
				"task");
		if (list != null && list.size() > 0) {
			for (Element e : list) {
				FrameworkTaskDef taskDef = getTaskDef(e, strategies);
				if (taskDef != null) {
					tasks.put(taskDef.getName(), taskDef);
				}
			}
		}
		return tasks;
	}
	/**
	 * reads the associated modules of this element
	 * 
	 * @param element Dom element
	 * @return a Map with the associated modules
	 */
	static Map<ModuleName,String> getAssociatedModules(Element element) {
		Map<ModuleName,String> associatedModules = new HashMap<ModuleName, String>();
		List<Element> nl = XmlUtils.getChildren(element,"associatedmodule");
		String elementName = element.getAttribute("name");
		if (nl != null && nl.size() > 0) {
			for (Element assocModuleElement:nl ) {
				String assocMod=XmlUtils.getValue(assocModuleElement);
				String function = assocModuleElement.getAttribute("function").trim();
				ModuleName name = ModuleName.getModuleName(assocMod);
				if(name !=null){
					associatedModules.put(name, function);
				}else{
					logger.log(Level.WARNING, "{1} is not a defined ModuleName so it cannot be an associate module of {2}", 
							new Object[]{0L,assocMod,elementName});
				}
			}
		}
		return associatedModules;
	}

	/**
	 * @param e Dom element
	 * @param strategies
	 *            Map with {@link StrategyDef} indexed by name
	 * @return the {@link FrameworkTaskDef} definition
	 */
	static FrameworkTaskDef getTaskDef(Element e, Map<String, StrategyDef> strategies) {
		FrameworkTaskDef taskDef = null;
		String className = XmlUtils.getTextValue(e, "class");
		String name = e.getAttribute("name");
		int ticksPerRun = XmlUtils.getIntegerValue(e, "ticksperrun");
		Map<String, String> behav = new HashMap<String, String>();
		List<String> list = XmlUtils.getChildrenValues(e, "defaultstrategy");
		checkStrategies(list, strategies);
		for (String s : list) {
			StrategyDef bd = strategies.get(s);
			behav.put(bd.getType(), s);
		}

		Map<String, Object> params = XmlUtils.getTypedParams(e);

		Map<ModuleName,String> associatedModules = getAssociatedModules(e);
		taskDef = new FrameworkTaskDef();
		taskDef.setClassName(className.trim());
		taskDef.setName(name.trim());
		taskDef.setParams(params);
		taskDef.setDefaultStrategies(behav);
		taskDef.setTicksPerRun(ticksPerRun);
		taskDef.setAssociatedModules(associatedModules);
		return taskDef;
	}

	/**
	 * Verifies if the List of Strategies names are defined
	 * @param strat Strategies names to validate
	 * @param strategies
	 *            Map with {@link StrategyDef} indexed by name
	 */
	static void checkStrategies(List<String> strat, Map<String, StrategyDef> strategies) {
		Iterator<String> it = strat.iterator();
		String b;
		while (it.hasNext()) {
			b = it.next();
			if (!strategies.containsKey(b)) {
				logger.log(Level.WARNING, "{1} is not a defined Strategy. It is excluded", 
						new Object[]{0L,b});
				it.remove();
			}
		}
	}
}
