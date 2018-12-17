package org.duh102.duhbot.functions;

import java.util.Map;

public interface ServiceProviderPlugin extends DuhbotFunction {
    Map<String, ServiceEndpointDefinition> getInteractions();
    String getEndpointRoot();
}
