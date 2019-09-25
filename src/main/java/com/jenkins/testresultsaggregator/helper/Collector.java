package com.jenkins.testresultsaggregator.helper;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JenkinsBuildDTO;
import com.jenkins.testresultsaggregator.data.JenkinsJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ResultsDTO;

import hudson.model.BuildListener;

public class Collector {
	
	public static final String JOB = "job";
	public static final String API_JSON_URL = "api/json";
	public static final String LASTBUILD = "lastBuild";
	public static final String CHANGES = "changes";
	public static final String FAILCOUNT = "failCount";
	public static final String SKIPCOUNT = "skipCount";
	public static final String TOTALCOUNT = "totalCount";
	public static final String CONSOLE_OUTPUT = "console";
	public static final String TESTNG_REPORT = "testngreports";
	public static final String JUNIT_REPORT = "testReport";
	
	private String username;
	private String password;
	private String jenkinsUrl;
	private BuildListener listener;
	
	public Collector(BuildListener listener, String username, String password, String jenkinsUrl) {
		this.username = username;
		this.password = password;
		this.jenkinsUrl = jenkinsUrl;
		this.listener = listener;
		
	}
	
	public void collectResults(List<DataDTO> dataJob) {
		List<DataJobDTO> allDataJobDTO = new ArrayList<>();
		for (DataDTO temp : dataJob) {
			allDataJobDTO.addAll(temp.getJobs());
		}
		for (DataJobDTO tempDataJobDTO : allDataJobDTO) {
			listener.getLogger().print("Collecting Data from Jenkins Job named '" + tempDataJobDTO.getJobName() + "'");
			// Get Jenkins Job Info
			tempDataJobDTO.setJenkinsJob(getJobInfo(tempDataJobDTO));
			if (tempDataJobDTO.getJenkinsJob() == null) {
				// Job Not Found
				tempDataJobDTO.setJenkinsJob(new JenkinsJobDTO());
				tempDataJobDTO.setJenkinsBuild(new JenkinsBuildDTO(JobStatus.NOT_FOUND.name()));
				tempDataJobDTO.setResultsDTO(new ResultsDTO(JobStatus.NOT_FOUND.name(), null));
				listener.getLogger().println("...Job Not Found");
			} else if (!tempDataJobDTO.getJenkinsJob().getBuildable()) {
				// Job is Disabled/ Not Buildable
				tempDataJobDTO.setJenkinsJob(new JenkinsJobDTO());
				tempDataJobDTO.setJenkinsBuild(new JenkinsBuildDTO(JobStatus.DISABLED.name()));
				tempDataJobDTO.setResultsDTO(new ResultsDTO(JobStatus.DISABLED.name(), null));
				listener.getLogger().println("...Job is Disabled");
			} else if (tempDataJobDTO.getJenkinsJob() != null) {
				// Job Found and is Buildable
				// Get Job Results
				tempDataJobDTO.setJenkinsBuild(getJobResults(tempDataJobDTO));
				// Get Actual Results
				tempDataJobDTO.setResultsDTO(getResults(tempDataJobDTO));
				listener.getLogger().println("...DONE");
			} else {
				listener.getLogger().println("...");
			}
		}
	}
	
