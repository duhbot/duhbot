package org.duh102.duhbot.exception;

public class NoSuchEndpointException extends Exception {
    private static String getMessage(String endpoint) {
        return String.format("No such endpoint '%s' registered", endpoint);
    }
    public NoSuchEndpointException(String endpoint) {
        super(getMessage(endpoint));
    }
    public NoSuchEndpointException(String endpoint, Throwable throwable) {
        super(getMessage(endpoint), throwable);
    }
}
