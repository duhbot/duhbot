package org.duh102.duhbot.exception;

public class MismatchedInteractionResponseClass extends Exception {
    private static String getMessage(Class<?> expected, Class<?> gotten) {
        return String.format("Interaction expected '%s', got '%s'", expected.getCanonicalName(), gotten.getCanonicalName());
    }
    public MismatchedInteractionResponseClass(Class<?> prescribedResponse, Class<?> responseClass) {
        super(getMessage(prescribedResponse, responseClass));
    }
    public MismatchedInteractionResponseClass(Class<?> prescribedResponse, Class<?> responseClass, Throwable throwable) {
        super(getMessage(prescribedResponse, responseClass), throwable);
    }
}
