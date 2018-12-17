package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.*;

public interface ServiceMediator {
    public ServiceResponse<?> interact(String endpoint, String path, Object request, Class<?> responseClass) throws NoSuchEndpointException, NoSuchPathForEndpointException, MismatchedServiceRequestClass, MismatchedServiceResponseClass, ServiceProviderException;
}
