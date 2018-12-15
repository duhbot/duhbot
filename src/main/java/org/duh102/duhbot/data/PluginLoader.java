package org.duh102.duhbot.data;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.Utils;
import org.duh102.duhbot.exception.DuplicateEndpointException;
import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.functions.*;

public class PluginLoader {
	public static final String PLUGIN_LOC = "./plugins/";

	List<DuhbotFunction> allPlugins;
	HelpFunction help;
	List<ListenerAdapter> listenerPlugins;
	Map<String, ServiceProviderPlugin> providerPlugins;
	List<ServiceConsumerPlugin> consumerPlugins;

	public PluginLoader(HelpFunction help) {
		this.help = help;
		allPlugins = new ArrayList<>();
		listenerPlugins = new ArrayList<>();
		providerPlugins = new HashMap<>();
		consumerPlugins = new ArrayList<>();
	}

	public List<String> getPluginsInDir() {
		String filename;
		File folder = new File(PLUGIN_LOC);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			return null;
		}
		ArrayList<String> plugins = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				filename = listOfFiles[i].getName();
				if (filename.toLowerCase().endsWith(".jar")) {
					plugins.add(filename);
				}
			}
		}
		return plugins;
	}

	public ImmutableList<ListenerAdapter> getLoadedListeners() {
		return new ImmutableList.Builder<ListenerAdapter>()
				.addAll(this.listenerPlugins).build();
	}
	public ImmutableMap<String, ServiceProviderPlugin> getLoadedServiceProviders() {
		return new ImmutableMap.Builder<String, ServiceProviderPlugin>()
				.putAll(this.providerPlugins).build();
	}
	public ImmutableList<ServiceConsumerPlugin> getLoadedServiceConsumers() {
		return new ImmutableList.Builder<ServiceConsumerPlugin>()
				.addAll(this.consumerPlugins).build();
	}

	private static URL filenameToURL(String filename) {
		try {
			return (new File(PLUGIN_LOC + filename)).toURI().toURL();
		} catch (MalformedURLException mfue) {
			System.err.println(Utils.formatLogMessage(String.format("Unable to "
					+ "turn filename %s into a URL", filename)));
			mfue.printStackTrace();
			return null;
		}
	}

	public void loadAllPlugins() {
		List<String> plugins = getPluginsInDir();
		if (plugins == null) {
			return;
		}
		URL[] pluginURLs =
				plugins.stream().map(PluginLoader::filenameToURL).filter(
						item -> item != null).toArray(URL[]::new);
		System.err.println(Utils.formatLogMessage(String.format("Set plugin classpath to %s",
				Arrays.toString(pluginURLs))));
		URLClassLoader ucl = new URLClassLoader(pluginURLs,
				getClass().getClassLoader());
		for (String plugin : plugins) {
			System.err.println(Utils.formatLogMessage(String.format("Loading" +
					" %s", plugin)));
			loadPlugin(plugin, ucl);
		}
	}

	public DuhbotFunction loadClass(String filename, ClassLoader classLoader)
			throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		URL pluginURL = (new File(PLUGIN_LOC + filename)).toURI().toURL();
		URL u = new URL("jar", "", pluginURL + "!/");
		JarURLConnection uc = (JarURLConnection) u.openConnection();
		Attributes attr = uc.getMainAttributes();
		String mainClassName = (attr != null ?
				attr.getValue(Attributes.Name.MAIN_CLASS) : null);
		@SuppressWarnings("rawtypes")
		Class<?> c = classLoader.loadClass(mainClassName);
		DuhbotFunction func = (DuhbotFunction) c.newInstance();
		return func;
	}

	public void handleListeningPlugin(ListeningPlugin listeningPlugin) {
		HashMap<String, String> helpFuncs = listeningPlugin.getHelpFunctions();
		int helpTopics = helpFuncs.size();
		help.registerHelp(HelpFunction.properHelpFunction(listeningPlugin.getPluginName()),
				helpFuncs);
		listenerPlugins.add(listeningPlugin.getAdapter());
		if (helpTopics > 0) {
			System.err.println(Utils.formatLogMessage(String.format(
					"Registered %d help functions", helpTopics)));
		}
	}

	public void handleServiceProviderPlugin(ServiceProviderPlugin serviceProvider) {
		String endpoint = serviceProvider.getEndpointRoot();
		try {
			if (providerPlugins.containsKey(endpoint)) {
				throw new DuplicateEndpointException(endpoint);
			}
			providerPlugins.put(endpoint, serviceProvider);
			System.err.println(Utils.formatLogMessage(String.format(
					"Registered service endpoint \"%s\"", endpoint)));
		} catch( DuplicateEndpointException dee ) {
			System.err.println(Utils.formatLogMessage(String.format(
					"Endpoint \"%s\" already registered, cannot re-register",
					endpoint)));
		}
	}

	public void handleServiceConsumerPlugin(ServiceConsumerPlugin serviceConsumer) {
		consumerPlugins.add(serviceConsumer);
	}

	public void loadPlugin(String filename, ClassLoader classLoader) {
		try {

			DuhbotFunction func = loadClass(filename, classLoader);

			if (func != null) {
				allPlugins.add(func);
			}

			//A plugin that listens to chat events
			if( func instanceof ListeningPlugin ) {
				ListeningPlugin listeningPlugin = (ListeningPlugin) func;
				handleListeningPlugin(listeningPlugin);
			}
			//A plugin that provides service endpoints for plugins to use
			if( func instanceof ServiceProviderPlugin) {
				ServiceProviderPlugin serviceProvider =
						(ServiceProviderPlugin) func;
				handleServiceProviderPlugin(serviceProvider);
			}
			//A plugin that calls out to other plugins' endpoints
			if( func instanceof ServiceConsumerPlugin) {
				ServiceConsumerPlugin serviceConsumer =
						(ServiceConsumerPlugin) func;
				handleServiceConsumerPlugin(serviceConsumer);
			}
			System.err.println(Utils.formatLogMessage(String.format("Loaded " +
					"\"%s\" from %s\n", func.getPluginName(),
					filename)));
		} catch( Exception e ) {
			System.err.println(Utils.formatLogMessage(String.format(
					"Unable to load %s", filename)));
			e.printStackTrace();
		}
	}
}
