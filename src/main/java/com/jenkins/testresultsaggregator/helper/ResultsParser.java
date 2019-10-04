package com.jenkins.testresultsaggregator.helper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JenkinsJobDTO;
import com.jenkins.testresultsaggregator.data.ResultsDTO;
import com.jenkins.testresultsaggregator.reporter.XMLReporter;

import hudson.FilePath;

public class ResultsParser {
	
	public ResultsParser() {
	}
	
	public AggregatedDTO parse(FilePath[] paths) {
		if (null == paths) {
			return new AggregatedDTO();
		}
		AggregatedDTO finalResults = new AggregatedDTO();
		for (FilePath path : paths) {
			File file = new File(path.getRemote());
			if (!file.isFile()) {
				continue;
			} else {
				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = dBuilder.parse(file);
					doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName(XMLReporter.ROOT);
					Node aggregated = nList.item(0);
					Node results = null, jobs = null;
					// Resolve
					for (int i = 0; i < aggregated.getChildNodes().getLength(); i++) {
						if (aggregated.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
							if (XMLReporter.RESULTS.equalsIgnoreCase(aggregated.getChildNodes().item(i).getNodeName())) {
								results = aggregated.getChildNodes().item(i);
							} else if (XMLReporter.JOBS.equalsIgnoreCase(aggregated.getChildNodes().item(i).getNodeName())) {
								jobs = aggregated.getChildNodes().item(i);
							}
						}
					}
					if (results != null) {
						for (int i = 0; i < results.getChildNodes().getLength(); i++) {
							Node currentNodeResults = results.getChildNodes().item(i);
							if (currentNodeResults.getNodeType() == Node.ELEMENT_NODE) {
								if (currentNodeResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.ABORTED)) {
									finalResults.setCountJobAborted(Integer.parseInt(currentNodeResults.getTextContent()));
								} else if (currentNodeResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.FAILED)) {
									finalResults.setCountJobFailures(Integer.parseInt(currentNodeResults.getTextContent()));
								} else if (currentNodeResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.RUNNING)) {
									finalResults.setCountJobRunning(Integer.parseInt(currentNodeResults.getTextContent()));
								} else if (currentNodeResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.SUCCESS)) {
									finalResults.setCountJobSuccess(Integer.parseInt(currentNodeResults.getTextContent()));
								} else if (currentNodeResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.UNSTABLE)) {
									finalResults.setCountJobUnstable(Integer.parseInt(currentNodeResults.getTextContent()));
								}
							}
						}
					}
					
					if (jobs != null) {
						List<DataJobDTO> dataJobs = new ArrayList<>();
						List<DataDTO> data = new ArrayList<>();
						data.add(new DataDTO("", dataJobs));
						finalResults.setData(data);
						for (int i = 0; i < jobs.getChildNodes().getLength(); i++) {
							Node currentNodeResults = jobs.getChildNodes().item(i);
							if (XMLReporter.JOB.equalsIgnoreCase(currentNodeResults.getNodeName())) {
								DataJobDTO dataJob = new DataJobDTO("", "");
								dataJobs.add(dataJob);
								dataJob.setResultsDTO(new ResultsDTO(null, null));
								dataJob.setJenkinsJob(new JenkinsJobDTO());
								for (int j = 0; j < currentNodeResults.getChildNodes().getLength(); j++) {
									Node jobResults = currentNodeResults.getChildNodes().item(j);
									if (jobResults.getNodeName().equalsIgnoreCase(XMLReporter.NAME)) {
										dataJob.setJobName(jobResults.getTextContent());
									} else if (jobResults.getNodeName().equalsIgnoreCase(XMLReporter.FNAME)) {
										dataJob.setJobFriendlyName(jobResults.getTextContent());
									} else if (jobResults.getNodeName().equalsIgnoreCase(XMLReporter.STATUS)) {
										dataJob.getResultsDTO().setCurrentResult(jobResults.getTextContent());
									} else if (jobResults.getNodeName().equalsIgnoreCase(XMLReporter.URL)) {
										try {
											dataJob.getJenkinsJob().setUrl(new URL(jobResults.getTextContent()));
										} catch (Exception ex) {
											
										}
									} else if (jobResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.SUCCESS)) {
										dataJob.getResultsDTO().setPass(Integer.parseInt(jobResults.getTextContent()));
									} else if (jobResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.ABORTED)) {
										dataJob.getResultsDTO().setSkip(Integer.parseInt(jobResults.getTextContent()));
									} else if (jobResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.FAILED)) {
										dataJob.getResultsDTO().setFail(Integer.parseInt(jobResults.getTextContent()));
									} else if (jobResults.getNodeName().equalsIgnoreCase(TestResultsAggregatorProjectAction.TOTAL)) {
										dataJob.getResultsDTO().setTotal(Integer.parseInt(jobResults.getTextContent()));
									}
								}
							}
						}
					}
				} catch (Exception ex) {
					
				}
			}
		}
		return finalResults;
	}
	
}
