package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.MismatchedServiceResponseClass;
import org.duh102.duhbot.exception.NoSuchEndpointException;
import org.duh102.duhbot.functions.ServiceEndpointDefinition;
import org.duh102.duhbot.functions.ServiceProviderPlugin;
import org.duh102.duhbot.functions.ServiceResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUnsynchronizedMediator {
    private static String SIMPLE_ENDPOINT = "a";


    @Test
    public void testInteractNoEndpoints() {
        ImmutableMap<String, ServiceProviderPlugin> emptyMap = getProviderMap();
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(emptyMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact("anendpoint", "apath", new Object(),
                    String.class);
        });
    }
    @Test
    public void testInteractNullEndpoints() {
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(null);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact("anendpoint", "apath", new Object(),
                    String.class);
        });
    }
    @Test
    public void testInteractWrongEndpoint() {
        ImmutableMap<String, ServiceProviderPlugin> emptyMap = getProviderMap();
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(emptyMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact("anendpoint", "apath", new Object(),
                    String.class);
        });
    }

    private static ImmutableMap<String, ServiceProviderPlugin> getProviderMap(ServiceProviderPlugin ... plugins) {
        ImmutableMap.Builder<String, ServiceProviderPlugin> builder =
                new ImmutableMap.Builder<>();
        for( ServiceProviderPlugin plugin : plugins) {
            String endpoint = plugin.getEndpointRoot();
            builder.put(endpoint, plugin);
        }
        return builder.build();
    }

    private class SimpleServiceProvider implements ServiceProviderPlugin {
        @Override
        public Map<String, ServiceEndpointDefinition> getInteractions() {
            return null;
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
    private class SimpleServiceEndpoint implements ServiceEndpointDefinition {
        @Override
        public Class<?> getRequestClass() {
            return SimpleServiceRequest.class;
        }

        @Override
        public Class<?> getResponseClass() {
            return SimpleServiceResponse.class;
        }

        @Override
        public ServiceResponse interact(Object data) {
            try {
                return new ServiceResponse<>(SimpleServiceResponse.class, new Object());
            } catch( MismatchedServiceResponseClass msrc) {
                return null;
            }
        }
    }
    private class SimpleServiceRequest {

    }
    private class SimpleServiceResponse {

    }
}
