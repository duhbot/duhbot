package org.duh102.duhbot.functions;

import com.google.common.collect.ImmutableMap;

public class Utils {
    public static void generateNPE() {
        throw new NullPointerException();
    }
    public static ImmutableMap<String, ServiceProviderPlugin> getProviderMap(ServiceProviderPlugin ... plugins) {
        ImmutableMap.Builder<String, ServiceProviderPlugin> builder =
                new ImmutableMap.Builder<>();
        for( ServiceProviderPlugin plugin : plugins) {
            String endpoint = plugin.getEndpointRoot();
            builder.put(endpoint, plugin);
        }
        return builder.build();
    }
}