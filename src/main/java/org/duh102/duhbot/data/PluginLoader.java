package org.duh102.duhbot.data;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.jar.*;

import com.google.common.collect.ImmutableMap;
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

	public ImmutableList<ListenerAdapter> getLoadedPlugins() {
		return new ImmutableList.Builder<ListenerAdapter>().addAll(this.listenerPlugins).build();
	}
	public ImmutableMap<String, ServiceProviderPlugin> getLoadedInteractions() {
		return new ImmutableMap.Builder<String, ServiceProviderPlugin>().putAll(this.providerPlugins).build();
	}
	public ImmutableList<ServiceConsumerPlugin> getLoadedInteractors() {
		return new ImmutableList.Builder<ServiceConsumerPlugin>().addAll(this.consumerPlugins).build();
	}

	private static URL filenameToURL(String filename) {
		try {
			return (new File(PLUGIN_LOC + filename)).toURI().toURL();
		} catch (MalformedURLException mfue) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to turn filename %s into a URL\n",
					timestamp.toString(), filename);
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
				plugins.stream().map(PluginLoader::filenameToURL).filter(item -> item != null).toArray(URL[]::new);
		URLClassLoader ucl = new URLClassLoader(pluginURLs,
				getClass().getClassLoader());
		for (String plugin : plugins) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Loading: %s\n", derp.toString(), plugin);
			ListenerAdapter newPlug = loadPlugin(plugin, ucl);
			if( newPlug != null ) {
				this.listenerPlugins.add(newPlug);
			}
		},
	}

	public ListenerAdapter loadPlugin(String filename,
									  ClassLoader classLoader) {
		ListenerAdapter toRet = null;
		try {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			URL pluginURL = (new File(PLUGIN_LOC + filename)).toURI().toURL();
			URL u = new URL("jar", "", pluginURL + "!/");
			JarURLConnection uc = (JarURLConnection) u.openConnection();
			Attributes attr = uc.getMainAttributes();
			String mainClassName = (attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null);
			@SuppressWarnings("rawtypes")
			Class<?> c = classLoader.loadClass(mainClassName);
			DuhbotFunction func = (DuhbotFunction) c.newInstance();

			if (func != null) {
				allPlugins.add(func);
			}

			if( func instanceof ListeningPlugin ) {
				ListeningPlugin listeningPlugin = (ListeningPlugin)func;
				HashMap<String, String> helpFuncs = listeningPlugin.getHelpFunctions();
				int helpTopics = helpFuncs.size();
				help.registerHelp(HelpFunction.properHelpFunction(func.getPluginName()), helpFuncs);
				toRet = listeningPlugin.getAdapter();
				if (helpTopics > 0) {
					System.err.printf("%s | Loaded: %s; registered %d help functions\n", timestamp.toString(), filename,
							helpTopics);
				} else {
					System.err.printf("%s | Loaded: %s\n", timestamp.toString(), filename);
				}
			}
			if( func instanceof ServiceProviderPlugin) {
				try {
					ServiceProviderPlugin serviceProvider = (ServiceProviderPlugin) func;
					String endpoint = serviceProvider.getEndpointRoot();
					try {
						if (providerPlugins.containsKey(endpoint)) {
							throw new DuplicateEndpointException(endpoint);
						}
						providerPlugins.put(endpoint, serviceProvider);
					} catch( DuplicateEndpointException dee ) {
						System.err.printf("%s | Endpoint %s already " +
								"registered\n", timestamp.toString());
					}
				} catch( Exception e ) {
					System.err.printf("%s | Unable to register  service " +
									"provider\n",
							timestamp.toString());
					e.printStackTrace();
				}
			}
			if( func instanceof ServiceConsumerPlugin) {
				try {
					consumerPlugins.add((ServiceConsumerPlugin)func);
				} catch( Exception e ) {
					System.err.printf("%s | Unable to register plugin for " +
							"interacting with other listenerPlugins: %s\n",
							timestamp.toString(), e.getLocalizedMessage());
				}
			}
		} catch (IOException ioe) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", timestamp.toString(), filename);
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", timestamp.toString(), filename);
			cnfe.printStackTrace();
		} catch (InstantiationException ie) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", timestamp.toString(), filename);
			ie.printStackTrace();
		} catch (IllegalAccessException iae) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", timestamp.toString(), filename);
			iae.printStackTrace();
		} catch( Exception e ) {
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", timestamp.toString(), filename);
			e.printStackTrace();
		}
		return toRet;
	}
}
