package org.duh102.duhbot.functions;

import java.util.Map;

public interface InteractionRegisterable extends DuhbotFunction {
    public Map<String, RegisteredInteraction> getInteractions();
    public String getEndpointRoot();
    public void setInteraactionMediator(InteractionMediator mediator);
}
