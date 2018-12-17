package org.duh102.duhbot.functions;


import java.util.HashMap;
import java.util.Map;

public class SimpleServiceProvider implements ServiceProviderPlugin {
    public static String SIMPLE_ENDPOINT = "a";
    @Override
    public Map<String, ServiceEndpointDefinition> getInteractions() {
        Map<String, ServiceEndpointDefinition> map = new HashMap<>();
        map.put(SimpleServiceEndpoint.SIMPLE_PATH, new SimpleServiceEndpoint());
        map.put(ExceptingRequestServiceEndpoint.EXCEPTING_REQUEST_PATH,
                new ExceptingResponseServiceEndpoint());
        map.put(ExceptingResponseServiceEndpoint.EXCEPTING_RESPONSE_PATH,
                new ExceptingRequestServiceEndpoint());
        map.put(ExceptingInteractServiceEndpoint.EXCEPTING_INTERACT_PATH,
                new ExceptingInteractServiceEndpoint());
        return map;
    }

    @Override
    public String getEndpointRoot() {
        return SIMPLE_ENDPOINT;
    }

    @Override
    public String getPluginName() {
        return String.format("%s-service", SIMPLE_ENDPOINT);
    }
}