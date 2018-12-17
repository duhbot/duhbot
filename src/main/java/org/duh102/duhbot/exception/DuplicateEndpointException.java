package org.duh102.duhbot.exception;

public class DuplicateEndpointException extends Exception {
    private String endpoint;
    private static String formatMessage(String endpoint) {
        return String.format("Endpoint %s already registered", endpoint);
    }
    public DuplicateEndpointException(String endpoint) {
        super(formatMessage(endpoint));
        this.endpoint = endpoint;
    }
    public DuplicateEndpointException(String endpoint, Throwable throwable) {
        super(formatMessage(endpoint), throwable);
        this.endpoint = endpoint;
    }
    public String getEndpoint() {
        return endpoint;
    }
}
