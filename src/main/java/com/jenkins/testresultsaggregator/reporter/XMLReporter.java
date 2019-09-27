package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.ResultsDTO;

import hudson.FilePath;
import hudson.model.BuildListener;

public class XMLReporter {
	
	private static final String REPORT_FILE_JUNIT = "test.xml";
	private static final String FOLDER = "/xml";
	private BuildListener listener;
	private FilePath workspace;
	
	public XMLReporter(BuildListener listener, FilePath workspace) {
		this.listener = listener;
		this.workspace = workspace;
	}
	
	public void junit(List<DataJobDTO> listDataJobDTO, int total, int failed, int skipped) {
		listener.getLogger().print("Generate XML Report");
		try {
			File directory = createFolder(workspace + System.getProperty("file.separator") + FOLDER);
			for (DataJobDTO temp : listDataJobDTO) {
				if (!Strings.isNullOrEmpty(temp.getJobName())) {
					ResultsDTO resultsDTO = temp.getResultsDTO();
					String jobName = temp.getJobName();
					String fileName = directory.getAbsolutePath() + System.getProperty("file.separator") + jobName + "_" + REPORT_FILE_JUNIT;
					PrintWriter writer = new PrintWriter(fileName, "UTF-8");
					writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					writer.println("<testsuite name=\"" + jobName + "\" time=\"0\" tests=\"" + resultsDTO.getTotal() + "\" errors=\"" + 0 + "\" skipped=\"" + resultsDTO.getSkip() + "\" failures=\"" + resultsDTO.getFail()
							+ "\">");
					writer.println("<properties></properties>");
					for (int i = 1; i <= resultsDTO.getFail(); i++) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"" + jobName + "\" time=\"0\">");
						writer.println("<failure type=\"java.lang.AssertionError:\">");
						writer.println("</failure>");
						writer.println("</testcase>");
					}
					for (int i = 1; i <= resultsDTO.getSkip(); i++) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"" + jobName + "\" time=\"0\">");
						writer.println("<skipped></skipped>");
						writer.println("</testcase>");
					}
					for (int i = 1; i <= resultsDTO.getPass(); i++) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"" + jobName + "\" time=\"0\">");
						writer.println("</testcase>");
					}
					/*
					if (resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.SUCCESS.name()) || resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.FIXED.name())) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"jenkins.results.aggregator\" time=\"0\">");
						writer.println("</testcase>");
					} else if (resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.FAILURE.name()) || resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.STILL_FAILING.name())) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"jenkins.results.aggregator\" time=\"0\">");
						writer.println("<failure type=\"java.lang.AssertionError:\">");
						writer.println("</failure>");
						writer.println("</testcase>");
					} else if (resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.ABORTED.name()) || resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.STILL_UNSTABLE.name())
							|| resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.UNSTABLE.name()) || resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.DISABLED.name())
							|| resultsDTO.getCurrentResult().equalsIgnoreCase(JobStatus.NOT_FOUND.name())) {
						writer.println("<testcase name=\"" + jobName + "\" classname=\"jenkins.results.aggregator\" time=\"0\">");
						writer.println("<skipped></skipped>");
						writer.println("</testcase>");
					}*/
					writer.println("</testsuite>");
					writer.close();
				}
			}
			listener.getLogger().println("...Finished XML Report");
		} catch (IOException e) {
			listener.getLogger().println("");
			listener.getLogger().printf("Error Occurred : %s ", e);
		}
	}
	
	private static File createFolder(String folder) {
		File theDir = new File(folder);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}
		return theDir;
	}
	
}
