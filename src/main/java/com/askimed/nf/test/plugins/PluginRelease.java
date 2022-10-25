package com.askimed.nf.test.plugins;

import java.util.Date;
import java.util.Map;

public class PluginRelease {

	private String url;

	private String version;

	private Date date;

	private String sha512sum;
	
	private Plugin plugin;

	public PluginRelease(Plugin plugin, Map<String, Object> map) {
		this.plugin = plugin;
		this.url = map.get("url").toString();
		this.version = map.get("version").toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSha512sum() {
		return sha512sum;
	}

	public void setSha512sum(String sha512sum) {
		this.sha512sum = sha512sum;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

}
