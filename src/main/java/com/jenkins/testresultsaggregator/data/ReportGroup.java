package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import com.jenkins.testresultsaggregator.helper.Helper;

public class ReportGroup implements Serializable {
	
	private static final long serialVersionUID = 3491199923666L;
	
	private String status;
	private String percentage;
	private int jobSuccess;
	private int jobFailed;
	private int jobUnstable;
	private int jobRunning;
	private int jobAborted;
	private Results results;
	
	public ReportGroup() {
		this.jobSuccess = 0;
		this.jobFailed = 0;
		this.jobUnstable = 0;
		this.jobRunning = 0;
		this.jobAborted = 0;
		this.setResults(new Results());
	}
	
	public ReportGroup(String status, int jobSuccess, int jobFailed, int jobUnstable, int jobRunning, int jobAborted, Results resultsDTO) {
		super();
		this.status = status;
		this.jobSuccess = jobSuccess;
		this.jobFailed = jobFailed;
		this.jobUnstable = jobUnstable;
		this.jobRunning = jobRunning;
		this.jobAborted = jobAborted;
		this.setResults(resultsDTO);
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	/*public String getStatusColor() {
		final String SUCCESS = "<font color='" + Colors.htmlSUCCESS() + "'>SUCCESS</font>";
		final String FAILED = "<font color='" + Colors.htmlFAILED() + "'>FAIL</font>";
		final String UNSTABLE = "<font color='" + Colors.htmlUNSTABLE() + "'>UNSTABLE</font>";
		if (JobStatus.FAILURE.name().equalsIgnoreCase(status)) {
			return FAILED;
		} else if (JobStatus.UNSTABLE.name().equalsIgnoreCase(status)) {
			return UNSTABLE;
		} else if (JobStatus.SUCCESS.name().equalsIgnoreCase(status)) {
			return SUCCESS;
		}
		return status;
	}*/
	
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
	
	public Results getResults() {
		return results;
	}
	
	public void setResults(Results results) {
		this.results = results;
	}
	
	public String getPercentage(boolean withColor) {
		if (withColor) {
			setPercentage(Helper.colorizePercentage(Double.valueOf(percentage)));
		}
		return percentage;
	}
	
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
}
