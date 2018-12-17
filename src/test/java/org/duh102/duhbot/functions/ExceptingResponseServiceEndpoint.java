package org.duh102.duhbot.functions;

public class ExceptingResponseServiceEndpoint extends SimpleServiceEndpoint {
    public static String EXCEPTING_RESPONSE_PATH = "exception_response";

    @Override
    public Class<?> getResponseClass() {
        Utils.generateNPE();
        return super.getResponseClass();
    }
}
