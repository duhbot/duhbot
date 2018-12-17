package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.*;
import org.duh102.duhbot.functions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUnsynchronizedMediator {
    private static String INCORRECT_ENDPOINT = "notanendpoint";
    private static String INCORRECT_PATH= "notapath";

    @Test
    public void testInteractNoEndpoints() {
        ImmutableMap<String, ServiceProviderPlugin> emptyMap = Utils.getProviderMap();
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(emptyMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractNullEndpoints() {
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(null);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractWrongEndpoint() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(NoSuchEndpointException.class, () -> {
            mediator.interact(INCORRECT_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractWrongPath() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(NoSuchPathForEndpointException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, INCORRECT_PATH, new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractBadServiceRequest() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(MismatchedServiceRequestClass.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH,
                    new Object(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractBadServiceResponse() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(MismatchedServiceResponseClass.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH,
                    new SimpleServiceRequest(),
                    Object.class);
        });
    }
    @Test
    public void testRequestServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, ExceptingRequestServiceEndpoint.EXCEPTING_REQUEST_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testResponseServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, ExceptingResponseServiceEndpoint.EXCEPTING_RESPONSE_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testInteractServiceException() {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator = new UnsynchronizedMediator(simpleMap);
        assertThrows(ServiceProviderException.class, () -> {
            mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, ExceptingInteractServiceEndpoint.EXCEPTING_INTERACT_PATH,
                    new SimpleServiceRequest(),
                    SimpleServiceResponse.class);
        });
    }
    @Test
    public void testGood() throws Exception {
        ImmutableMap<String, ServiceProviderPlugin> simpleMap =
                Utils.getProviderMap(new SimpleServiceProvider());
        UnsynchronizedMediator mediator =
                new UnsynchronizedMediator(simpleMap);
        ServiceResponse<?> serviceResponse =
                mediator.interact(SimpleServiceProvider.SIMPLE_ENDPOINT, SimpleServiceEndpoint.SIMPLE_PATH,
                new SimpleServiceRequest(), SimpleServiceResponse.class);
        SimpleServiceResponse response =
                (SimpleServiceResponse)serviceResponse.getResponse();
        assertEquals(new SimpleServiceResponse(), response);
    }
}
