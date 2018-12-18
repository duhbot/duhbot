package org.duh102.duhbot.data;

import com.google.common.collect.ImmutableList;
import org.duh102.duhbot.exception.DuplicateEndpointException;
import org.duh102.duhbot.functions.*;
import org.junit.jupiter.api.Test;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestPluginLoader {
    List<URL> plugins;
    Path pluginPath;
    public TestPluginLoader() {
        try {
            URI uri = new File("src/test/resources").toURI();
            pluginPath = Paths.get(uri);
            plugins =
                    Files.list(pluginPath).map((path) -> path.toFile()).filter((file) -> file.getName().endsWith(".jar")).map(PluginLoader::fileToURL).collect(Collectors.toList());
        } catch(Exception e) {
            e.printStackTrace();
            plugins = new ArrayList<>();
        }
    }

    @Test
    public void testGetPluginsInDir() {
        PluginLoader loader = new PluginLoader(pluginPath.toString());

        List<URL> foundPlugins = loader.getPluginsInDir();
        assertEquals(plugins, foundPlugins);
    }

    @Test
    public void testRegisterDoubleEndpoint() throws Exception {
        PluginLoader loader = new PluginLoader(pluginPath.toString());
        SimpleServiceProvider provider = new SimpleServiceProvider();
        loader.handleServiceProviderPlugin(provider);
        assertThrows(DuplicateEndpointException.class, () -> {
            loader.handleServiceProviderPlugin(provider);
        });
        assertEquals(1, loader.getLoadedServiceProviders().size());
    }
    @Test
    public void testRegisterServiceProvider() throws Exception {
        PluginLoader loader = new PluginLoader(pluginPath.toString());
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
        PluginLoader loader = new PluginLoader(pluginPath.toString());
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
        PluginLoader loader = new PluginLoader(pluginPath.toString());
        SimpleListeningPlugin plugin = new SimpleListeningPlugin();
        List<ListenerAdapter> adapters = loader.getLoadedListeners();
        assertEquals(2, adapters.size());
        assertTrue(adapters.get(0) instanceof HelpFunction);
        assertTrue(adapters.get(1) instanceof LogBotListener);
        int helpFunctions = loader.handleListeningPlugin(plugin);
        adapters = loader.getLoadedListeners();
        assertEquals(3, adapters.size());
        assertEquals(plugin.getAdapter(), adapters.get(2));
        assertEquals(plugin.getHelpFunctions().size(), helpFunctions);
    }
    @Test
    public void testMakeClassLoader() throws Exception {
        PluginLoader loader = new PluginLoader(pluginPath.toString());
        List<URL> oneJar = plugins.subList(0,1);
        URLClassLoader classLoader = loader.makeClassLoader(oneJar);
        URL[] classLoaderURLs = classLoader.getURLs();
        assertTrue(Files.isReadable(Paths.get(classLoaderURLs[0].toURI())));
        assertEquals(1, classLoaderURLs.length);
        assertTrue(classLoaderURLs[0].toString().contains(oneJar.get(0).toString()));
        URL u = new URL("jar", "", classLoaderURLs[0].toString() + "!/");
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        String mainClassName = (attr != null ?
                attr.getValue(Attributes.Name.MAIN_CLASS) : null);
        @SuppressWarnings("rawtypes")
        Class<?> c = classLoader.loadClass(mainClassName);
        assertTrue(DuhbotFunction.class.isAssignableFrom(c));
        DuhbotFunction func = (DuhbotFunction)c.newInstance();
        List<String> pluginNames = Arrays.asList("service-provider-example",
                "service-consumer-example", "Plugin Template");
        assertTrue(pluginNames.contains(func.getPluginName()));
    }
    @Test
    public void testLoadClass() throws Exception {
        PluginLoader loader = new PluginLoader(pluginPath.toString());
        List<URL> oneJar = plugins.subList(0,1);
        URLClassLoader classLoader = loader.makeClassLoader(oneJar);
        DuhbotFunction aFunction = loader.loadClass(oneJar.get(0),
                classLoader);
        //Similar to testLoadAllPlugins, fragile and depends on keeping up to
        // date with the plugins
        List<String> pluginNames = Arrays.asList("service-provider-example",
                "service-consumer-example", "Plugin Template");
        assertTrue(pluginNames.contains(aFunction.getPluginName()));
    }
    // This is a fragile test because we're testing against stuff in the
    // plugins we're loading, if they ever change their names or endpoints,
    // update this test!
    @Test
    public void testLoadAllPlugins() {
        PluginLoader loader = new PluginLoader(pluginPath.toString());
        loader.loadAllPlugins();
        ImmutableList<ListenerAdapter> listeners = loader.getLoadedListeners();
        List<ServiceConsumerPlugin> consumers =
                loader.getLoadedServiceConsumers();
        Map<String, ServiceProviderPlugin> providers =
                loader.getLoadedServiceProviders();
        assertEquals(3, listeners.size());
        assertEquals(1, consumers.size());
        assertEquals(1, providers.size());
        ListenerAdapter help = listeners.get(0);
        ListenerAdapter loggerAdapter = listeners.get(1);
        ListenerAdapter templateAdapter = listeners.get(2);
        ServiceConsumerPlugin templateConsumer = consumers.get(0);
        ServiceProviderPlugin templateProvider = providers.get("service-provider-example");
        assertEquals(HelpFunction.class, help.getClass());
        assertEquals(LogBotListener.class, loggerAdapter.getClass());
        assertTrue(templateAdapter instanceof DuhbotFunction);
        DuhbotFunction templatePlugin = (DuhbotFunction) templateAdapter;
        assertEquals("Plugin Template", templatePlugin.getPluginName());
        assertEquals("service-consumer-test",
                templateConsumer.getPluginName());
        assertEquals("service-provider-example",
                templateProvider.getPluginName());
    }
}
