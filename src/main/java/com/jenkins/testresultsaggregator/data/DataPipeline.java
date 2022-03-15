package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class DataPipeline extends AbstractDescribableImpl<DataPipeline> implements Serializable {
	
	private static final long serialVersionUID = 3491974223666L;
	
	private String groupName;
	private String jobName;
	private String jobFriendlyName;
	
	@Extension
	public static class DataDescriptor extends Descriptor<DataPipeline> {
		@Override
		public String getDisplayName() {
			return "";
		}
	}
	
	@DataBoundConstructor
	public DataPipeline() {
		
	}
	
	public DataPipeline(String groupName, String jobName, String jobFriendlyName) {
		setGroupName(groupName);
		setJobName(jobName);
		setJobFriendlyName(jobFriendlyName);
	}
	
	public String getGroupName() {
		if (groupName != null) {
			return groupName.trim();
		}
		// Empty String for null groupName
		return "";
	}
	
	@DataBoundSetter
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	@DataBoundSetter
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobFriendlyName() {
		return jobFriendlyName;
	}
	
	@DataBoundSetter
	public void setJobFriendlyName(String jobFriendlyName) {
		this.jobFriendlyName = jobFriendlyName;
	}
	
}
