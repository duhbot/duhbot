package org.duh102.duhbot.functions;

public interface ServiceEndpointDefinition {
    public Class<?> getRequestClass();
    public Class<?> getResponseClass();
    public ServiceResponse interact(Object data);
}
