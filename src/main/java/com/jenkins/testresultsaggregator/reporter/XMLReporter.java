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
			writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS, aggregated.getCountJobSuccess()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED, aggregated.getCountJobAborted()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED, aggregated.getCountJobFailures()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.RUNNING, aggregated.getCountJobRunning()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.UNSTABLE, aggregated.getCountJobUnstable()));
			
			writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL_TEST, aggregated.getResults().getTotal()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL_P_TEST, aggregated.getResults().getTotalDif()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS_TEST, aggregated.getResults().getPass()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P_TEST, aggregated.getResults().getPassDif()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED_TEST, aggregated.getResults().getFail()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED_P_TEST, aggregated.getResults().getFailDif()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED_TEST, aggregated.getResults().getSkip()));
			writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED_P_TEST, aggregated.getResults().getSkipDif()));
			writer.println(SE + RESULTS + E);
			
			writer.println(S + JOBS + E);
			for (DataDTO data : aggregated.getData()) {
				for (DataJobDTO dataJob : data.getJobs()) {
					if (dataJob.getJenkinsBuild() != null) {
						writer.println(S + JOB + E);
						writer.println(xmlTag(NAME, dataJob.getJobName()));
						writer.println(xmlTag(FNAME, dataJob.getJobFriendlyName()));
						writer.println(xmlTag(STATUS, dataJob.getResultsDTO().getCurrentResult()));
						
						if (JobStatus.DISABLED.name().equalsIgnoreCase(dataJob.getResultsDTO().getCurrentResult())) {
							writer.println(xmlTag(URL, dataJob.getJenkinsJob().getUrl().toString()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL, dataJob.getResultsDTO().getTotal()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, dataJob.getResultsDTO().getTotalDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS, dataJob.getResultsDTO().getPass()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, dataJob.getResultsDTO().getPassDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED, dataJob.getResultsDTO().getSkip()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, dataJob.getResultsDTO().getSkipDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED, dataJob.getResultsDTO().getFail()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED_P, dataJob.getResultsDTO().getFailDif()));
						} else if (JobStatus.NOT_FOUND.name().equalsIgnoreCase(dataJob.getResultsDTO().getCurrentResult())) {
							writer.println(xmlTag(URL, null));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED, 0));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED_P, 0));
						} else {
							writer.println(xmlTag(URL, dataJob.getJenkinsJob().getLastBuild().getUrl().toString()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL, dataJob.getResultsDTO().getTotal()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, dataJob.getResultsDTO().getTotalDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS, dataJob.getResultsDTO().getPass()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, dataJob.getResultsDTO().getPassDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED, dataJob.getResultsDTO().getSkip()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, dataJob.getResultsDTO().getSkipDif()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED, dataJob.getResultsDTO().getFail()));
							writer.println(xmlTag(TestResultsAggregatorProjectAction.FAILED_P, dataJob.getResultsDTO().getFailDif()));
						}
						writer.println(SE + JOB + E);
					}
				}
			}
			writer.println(SE + JOBS + E);
			
			writer.println(SE + ROOT + E);
			writer.close();
			logger.println(LocalMessages.FINISHED.toString() + " " + LocalMessages.XML_REPORT.toString());
		} catch (
		
		IOException e) {
			logger.println("");
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
		}
	}
	
	private String xmlTag(String tag, Object content) {
		if (content != null) {
			return "<" + tag + ">" + content + "</" + tag + ">";
		}
		return "<" + tag + "></" + tag + ">";
	}
}
