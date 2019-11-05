package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction;
import com.jenkins.testresultsaggregator.data.Aggregated;
import com.jenkins.testresultsaggregator.data.Data;
import com.jenkins.testresultsaggregator.data.Job;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

public class XMLReporter {
	public static final String REPORT_XML_FILE = "aggregated.xml";
	private PrintStream logger;
	private File workspace;
	public static final String S = "<";
	public static final String SE = "</";
	public static final String E = ">";
	public static final String TAB = "\t";
	
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
	
	public void generateXMLReport(Aggregated aggregated) {
		try {
			logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.XML_REPORT.toString());
			String fileName = workspace.getAbsolutePath() + System.getProperty("file.separator") + REPORT_XML_FILE;
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println(S + ROOT + E);
			
			writer.println(TAB + S + RESULTS + E);
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS, aggregated.getSuccessJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FIXED, aggregated.getFixedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED, aggregated.getAbortedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED, aggregated.getFailedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_KEEP, aggregated.getKeepFailJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.RUNNING, aggregated.getRunningJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.UNSTABLE, aggregated.getUnstableJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.UNSTABLE_KEEP, aggregated.getKeepUnstableJobs()));
			
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL_TEST, aggregated.getResults().getTotal()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL_P_TEST, aggregated.getResults().getTotalDif()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS_TEST, aggregated.getResults().getPass()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P_TEST, aggregated.getResults().getPassDif()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_TEST, aggregated.getResults().getFail()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_P_TEST, aggregated.getResults().getFailDif()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED_TEST, aggregated.getResults().getSkip()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED_P_TEST, aggregated.getResults().getSkipDif()));
			writer.println(TAB + SE + RESULTS + E);
			
			writer.println(TAB + S + JOBS + E);
			for (Data data : aggregated.getData()) {
				for (Job dataJob : data.getJobs()) {
					if (dataJob.getBuildInfo() != null) {
						writer.println(TAB + S + JOB + E);
						writer.println(TAB + TAB + xmlTag(NAME, dataJob.getJobName()));
						writer.println(TAB + TAB + xmlTag(FNAME, dataJob.getJobFriendlyName()));
						writer.println(TAB + TAB + xmlTag(STATUS, dataJob.getResults().getStatus()));
						
						if (JobStatus.DISABLED.name().equalsIgnoreCase(dataJob.getResults().getCurrentResult())) {
							writer.println(TAB + TAB + xmlTag(URL, dataJob.getJobInfo().getUrl().toString()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL, dataJob.getResults().getTotal()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, dataJob.getResults().getTotalDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS, dataJob.getResults().getPass()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, dataJob.getResults().getPassDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED, dataJob.getResults().getSkip()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, dataJob.getResults().getSkipDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED, dataJob.getResults().getFail()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_P, dataJob.getResults().getFailDif()));
						} else if (JobStatus.NOT_FOUND.name().equalsIgnoreCase(dataJob.getResults().getCurrentResult())) {
							writer.println(TAB + TAB + xmlTag(URL, null));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED, 0));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_P, 0));
						} else {
							writer.println(TAB + TAB + xmlTag(URL, dataJob.getJobInfo().getLastBuild().getUrl().toString()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL, dataJob.getResults().getTotal()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TOTAL_P, dataJob.getResults().getTotalDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS, dataJob.getResults().getPass()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.SUCCESS_P, dataJob.getResults().getPassDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED, dataJob.getResults().getSkip()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.ABORTED_P, dataJob.getResults().getSkipDif()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED, dataJob.getResults().getFail()));
							writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.FAILED_P, dataJob.getResults().getFailDif()));
						}
						writer.println(TAB + SE + JOB + E);
					}
				}
			}
			writer.println(TAB + SE + JOBS + E);
			
			writer.println(SE + ROOT + E);
			writer.close();
			logger.println(LocalMessages.FINISHED.toString() + " " + LocalMessages.XML_REPORT.toString());
		} catch (IOException e) {
			logger.println("");
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String xmlTag(String tag, Object content) {
		if (content != null) {
			return "<" + tag + ">" + content + "</" + tag + ">";
		}
		return "<" + tag + "></" + tag + ">";
	}
}
