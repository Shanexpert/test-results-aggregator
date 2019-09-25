package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.IOException;

public class XMLReporter {
	
	private static final String REPORT_FILE_TESTNG = "testng-results.xml";
	private static final String REPORT_FILE_JUNIT = "test.xml";
	private static final String FOLDER = "/xml";
	
	public static void junit() {
		
	}
	
	private static File createFolder(String folder) {
		File theDir = new File(getCurrentDirectory() + folder);
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
	
	private static String getCurrentDirectory() {
		String current = null;
		try {
			current = new java.io.File(".").getCanonicalPath();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return current;
	}
	
}
