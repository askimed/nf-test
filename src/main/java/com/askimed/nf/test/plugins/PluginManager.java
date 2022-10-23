package com.askimed.nf.test.plugins;

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

	public static final String DEFAULT_REPOSITORY = "https://raw.githubusercontent.com/lukfor/nf-test-plugins/main/plugins.json";

	public static final String MODULE_META_INF_FILE = "META-INF/nf-test-plugin";

	private List<String> staticImports = new Vector<String>();

	private ClassLoader classLoader = this.getClass().getClassLoader();

	private List<String> repositories = new Vector<String>();

	private boolean forceUpdate;
	
	public PluginManager(boolean forceUpdate) {
		repositories.add(DEFAULT_REPOSITORY);
		this.forceUpdate = forceUpdate;
	}

	public void load(String id) throws IOException {
		PluginRepository repository = new PluginRepository(repositories, forceUpdate);
		PluginRelease pluginRelease = repository.findById(id);
		InstalledPlugin plugin = repository.resolveRelease(pluginRelease);
		loadFromFile(plugin.getPath());

	}

	public void loadFromFile(String filename) throws IOException {
		loadFromFile(new File(filename));
	}

	public void loadFromFile(File file) throws IOException {

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
			// System.out.println(
			// "Register plugin " + props.getProperty("moduleName") + " " +
			// props.getProperty("moduleVersion"));

			/*
			 * This code was extracted from GRAP. They have the same situation, that
			 * extensions are not loaded after adding jar at runtime. See also:
			 * https://stackoverflow.com/a/36770273 (we removed subclasses support -
			 * GROOVY-5543)
			 */

			Map<CachedClass, List<MetaMethod>> metaMethods = new HashMap<CachedClass, List<MetaMethod>>();
			MetaClassRegistryImpl mcRegistry = (MetaClassRegistryImpl) GroovySystem.getMetaClassRegistry();
			mcRegistry.registerExtensionModuleFromProperties(props, classLoader, metaMethods);
			for (CachedClass c : metaMethods.keySet()) {
				List<MetaMethod> methods = metaMethods.get(c);
				// System.out.println("Update " + c + " with " + methods);
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

	public void repository(String reposistory) {
		this.repositories.add(reposistory);
	}

	public List<String> getStaticImports() {
		return staticImports;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
