package org.duh102.duhbot.exception;

public class NoSuchPathForEndpointException extends Exception {
    private static String getMessage(String endpoint, String path) {
        return String.format("No such path '%s' for endpoint '%s'", path, endpoint);
    }
    public NoSuchPathForEndpointException(String endpoint, String path) {
        super(getMessage(path, endpoint));
    }
    public NoSuchPathForEndpointException(String endpoint, String path, Throwable throwable) {
        super(getMessage(path, endpoint), throwable);
    }
}
