
package com.netease.backend.db.common.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class XMLUtils {
	public static Element getChildElement(Element parent, String name) {
		for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n instanceof Element) {
				Element e = (Element)n;
				if (e.getNodeName().equals(name)) {
					return e;
				}
			}
		}
		return null;
	}
	
	public static String extractOneText(Element root, String name) {
		Element e = getChildElement(root, name);
		if (e == null)
			throw new IllegalArgumentException("Subelement " + name + "not found");
		return e.getTextContent();
	}
	
	public static String extractOneText(Element root, String name, String defaultValue) {
		Element e = getChildElement(root, name);
		if (e == null)
			return defaultValue;
		return e.getTextContent();
	}

	public static boolean extractOneBoolean(Element root, String name) {
		String s = extractOneText(root, name);
		return Boolean.getBoolean(s);
	}
	
	public static int extractOneInt(Element root, String name) {
		String s = extractOneText(root, name);
		return Integer.parseInt(s);
	}
	
	public static long extractOneLong(Element root, String name) {
		String s = extractOneText(root, name);
		return Long.parseLong(s);
	}
}
