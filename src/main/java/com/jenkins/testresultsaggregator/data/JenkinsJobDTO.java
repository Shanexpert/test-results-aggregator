package com.jenkins.testresultsaggregator.data;

import java.net.URL;

public class JenkinsJobDTO {
	
	private URL url;
	private Boolean buildable;
	private Boolean inQueue;
	private BuildDTO lastBuild;
	private BuildDTO lastCompletedBuild;
	
	public JenkinsJobDTO() {
	}
	
	public JenkinsJobDTO(URL url) {
		setUrl(url);
	}
	
	public Boolean getBuildable() {
		return buildable;
	}
	
	public void setBuildable(Boolean buildable) {
		this.buildable = buildable;
	}
	
	public BuildDTO getLastBuild() {
		return lastBuild;
	}
	
	public void setLastBuild(BuildDTO lastBuild) {
		this.lastBuild = lastBuild;
	}
	
	public BuildDTO getLastCompletedBuild() {
		return lastCompletedBuild;
	}
	
	public void setLastCompletedBuild(BuildDTO lastCompletedBuild) {
		this.lastCompletedBuild = lastCompletedBuild;
	}
	
	public Boolean getInQueue() {
		return inQueue;
	}
	
	public void setInQueue(Boolean inQueue) {
		this.inQueue = inQueue;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
}
