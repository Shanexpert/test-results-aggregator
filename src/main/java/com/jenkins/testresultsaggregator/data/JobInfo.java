package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class JobInfo implements Serializable {
	
	private static final long serialVersionUID = 74223666L;
	
	private URL url;
	private boolean buildable;
	private Boolean inQueue;
	private BuildDTO lastBuild;
	private BuildDTO lastCompletedBuild;
	private List<HealthReport> healthReport;
	private Integer nextBuildNumber;
	
	public JobInfo() {
	}
	
	public JobInfo(URL url) {
		setUrl(url);
	}
	
	public boolean getBuildable() {
		return buildable;
	}
	
	public void setBuildable(boolean buildable) {
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
	
	public List<HealthReport> getHealthReport() {
		return healthReport;
	}
	
	public String getHealthReport(boolean icon) {
		if (icon && healthReport != null) {
			for (HealthReport temp : healthReport) {
				if (temp.getDescription().startsWith("Build stability")) {
					return ImagesMap.getImage(temp.getScore());
				}
			}
		}
		return null;
	}
	
	public void setHealthReport(List<HealthReport> healthReport) {
		this.healthReport = healthReport;
	}
	
	public Integer getNextBuildNumber() {
		return nextBuildNumber;
	}

	public void setNextBuildNumber(Integer nextBuildNumber) {
		this.nextBuildNumber = nextBuildNumber;
	}

	public static class HealthReport implements Serializable {
		
		private static final long serialVersionUID = 742123666L;
		
		private int score;
		private String description;
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public int getScore() {
			return score;
		}
		
		public void setScore(int score) {
			this.score = score;
		}
		
	}
}
