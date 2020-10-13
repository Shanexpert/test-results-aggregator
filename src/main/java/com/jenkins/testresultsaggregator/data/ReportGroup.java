package com.jenkins.testresultsaggregator.data;

import java.awt.Color;
import java.io.Serializable;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.helper.Colors;
import com.jenkins.testresultsaggregator.helper.Helper;

public class ReportGroup implements Serializable {
	
	private static final long serialVersionUID = 3491199923666L;
	
	private String status;
	private String percentageForJobs;
	private String percentageForTests;
	private int jobSuccess;
	private int jobFailed;
	private int jobUnstable;
	private int jobRunning;
	private int jobAborted;
	private int jobDisabled;
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
	
	public String getPercentageForJobs(boolean withColor, Integer fontSize) {
		if (Double.valueOf(percentageForJobs) > 0) {
			if (withColor) {
				setPercentageForJobs(Helper.colorizePercentage(Double.valueOf(percentageForJobs), fontSize, status));
			}
		} else {
			return "";
		}
		return percentageForJobs;
	}
	
	public void setPercentageForJobs(String percentageForJobs) {
		this.percentageForJobs = percentageForJobs;
	}
	
	public String getPercentageForTests(boolean withColor, Integer fontSize) {
		if (Double.valueOf(percentageForTests) > 0 && Double.valueOf(percentageForTests) != 100.0) {
			if (withColor) {
				setPercentageForTests(Helper.colorizePercentage(Double.valueOf(percentageForTests), fontSize, status));
			}
		} else {
			return "";
		}
		return percentageForTests;
	}
	
	public void setPercentageForTests(String percentageForTests) {
		this.percentageForTests = percentageForTests;
	}
	
	//
	public String getPercentage(boolean jobs, boolean tests, boolean withColor) {
		StringBuilder percentage = new StringBuilder();
		int fontSize = 12;
		String fontColor = Colors.html(Color.gray);
		if (jobs) {
			String jobPercentage = getPercentageForJobs(false, null);
			if (!Strings.isNullOrEmpty(jobPercentage)) {
				percentage.append(getPercentageForJobs(withColor, fontSize));
			}
		}
		if (tests) {
			String testPercentage = getPercentageForTests(false, null);
			if (!Strings.isNullOrEmpty(testPercentage)) {
				if (!Strings.isNullOrEmpty(percentage.toString())) {
					percentage.append("<font style='font-size:" + (fontSize - 2) + "px;color:" + fontColor + "'> Jobs</font>").append("<br>");
				}
				percentage.append(getPercentageForTests(true, fontSize)).append("<font style='font-size:" + (fontSize - 2) + "px;color:" + fontColor + "'> Tests</font>");
			} else {
				// Print something here?
			}
		}
		return percentage.toString();
	}
	
	public int getJobDisabled() {
		return jobDisabled;
	}
	
	public void setJobDisabled(int jobDisabled) {
		this.jobDisabled = jobDisabled;
	}
}
