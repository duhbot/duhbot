package org.duh102.duhbot.data;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.jar.*;

import org.pircbotx.hooks.*;

import com.google.common.collect.ImmutableList;

import org.duh102.duhbot.functions.*;

public class PluginLoader {
	public static final String PLUGIN_LOC = "./plugins/";

	HelpFunction help;
	ArrayList<ListenerAdapter> plugins;

	public PluginLoader(HelpFunction help) {
		this.help = help;
		plugins = new ArrayList<ListenerAdapter>();
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
		return new ImmutableList.Builder<ListenerAdapter>().addAll(this.plugins).build();
	}

	public void loadAllPlugins() {
		List<String> plugins = getPluginsInDir();
		if (plugins == null) {
			return;
		}
		for (String plugin : plugins) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Loading: %s\n", derp.toString(), plugin);
			ListenerAdapter newPlug = loadPlugin(plugin);
			this.plugins.add(newPlug);
		}
	}

	public ListenerAdapter loadPlugin(String filename) {
		ListenerAdapter toRet = null;
		try {
			URL pluginURL = (new File(PLUGIN_LOC + filename)).toURI().toURL();
			URLClassLoader ucl = new URLClassLoader(new URL[] { pluginURL }, getClass().getClassLoader());
			URL u = new URL("jar", "", pluginURL + "!/");
			JarURLConnection uc = (JarURLConnection) u.openConnection();
			Attributes attr = uc.getMainAttributes();
			String mainClassName = (attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null);
			@SuppressWarnings("rawtypes")
			Class c = ucl.loadClass(mainClassName);
			DuhbotFunction func = (DuhbotFunction) c.newInstance();
			int helpTopics = 0;

			if (func != null) {
				HashMap<String, String> helpFuncs = func.getHelpFunctions();
				helpTopics = helpFuncs.size();
				help.registerHelp(HelpFunction.properHelpFunction(func.getPluginName()), helpFuncs);
				toRet = func.getAdapter();
			}
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			if (helpTopics > 0) {
				System.err.printf("%s | Loaded: %s; registered %d help functions\n", derp.toString(), filename,
						helpTopics);
			} else {
				System.err.printf("%s | Loaded: %s\n", derp.toString(), filename);
			}
			ucl.close();
		} catch (MalformedURLException mfue) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", derp.toString(), filename);
			mfue.printStackTrace();
		} catch (IOException ioe) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", derp.toString(), filename);
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", derp.toString(), filename);
			cnfe.printStackTrace();
		} catch (InstantiationException ie) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", derp.toString(), filename);
			ie.printStackTrace();
		} catch (IllegalAccessException iae) {
			java.util.Date date = new java.util.Date();
			Timestamp derp = new Timestamp(date.getTime());
			System.err.printf("%s | Unable to load: %s\n", derp.toString(), filename);
			iae.printStackTrace();
		}
		return toRet;
	}
}