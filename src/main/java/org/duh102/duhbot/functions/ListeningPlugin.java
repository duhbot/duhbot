package org.duh102.duhbot.functions;

import org.pircbotx.hooks.ListenerAdapter;

import java.util.HashMap;

public interface ListeningPlugin extends DuhbotFunction {
    public HashMap<String, String> getHelpFunctions();

    public ListenerAdapter getAdapter();
}
