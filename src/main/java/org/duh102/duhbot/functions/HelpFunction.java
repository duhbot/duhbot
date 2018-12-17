package org.duh102.duhbot.functions;

import java.util.*;

import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;

import org.duh102.duhbot.*;

public class HelpFunction extends ListenerAdapter {
	HashMap<String, HashMap<String, String>> helpLookup = new HashMap<String, HashMap<String, String>>();

	ArrayList<String> pluginListing = new ArrayList<String>();
	ArrayList<ArrayList<String>> functionListing = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> helpListing = new ArrayList<ArrayList<String>>();

	public static final String HELP_PREFIX = "help";

	public HelpFunction() {
		super();
	}

	public ListenerAdapter getAdapter() {
		return this;
	}

	public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
		String message = event.getMessage().toLowerCase().trim();
		if (message.startsWith(HELP_PREFIX)) {
			String[] messageParts = message.split(" ");
			String[] params = Arrays.copyOfRange(messageParts, 1, messageParts.length);
			if (messageParts.length > 1) {
				int sel = Integer.parseInt(params[0]);
				if (pluginListing.size() > sel) {
					ArrayList<String> funcs = functionListing.get(sel);
					if (funcs.isEmpty()) {
						event.respond("Topic " + pluginListing.get(sel)
								+ " has no user accessible functions associated with it.");
					} else {
						if (params.length == 1) {
							event.respond("Functions available in " + pluginListing.get(sel) + ": "
									+ Utils.toNCSV(funcs.toArray()));
						} else {
							int sel2 = Integer.parseInt(params[1]);
							if (funcs.size() > sel2) {
								ArrayList<String> descs = helpListing.get(sel);
								event.respond("Help for " + funcs.get(sel2) + ": " + descs.get(sel2));
							} else {
								event.respond("Unknown function. The following functions are available in that topic: "
										+ Utils.toNCSV(funcs.toArray()));
							}
						}
					}
				} else {
					event.respond("Unknown topic. The following help topics are available: "
							+ Utils.toNCSV(pluginListing.toArray()));
				}
			} else {
				event.respond("The following help topics are available: " + Utils.toNCSV(pluginListing.toArray()));
			}
		}
	}

	public HashMap<String, HashMap<String, String>> getHelpMap() {
		return helpLookup;
	}

	public void registerHelp(String functionName, Map<String, String> helpMap) {
		String realFunctionName = properHelpFunction(functionName);
		if (!pluginListing.contains(realFunctionName)) {
			pluginListing.add(realFunctionName);
			ArrayList<String> funcs = new ArrayList<String>();
			ArrayList<String> descs = new ArrayList<String>();
			for (String func : helpMap.keySet()) {
				funcs.add(func);
				descs.add(helpMap.get(func));
			}
			functionListing.add(funcs);
			helpListing.add(descs);
		}
	}

	public static String properHelpFunction(String input) {
		return input.toLowerCase().replaceAll(" ", "_");
	}
}
