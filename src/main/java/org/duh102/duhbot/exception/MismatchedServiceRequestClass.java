package org.duh102.duhbot.exception;

public class MismatchedServiceRequestClass extends Exception {
    private static String getMessage(Class<?> expected, Class<?> gotten) {
        return String.format("Interaction expected '%s', got '%s'", expected.getCanonicalName(), gotten.getCanonicalName());
    }
    public MismatchedServiceRequestClass(Class<?> prescribedResponse, Class<?> responseClass) {
        super(getMessage(prescribedResponse, responseClass));
    }
    public MismatchedServiceRequestClass(Class<?> prescribedResponse, Class<?> responseClass, Throwable throwable) {
        super(getMessage(prescribedResponse, responseClass), throwable);
    }
}