	private String authenticationString() {
		if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
			return Base64.getEncoder().encodeToString(new String(username + ":" + new String(password)).getBytes());
		}
		return null;
	}
	
	public String getAPIConnection() throws IOException {
		URL jobUrlAPI = new URL(jenkinsUrl + "/" + API_JSON_URL);
		return Http.get(jobUrlAPI, authenticationString());
	}
	
	public JenkinsJobDTO getJobInfo(DataJobDTO dataJobDTO) {
		try {
			URL jobUrlAPI = new URL(jenkinsUrl + "/" + JOB + "/" + dataJobDTO.getJobName() + "/" + API_JSON_URL);
			String reply = Http.get(jobUrlAPI, authenticationString());
			return Deserialize.initializeObjectMapper().readValue(reply, JenkinsJobDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JenkinsBuildDTO getJobResults(DataJobDTO dataJobDTO) {
		try {
			URL jobUrlAPILastBuild = new URL(dataJobDTO.getJenkinsJob().getUrl() + "/" + LASTBUILD + "/" + API_JSON_URL);
			// Get Latest
			String reply = Http.get(jobUrlAPILastBuild, authenticationString());
			return Deserialize.initializeObjectMapper().readValue(reply, JenkinsBuildDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultsDTO getResults(DataJobDTO dataJobDTO) {
		if (dataJobDTO.getJenkinsBuild() != null) {
			ResultsDTO resultsDTO = new ResultsDTO();
			// Set Building status
			resultsDTO.setBuilding(dataJobDTO.getJenkinsBuild().getBuilding());
			// Find Revision
			try {
				for (HashMap<Object, Object> temp : dataJobDTO.getJenkinsBuild().getActions()) {
					if (temp.containsKey("lastBuiltRevision")) {
						String lastBuiltRevision = temp.get("lastBuiltRevision").toString();
						lastBuiltRevision = lastBuiltRevision.substring(lastBuiltRevision.indexOf("name="), lastBuiltRevision.length()).replaceAll("]", "").replaceAll("}", "").replace("name=", "").trim();
						lastBuiltRevision = lastBuiltRevision.substring(lastBuiltRevision.lastIndexOf("/") + 1, lastBuiltRevision.length());
						resultsDTO.setRevision(lastBuiltRevision);
						break;
					}
				}
			} catch (Exception ex) {
				
			}
			// Set Current Result
			resultsDTO.setCurrentResult(dataJobDTO.getJenkinsBuild().getResult());
			// Set Description
			resultsDTO.setDescription(dataJobDTO.getJenkinsBuild().getDescription());
			// Set Duration
			resultsDTO.setDuration(dataJobDTO.getJenkinsBuild().getDuration());
			// Set Number
			resultsDTO.setNumber(dataJobDTO.getJenkinsBuild().getNumber());
			// Set TimeStamp
			if (dataJobDTO.getJenkinsBuild().getTimestamp() != null) {
				try {
					DateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss:SSS");
					String dateFormatted = formatter.format(new Date(dataJobDTO.getJenkinsBuild().getTimestamp()));
					resultsDTO.setTimestamp(dateFormatted);
				} catch (Exception ex) {
					
				}
			}
			// Set Change Set
			if (dataJobDTO.getJenkinsBuild().getChangeSets() != null) {
				if (dataJobDTO.getJenkinsBuild().getChangeSets().size() > 0) {
					// More than One Change set
					// TODO : Sub them
					resultsDTO.setNumberOfChanges(dataJobDTO.getJenkinsBuild().getChangeSets().get(0).getItems().size());
				} else {
					resultsDTO.setNumberOfChanges(dataJobDTO.getJenkinsBuild().getChangeSets().size());
				}
			} else {
				resultsDTO.setNumberOfChanges(0);
			}
			// Set Changes URL
			resultsDTO.setChangesUrl(dataJobDTO.getJenkinsJob().getUrl() + "/" + dataJobDTO.getJenkinsBuild().getNumber() + "/" + CHANGES);
			
			// If Job is not running get results
			if (!resultsDTO.isBuilding()) {
				// Calculate FAIL,SKIP and TOTAL Test Results
				for (HashMap<Object, Object> temp : dataJobDTO.getJenkinsBuild().getActions()) {
					if (temp.containsKey(FAILCOUNT)) {
						resultsDTO.setFail((Integer) temp.get(FAILCOUNT));
					}
					if (temp.containsKey(SKIPCOUNT)) {
						resultsDTO.setSkip((Integer) temp.get(SKIPCOUNT));
					}
					if (temp.containsKey(TOTALCOUNT)) {
						resultsDTO.setTotal((Integer) temp.get(TOTALCOUNT));
					}
				}
				// Calculate Pass Results
				resultsDTO.setPass(resultsDTO.getTotal() - Math.abs(resultsDTO.getFail()) - Math.abs(resultsDTO.getSkip()));
				// Add Url
				resultsDTO.setConsoleUrl(dataJobDTO.getJenkinsJob().getUrl().toString() + dataJobDTO.getJenkinsBuild().getNumber() + "/" + CONSOLE_OUTPUT);
				String testNGUrl = dataJobDTO.getJenkinsJob().getUrl().toString() + dataJobDTO.getJenkinsBuild().getNumber() + "/" + TESTNG_REPORT;
				String junitsUrl = dataJobDTO.getJenkinsJob().getUrl().toString() + dataJobDTO.getJenkinsBuild().getNumber() + "/" + JUNIT_REPORT;
				if (Http.getResponseCode(testNGUrl, authenticationString()) == 200) {
					resultsDTO.setReportUrl(testNGUrl);
				} else if (Http.getResponseCode(junitsUrl, authenticationString()) == 200) {
					resultsDTO.setReportUrl(junitsUrl);
				} else {
					resultsDTO.setReportUrl(resultsDTO.getConsoleUrl());
				}
				// Calculate Previous Results
				if (dataJobDTO.getJenkinsBuild().getPreviousBuild() != null) {
					// Get Previous
					try {
						JenkinsBuildDTO jenkinsPreviousBuildDTO = Deserialize.initializeObjectMapper()
								.readValue(Http.get(dataJobDTO.getJenkinsBuild().getPreviousBuild().getUrl() + "/" + API_JSON_URL, authenticationString()), JenkinsBuildDTO.class);
						resultsDTO.setPreviousResult(jenkinsPreviousBuildDTO.getResult());
						// Calculate FAIL,SKIP and TOTAL of the Previous Test
						int previouslyFail = 0;
						int previouslyPass = 0;
						int previouslySkip = 0;
						int previously = 0;
						for (HashMap<Object, Object> temp : jenkinsPreviousBuildDTO.getActions()) {
							if (temp.containsKey(FAILCOUNT)) {
								resultsDTO.setFailDif(resultsDTO.getFail() - (Integer) temp.get(FAILCOUNT));
								previouslyFail += (Integer) temp.get(FAILCOUNT);
							}
							if (temp.containsKey(SKIPCOUNT)) {
								resultsDTO.setSkipDif(resultsDTO.getSkip() - (Integer) temp.get(SKIPCOUNT));
								previouslySkip += (Integer) temp.get(SKIPCOUNT);
							}
							if (temp.containsKey(TOTALCOUNT)) {
								resultsDTO.setTotalDif(resultsDTO.getTotal() - (Integer) temp.get(TOTALCOUNT));
								previouslyPass += (Integer) temp.get(TOTALCOUNT);
							}
						}
						// Calculate Pass Difference Results
						previously = previouslyPass - Math.abs(previouslyFail) - Math.abs(previouslySkip);
						resultsDTO.setPassDif(resultsDTO.getPass() - previously);
					} catch (Exception ex) {
						
					}
				}
			} else {
				resultsDTO.setCurrentResult(JobStatus.RUNNING.name());
				// Add Url
				resultsDTO.setConsoleUrl(dataJobDTO.getJenkinsJob().getUrl() + "/" + dataJobDTO.getJenkinsBuild().getNumber() + "/" + CONSOLE_OUTPUT);
				resultsDTO.setReportUrl(resultsDTO.getConsoleUrl());
			}
			return resultsDTO;
		}
		return null;
	}
	
}
