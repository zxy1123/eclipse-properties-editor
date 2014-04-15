/**
 *  
 */
package com.erayt.solar2.criteriaResolve;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.runtime.FileLocator;

import com.erayt.solar2.Solar2PropertyEditorActivator;
import com.erayt.solar2.property.editor.Criteria;
import com.erayt.solar2.property.editor.Criteria.WrongPropertyExcetion;

/**
 * @author zhou
 * 
 */
public class CriteriaResolver {
	private static XMLInputFactory factroy;
	private static Map<String, Criteria.Rule> results;

	public static Map<String, Criteria.Rule> getRules()
			throws WrongPropertyExcetion {
		InputStreamReader contentReader = null;
		try {
			File file = new File(FileLocator.resolve(
					Solar2PropertyEditorActivator.getDefault().getBundle()
							.getEntry("/conf/rules.xml").toURI().toURL())
					.toURI());
			// File file = new File("conf/rules.xml");
			contentReader = new InputStreamReader(new FileInputStream(file),
					"UTF-8");
			XMLEventReader eventReader = factroy.newInstance()
					.createXMLEventReader(contentReader);
			results = readDocument(eventReader);
			return results;
		} catch (MalformedURLException e) {
			throw new WrongPropertyExcetion(e);
		} catch (URISyntaxException e) {
			throw new WrongPropertyExcetion(e);
		} catch (IOException e) {
			throw new WrongPropertyExcetion(e);
		} catch (XMLStreamException e) {
			throw new WrongPropertyExcetion(e);
		} catch (FactoryConfigurationError e) {
			throw new WrongPropertyExcetion(e);
		} finally {
			if (contentReader != null) {
				try {
					contentReader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static Map<String, Criteria.Rule> readDocument(
			XMLEventReader eventReader) throws XMLStreamException,
			WrongPropertyExcetion {
		while (eventReader.hasNext()) {
			XMLEvent peek = eventReader.peek();
			results = new HashMap<String, Criteria.Rule>();
			switch (peek.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
			case XMLStreamConstants.END_DOCUMENT:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CHARACTERS:
				eventReader.nextEvent();
				break;
			default:
				readNode(eventReader, results);
				break;
			}
		}
		return results;
	}

	private static void readNode(XMLEventReader reader,
			Map<String, Criteria.Rule> map) throws XMLStreamException,
			WrongPropertyExcetion {
		XMLEvent peek = reader.peek();
		switch (peek.getEventType()) {
		case XMLStreamConstants.START_DOCUMENT:
			reader.nextEvent();
			readNode(reader, map);
		case XMLEvent.START_ELEMENT:
			readElement(reader, map);
		default:
			reader.nextEvent();
			break;
		}
	}

	/**
	 * @param reader
	 * @param map
	 * @param namespaces
	 * @return
	 * @throws XMLStreamException
	 * @throws WrongPropertyExcetion
	 */
	@SuppressWarnings("rawtypes")
	private static void readElement(XMLEventReader reader, Map map)
			throws XMLStreamException, WrongPropertyExcetion {
		StartElement asStartElement = reader.nextEvent().asStartElement();
		String tag = asStartElement.getName().getLocalPart();
		if (tag.equals("rule")) {
			map = new HashMap<String, Object>();
		} else if (tag.equals("property")) {
			createProperty(asStartElement, map);
		} else {
		}
		while (true) {
			XMLEvent peek = reader.peek();
			if (peek.isEndElement()) {
				EndElement asEndElement = reader.nextEvent().asEndElement();
				if (asEndElement.getName().getLocalPart().equals("rule")) {
					results.put((String) map.get("key"), new Criteria.Rule(map));
					map = results;
				}
				break;
			}
			readNode(reader, map);
		}

	}

	/**
	 * @param reader
	 * @param map
	 * @throws WrongPropertyExcetion
	 */
	private static void createProperty(StartElement property,
			Map<String, Object> map) throws WrongPropertyExcetion {
		String key = null;
		Object ret = null;
		String value = null;
		for (Iterator i = property.getAttributes(); i.hasNext();) {
			Attribute attr = (Attribute) i.next();
			String name = attr.getName().getLocalPart();
			value = attr.getValue();
			if (name.equals("key")) {
				key = value;
			} else if (name.equals("value")) {
				ret = value;
			}
		}

		if (key == null) {
			throw new WrongPropertyExcetion();
		}

		if (key.equals("require")) {
			ret = Boolean.valueOf((String) ret);
		} else if (key.equals("rule")) {
			ret = Pattern.compile((String) ret);
		}
		map.put(key, ret);
	}

}
