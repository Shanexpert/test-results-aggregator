package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

public class XMLReporter {
	public static final String REPORT_XML_FILE = "aggregated.xml";
	private PrintStream logger;
	private File workspace;
	public static final String S = "<";
	public static final String SE = "</";
	public static final String E = ">";
	
	public static final String ROOT = "Aggregated";
	public static final String RESULTS = "Results";
	public static final String JOBS = "JOBS";
	public static final String JOB = "JOB";
	public static final String NAME = "Name";
	public static final String FNAME = "FName";
	public static final String STATUS = "Status";
	public static final String URL = "URL";
	
	public XMLReporter(PrintStream logger, File rootDir) {
		this.logger = logger;
		this.workspace = rootDir;
	}
	
	public void generateXMLReport(AggregatedDTO aggregated) {
		try {
			logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.XML_REPORT.toString());
			String fileName = workspace.getAbsolutePath() + System.getProperty("file.separator") + REPORT_XML_FILE;
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println(S + ROOT + E);
			
			writer.println(S + RESULTS + E);
			writer.println("<" + TestResultsAggregatorProjectAction.SUCCESS + ">" + aggregated.getCountJobSuccess() + "</" + TestResultsAggregatorProjectAction.SUCCESS + ">");
			writer.println("<" + TestResultsAggregatorProjectAction.ABORTED + ">" + aggregated.getCountJobAborted() + "</" + TestResultsAggregatorProjectAction.ABORTED + ">");
			writer.println("<" + TestResultsAggregatorProjectAction.FAILED + ">" + aggregated.getCountJobFailures() + "</" + TestResultsAggregatorProjectAction.FAILED + ">");
			writer.println("<" + TestResultsAggregatorProjectAction.RUNNING + ">" + aggregated.getCountJobRunning() + "</" + TestResultsAggregatorProjectAction.RUNNING + ">");
			writer.println("<" + TestResultsAggregatorProjectAction.UNSTABLE + ">" + aggregated.getCountJobUnstable() + "</" + TestResultsAggregatorProjectAction.UNSTABLE + ">");
			writer.println(SE + RESULTS + E);
			
			writer.println(S + JOBS + E);
			for (DataDTO data : aggregated.getData()) {
				for (DataJobDTO dataJob : data.getJobs()) {
					writer.println(S + JOB + E);
					writer.println("<" + NAME + ">" + dataJob.getJobName() + "</" + NAME + ">");
					writer.println("<" + FNAME + ">" + dataJob.getJobFriendlyName() + "</" + FNAME + ">");
					writer.println("<" + STATUS + ">" + dataJob.getResultsDTO().getCurrentResult() + "</" + STATUS + ">");
					if (JobStatus.DISABLED.name().equalsIgnoreCase(dataJob.getResultsDTO().getCurrentResult())) {
						writer.println("<" + URL + ">" + dataJob.getJenkinsJob().getUrl().toString() + "</" + URL + ">");
					} else {
						writer.println("<" + URL + ">" + dataJob.getJenkinsJob().getLastBuild().getUrl().toString() + "</" + URL + ">");
					}
					writer.println("<" + TestResultsAggregatorProjectAction.TOTAL + ">" + dataJob.getResultsDTO().getTotal() + "</" + TestResultsAggregatorProjectAction.TOTAL + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.TOTAL_P + ">" + dataJob.getResultsDTO().getTotalDif() + "</" + TestResultsAggregatorProjectAction.TOTAL_P + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.SUCCESS + ">" + dataJob.getResultsDTO().getPass() + "</" + TestResultsAggregatorProjectAction.SUCCESS + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.SUCCESS_P + ">" + dataJob.getResultsDTO().getPassDif() + "</" + TestResultsAggregatorProjectAction.SUCCESS_P + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.ABORTED + ">" + dataJob.getResultsDTO().getSkip() + "</" + TestResultsAggregatorProjectAction.ABORTED + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.ABORTED_P + ">" + dataJob.getResultsDTO().getSkipDif() + "</" + TestResultsAggregatorProjectAction.ABORTED_P + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.FAILED + ">" + dataJob.getResultsDTO().getFail() + "</" + TestResultsAggregatorProjectAction.FAILED + ">");
					writer.println("<" + TestResultsAggregatorProjectAction.FAILED_P + ">" + dataJob.getResultsDTO().getFailDif() + "</" + TestResultsAggregatorProjectAction.FAILED_P + ">");
					writer.println(SE + JOB + E);
				}
			}
			writer.println(SE + JOBS + E);
			
			writer.println(SE + ROOT + E);
			writer.close();
			logger.println(LocalMessages.FINISHED.toString() + " " + LocalMessages.XML_REPORT.toString());
		} catch (IOException e) {
			logger.println("");
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
		}
	}
	
}
