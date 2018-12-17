package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.*;
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
    public ServiceResponse<?> interact(String endpoint, String path,
                                       Object request,
                                       Class<?> responseClass) throws
            NoSuchEndpointException, NoSuchPathForEndpointException,
            MismatchedServiceRequestClass, MismatchedServiceResponseClass,
            ServiceProviderException {
        if (servicePlugins == null || !servicePlugins.containsKey(endpoint)) {
            throw new NoSuchEndpointException(endpoint);
        }
        ServiceProviderPlugin serviceProvider = servicePlugins.get(endpoint);
        Map<String, ServiceEndpointDefinition> interactions;
        try {
            interactions = serviceProvider.getInteractions();
        } catch( Exception e ) {
            throw new ServiceProviderException(e);
        }
        if (interactions == null || !interactions.containsKey(path)) {
            throw new NoSuchPathForEndpointException(endpoint, path);
        }
        ServiceEndpointDefinition interaction = interactions.get(path);
        Class<?> prescribedResponse;
        try {
            prescribedResponse = interaction.getResponseClass();
        } catch( Exception e ) {
            throw new ServiceProviderException(e);
        }
        if (prescribedResponse != responseClass) {
            throw new MismatchedServiceResponseClass(prescribedResponse, responseClass);
        }
        Class<?> requiredInput;
        try {
            requiredInput = interaction.getRequestClass();
        } catch( Exception e ) {
            throw new ServiceProviderException(e);
        }
        if (request.getClass() != requiredInput) {
            throw new MismatchedServiceRequestClass(requiredInput, request.getClass());
        }
        try {
            ServiceResponse response = interaction.interact(request);
            return response;
        } catch(Exception e) {
            throw new ServiceProviderException(e);
        }
    }
}
