package com.jenkins.testresultsaggregator.data;

import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.model.AbstractDescribableImpl;

public class DataDTO extends AbstractDescribableImpl<DataDTO> {
	
	private String groupName;
	private List<DataJobDTO> jobs;
	private AggregatedGroupDTO aggregatedGroup;
	
	@DataBoundConstructor
	public DataDTO(String groupName, List<DataJobDTO> jobs) {
		setGroupName(groupName);
		setJobs(jobs);
	}
	
	public String getGroupName() {
		if (groupName != null) {
			return groupName.trim();
		}
		return groupName;
	}
	
	@DataBoundSetter
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public List<DataJobDTO> getJobs() {
		return jobs;
	}
	
	@DataBoundSetter
	public void setJobs(List<DataJobDTO> jobs) {
		this.jobs = jobs;
	}
	
	public AggregatedGroupDTO getAggregatedGroup() {
		return aggregatedGroup;
	}
	
	public void setAggregatedGroup(AggregatedGroupDTO aggregatedGroup) {
		this.aggregatedGroup = aggregatedGroup;
	}
	
}
