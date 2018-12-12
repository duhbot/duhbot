package org.duh102.duhbot;

import java.sql.Timestamp;

import org.pircbotx.*;
import org.pircbotx.delay.AdaptingDelay;
import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.data.*;
import org.duh102.duhbot.functions.*;

public class DuhBot {
	private ConfigData config;

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
}
