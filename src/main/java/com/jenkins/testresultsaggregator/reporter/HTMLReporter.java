package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import com.jenkins.testresultsaggregator.data.AggregatedDTO;

import hudson.FilePath;
import hudson.model.BuildListener;

public class HTMLReporter {
	
	private static final String OVERVIEW_FILE = "index.html";
	private static final String FOLDER = "/html";
	private static final String REPORT = "htmlreport.jelly";
	private BuildListener listener;
	private FilePath workspace;
	
	public HTMLReporter(BuildListener listener, FilePath workspace) {
		this.listener = listener;
		this.workspace = workspace;
	}
	
	public String createOverview(AggregatedDTO aggregated, List<String> columns) {
		try {
			listener.getLogger().print("Generate HTML Report");
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
			listener.getLogger().println("...Finished HTML Report");
			return file;
		} catch (Exception e) {
			listener.getLogger().printf("Error Occurred : %s ", e.getMessage());
		}
		return null;
	}
	
	private File createFolder(String folder) {
		File theDir = new File(folder);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				listener.getLogger().println("");
				listener.getLogger().printf("Error Occurred : %s ", se);
			}
		}
		return theDir;
	}
	
}
