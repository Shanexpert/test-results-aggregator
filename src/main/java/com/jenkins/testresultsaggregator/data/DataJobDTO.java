package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.helper.Colors;

import hudson.model.AbstractDescribableImpl;

public class DataJobDTO extends AbstractDescribableImpl<DataJobDTO> implements Serializable {
	
	private static final long serialVersionUID = 34911974223666L;
	
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
	
	public String getJobNameFromFriendlyName(boolean withLinktoResults) {
		if (withLinktoResults) {
			String reportUrl = null;
			if (resultsDTO == null) {
				reportUrl = null;
			} else if (Strings.isNullOrEmpty(resultsDTO.getUrl())) {
				reportUrl = null;
			} else if (JobStatus.DISABLED.name().equalsIgnoreCase(resultsDTO.getCurrentResult())) {
				reportUrl = resultsDTO.getUrl();
			} else {
				reportUrl = resultsDTO.getReportUrl();
			}
			return "<a href='" + reportUrl + "' style='text-decoration:none;'><font color='" + Colors.htmlJOB_NAME_URL() + "'>" + getJobNameFromFriendlyName() + "</font></a>";
		}
		return getJobNameFromFriendlyName();
	}
	
	public AggregateJobDTO getAggregate() {
		return aggregate;
	}
	
	public void setAggregate(AggregateJobDTO aggregate) {
		this.aggregate = aggregate;
	}
}
