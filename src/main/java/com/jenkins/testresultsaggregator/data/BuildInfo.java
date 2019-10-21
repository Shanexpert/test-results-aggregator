package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BuildInfo implements Serializable {
	
	private static final long serialVersionUID = 3491196L;
	
	private Boolean building;
	private String description;
	private Long duration;
	private int number;
	private String result;
	private PreviousBuildInfo previousBuild;
	private List<HashMap<Object, Object>> actions;
	private String fullDisplayName;
	private String displayName;
	private List<ChangeSet> changeSets;
	private Long timestamp;
	
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
	
}
