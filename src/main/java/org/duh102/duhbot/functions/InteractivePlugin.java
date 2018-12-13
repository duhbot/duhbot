package org.duh102.duhbot.functions;

import java.util.Map;

public interface InteractivePlugin extends DuhbotFunction {
    public Map<String, RegisteredInteraction> getInteractions();
    public String getEndpointRoot();
}
