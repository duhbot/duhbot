package org.duh102.duhbot;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.pircbotx.*;
import org.pircbotx.delay.AdaptingDelay;
import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.data.*;
import org.duh102.duhbot.functions.*;

public class DuhBot {
    private ConfigData config;
    private MultiBotManager multiBot;
    private ServiceMediator mediator;

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
        ImmutableList<ListenerAdapter> allListeningPlugins =
                new ImmutableList.Builder<ListenerAdapter>()
                .add(helpPlugin).add(logPlugin)
                .addAll(loader.getLoadedListeners()).build();

        mediator =
                new UnsynchronizedMediator(loader.getLoadedServiceProviders());

        ImmutableList<ServiceConsumerPlugin> consumers =
                loader.getLoadedServiceConsumers();
        for (ServiceConsumerPlugin consumer : consumers) {
            try {
                consumer.setInteraactionMediator(mediator);
            } catch (Exception e) {
                System.err.println(Utils.formatLogMessage(
                        "Failed to register mediator with plugin"));
                e.printStackTrace();
            }
        }

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
            for (ListenerAdapter plugin : allListeningPlugins) {
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
}
