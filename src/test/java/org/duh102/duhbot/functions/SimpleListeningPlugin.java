package org.duh102.duhbot.functions;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;

public class SimpleListeningPlugin extends ListenerAdapter implements ListeningPlugin {
    public static final String PLUGIN_NAME = "listenerplugin";
    @Override
    public void onMessage(MessageEvent message) {
        message.respond("I'M LISTENING!");
    }
    @Override
    public Map<String, String> getHelpFunctions() {
        Map<String, String> help = new HashMap<>();
        help.put("every message",
                "Responds with \"I'M LISTENING\" in a really annoying way");
        return help;
    }

    @Override
    public ListenerAdapter getAdapter() {
        return this;
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }
}
