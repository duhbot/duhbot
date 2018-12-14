package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedServiceRequestClass;
import org.duh102.duhbot.exception.MismatchedServiceResponseClass;
import org.duh102.duhbot.exception.NoSuchEndpointException;
import org.duh102.duhbot.exception.NoSuchPathForEndpointException;

public interface ServiceMediator {
    public ServiceResponse<?> interact(String endpoint, String path, Object request, Class<?> responseClass) throws NoSuchEndpointException, NoSuchPathForEndpointException, MismatchedServiceRequestClass, MismatchedServiceResponseClass;
}
