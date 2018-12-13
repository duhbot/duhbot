package org.duh102.duhbot.exception;

public class DuplicateEndpointException extends Exception {
    public DuplicateEndpointException(String message) {
        super(message);
    }
    public DuplicateEndpointException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
