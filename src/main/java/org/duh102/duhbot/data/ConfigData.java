package org.duh102.duhbot.data;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.duh102.duhbot.exception.*;

public class ConfigData {
	public static final String CONFIG_FILE = "config.xml";

	public ArrayList<String> preferredNicks;
	public ArrayList<IRCServer> servers;

	public ConfigData() {
		loadConfig();
	}

	// ##########################
	// UTILITY
	// ##########################
	private void loadConfig() {
		servers = new ArrayList<IRCServer>();
		preferredNicks = new ArrayList<String>();

		File fXmlFile = new File(CONFIG_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				NodeList serverList = doc.getElementsByTagName("server");

				String serverAddr;
				int serverPort;
				boolean serverSSL;
				String serverPass;
				ArrayList<IRCChannel> serverChannels;

				String channelName;
				String channelPass;

				for (int i = 0; i < serverList.getLength(); i++) {
					Node server = serverList.item(i);

					try {
						Element serverElement = (Element) server;
						serverAddr = requireStringAttribute(serverElement, "addr");
						serverPort = optionalIntegerAttribute(serverElement, "port", 6667);
						serverSSL = optionalBooleanAttribute(serverElement, "ssl", false);
						serverPass = optionalStringAttribute(serverElement, "pass", "");
						serverChannels = new ArrayList<IRCChannel>();
						NodeList channels = ((Element) server).getElementsByTagName("channel");
						for (int j = 0; j < channels.getLength(); j++) {
							Element channelElement = (Element) channels.item(j);
							try {
								channelName = requireStringAttribute(channelElement, "name");
								channelPass = optionalStringAttribute(channelElement, "pass", "");
								IRCChannel temp = new IRCChannel(channelName, channelPass);
								serverChannels.add(temp);
							} catch (AbsentRequiredAttributeException afae) {
								System.err.println("Channel name attribute missing, skipping channel " + j);
							}
						}
						IRCServer temp = new IRCServer(serverAddr, serverPort, serverPass, serverSSL, serverChannels);
						servers.add(temp);
					} catch (AbsentRequiredAttributeException afae) {
						System.err.println("Server address attribute missing, skipping server" + i);
					} catch (NumberFormatException nfe) {
						System.err.println("Port formatted incorrectly, skipping server " + i);
					}
				}

				NodeList nickList = doc.getElementsByTagName("nick");
				String temp;
				for (int i = 0; i < nickList.getLength(); i++) {
					temp = ((Element) nickList.item(i)).getFirstChild().getNodeValue();
					if (temp != null && temp.length() > 0) {
						preferredNicks.add(temp);
					} else {
						System.err.println("Empty nick, skipping nick");
					}
				}
			} catch (SAXException saxe) {
				saxe.printStackTrace();
				System.exit(1);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			System.exit(1);
		}
	}

	// ##########################
	// UTILITY FOR THE UTILITIES
	// ##########################

	public static String requireStringAttribute(Element element, String attribute)
			throws AbsentRequiredAttributeException {
		String temp = element.getAttribute(attribute);
		if (temp == null) {
			throw new AbsentRequiredAttributeException(attribute);
		} else {
			return temp;
		}
	}

	public static int requireIntegerAttribute(Element element, String attribute)
			throws AbsentRequiredAttributeException {
		String temp = element.getAttribute(attribute);
		if (temp == null) {
			throw new AbsentRequiredAttributeException(attribute);
		} else {
			try {
				return Integer.parseInt(temp);
			} catch (NumberFormatException nfe) {
				throw new AbsentRequiredAttributeException(attribute + ": " + nfe.getMessage());
			}
		}
	}

	public static boolean requireBooleanAttribute(Element element, String attribute)
			throws AbsentRequiredAttributeException {
		String temp = element.getAttribute(attribute);
		if (temp == null) {
			throw new AbsentRequiredAttributeException(attribute);
		} else {
			try {
				return parseBoolean(temp);
			} catch (BooleanFormatException bfe) {
				throw new AbsentRequiredAttributeException(attribute + ": " + bfe.getMessage());
			}
		}
	}

	public static String optionalStringAttribute(Element element, String attribute, String defaultValue) {
		try {
			return requireStringAttribute(element, attribute);
		} catch (AbsentRequiredAttributeException arae) {
			return defaultValue;
		}
	}

	public static int optionalIntegerAttribute(Element element, String attribute, int defaultValue) {
		try {
			return requireIntegerAttribute(element, attribute);
		} catch (AbsentRequiredAttributeException arae) {
			return defaultValue;
		}
	}

	public static boolean optionalBooleanAttribute(Element element, String attribute, boolean defaultValue) {
		try {
			return requireBooleanAttribute(element, attribute);
		} catch (AbsentRequiredAttributeException arae) {
			return defaultValue;
		}
	}

	public static final Pattern truePattern = Pattern.compile("true", Pattern.CASE_INSENSITIVE),
			falsePattern = Pattern.compile("false", Pattern.CASE_INSENSITIVE);

	public static boolean parseBoolean(String bool) throws BooleanFormatException {
		Matcher trueMatcher = truePattern.matcher(bool), falseMatcher = falsePattern.matcher(bool);
		if (trueMatcher.matches()) {
			return true;
		} else if (falseMatcher.matches()) {
			return false;
		} else {
			throw new BooleanFormatException(String.format("For input string: \"%s\"", bool));
		}
	}
}
