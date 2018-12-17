package org.duh102.duhbot.functions;

public class SimpleServiceConsumer implements ServiceConsumerPlugin {
    public static final String PLUGIN_NAME = "simpleconsumer";
    private ServiceMediator mediator;
    @Override
    public void setInteractionMediator(ServiceMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }
}
