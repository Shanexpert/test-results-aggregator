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
	
	public static final String ROOT = "AGGREGATED";
	public static final String RESULTS = "RESULTS";
	public static final String JOBS = "JOBS";
	public static final String JOB = "JOB";
	public static final String NAME = "NAME";
	public static final String STATUS = "STATUS";
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
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_SUCCESS, aggregated.getSuccessJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_FIXED, aggregated.getFixedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_ABORTED, aggregated.getAbortedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_FAILED, aggregated.getFailedJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_FAILED_KEEP, aggregated.getKeepFailJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_RUNNING, aggregated.getRunningJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_UNSTABLE, aggregated.getUnstableJobs()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.JOB_UNSTABLE_KEEP, aggregated.getKeepUnstableJobs()));
			
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_TOTAL, aggregated.getResults().getTotal()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SUCCESS, aggregated.getResults().getPass()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_FAILED, aggregated.getResults().getFail()));
			writer.println(TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SKIPPED, aggregated.getResults().getSkip()));
			writer.println(TAB + SE + RESULTS + E);
			
			writer.println(TAB + S + JOBS + E);
			for (Data data : aggregated.getData()) {
				for (Job dataJob : data.getJobs()) {
					if (dataJob.getBuildInfo() != null) {
						writer.println(TAB + TAB + S + JOB + E);
						writer.println(TAB + TAB + TAB + xmlTag(NAME, dataJob.getJobName()));
						if (dataJob.getResults() != null) {
							writer.println(TAB + TAB + TAB + xmlTag(STATUS, dataJob.getResults().getStatus()));
							if (JobStatus.DISABLED.name().equalsIgnoreCase(dataJob.getResults().getCurrentResult())) {
								jobStatus(writer, dataJob, dataJob.getJobInfo().getUrl(), true);
							} else if (JobStatus.NOT_FOUND.name().equalsIgnoreCase(dataJob.getResults().getCurrentResult())) {
								jobStatus(writer, dataJob, null, false);
							} else {
								jobStatus(writer, dataJob, dataJob.getJobInfo().getLastBuild().getUrl(), true);
							}
						} else {
							jobStatus(writer, dataJob, dataJob.getJobInfo().getLastBuild().getUrl(), true);
						}
						writer.println(TAB + TAB + SE + JOB + E);
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
	
	private void jobStatus(PrintWriter writer, Job dataJob, java.net.URL url, boolean found) {
		writer.println(TAB + TAB + TAB + xmlTag(URL, url));
		if (found && dataJob.getResults() != null) {
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_TOTAL, dataJob.getResults().getTotal()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SUCCESS, dataJob.getResults().getPass()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SKIPPED, dataJob.getResults().getSkip()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_FAILED, dataJob.getResults().getFail()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_PACKAGES, dataJob.getResults().getCcPackages()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_FILES, dataJob.getResults().getCcFiles()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_CLASSES, dataJob.getResults().getCcClasses()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_METHODS, dataJob.getResults().getCcMethods()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_LINES, dataJob.getResults().getCcLines()));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_CONDTITIONALS, dataJob.getResults().getCcConditions()));
		} else {
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_TOTAL, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SUCCESS, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_SKIPPED, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.TEST_FAILED, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_PACKAGES, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_FILES, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_CLASSES, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_METHODS, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_LINES, 0));
			writer.println(TAB + TAB + TAB + xmlTag(TestResultsAggregatorProjectAction.CC_CONDTITIONALS, 0));
		}
	}
}
