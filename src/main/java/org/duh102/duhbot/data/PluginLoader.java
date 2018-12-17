package org.duh102.duhbot.data;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.duh102.duhbot.Utils;
import org.duh102.duhbot.exception.DuplicateEndpointException;
import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.functions.*;

public class PluginLoader {
	public static final String PLUGIN_LOC = "./plugins/";

	private String pluginLocation;
	private List<DuhbotFunction> allPlugins;
	private HelpFunction help;
	List<ListenerAdapter> listenerPlugins;
	Map<String, ServiceProviderPlugin> providerPlugins;
	List<ServiceConsumerPlugin> consumerPlugins;

	public PluginLoader(String pluginLocation) {
		this.pluginLocation = pluginLocation;
		help = new HelpFunction();
		allPlugins = new ArrayList<>();
		listenerPlugins = new ArrayList<>();
		providerPlugins = new HashMap<>();
		consumerPlugins = new ArrayList<>();

		listenerPlugins.add(help.getAdapter());
	}

	public PluginLoader() {
		this(PLUGIN_LOC);
	}

	protected static URL fileToURL(File file) {
		try{
			return file.toURI().toURL();
		} catch( MalformedURLException mue ) {
			System.err.printf("Exception while turning file %s into URL\n",
					file.toString());
			mue.printStackTrace();
			return null;
		}
	}

	public List<URL> getPluginsInDir() {
		File folder = new File(pluginLocation);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			return null;
		}
		return Arrays.asList(listOfFiles).stream().filter(
				(file) -> file.getName().toLowerCase().endsWith(".jar"))
				.map(PluginLoader::fileToURL)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
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

	protected URLClassLoader makeClassLoader(List<URL> pluginURLs) {
		return new URLClassLoader(pluginURLs.toArray(new URL[0]),
				getClass().getClassLoader());
	}

	public void loadAllPlugins() {
		List<URL> plugins = getPluginsInDir();
		if (plugins == null) {
			return;
		}
		URLClassLoader ucl = makeClassLoader(plugins);
		URL[] pluginURLs = ucl.getURLs();
		System.err.println(Utils.formatLogMessage(String.format("Set plugin classpath to %s",
				Arrays.toString(pluginURLs))));
		for (URL plugin : plugins) {
			loadPlugin(plugin, ucl);
		}
	}

	public DuhbotFunction loadClass(URL pluginURL, ClassLoader classLoader)
			throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
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

	public int handleListeningPlugin(ListeningPlugin listeningPlugin) {
		Map<String, String> helpFuncs = listeningPlugin.getHelpFunctions();
		int helpTopics = helpFuncs.size();
		help.registerHelp(HelpFunction.properHelpFunction(listeningPlugin.getPluginName()),
				helpFuncs);
		listenerPlugins.add(listeningPlugin.getAdapter());
		return helpTopics;
	}

	public void handleServiceProviderPlugin(ServiceProviderPlugin serviceProvider) throws DuplicateEndpointException {
		String endpoint = serviceProvider.getEndpointRoot();
		if (providerPlugins.containsKey(endpoint)) {
			throw new DuplicateEndpointException(endpoint);
		}
		providerPlugins.put(endpoint, serviceProvider);

	}

	public void handleServiceConsumerPlugin(ServiceConsumerPlugin serviceConsumer) {
		consumerPlugins.add(serviceConsumer);
	}

	public void loadPlugin(URL pluginURL, ClassLoader classLoader) {
		String filename;
		try {
			File pluginFile = new File(pluginURL.toURI());
			filename = pluginFile.getName();
		} catch( URISyntaxException use ) {
			filename = "(bad uri)";
		}
		try {
			System.err.println(Utils.formatLogMessage(String.format("Loading" +
					" %s", filename)));
			DuhbotFunction func = loadClass(pluginURL, classLoader);

			if (func != null) {
				allPlugins.add(func);
			}

			//A plugin that listens to chat events
			if( func instanceof ListeningPlugin ) {
				ListeningPlugin listeningPlugin = (ListeningPlugin) func;
				int helpTopics = handleListeningPlugin(listeningPlugin);
				if (helpTopics > 0) {
					System.err.println(Utils.formatLogMessage(String.format(
							"Registered %d help functions", helpTopics)));
				}
			}
			//A plugin that provides service endpoints for plugins to use
			if( func instanceof ServiceProviderPlugin) {
				ServiceProviderPlugin serviceProvider =
						(ServiceProviderPlugin) func;
				try {
					handleServiceProviderPlugin(serviceProvider);
				} catch( DuplicateEndpointException dee ) {
					System.err.println(Utils.formatLogMessage(String.format(
							"Endpoint \"%s\" already registered, cannot re-register",
							dee.getEndpoint())));
				}
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
