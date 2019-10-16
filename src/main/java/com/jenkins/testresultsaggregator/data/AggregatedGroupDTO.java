package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import com.jenkins.testresultsaggregator.helper.Colors;

public class AggregatedGroupDTO implements Serializable {
	
	private static final long serialVersionUID = 3491199923666L;
	
	private String calculatedGroupStatus;
	private String calculatedGroupPercentage;
	private int jobSuccess;
	private int jobFailed;
	private int jobUnstable;
	private int jobRunning;
	private int jobAborted;
	private ResultsDTO resultsDTO;
	
	public AggregatedGroupDTO() {
		this.jobSuccess = 0;
		this.jobFailed = 0;
		this.jobUnstable = 0;
		this.jobRunning = 0;
		this.jobAborted = 0;
		this.setResultsDTO(new ResultsDTO());
	}
	
	public AggregatedGroupDTO(String calculatedGroupStatus, int jobSuccess, int jobFailed, int jobUnstable, int jobRunning, int jobAborted, ResultsDTO resultsDTO) {
		super();
		this.calculatedGroupStatus = calculatedGroupStatus;
		this.jobSuccess = jobSuccess;
		this.jobFailed = jobFailed;
		this.jobUnstable = jobUnstable;
		this.jobRunning = jobRunning;
		this.jobAborted = jobAborted;
		this.setResultsDTO(resultsDTO);
	}
	
	public String getCalculatedGroupStatus() {
		return calculatedGroupStatus;
	}
	
	public void setCalculatedGroupStatus(String calculatedGroupStatus) {
		this.calculatedGroupStatus = calculatedGroupStatus;
	}
	
	public String getCalculatedGroupStatusWithColor() {
		final String SUCCESS = "<font color='" + Colors.htmlSUCCESS() + "'>SUCCESS</font>";
		final String FAILED = "<font color='" + Colors.htmlFAILED() + "'>FAIL</font>";
		final String UNSTABLE = "<font color='" + Colors.htmlUNSTABLE() + "'>UNSTABLE</font>";
		if (JobStatus.FAILURE.name().equalsIgnoreCase(calculatedGroupStatus)) {
			return FAILED;
		} else if (JobStatus.UNSTABLE.name().equalsIgnoreCase(calculatedGroupStatus)) {
			return UNSTABLE;
		} else if (JobStatus.SUCCESS.name().equalsIgnoreCase(calculatedGroupStatus)) {
			return SUCCESS;
		}
		return calculatedGroupStatus;
		
	}
	
	public int getJobSuccess() {
		return jobSuccess;
	}
	
	public void setJobSuccess(int jobSuccess) {
		this.jobSuccess = jobSuccess;
	}
	
	public int getJobFailed() {
		return jobFailed;
	}
	
	public void setJobFailed(int jobFailed) {
		this.jobFailed = jobFailed;
	}
	
	public int getJobUnstable() {
		return jobUnstable;
	}
	
	public void setJobUnstable(int jobUnstable) {
		this.jobUnstable = jobUnstable;
	}
	
	public int getJobRunning() {
		return jobRunning;
	}
	
	public void setJobRunning(int jobRunning) {
		this.jobRunning = jobRunning;
	}
	
	public int getJobAborted() {
		return jobAborted;
	}
	
	public void setJobAborted(int jobAborted) {
		this.jobAborted = jobAborted;
	}
	
	public ResultsDTO getResultsDTO() {
		return resultsDTO;
	}
	
	public void setResultsDTO(ResultsDTO resultsDTO) {
		this.resultsDTO = resultsDTO;
	}
	
	public String getCalculatedGroupPercentage() {
		return calculatedGroupPercentage;
	}
	
	public void setCalculatedGroupPercentage(String calculatedGroupPercentage) {
		this.calculatedGroupPercentage = calculatedGroupPercentage;
	}
}
