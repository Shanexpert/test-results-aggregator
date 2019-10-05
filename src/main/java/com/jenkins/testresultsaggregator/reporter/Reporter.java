package com.jenkins.testresultsaggregator.reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import hudson.FilePath;

public class Reporter {
	
	private PrintStream logger;
	private FilePath workspace;
	private File rootDir;
	private String mailhost;
	private String mailNotificationFrom;
	
	private boolean foundAtLeastOneGroupName;
	private List<String> columns;
	
	public Reporter(PrintStream logger, FilePath workspace, File rootDir, String mailhost, String mailNotificationFrom) {
		this.logger = logger;
		this.workspace = workspace;
		this.rootDir = rootDir;
		this.mailhost = mailhost;
		this.mailNotificationFrom = mailNotificationFrom;
	}
	
	public void publishResuts(String recipientsList, String outOfDateResults,
			AggregatedDTO aggregated,
			boolean optionPrintGroupStatusInNewColumn,
			String theme,
			String preBodyText,
			String afterBodyText)
			throws Exception {
		List<DataDTO> dataJob = aggregated.getData();
		foundAtLeastOneGroupName = false;
		for (DataDTO data : dataJob) {
			if (!Strings.isNullOrEmpty(data.getGroupName())) {
				foundAtLeastOneGroupName = true;
				break;
			}
		}
		// Calculate and Generate Columns
		columns = new ArrayList<>();
		if (foundAtLeastOneGroupName) {
			columns = new ArrayList<>(Arrays.asList(LocalMessages.COLUMN_GROUP.toString()));
		}
		if (optionPrintGroupStatusInNewColumn) {
			columns.add(LocalMessages.COLUMN_GROUP_STATUS.toString());
		}
		columns.addAll(new ArrayList<>(Arrays.asList(
				LocalMessages.COLUMN_JOB.toString(),
				LocalMessages.COLUMN_JOB_STATUS.toString(),
				LocalMessages.COLUMN_TESTS.toString(),
				LocalMessages.COLUMN_PASS.toString(),
				LocalMessages.COLUMN_FAIL.toString(),
				LocalMessages.COLUMN_SKIP.toString(),
				LocalMessages.COLUMN_LAST_RUN.toString(),
				LocalMessages.COLUMN_COMMITS.toString(),
				LocalMessages.COLUMN_REPORT.toString())));
		// Generate HTML Report
		String htmlReport = new HTMLReporter(logger, workspace).createOverview(aggregated, columns, theme);
		// Generate and Send Mail report
		new MailNotification(logger, dataJob).send(recipientsList, mailNotificationFrom, generateMailSubject(aggregated), generateMailBody(htmlReport), mailhost, preBodyText, afterBodyText);
		// Generate XML Report
		new XMLReporter(logger, rootDir).generateXMLReport(aggregated);
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
		buf.close();
		return sb.toString();
	}
	
	private String generateMailSubject(AggregatedDTO aggregated) {
		String subject = LocalMessages.TEST_RESULTS.toString();
		if (aggregated.getCountJobRunning() > 0) {
			subject += " " + LocalMessages.RESULTS_RUNNING.toString() + " : " + aggregated.getCountJobRunning();
		}
		if (aggregated.getCountJobSuccess() > 0) {
			subject += " " + LocalMessages.RESULTS_SUCCESS.toString() + " : " + aggregated.getCountJobSuccess();
		}
		if (aggregated.getCountJobFailures() > 0) {
			subject += " " + LocalMessages.RESULTS_FAILED.toString() + " : " + aggregated.getCountJobFailures();
		}
		if (aggregated.getCountJobUnstable() > 0) {
			subject += " " + LocalMessages.RESULTS_UNSTABLE.toString() + " : " + aggregated.getCountJobUnstable();
		}
		if (aggregated.getCountJobAborted() > 0) {
			subject += " " + LocalMessages.RESULTS_ABORTED.toString() + " : " + aggregated.getCountJobAborted();
		}
		return subject;
	}
	
}
