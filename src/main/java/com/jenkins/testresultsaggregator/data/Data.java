package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.model.AbstractDescribableImpl;

public class Data extends AbstractDescribableImpl<Data> implements Serializable {
	
	private static final long serialVersionUID = 3491974223666L;
	
	private String groupName;
	private List<Job> jobs;
	private ReportGroup reportGroup;
	
	@DataBoundConstructor
	public Data(String groupName, List<Job> jobs) {
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
	
	public List<Job> getJobs() {
		return jobs;
	}
	
	@DataBoundSetter
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	public ReportGroup getReportGroup() {
		return reportGroup;
	}
	
	public void setReportGroup(ReportGroup reportGroup) {
		this.reportGroup = reportGroup;
	}
	
}
