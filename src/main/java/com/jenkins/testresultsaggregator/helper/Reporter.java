package com.jenkins.testresultsaggregator.helper;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ResultsDTO;
import com.jenkins.testresultsaggregator.reporter.HTMLReporter;
import com.jenkins.testresultsaggregator.reporter.MailNotification;

import hudson.FilePath;
import hudson.model.BuildListener;

public class Reporter {
	
	private BuildListener listener;
	private FilePath workspace;
	private String mailhost;
	private String mailNotificationFrom;
	
	public Reporter(BuildListener listener, FilePath workspace, String mailhost, String mailNotificationFrom) {
		this.listener = listener;
		this.workspace = workspace;
		this.mailhost = mailhost;
		this.mailNotificationFrom = mailNotificationFrom;
	}
	
	private String table(boolean foundAtLeastOneGroupName) {
		final String LINE = "\n";
		String body = LINE +
				"<table style='width:100%';'>" + LINE +
				"<tr bgcolor='black'>" + LINE;
		if (foundAtLeastOneGroupName) {
			body += "<th style='text-align: center;color: white;'>GROUP/TEAM</th>" + LINE;
			body += "<th style='text-align: center;color: white;'>STATUS & PASS RATE</th>" + LINE;
		}
		body += "<th style='text-align: center;color: white;'>JOB</th>" + LINE +
				"<th style='text-align: center;color: white;'>JOB STATUS</th> " + LINE +
				"<th style='text-align: center;color: white;'>TESTS</th>" + LINE +
				"<th style='text-align: center;color: white;'>PASS</th>" + LINE +
				"<th style='text-align: center;color: white;'>FAIL</th>" + LINE +
				"<th style='text-align: center;color: white;'>SKIP</th>" + LINE +
				"<th style='text-align: center;color: white;'>LAST RUN</th>" + LINE +
				"<th style='text-align: center;color: white;'>COMMITS</th>" + LINE +
				"<th style='text-align: center;color: white;'>REPORT</th>" + LINE +
				"</tr>" + LINE;
		
		return body;
	}
	
