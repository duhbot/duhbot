package org.duh102.duhbot.data;

import org.duh102.duhbot.exception.DuplicateEndpointException;
import org.duh102.duhbot.functions.*;
import org.junit.jupiter.api.Test;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestPluginLoader {
    URL url = this.getClass().getResource("/");
    File file = new File(url.getFile());
    List<File> plugins = List.of(file.listFiles()).stream()
            .filter((item) -> item.toString().endsWith(".jar"))
            .collect(Collectors.toList());

    @Test
    public void testGetPluginsInDir() {
        PluginLoader loader = new PluginLoader(file.toString());

        List<String> foundPlugins = loader.getPluginsInDir();
        assertEquals(1, foundPlugins.size());
        assertEquals(plugins.stream().map((file) -> file.getName()).collect(Collectors.toList()),
                foundPlugins);
    }

    @Test
    public void testRegisterDoubleEndpoint() throws Exception {
        PluginLoader loader = new PluginLoader(file.toString());
        SimpleServiceProvider provider = new SimpleServiceProvider();
        loader.handleServiceProviderPlugin(provider);
        assertThrows(DuplicateEndpointException.class, () -> {
            loader.handleServiceProviderPlugin(provider);
        });
        assertEquals(1, loader.getLoadedServiceProviders().size());
    }
    @Test
    public void testRegisterServiceProvider() throws Exception {
        PluginLoader loader = new PluginLoader(file.toString());
        SimpleServiceProvider provider = new SimpleServiceProvider();
        Map<String, ServiceProviderPlugin> providers =
                loader.getLoadedServiceProviders();
        assertEquals(0, providers.size());
        loader.handleServiceProviderPlugin(provider);
        providers = loader.getLoadedServiceProviders();
        assertEquals(1, providers.size());
        assertTrue(providers.containsKey(provider.getEndpointRoot()));
        assertEquals(provider, providers.get(provider.getEndpointRoot()));
    }
    @Test
    public void testRegisterServiceConsumer() {
        PluginLoader loader = new PluginLoader(file.toString());
        SimpleServiceConsumer consumer = new SimpleServiceConsumer();
        List<ServiceConsumerPlugin> consumers =
                loader.getLoadedServiceConsumers();
        assertEquals(0, consumers.size());
        loader.handleServiceConsumerPlugin(consumer);
        consumers = loader.getLoadedServiceConsumers();
        assertEquals(1, consumers.size());
        assertEquals(consumer, consumers.get(0));
    }
    @Test
    public void testRegisterListeningPlugin() {
        PluginLoader loader = new PluginLoader(file.toString());
        SimpleListeningPlugin plugin = new SimpleListeningPlugin();
        List<ListenerAdapter> adapters = loader.getLoadedListeners();
        assertEquals(1, adapters.size());
        assertTrue(adapters.get(0) instanceof HelpFunction);
        int helpFunctions = loader.handleListeningPlugin(plugin);
        adapters = loader.getLoadedListeners();
        assertEquals(2, adapters.size());
        assertEquals(plugin.getAdapter(), adapters.get(1));
        assertEquals(plugin.getHelpFunctions().size(), helpFunctions);
    }
}
