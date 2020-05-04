package com.jenkins.testresultsaggregator.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobListDTO {
	
	@JsonProperty(value = "_class")
	private String classString;
	private List<JobDTO> jobs;
	
	public JobListDTO() {
		
	}
	
	public static class JobDTO {
		
		@JsonProperty(value = "_class")
		private String classString;
		private String name;
		private String url;
		private String folder;
		
		public JobDTO() {
			
		}
		
		public String getClassString() {
			return classString;
		}
		
		public void setClassString(String classString) {
			this.classString = classString;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getUrl() {
			return url;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getFolder() {
			return folder;
		}
		
		public void setFolder(String folder) {
			this.folder = folder;
		}
	}
	
	public String getClassString() {
		return classString;
	}
	
	public void setClassString(String classString) {
		this.classString = classString;
	}
	
	public List<JobDTO> getJobs() {
		return jobs;
	}
	
	public void setJobs(List<JobDTO> jobs) {
		this.jobs = jobs;
	}
}
