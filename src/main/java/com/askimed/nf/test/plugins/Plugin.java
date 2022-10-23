package com.askimed.nf.test.plugins;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Plugin {

	private String id;

	private String description;

	private String url;

	private String license;

	private List<PluginRelease> releases = new Vector<PluginRelease>();

	public Plugin(Map<String, Object> map) {
		id = map.get("id").toString();
		//TODO: set other properties
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("releases");
		for (Map<String, Object> releaseMap: list) {
			releases.add(new PluginRelease(this, releaseMap));
		}
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setReleases(List<PluginRelease> releases) {
		this.releases = releases;
	}

	public List<PluginRelease> getReleases() {
		return releases;
	}

}
