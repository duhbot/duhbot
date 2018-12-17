package org.duh102.duhbot.functions;

public interface ServiceEndpointDefinition {
    Class<?> getRequestClass();
    Class<?> getResponseClass();
    ServiceResponse interact(Object data);
}
