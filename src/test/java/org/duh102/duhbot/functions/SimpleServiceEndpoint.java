package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedServiceResponseClass;

public class SimpleServiceEndpoint implements ServiceEndpointDefinition {
    public static String SIMPLE_PATH = "a";

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
