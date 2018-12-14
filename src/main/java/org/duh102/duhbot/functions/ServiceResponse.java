package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedServiceResponseClass;

public class ServiceResponse<T> {
    private final T response;

    public ServiceResponse(Class<T> responseClass, Object response) throws MismatchedServiceResponseClass {
        try {
            this.response = responseClass.cast(response);
        } catch( ClassCastException cce ) {
            throw new MismatchedServiceResponseClass(responseClass, response.getClass(), cce);
        }
    }

    public T getResponse() {
        return response;
    }
}
