package com.jenkins.testresultsaggregator.data;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.helper.Colors;
import com.jenkins.testresultsaggregator.helper.Helper;

public class AggregateJobDTO {
	
	private String calculatedJobStatus;
	private String calculatedTimestamp;
	private String calculatedTotal;
	private String calculatedPass;
	private String calculatedFailed;
	private String calculatedFailedColor;
	private String calculatedSkipped;
	private String calculatedChanges;
	private String calculatedReport;
	private String reportURL;
	
	public AggregateJobDTO() {
		
	}
	
	public String calculateJobStatus(ResultsDTO resultsDTO) {
		String currentResult = resultsDTO.getCurrentResult();
		String previousResult = resultsDTO.getPreviousResult();
		if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.SUCCESS.name().equals(previousResult)) {
			setCalculatedJobStatus(JobStatus.SUCCESS.name());
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			setCalculatedJobStatus(JobStatus.FIXED.name());
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			setCalculatedJobStatus(JobStatus.FIXED.name());
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && previousResult == null) {
			setCalculatedJobStatus(JobStatus.SUCCESS.name());
		} else if (JobStatus.UNSTABLE.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			setCalculatedJobStatus(JobStatus.STILL_UNSTABLE.name());
		} else if (JobStatus.FAILURE.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			setCalculatedJobStatus(JobStatus.STILL_FAILING.name());
		} else if (JobStatus.FAILURE.name().equals(currentResult)) {
			setCalculatedJobStatus(JobStatus.FAILURE.name());
		} else if (JobStatus.UNSTABLE.name().equals(currentResult)) {
			setCalculatedJobStatus(JobStatus.UNSTABLE.name());
		} else if (JobStatus.RUNNING.name().equals(currentResult)) {
			setCalculatedJobStatus(JobStatus.RUNNING.name());
		} else if (JobStatus.ABORTED.name().equals(currentResult)) {
			setCalculatedJobStatus(JobStatus.ABORTED.name());
		} else if (JobStatus.SUCCESS.name().equals(currentResult)) {
			setCalculatedJobStatus(JobStatus.SUCCESS.name());
		} else {
			setCalculatedJobStatus(resultsDTO.getCurrentResult());
		}
		return getCalculatedJobStatus();
	}
	
	public void calculateTimestamp(ResultsDTO resultsDTO, String outOfDateResults) {
		if (Strings.isNullOrEmpty(outOfDateResults)) {
			setCalculatedTimestamp(Helper.getTimeStamp(resultsDTO.getTimestamp()));
		} else {
			setCalculatedTimestamp(Helper.getTimeStamp(outOfDateResults, resultsDTO.getTimestamp()));
		}
	}
	
	public void calculateTotal(ResultsDTO resultsDTO) {
		setCalculatedTotal(Helper.diff(resultsDTO.getTotalDif(), resultsDTO.getTotal(), false));
	}
	
	public void calculatePass(ResultsDTO resultsDTO) {
		setCalculatedPass(Helper.diff(resultsDTO.getPassDif(), resultsDTO.getPass(), false));
	}
	
	public void calculateFailedColor(ResultsDTO resultsDTO) {
		setCalculatedFailedColor(Helper.diff(resultsDTO.getFailDif(), resultsDTO.getFail(), null, Colors.FAILED, false));
	}
	
	public void calculateFailed(ResultsDTO resultsDTO) {
		setCalculatedFailed(Helper.diff(resultsDTO.getFailDif(), resultsDTO.getFail(), false));
	}
	
	public void calculateSkipped(ResultsDTO resultsDTO) {
		setCalculatedSkipped(Helper.diff(resultsDTO.getSkipDif(), resultsDTO.getSkip(), false));
	}
	
	public String getCalculatedJobStatus() {
		return calculatedJobStatus;
	}
	
	public String getCalculatedJobStatusWithColor(boolean withLinktoResults) {
		if (withLinktoResults) {
			if (getReportURL() == null) {
				getCalculatedJobStatusWithColor();
			} else {
				return "<a href='" + getReportURL() + "' style='text-decoration:none;'>" + Helper.colorizeResultStatus(calculatedJobStatus) + "</a>";
			}
		}
		return getCalculatedJobStatusWithColor();
	}
	
	public String getCalculatedJobStatusWithColor() {
		return Helper.colorizeResultStatus(calculatedJobStatus);
	}
	
	public void setCalculatedJobStatus(String calculateJobStatus) {
		this.calculatedJobStatus = calculateJobStatus;
	}
	
	public String getCalculatedTimestamp() {
		return calculatedTimestamp;
	}
	
	public void setCalculatedTimestamp(String calculatedTimestamp) {
		this.calculatedTimestamp = calculatedTimestamp;
	}
	
	public void calculateChanges(ResultsDTO resultsDTO) {
		setCalculatedChanges(Helper.urlNumberofChanges(resultsDTO.getChangesUrl(), Helper.getNumber(resultsDTO.getNumberOfChanges())));
	}
	
	public void calculateReport(ResultsDTO resultsDTO) {
		if (resultsDTO == null) {
			setCalculatedReport(null);
			setReportURL(null);
		} else if (Strings.isNullOrEmpty(resultsDTO.getUrl())) {
			setCalculatedReport(null);
			setReportURL(null);
		} else if (JobStatus.DISABLED.name().equalsIgnoreCase(resultsDTO.getCurrentResult())) {
			setCalculatedReport("<a href='" + resultsDTO.getUrl() + "'>link</a>");
			setReportURL(resultsDTO.getUrl());
		} else {
			setCalculatedReport("<a href='" + resultsDTO.getReportUrl() + "'>link</a>");
			setReportURL(resultsDTO.getReportUrl());
		}
	}
	
	public String getCalculatedTotal() {
		return calculatedTotal;
	}
	
	public void setCalculatedTotal(String calculatedTotal) {
		this.calculatedTotal = calculatedTotal;
	}
	
	public String getCalculatedPass() {
		return calculatedPass;
	}
	
	public void setCalculatedPass(String calculatedPass) {
		this.calculatedPass = calculatedPass;
	}
	
	public String getCalculatedFailed() {
		return calculatedFailed;
	}
	
	public void setCalculatedFailed(String calculatedFailed) {
		this.calculatedFailed = calculatedFailed;
	}
	
	public String getCalculatedSkipped() {
		return calculatedSkipped;
	}
	
	public void setCalculatedSkipped(String calculatedSkipped) {
		this.calculatedSkipped = calculatedSkipped;
	}
	
	public String getCalculatedChanges() {
		return calculatedChanges;
	}
	
	public void setCalculatedChanges(String calculatedChanges) {
		this.calculatedChanges = calculatedChanges;
	}
	
	public String getCalculatedReport() {
		return calculatedReport;
	}
	
	public void setCalculatedReport(String calculatedReport) {
		this.calculatedReport = calculatedReport;
	}
	
	public String getCalculatedFailedColor() {
		return calculatedFailedColor;
	}
	
	public void setCalculatedFailedColor(String calculatedFailedColor) {
		this.calculatedFailedColor = calculatedFailedColor;
	}
	
	public String getReportURL() {
		return reportURL;
	}
	
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
}
