package org.duh102.duhbot;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.exception.MismatchedServiceRequestClass;
import org.duh102.duhbot.exception.MismatchedServiceResponseClass;
import org.duh102.duhbot.exception.NoSuchEndpointException;
import org.duh102.duhbot.exception.NoSuchPathForEndpointException;
import org.pircbotx.*;
import org.pircbotx.delay.AdaptingDelay;
import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.data.*;
import org.duh102.duhbot.functions.*;

public class DuhBot implements ServiceMediator {
    private ConfigData config;
    private ImmutableMap<String, ServiceProviderPlugin> servicePlugins;
    private MultiBotManager multiBot;

    public static void main(String[] args) {
        (new DuhBot()).start();
        System.err.println(Utils.formatLogMessage("Bot started!"));
    }

    public DuhBot() {
        config = new ConfigData();
        multiBot = new MultiBotManager();

        HelpFunction helpRegister = new HelpFunction();
        ListenerAdapter helpPlugin = helpRegister.getAdapter();
        LogBotListener defaultLogger = new LogBotListener();
        ListenerAdapter logPlugin = defaultLogger.getAdapter();

        PluginLoader loader = new PluginLoader(helpRegister);
        loader.loadAllPlugins();
        ImmutableList<ListenerAdapter> allPlugins =
                new ImmutableList.Builder<ListenerAdapter>()
                .add(helpPlugin).add(logPlugin)
                .addAll(loader.getLoadedListeners()).build();
        ImmutableList<ServiceConsumerPlugin> consumers =
                loader.getLoadedServiceConsumers();
        for (ServiceConsumerPlugin consumer : consumers) {
            try {
                consumer.setInteraactionMediator(this);
            } catch (Exception e) {
                System.err.println(Utils.formatLogMessage(
                        "Failed to register mediator with plugin"));
                e.printStackTrace();
            }
        }
        servicePlugins = loader.getLoadedServiceProviders();

        for (IRCServer serverDef : config.servers) {
            Configuration.Builder newConfig = new Configuration.Builder();
            newConfig.addServer(serverDef.serverAddr, serverDef.port);
            if (serverDef.password.length() > 0) {
                newConfig.setServerPassword(serverDef.password);
            }
            if (serverDef.ssl) {
                newConfig.setSocketFactory((new UtilSSLSocketFactory())
                        .trustAllCertificates());
            }
            newConfig.setAutoNickChange(true);
            newConfig.setName(config.preferredNicks.get(0));
            for (IRCChannel channel : serverDef.channels) {
                if (channel.password.length() > 0) {
                    newConfig.addAutoJoinChannel(channel.channel,
                            channel.password);
                } else {
                    newConfig.addAutoJoinChannel(channel.channel);
                }
            }
            for (ListenerAdapter plugin : allPlugins) {
                newConfig.addListener(plugin);
            }
            newConfig.setAutoReconnect(true);
            newConfig.setAutoReconnectDelay(new AdaptingDelay(1000, 20000));
            multiBot.addBot(newConfig.buildConfiguration());
        }
    }

    public void start() {
        multiBot.start();
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
