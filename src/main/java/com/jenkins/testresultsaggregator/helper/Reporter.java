package com.jenkins.testresultsaggregator.helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
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
	
	public void publishResuts(String recipientsList, String outOfDateResults, AggregatedDTO aggregated) throws Exception {
		List<DataDTO> dataJob = aggregated.getData();
		boolean foundAtLeastOneGroupName = false;
		for (DataDTO data : dataJob) {
			if (!Strings.isNullOrEmpty(data.getGroupName())) {
				foundAtLeastOneGroupName = true;
				break;
			}
		}
		// Calculate and Generate Columns
		List<String> columns;
		if (foundAtLeastOneGroupName) {
			columns = new ArrayList<>(Arrays.asList("GROUP/TEAM", "STATUS & PASS RATE", "JOB", "JOB STATUS", "TESTS", "PASS", "FAIL", "SKIP", "LAST RUN", "COMMITS", "REPORT"));
		} else {
			columns = new ArrayList<>(Arrays.asList("JOB", "JOB STATUS", "TESTS", "PASS", "FAIL", "SKIP", "LAST RUN", "COMMITS", "REPORT"));
		}
		// Generate HTML report
		String htmlReport = new HTMLReporter(listener, workspace).createOverview(aggregated, columns);
		// Generate XML report
		// new XMLReporter(listener, workspace).junit(listDataJobDTO, countJobSuccess, countJobFailures, countJobUnstable);
		// Generate Mail Body
		String content = generateMailBody(htmlReport);
		new MailNotification(listener, dataJob).send(recipientsList, mailNotificationFrom, generateMailSubject(aggregated), content, mailhost);
	}
	
	private String generateMailBody(String htmlReport) throws Exception {
		InputStream is = new FileInputStream(htmlReport);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();
		while (line != null) {
			sb.append(line).append("\n");
			line = buf.readLine();
		}
		return sb.toString();
	}
	
	private String generateMailSubject(AggregatedDTO aggregated) {
		String subject = "Test Results ";
		if (aggregated.getCountJobRunning() > 0) {
			subject += " Running : " + aggregated.getCountJobRunning();
		}
		if (aggregated.getCountJobSuccess() > 0) {
			subject += " Success : " + aggregated.getCountJobSuccess();
		}
		if (aggregated.getCountJobFailures() > 0) {
			subject += " Failed : " + aggregated.getCountJobFailures();
		}
		if (aggregated.getCountJobUnstable() > 0) {
			subject += " Unstable : " + aggregated.getCountJobUnstable();
		}
		if (aggregated.getCountJobAborted() > 0) {
			subject += " Aborted : " + aggregated.getCountJobAborted();
		}
		return subject;
	}
	
}
