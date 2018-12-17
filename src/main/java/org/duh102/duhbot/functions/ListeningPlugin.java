package org.duh102.duhbot.functions;

import org.pircbotx.hooks.ListenerAdapter;

import java.util.Map;

public interface ListeningPlugin extends DuhbotFunction {
    Map<String, String> getHelpFunctions();
    ListenerAdapter getAdapter();
}
