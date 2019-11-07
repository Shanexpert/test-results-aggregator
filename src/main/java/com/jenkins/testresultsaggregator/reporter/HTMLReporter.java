package com.jenkins.testresultsaggregator.reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.SAXException;

import com.jenkins.testresultsaggregator.data.Aggregated;
import com.jenkins.testresultsaggregator.data.ImagesMap;
import com.jenkins.testresultsaggregator.helper.Colors;
import com.jenkins.testresultsaggregator.helper.Helper;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import hudson.FilePath;

public class HTMLReporter {
	
	public static final String FOLDER = "html";
	private static final String OVERVIEW_FILE = "index.html";
	private static final String REPORT = "htmlreport.jelly";
	private PrintStream logger;
	private FilePath workspace;
	
	public HTMLReporter(PrintStream logger, FilePath workspace) {
		this.logger = logger;
		this.workspace = workspace;
	}
	
	public FilePath createOverview(Aggregated aggregated, List<LocalMessages> columns, String theme, boolean showGroups) throws JellyException, SAXException, IOException, InterruptedException {
		logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.HTML_REPORT.toString());
		FilePath directory = Helper.createFolder(workspace, FOLDER);
		FilePath file = Helper.createFile(directory, OVERVIEW_FILE);
		JellyContext context = new JellyContext();
		// Variables
		context.setVariable("name", "Test Result Aggregator");
		context.setVariable("showGroups", showGroups);
		context.setVariable("columns", columns);
		context.setVariable("aggregated", aggregated);
		// Themes light and dark
		context.setVariable("theme", theme);
		// Header & footer color
		context.setVariable("headerColor", Colors.htmlHEADER());
		context.setVariable("footerColor", Colors.htmlFOOTER());
		context.setVariable("footerTextColor", Colors.htmlFOOTERTEXT());
		// Line Seperator color
		context.setVariable("lineSeperatorcolor", Colors.htmlLINESEPERATOR());
		XMLOutput xmlOutput = XMLOutput.createXMLOutput(file.write());
		URL template = HTMLReporter.class.getResource("/" + REPORT);
		JellyContext jellyContext = context.runScript(template, xmlOutput);
		xmlOutput.endDocument();
		xmlOutput.flush();
		xmlOutput.close();
		jellyContext.clear();
		// Copy Images
		copyImages(directory);
		logger.println(LocalMessages.FINISHED.toString() + " " + LocalMessages.HTML_REPORT.toString());
		return file;
	}
	
	private void copyImages(FilePath directory) throws IOException, InterruptedException {
		Set<String> setImageID = ImagesMap.getImages().keySet();
		for (String contentId : setImageID) {
			copyStream(ImagesMap.getImages().get(contentId).getSourceInPlugin(), ImagesMap.getImages().get(contentId).getFileName(), directory.getRemote());
		}
	}
	
	protected void copyStream(String sourceFile, String destinationFile, String path) throws IOException {
		URL inputUrl = HTMLReporter.class.getResource(sourceFile);
		File dest = new File(path + "/" + destinationFile);
		FileUtils.copyURLToFile(inputUrl, dest);
	}
}
