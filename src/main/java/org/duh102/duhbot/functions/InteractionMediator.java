package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedInteractionRequestClass;
import org.duh102.duhbot.exception.MismatchedInteractionResponseClass;
import org.duh102.duhbot.exception.NoSuchEndpointException;
import org.duh102.duhbot.exception.NoSuchPathForEndpointException;

public interface InteractionMediator {
    public InteractionResult<?> interact(String endpoint, String path, Object request, Class<?> responseClass) throws NoSuchEndpointException, NoSuchPathForEndpointException, MismatchedInteractionRequestClass, MismatchedInteractionResponseClass;
}
