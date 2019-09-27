package com.jenkins.testresultsaggregator.data;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.model.AbstractDescribableImpl;

public class DataJobDTO extends AbstractDescribableImpl<DataJobDTO> {
	
	private String jobName;
	private String jobFriendlyName;
	//
	private JenkinsJobDTO jenkinsJob;
	private JenkinsBuildDTO JenkinsBuild;
	private ResultsDTO resultsDTO;
	private AggregateJobDTO aggregate;
	
	@DataBoundConstructor
	public DataJobDTO(String jobName, String jobFriendlyName) {
		setJobName(jobName);
		setJobFriendlyName(jobFriendlyName);
	}
	
	public String getJobName() {
		if (jobName != null) {
			return jobName.trim();
		}
		return jobName;
	}
	
	@DataBoundSetter
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobFriendlyName() {
		if (jobFriendlyName != null) {
			return jobFriendlyName.trim();
		}
		return jobFriendlyName;
	}
	
	@DataBoundSetter
	public void setJobFriendlyName(String jonFriendlyName) {
		this.jobFriendlyName = jonFriendlyName;
	}
	
	public JenkinsJobDTO getJenkinsJob() {
		return jenkinsJob;
	}
	
	public void setJenkinsJob(JenkinsJobDTO jenkinsJob) {
		this.jenkinsJob = jenkinsJob;
	}
	
	public JenkinsBuildDTO getJenkinsBuild() {
		return JenkinsBuild;
	}
	
	public void setJenkinsBuild(JenkinsBuildDTO jenkinsBuild) {
		JenkinsBuild = jenkinsBuild;
	}
	
	public ResultsDTO getResultsDTO() {
		return resultsDTO;
	}
	
	public void setResultsDTO(ResultsDTO resultsDTO) {
		this.resultsDTO = resultsDTO;
	}
	
	public String getJobNameFromFriendlyName() {
		if (jobFriendlyName == null || jobFriendlyName.isEmpty()) {
			return jobName;
		}
		return jobFriendlyName;
	}
	
	public AggregateJobDTO getAggregate() {
		return aggregate;
	}
	
	public void setAggregate(AggregateJobDTO aggregate) {
		this.aggregate = aggregate;
	}
}
