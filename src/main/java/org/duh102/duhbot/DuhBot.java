package org.duh102.duhbot;

import java.sql.Timestamp;
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
	private ImmutableMap<String, ServiceProviderPlugin> interactivePlugins;

	public static void main(String[] args) {
		new DuhBot();
		java.util.Date date = new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		System.err.println("Bot started: " + now);
	}

	public DuhBot() {
		config = new ConfigData();
		MultiBotManager multiBot = new MultiBotManager();

		HelpFunction helpRegister = new HelpFunction();
		ListenerAdapter helpPlugin = helpRegister.getAdapter();
		LogBotListener defaultLogger = new LogBotListener();
		ListenerAdapter logPlugin = defaultLogger.getAdapter();

		PluginLoader loader = new PluginLoader(helpRegister);
		loader.loadAllPlugins();
		ImmutableList<ListenerAdapter> allPlugins = new ImmutableList.Builder<ListenerAdapter>()
				.add(helpPlugin).add(logPlugin).addAll(loader.getLoadedPlugins()).build();
		ImmutableList<ServiceConsumerPlugin> interactors =
				loader.getLoadedInteractors();
		for( ServiceConsumerPlugin interactor : interactors ) {
			try {
				interactor.setInteraactionMediator(this);
			} catch( Exception e ) {
				java.util.Date date = new java.util.Date();
				Timestamp derp = new Timestamp(date.getTime());
				System.err.printf("%s | Unable to hook up plugin to mediator:" +
						" %s\n", derp.toString(), e.getLocalizedMessage());
			}
		}
		interactivePlugins = loader.getLoadedInteractions();

		for (IRCServer server : config.servers) {
			Configuration.Builder newConfig = new Configuration.Builder();
			newConfig.addServer(server.serverAddr, server.port);
			if (server.password.length() > 0) {
				newConfig.setServerPassword(server.password);
			}
      if( server.ssl ) {
        newConfig.setSocketFactory((new UtilSSLSocketFactory()).trustAllCertificates());
      }
			newConfig.setAutoNickChange(true);
			newConfig.setName(config.preferredNicks.get(0));
			for (IRCChannel channel : server.channels) {
				if (channel.password.length() > 0) {
					newConfig.addAutoJoinChannel(channel.channel, channel.password);
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
		multiBot.start();
	}

	@Override
	public ServiceResponse<?> interact(String endpoint, String path, Object request, Class<?> responseClass) throws NoSuchEndpointException, NoSuchPathForEndpointException, MismatchedServiceRequestClass, MismatchedServiceResponseClass {
		if(interactivePlugins == null || ! interactivePlugins.containsKey(endpoint)) {
			throw new NoSuchEndpointException(endpoint);
		}
		ServiceProviderPlugin interactive = interactivePlugins.get(endpoint);
		Map<String, ServiceEndpointDefinition> interactions = interactive.getInteractions();
		if( interactions == null || !interactions.containsKey(path) ) {
			throw new NoSuchPathForEndpointException(endpoint, path);
		}
		ServiceEndpointDefinition interaction = interactions.get(path);
		Class<?> prescribedResponse = interaction.getResponseClass();
		if( prescribedResponse != responseClass	) {
			throw new MismatchedServiceResponseClass(prescribedResponse, responseClass);
		}
		Class<?> requiredInput = interaction.getRequestClass();
		if( request.getClass() != requiredInput ) {
			throw new MismatchedServiceRequestClass(requiredInput, request.getClass());
		}
		ServiceResponse response = interaction.interact(request);
		return response;
	}
}