	public void publishResuts(String recipientsList, String outOfDateResults, List<DataDTO> dataJob) throws Exception {
		final String LINE = "\n";
		final String REPLACE_ICON = "REPLACE_ICON";
		final String PASS_ICON = "<br><font color='green'>SUCCESS</font>";
		final String FAIL_ICON = "<br><font color='red'>FAIL</font>";
		final String SKIP_ICON = "<br><font color='orange'>UNSTABLE</font>";
		// Check if Groups/Names are used
		boolean foundAtLeastOneGroupName = false;
		for (DataDTO data : dataJob) {
			if (!Strings.isNullOrEmpty(data.getGroupName())) {
				foundAtLeastOneGroupName = true;
				break;
			}
		}
		// Order List per Group Name
		if (foundAtLeastOneGroupName) {
			Collections.sort(dataJob, new Comparator<DataDTO>() {
				@Override
				public int compare(DataDTO dataDTO1, DataDTO dataDTO2) {
					return dataDTO1.getGroupName().compareTo(dataDTO2.getGroupName());
				}
			});
		}
		// Order Jobs per Group
		for (DataDTO data : dataJob) {
			Collections.sort(data.getJobs(), new Comparator<DataJobDTO>() {
				@Override
				public int compare(DataJobDTO dataJobDTO1, DataJobDTO dataJobDTO2) {
					return dataJobDTO1.getJobNameFromFriendlyName().compareTo(dataJobDTO2.getJobNameFromFriendlyName());
				}
			});
		}
		String body = table(foundAtLeastOneGroupName);
		List<String> bgColor = new ArrayList<String>(Arrays.asList("#EEEEEE", "#DFDEDE"));
		int colorSelected = 0;
		int countJobFailures = 0;
		int countJobUnstable = 0;
		int countJobSuccess = 0;
		int countJobRunning = 0;
		int countJobAborted = 0;
		int index = 0;
		ResultsDTO totalResults = new ResultsDTO();
		for (DataDTO data : dataJob) {
			boolean foundFailure = false;
			boolean foundSkip = false;
			boolean groupAdded = false;
			ResultsDTO totalResultsPerGroup = new ResultsDTO();
			colorSelected = index % 2;
			index++;
			for (DataJobDTO job : data.getJobs()) {
				String result = getResult(job.getResultsDTO());
				totalResultsPerGroup.setPass(totalResultsPerGroup.getPass() + job.getResultsDTO().getPass());
				totalResultsPerGroup.setSkip(totalResultsPerGroup.getSkip() + job.getResultsDTO().getSkip());
				totalResultsPerGroup.setTotal(totalResultsPerGroup.getTotal() + job.getResultsDTO().getTotal());
				body += "<tr bgcolor='" + bgColor.get(colorSelected) + "'>" + LINE;
				if (!groupAdded && foundAtLeastOneGroupName) {
					body += "<th rowspan=\"" + data.getJobs().size() + "\">" + data.getGroupName() + "</th>" + LINE;
					body += "<th rowspan=\"" + data.getJobs().size() + "\">" + REPLACE_ICON + LINE + "</th>" + LINE;
					groupAdded = true;
				}
				if (job.getJobFriendlyName() != null && !job.getJobFriendlyName().isEmpty()) {
					body += "<td>" + job.getJobFriendlyName() + "</td>" + LINE;
				} else {
					body += "<td>" + job.getJobName() + "</td>" + LINE;
				}
				body += "<td style='text-align: center;'>" + colorizeResultStatus(result) + "</td> " + LINE;
				body += "<td style='text-align: center;'>" + job.getResultsDTO().getTotal() + singInteger(job.getResultsDTO().getTotalDif()) + "</td>" + LINE;
				body += "<td style='text-align: center;'>" + job.getResultsDTO().getPass() + singInteger(job.getResultsDTO().getPassDif()) + "</td>" + LINE;
				body += "<td style='text-align: center;'>" + colorizeFailResult(job.getResultsDTO().getFail()) + singInteger(job.getResultsDTO().getFailDif()) + "</td>" + LINE;
				body += "<td style='text-align: center;'>" + job.getResultsDTO().getSkip() + singInteger(job.getResultsDTO().getSkipDif()) + "</td>" + LINE;
				if (Strings.isNullOrEmpty(outOfDateResults)) {
					body += "<td style='text-align: center;'>" + getTimeStamp(job.getResultsDTO().getTimestamp()) + "</td> " + LINE;
				} else {
					body += "<td style='text-align: center;'>" + getTimeStamp(outOfDateResults, job.getResultsDTO().getTimestamp()) + "</td> " + LINE;
				}
				body += "<td style='text-align: center;'>" + urlNumberofChanges(job.getResultsDTO().getChangesUrl(), getNumber(job.getResultsDTO().getNumberOfChanges())) + LINE;
				body += "<td style='text-align: center;'><a href='" + job.getResultsDTO().getReportUrl() + "'>link</a></td>" + LINE;
				body += "</tr>" + LINE;
				if (JobStatus.SUCCESS.name().equals(result) || JobStatus.FIXED.name().equals(result)) {
					countJobSuccess++;
				} else if (JobStatus.FAILURE.name().equals(result) || JobStatus.STILL_FAILING.name().equals(result)) {
					countJobFailures++;
					foundFailure = true;
				} else if (JobStatus.UNSTABLE.name().equals(result) || JobStatus.STILL_UNSTABLE.name().equals(result)) {
					countJobUnstable++;
					foundSkip = true;
				} else if (JobStatus.RUNNING.name().equals(result)) {
					countJobRunning++;
				} else if (JobStatus.ABORTED.name().equals(result)) {
					countJobAborted++;
					foundSkip = true;
				}
				totalResults.sub(job.getResultsDTO());
			}
			if (foundFailure) {
				body = body.replace(REPLACE_ICON, FAIL_ICON + "<br>" + countPercentage(totalResultsPerGroup));
			} else if (foundSkip) {
				body = body.replace(REPLACE_ICON, SKIP_ICON + "<br>" + countPercentage(totalResultsPerGroup));
			} else {
				body = body.replace(REPLACE_ICON, PASS_ICON + "<br>" + countPercentage(totalResultsPerGroup));
			}
		}
		
		body += "<tr bgcolor='#e0e0eb'>" + LINE +
				"<td style='text-align: center;'>SUMMARY</td>" + LINE;
		if (foundAtLeastOneGroupName) {
			body += "<td></td>" + LINE +
					"<td></td> " + LINE;
		}
		body += "<td></td>" + LINE +
				"<td style='text-align: center;'>" + totalResults.getTotal() + singInteger(totalResults.getTotalDif()) + "</td>" + LINE +
				"<td style='text-align: center;'>" + totalResults.getPass() + singInteger(totalResults.getPassDif()) + "</td>" + LINE +
				"<td style='text-align: center;'>" + colorizeFailResult(totalResults.getFail()) + singInteger(totalResults.getFailDif()) + "</td>" + LINE +
				"<td style='text-align: center;'>" + totalResults.getSkip() + singInteger(totalResults.getSkipDif()) + "</td>" + LINE +
				"<td></td>" + LINE +
				"<td></td>" + LINE +
				"<td></td>" + LINE +
				"</tr>" + LINE;
		
		body += "</table>";
		String htmlFile = new HTMLReporter(listener, workspace).createOverview(body);
		
		if (!Strings.isNullOrEmpty(recipientsList)) {
			// Generate Mail Subject
			String subject = "Test Results ";
			if (countJobRunning > 0) {
				subject += " Running : " + countJobRunning;
			}
			if (countJobSuccess > 0) {
				subject += " Success : " + countJobSuccess;
			}
			if (countJobFailures > 0) {
				subject += " Failed : " + countJobFailures;
			}
			if (countJobUnstable > 0) {
				subject += " Unstable : " + countJobUnstable;
			}
			if (countJobAborted > 0) {
				subject += " Aborted : " + countJobAborted;
			}
			
			new MailNotification(listener, dataJob).send(recipientsList, mailNotificationFrom, subject, body, mailhost);
		}
	}
	
