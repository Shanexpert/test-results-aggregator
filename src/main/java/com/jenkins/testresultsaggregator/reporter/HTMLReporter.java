package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import hudson.FilePath;

public class HTMLReporter {
	
	private static final String OVERVIEW_FILE = "index.html";
	private static final String FOLDER = "/html";
	private static final String REPORT = "htmlreport.jelly";
	private PrintStream logger;
	private FilePath workspace;
	
	public HTMLReporter(PrintStream logger, FilePath workspace) {
		this.logger = logger;
		this.workspace = workspace;
	}
	
	public String createOverview(AggregatedDTO aggregated, List<String> columns) {
		try {
			logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.HTML_REPORT.toString());
			File directory = createFolder(workspace + System.getProperty("file.separator") + FOLDER);
			String file = directory + System.getProperty("file.separator") + OVERVIEW_FILE;
			OutputStream output = new FileOutputStream(file);
			JellyContext context = new JellyContext();
			context.setVariable("name", "Test Result Aggregator");
			context.setVariable("columns", columns);
			context.setVariable("aggregated", aggregated);
			XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
			URL template = HTMLReporter.class.getResource("/" + REPORT);
			context.runScript(template, xmlOutput);
			xmlOutput.endDocument();
			xmlOutput.flush();
			output.close();
			xmlOutput.close();
			logger.println(LocalMessages.FINISHED.toString() + " " + LocalMessages.HTML_REPORT.toString());
			return file;
		} catch (Exception e) {
			logger.println("");
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
		}
		return null;
	}
	
	private File createFolder(String folder) {
		File theDir = new File(folder);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException e) {
				logger.println("");
				logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
			}
		}
		return theDir;
	}
	
}
