package org.duh102.duhbot.functions;

public class ExceptingInteractServiceEndpoint extends SimpleServiceEndpoint {
    public static String EXCEPTING_INTERACT_PATH = "exception_interact";

    @Override
    public ServiceResponse interact(Object data) {
        Utils.generateNPE();
        return super.interact(data);
    }
}
