package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;

import groovy.lang.GroovySystem;
import groovy.lang.MetaMethod;

public class PluginManager {

	public static final String MODULE_META_INF_FILE = "META-INF/nf-test-plugin";

	private List<String> staticImports = new Vector<String>();

	private static PluginManager instance;

	private ClassLoader classLoader = this.getClass().getClassLoader();

	private PluginManager() {

	}

	public static PluginManager getInstance() {
		if (instance == null) {
			instance = new PluginManager();
		}
		return instance;
	}

	public void loadPluginFromFile(File file) throws IOException {

		if (!file.exists()) {
			System.out.println("Plugin '" + file + "' not found.");
			return;
		}

		classLoader = new URLClassLoader(new URL[] { file.toURL() }, classLoader);
		JarFile jar = new JarFile(file);
		ZipEntry entry = jar.getEntry(MODULE_META_INF_FILE);
		System.out.println("Load " + file);
		if (entry != null) {
			Properties props = new Properties();
			props.load(jar.getInputStream(entry));
			System.out.println(
					"Register plugin " + props.getProperty("moduleName") + " " + props.getProperty("moduleVersion"));

			/*
			 * This code was extracted from GRAP. They have the same situation, that
			 * extensions are not loaded after adding jar at runtime.
			 */

			Map<CachedClass, List<MetaMethod>> metaMethods = new HashMap<CachedClass, List<MetaMethod>>();
			MetaClassRegistryImpl mcRegistry = (MetaClassRegistryImpl) GroovySystem.getMetaClassRegistry();
			mcRegistry.registerExtensionModuleFromProperties(props, classLoader, metaMethods);
			for (CachedClass c : metaMethods.keySet()) {
				List<MetaMethod> methods = metaMethods.get(c);
				System.out.println("Update " + c + " with " + methods);
				c.addNewMopMethods(methods);
			}

			// nf-test specific extension to provide classes that are addStaticStars
			if (props.containsKey("extensionMethods")) {
				// TODO: split by ';' to support multiple classes
				staticImports.add(props.getProperty("extensionMethods"));
			}

		} else {
			System.out.println("No valid plugin detected.");
		}
		jar.close();

	}

	public List<String> getStaticImports() {
		return staticImports;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
