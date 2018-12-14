package org.duh102.duhbot.functions;

import java.util.Map;

public interface ServiceProviderPlugin extends DuhbotFunction {
    public Map<String, ServiceEndpointDefinition> getInteractions();
    public String getEndpointRoot();
}