	private String colorizeResultStatus(String result) {
		if (JobStatus.SUCCESS.name().equals(result)) {
			return "<font color='green'>" + result + "</font>";
		} else if (JobStatus.FAILURE.name().equals(result)) {
			return "<font color='red'><b>" + result + "</b></font>";
		} else if (JobStatus.STILL_FAILING.name().equals(result)) {
			return "<font color='red'><b>" + result + "</b></font>";
		} else if (JobStatus.FIXED.name().equals(result)) {
			return "<font color='green'>" + result + "</font>";
		} else if (JobStatus.UNSTABLE.name().equals(result)) {
			return "<font color='orange'><b>" + result + "</b></font>";
		} else if (JobStatus.STILL_UNSTABLE.name().equals(result)) {
			return "<font color='orange'><b>" + result + "</b></font>";
		}
		return result;
	}
	
	private String getResult(ResultsDTO resultsDTO) {
		String currentResult = resultsDTO.getCurrentResult();
		String previousResult = resultsDTO.getPreviousResult();
		if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.SUCCESS.name().equals(previousResult)) {
			return JobStatus.SUCCESS.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			return JobStatus.FIXED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			return JobStatus.FIXED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && previousResult == null) {
			return JobStatus.SUCCESS.name();
		} else if (JobStatus.UNSTABLE.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			return JobStatus.STILL_UNSTABLE.name();
		} else if (JobStatus.FAILURE.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			return JobStatus.STILL_FAILING.name();
		} else if (JobStatus.FAILURE.name().equals(currentResult)) {
			return JobStatus.FAILURE.name();
		} else if (JobStatus.UNSTABLE.name().equals(currentResult)) {
			return JobStatus.UNSTABLE.name();
		} else if (JobStatus.RUNNING.name().equals(currentResult)) {
			return JobStatus.RUNNING.name();
		} else if (JobStatus.ABORTED.name().equals(currentResult)) {
			return JobStatus.ABORTED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult)) {
			return JobStatus.SUCCESS.name();
		} else
			return resultsDTO.getCurrentResult();
	}
	
	private String singInteger(int value) {
		if (value < 0) {
			return "(" + Integer.toString(value) + ")";
		} else if (value > 0) {
			return "(+" + value + ")";
		} else {
			return "";
		}
	}
	
	private String colorizeFailResult(int result) {
		if (result > 0) {
			return "<b><font color='red'>" + result + "</font></b>";
		}
		return Integer.toString(result);
	}
	
	private String getTimeStamp(String hours, String timeStamp) {
		int outOfDate = 43200;
		try {
			outOfDate = Integer.parseInt(hours) * 3600;
		} catch (Exception ex) {
			
		}
		if (timeStamp == null || timeStamp.isEmpty()) {
			return "";
		} else {
			LocalDateTime today = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
			Duration d = Duration.between(date, today);
			long currentHours = d.getSeconds() / 3600;
			long bDours = currentHours % 24;
			long bDays = currentHours / 24;
			if (d.getSeconds() > outOfDate) {
				if (bDays > 0) {
					return "<font color='red'>" + bDays + " Days and" + bDours + " hours ago</font>";
				} else {
					return "<font color='red'>" + bDours + " hours ago</font>";
				}
			}
			return currentHours + " hours ago";
		}
	}
	
	private String getTimeStamp(String timeStamp) {
		if (timeStamp == null || timeStamp.isEmpty()) {
			return "";
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
			return date.toString();
		}
	}
	
	private String getNumber(int value) {
		if (value < 0) {
			return Integer.toString(value);
		} else if (value > 0) {
			return Integer.toString(value);
		} else {
			return "";
		}
	}
	
	private String singDoubleNo(double value) {
		DecimalFormat df = new DecimalFormat("#.##");
		String valueAsString = df.format(value);
		value = Double.valueOf(valueAsString);
		if (Math.abs(value) == 0) {
			return "";
		} else if (value < 0.00) {
			return df.format(value);
		} else if (value > 0) {
			return df.format(value);
		} else {
			return "";
		}
	}
	
	private String urlNumberofChanges(String url, String number) {
		if (!number.isEmpty()) {
			return "<a href = '" + url + "'>" + number + "</a>";
		}
		return "";
	}
	
	private static String countPercentage(ResultsDTO resultsDTO) {
		String percentage = "0";
		try {
			percentage = singDoubleSingle((resultsDTO.getPass() + resultsDTO.getSkip()) * 100 / resultsDTO.getTotal());
		} catch (Exception ex) {
			
		}
		double percentageDouble = 0;
		try {
			percentageDouble = Double.parseDouble(percentage);
		} catch (Exception ex) {
			
		}
		if (percentageDouble >= 100) {
			percentage = "Pass Rate : <font color='green'>" + percentage + "%" + "</font>";
		} else if (percentageDouble >= 95) {
			percentage = "Pass Rate : <font color='orange'>" + percentage + "%" + "</font>";
		} else {
			percentage = "Pass Rate : <font color='red'>" + percentage + "%" + "</font>";
		}
		return percentage;
	}
	
	private static String singDoubleSingle(double value) {
		DecimalFormat df = new DecimalFormat("#.####");
		String valueAsString = df.format(value);
		value = Double.valueOf(valueAsString);
		if (Math.abs(value) < 0.005) {
			return "";
		} else if (Math.abs(value) == 0) {
			return "";
		} else if (value < 0.00) {
			return df.format(value);
		} else if (value > 0) {
			return df.format(value);
		} else {
			return "";
		}
	}
}
