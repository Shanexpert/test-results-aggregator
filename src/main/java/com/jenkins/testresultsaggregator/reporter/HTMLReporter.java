package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import hudson.FilePath;
import hudson.model.BuildListener;

public class HTMLReporter {
	
	private static final String OVERVIEW_FILE = "index.html";
	private static final String FOLDER = "/html";
	private BuildListener listener;
	private FilePath workspace;
	
	public HTMLReporter(BuildListener listener, FilePath workspace) {
		this.listener = listener;
		this.workspace = workspace;
	}
	
	public String createOverview(String text) {
		File directory = createFolder(workspace + System.getProperty("file.separator") + FOLDER);
		String file = directory.getAbsolutePath() + System.getProperty("file.separator") + OVERVIEW_FILE;
		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
			listener.getLogger().print("Generate HTML Report");
			writer.println("<!DOCTYPE html><html><body>");
			writer.println(text);
			writer.println("</body></html>");
			writer.close();
			listener.getLogger().println("...Finished HTML Report");
			return file;
		} catch (FileNotFoundException e) {
			listener.getLogger().println("");
			listener.getLogger().printf("Error Occurred : %s ", e);
		} catch (UnsupportedEncodingException e) {
			listener.getLogger().println("");
			listener.getLogger().printf("Error Occurred : %s ", e);
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
