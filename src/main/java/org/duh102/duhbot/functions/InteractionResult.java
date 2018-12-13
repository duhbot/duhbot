package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedInteractionResponseClass;

public class InteractionResult<T> {
    private final T response;

    public InteractionResult(Class<T> responseClass, Object response) throws MismatchedInteractionResponseClass {
        try {
            this.response = responseClass.cast(response);
        } catch( ClassCastException cce ) {
            throw new MismatchedInteractionResponseClass(responseClass, response.getClass(), cce);
        }
    }

    public T getResponse() {
        return response;
    }
}
