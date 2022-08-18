package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BuildInfo implements Serializable {
	
	private static final long serialVersionUID = 3491196L;
	
	private Boolean building;
	private boolean ignore;
	private String description;
	private Long duration;
	private Long estimatedDuration;
	private int number;
	private String result;
	private PreviousBuildInfo previousBuild;
	private List<HashMap<Object, Object>> actions;
	private String fullDisplayName;
	private String displayName;
	private List<ChangeSet> changeSets;
	private Long timestamp;
	private String url;
	private String buildNumberUrl;
	
	public BuildInfo() {
		
	}
	
	public BuildInfo(String result) {
		setResult(result);
	}
	
	public Boolean getBuilding() {
		return building;
	}
	
	public void setBuilding(Boolean building) {
		this.building = building;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getDuration() {
		return duration;
	}
	
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public PreviousBuildInfo getPreviousBuild() {
		return previousBuild;
	}
	
	public void setPreviousBuild(PreviousBuildInfo previousBuild) {
		this.previousBuild = previousBuild;
	}
	
	public List<HashMap<Object, Object>> getActions() {
		return actions;
	}
	
	public void setActions(List<HashMap<Object, Object>> actions) {
		this.actions = actions;
	}
	
	public String getFullDisplayName() {
		return fullDisplayName;
	}
	
	public void setFullDisplayName(String fullDisplayName) {
		this.fullDisplayName = fullDisplayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public List<ChangeSet> getChangeSets() {
		return changeSets;
	}
	
	public void setChangeSets(List<ChangeSet> changeSets) {
		this.changeSets = changeSets;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getBuildNumberUrl() {
		return "<a href='" + url + "' style='text-decoration:none;'>" + number + "</a>";
	}
	
	public Long getEstimatedDuration() {
		if (building) {
			return estimatedDuration;
		}
		return 0L;
	}
	
	public void setEstimatedDuration(Long estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}
	
	public boolean getIgnore() {
		return ignore;
	}
	
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
}
