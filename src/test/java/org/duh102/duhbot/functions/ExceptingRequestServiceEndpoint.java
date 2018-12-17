package org.duh102.duhbot.functions;

public class ExceptingRequestServiceEndpoint extends SimpleServiceEndpoint {
    public static String EXCEPTING_REQUEST_PATH = "exception_request";

    @Override
    public Class<?> getRequestClass() {
        Utils.generateNPE();
        return super.getRequestClass();
    }
}
