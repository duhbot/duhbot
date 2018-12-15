package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.*;
import org.duh102.duhbot.functions.ServiceEndpointDefinition;
import org.duh102.duhbot.functions.ServiceProviderPlugin;
import org.duh102.duhbot.functions.ServiceResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUnsynchronizedMediator {
    private static String SIMPLE_ENDPOINT = "a";
    private static String INCORRECT_ENDPOINT = "notanendpoint";
    private static String SIMPLE_PATH = "a";
    private static String EXCEPTING_REQUEST_PATH = "exception_request";
    private static String EXCEPTING_RESPONSE_PATH = "exception_response";
    private static String EXCEPTING_INTERACT_PATH = "exception_interact";
    private static String INCORRECT_PATH= "notapath";


    @Test
    public void testInteractNoEndpoints() {
        ImmutableMap<String, ServiceProviderPlugin> emptyMap = getProviderMap();
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(emptyMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractNullEndpoints() {
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(null);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractWrongEndpoint() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(INCORRECT_ENDPOINT, SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractWrongPath() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(NoSuchPathForEndpointException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, INCORRECT_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractBadServiceRequest() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(MismatchedServiceRequestClass.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, SIMPLE_PATH,
                    new Object(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractBadServiceResponse() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(MismatchedServiceResponseClass.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, SIMPLE_PATH,
                    new SimpleServiceRequest(),
                    Object.class);
        });
    }
    @Test
    public void testRequestServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, EXCEPTING_REQUEST_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testResponseServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, EXCEPTING_RESPONSE_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SIMPLE_ENDPOINT, EXCEPTING_INTERACT_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testGood() throws Exception {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator =
                new UnsynchronizedMediator(simpleMap);
        ServiceResponse<?> serviceResponse =
                mediator.interact(SIMPLE_ENDPOINT, SIMPLE_PATH,
                new SimpleServiceRequest(), SimpleServiceResponse.class);
        SimpleServiceResponse response =
                (SimpleServiceResponse)serviceResponse.getResponse();
        assertEquals(new SimpleServiceResponse(), response);
    }

    /*
    Test support methods/classes
     */


    private static void generateNPE() {
        throw new NullPointerException();
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
            Map<String, ServiceEndpointDefinition> map = new HashMap<>();
            map.put(SIMPLE_PATH, new SimpleServiceEndpoint());
            map.put(EXCEPTING_REQUEST_PATH,
                    new ExceptingResponseServiceEndpoint());
            map.put(EXCEPTING_RESPONSE_PATH,
                    new ExceptingRequestServiceEndpoint());
            map.put(EXCEPTING_INTERACT_PATH,
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
                return new ServiceResponse<>(SimpleServiceResponse.class,
                        new SimpleServiceResponse());
            } catch( MismatchedServiceResponseClass msrc) {
                return null;
            }
        }
    }
    private class SimpleServiceRequest {
        public SimpleServiceRequest() {
        }
        public boolean equals(Object other) {
            return (other instanceof SimpleServiceRequest);
        }
    }
    private class SimpleServiceResponse {
        public SimpleServiceResponse() {
        }
        public boolean equals(Object other) {
            return (other instanceof SimpleServiceResponse);
        }
    }
    private class ExceptingRequestServiceEndpoint extends SimpleServiceEndpoint {
        @Override
        public Class<?> getRequestClass() {
            generateNPE();
            return super.getRequestClass();
        }
    }
    private class ExceptingResponseServiceEndpoint extends SimpleServiceEndpoint {
        @Override
        public Class<?> getResponseClass() {
            generateNPE();
            return super.getResponseClass();
        }
    }
    private class ExceptingInteractServiceEndpoint extends SimpleServiceEndpoint {
        @Override
        public ServiceResponse interact(Object data) {
            generateNPE();
            return super.interact(data);
        }
    }
}
