package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.MismatchedServiceRequestClass;
import org.duh102.duhbot.exception.MismatchedServiceResponseClass;
import org.duh102.duhbot.exception.NoSuchEndpointException;
import org.duh102.duhbot.exception.NoSuchPathForEndpointException;
import org.duh102.duhbot.functions.ServiceEndpointDefinition;
import org.duh102.duhbot.functions.ServiceMediator;
import org.duh102.duhbot.functions.ServiceProviderPlugin;
import org.duh102.duhbot.functions.ServiceResponse;

import java.util.Map;

public class UnsynchronizedMediator implements ServiceMediator {
    private ImmutableMap<String, ServiceProviderPlugin> servicePlugins;
    public UnsynchronizedMediator(ImmutableMap<String, ServiceProviderPlugin> servicePlugins) {
        this.servicePlugins = servicePlugins;
    }
    @Override
    public ServiceResponse<?> interact(String endpoint, String path, Object request, Class<?> responseClass) throws NoSuchEndpointException, NoSuchPathForEndpointException, MismatchedServiceRequestClass, MismatchedServiceResponseClass {
        if (servicePlugins == null || !servicePlugins.containsKey(endpoint)) {
            throw new NoSuchEndpointException(endpoint);
        }
        ServiceProviderPlugin interactive = servicePlugins.get(endpoint);
        Map<String, ServiceEndpointDefinition> interactions = interactive.getInteractions();
        if (interactions == null || !interactions.containsKey(path)) {
            throw new NoSuchPathForEndpointException(endpoint, path);
        }
        ServiceEndpointDefinition interaction = interactions.get(path);
        Class<?> prescribedResponse = interaction.getResponseClass();
        if (prescribedResponse != responseClass) {
            throw new MismatchedServiceResponseClass(prescribedResponse, responseClass);
        }
        Class<?> requiredInput = interaction.getRequestClass();
        if (request.getClass() != requiredInput) {
            throw new MismatchedServiceRequestClass(requiredInput, request.getClass());
        }
        ServiceResponse response = interaction.interact(request);
        return response;
    }
}
