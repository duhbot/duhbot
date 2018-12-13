package org.duh102.duhbot.functions;

public interface RegisteredInteraction {
    public Class<?> getRequestClass();
    public Class<?> getResponseClass();
    public InteractionResult interact(String endpoint, Object data);
}
