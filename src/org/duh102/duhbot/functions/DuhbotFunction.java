package org.duh102.duhbot.functions;

import java.util.*;

import org.pircbotx.hooks.*;

public interface DuhbotFunction {
	public HashMap<String, String> getHelpFunctions();

	public String getPluginName();

	public ListenerAdapter getAdapter();
}