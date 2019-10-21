package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.helper.Colors;
import com.jenkins.testresultsaggregator.helper.GetEnumFromString;
import com.jenkins.testresultsaggregator.helper.Helper;

public class ReportJob implements Serializable {
	
	private static final long serialVersionUID = 3491366L;
	
	private String status;
	private String timestamp;
	private String total;
	private String pass;
	private String failed;
	private String failedColor;
	private String skipped;
	private String changes;
	private String reportURL;
	private String duration;
	private String description;
	private String parameters;
	private String percentage;
	
	public ReportJob() {
		
	}
	
	public String calculateStatus(Results resultsDTO) {
		setStatus(Helper.calculateStatus(resultsDTO.getCurrentResult(), resultsDTO.getPreviousResult()));
		return getStatus();
	}
	
	private String fixStatusName(String jobStatus) {
		return jobStatus.replaceAll("_", " ");
	}
	
	public void calculateTimestamp(Results resultsDTO, String outOfDateResults) {
		if (Strings.isNullOrEmpty(outOfDateResults)) {
			setTimestamp(Helper.getTimeStamp(resultsDTO.getTimestamp()));
		} else {
			setTimestamp(Helper.getTimeStamp(outOfDateResults, resultsDTO.getTimestamp()));
		}
	}
	
	public void calculateTotal(Results resultsDTO) {
		if (resultsDTO != null) {
			setTotal(Helper.diff(resultsDTO.getTotalDif(), resultsDTO.getTotal(), false));
		} else {
			setTotal("0");
		}
	}
	
	public void calculatePass(Results resultsDTO) {
		if (resultsDTO != null) {
			setPass(Helper.diff(resultsDTO.getPassDif(), resultsDTO.getPass(), false));
		} else {
			setPass("0");
		}
	}
	
	public void calculateFailedColor(Results resultsDTO) {
		if (resultsDTO != null) {
			setFailedColor(Helper.diff(resultsDTO.getFailDif(), resultsDTO.getFail(), null, Colors.FAILED, false));
		} else {
			setFailedColor("0");
		}
	}
	
	public void calculateFailed(Results resultsDTO) {
		if (resultsDTO != null) {
			setFailed(Helper.diff(resultsDTO.getFailDif(), resultsDTO.getFail(), false));
		} else {
			setFailed("0");
		}
	}
	
	public void calculateSkipped(Results resultsDTO) {
		if (resultsDTO != null) {
			setSkipped(Helper.diff(resultsDTO.getSkipDif(), resultsDTO.getSkip(), false));
		} else {
			setSkipped("0");
		}
	}
	
	public String getStatus() {
		return status;
	}
	
	public JobStatus getStatusFromEnum() {
		return GetEnumFromString.get(com.jenkins.testresultsaggregator.data.JobStatus.class, status);
	}
	
	public String getStatusColor(boolean withLinktoResults) {
		if (withLinktoResults) {
			if (getReportURL() != null) {
				return "<a href='" + getReportURL() + "' style='text-decoration:none;'>" + fixStatusName(Helper.colorizeResultStatus(status)) + "</a>";
			}
		}
		return getStatusColor();
	}
	
	public String getStatusColor() {
		return Helper.colorizeResultStatus(status);
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public void calculateChanges(Results resultsDTO) {
		setChanges(Helper.urlNumberofChanges(resultsDTO.getChangesUrl(), Helper.getNumber(resultsDTO.getNumberOfChanges())));
	}
	
	public void calculateReport(Results resultsDTO) {
		if (resultsDTO == null) {
			setReportURL(null);
		} else if (Strings.isNullOrEmpty(resultsDTO.getUrl())) {
			setReportURL(null);
		} else if (JobStatus.DISABLED.name().equalsIgnoreCase(resultsDTO.getCurrentResult())) {
			setReportURL(resultsDTO.getUrl());
		} else {
			setReportURL(resultsDTO.getReportUrl());
		}
	}
	
	public String getTotal(boolean withLinktoResults) {
		if (withLinktoResults) {
			if (!Strings.isNullOrEmpty(getTotal()) && !"0".equals(getTotal())) {
				return "<a href='" + getReportURL() + "' style='text-decoration:none;'>" + getTotal() + "</a>";
			} else {
				return "";
			}
		}
		return total;
	}
	
	public String getTotal() {
		return total;
	}
	
	public void setTotal(String total) {
		this.total = total;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String getFailed() {
		return failed;
	}
	
	public void setFailed(String failed) {
		this.failed = failed;
	}
	
	public String getSkipped() {
		return skipped;
	}
	
	public void setSkipped(String skipped) {
		this.skipped = skipped;
	}
	
	public String getChanges() {
		return changes;
	}
	
	public void setChanges(String calculatedChanges) {
		this.changes = calculatedChanges;
	}
	
	public String getFailedColor(boolean withLinktoResults) {
		if (withLinktoResults) {
			if (!Strings.isNullOrEmpty(getFailedColor()) && !"0".equals(getFailedColor())) {
				return "<a href='" + getReportURL() + "' style='text-decoration:none;'>" + getFailedColor() + "</a>";
			} else {
				return "";
			}
		}
		return failedColor;
	}
	
	public String getFailedColor() {
		return failedColor;
	}
	
	public void setFailedColor(String failedColor) {
		this.failedColor = failedColor;
	}
	
	public String getReportURL() {
		return reportURL;
	}
	
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
	public String calculateDuration(Long millis) {
		setDuration(Helper.duration(millis));
		return getDuration();
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String calculateDescription(String jobDescription) {
		setDescription(jobDescription);
		return getDescription();
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getParameters() {
		return parameters;
	}
	
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public String calculatePercentage(Results results) {
		if (results.getCurrentResult().equalsIgnoreCase(JobStatus.ABORTED.name()) ||
				results.getCurrentResult().equalsIgnoreCase(JobStatus.DISABLED.name()) ||
				results.getCurrentResult().equalsIgnoreCase(JobStatus.FAILURE.name()) ||
				results.getCurrentResult().equalsIgnoreCase(JobStatus.NOT_FOUND.name()) ||
				results.getCurrentResult().equalsIgnoreCase(JobStatus.RUNNING.name())) {
			setPercentage(null);
		} else {
			setPercentage(Double.toString(Helper.countPercentage(results)));
		}
		return percentage;
	}
	
	public String getPercentage(boolean withColor) {
		if (withColor && !Strings.isNullOrEmpty(percentage)) {
			return Helper.colorizePercentage(Double.valueOf(percentage));
		}
		return percentage;
	}
	
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
}
