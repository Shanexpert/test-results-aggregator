package com.jenkins.testresultsaggregator.helper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.BuildInfo;
import com.jenkins.testresultsaggregator.data.ChangeSet;
import com.jenkins.testresultsaggregator.data.CoberturaCoverage;
import com.jenkins.testresultsaggregator.data.CoberturaCoverage.Element;
import com.jenkins.testresultsaggregator.data.Data;
import com.jenkins.testresultsaggregator.data.Job;
import com.jenkins.testresultsaggregator.data.JobInfo;
import com.jenkins.testresultsaggregator.data.JobListDTO;
import com.jenkins.testresultsaggregator.data.JobListDTO.JobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ReportJob;
import com.jenkins.testresultsaggregator.data.Results;

import hudson.util.Secret;

public class Collector {
	
	public static final String JOB = "job";
	public static final String API_JSON_URL = "api/json";
	public static final String API_JSON_JOBS = API_JSON_URL + "?tree=jobs[name,url]";
	public static final String API_JSON_COBERTURA = "cobertura/" + API_JSON_URL + "?depth=2";
	public static final String API_JSON_JACOCO = API_JSON_URL + "?depth=1";
	public static final String LASTBUILD = "lastBuild";
	public static final String CHANGES = "changes";
	public static final String FAILCOUNT = "failCount";
	public static final String SKIPCOUNT = "skipCount";
	public static final String TOTALCOUNT = "totalCount";
	
	public static final String JACOCO_BRANCH = "branchCoverage";
	public static final String JACOCO_CLASS = "classCoverage";
	public static final String JACOCO_LINES = "lineCoverage";
	public static final String JACOCO_METHODS = "methodCoverage";
	public static final String JACOCO_INSTRUCTION = "instructionCoverage";
	
	public static final String SONAR_URL = "sonarqubeDashboardUrl";
	public static final int MAXRETRIES = 3;
	
	private String username;
	private Secret password;
	private String jenkinsUrl;
	private PrintStream logger;
	
	public Collector(PrintStream logger, String username, Secret password, String jenkinsUrl) {
		this.username = username;
		this.password = password;
		this.jenkinsUrl = jenkinsUrl;
		this.logger = logger;
	}
	
	public void collectResults(List<Data> dataJob, boolean compareWithPreviousRun) throws InterruptedException {
		List<Job> allDataJobDTO = new ArrayList<>();
		for (Data temp : dataJob) {
			if (temp.getJobs() != null && !temp.getJobs().isEmpty()) {
				allDataJobDTO.addAll(temp.getJobs());
			}
		}
		ReportThread[] threads = new ReportThread[allDataJobDTO.size()];
		int index = 0;
		for (Job tempDataJobDTO : allDataJobDTO) {
			threads[index] = new ReportThread(tempDataJobDTO, compareWithPreviousRun);
			index++;
		}
		index = 0;
		for (ReportThread thread : threads) {
			thread.start();
			index++;
			if (index % 3 == 0) {
				Thread.sleep(500);
			}
		}
		for (ReportThread thread : threads) {
			thread.join(Http.HTTPTIMEOUT * MAXRETRIES);
		}
	}
	
	private String authenticationString() {
		if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password.getPlainText())) {
			byte[] encode = (username + ":" + password.getPlainText()).getBytes(Charset.forName("UTF-8"));
			return Base64.getEncoder().encodeToString(encode);
		}
		return null;
	}
	
	public String getAPIConnection() throws IOException {
		URL jobUrlAPI = new URL(jenkinsUrl + "/" + API_JSON_URL);
		return Http.get(jobUrlAPI, authenticationString());
	}
	
	public void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
		}
	}
	
	public JobListDTO getJobList() {
		return getJobList(jenkinsUrl);
	}
	
	public JobListDTO getJobList(String url) {
		JobListDTO jobListDTO = null;
		int retries = 1;
		while (jobListDTO == null && retries <= MAXRETRIES) {
			try {
				URL jobUrlAPI = new URL(url + "/" + API_JSON_JOBS);
				jobListDTO = Deserialize.initializeObjectMapper().readValue(Http.get(jobUrlAPI, authenticationString()), JobListDTO.class);
			} catch (IOException e) {
			}
			retries++;
			if (jobListDTO == null) {
				delay(2000);
			}
		}
		return jobListDTO;
	}
	
	public List<Data> resolveJobs(List<Data> validatedData) {
		List<JobDTO> allJobs = getAllJenkinsJobs();
		// Get a list
		for (Data group : validatedData) {
			if (group.getJobs() != null && !group.getJobs().isEmpty()) {
				for (Job jobInGroup : group.getJobs()) {
					// Check if job name exists in list with all jenkins jobs
					for (JobDTO temp : allJobs) {
						if (jobInGroup.getJobName().equalsIgnoreCase(temp.getName())) {
							// Found
							jobInGroup.setUrl(temp.getUrl());
							break;
						}
					}
				}
			}
		}
		return validatedData;
	}
	
	public List<JobDTO> getAllJenkinsJobs() {
		List<JobDTO> returnList = new ArrayList<>();
		// Get a List with all jobs
		JobListDTO original = getJobList();
		for (JobDTO tempInOriginalList : original.getJobs()) {
			if (!Strings.isNullOrEmpty(tempInOriginalList.getClassString()) && tempInOriginalList.getClassString().endsWith(".Folder")) {
				// Get a List with all jobs insider the folder
				JobListDTO listTemp = getJobList(tempInOriginalList.getUrl());
				for (JobDTO temp : listTemp.getJobs()) {
					if (!Strings.isNullOrEmpty(temp.getClassString()) && temp.getClassString().endsWith(".Folder")) {
						// Do nothing , works only for one level of folders
					} else {
						temp.setFolder(tempInOriginalList.getName());
						returnList.add(temp);
					}
				}
			} else {
				// Add to return list
				tempInOriginalList.setFolder("root");
				returnList.add(tempInOriginalList);
			}
		}
		return returnList;
	}
	
	public JobInfo getJobInfo(Job job) {
		JobInfo jobInfo = null;
		int retries = 1;
		while (jobInfo == null && retries <= MAXRETRIES) {
			try {
				URL jobUrlAPI = null;
				if (Strings.isNullOrEmpty(job.getUrl())) {
					jobUrlAPI = new URL(jenkinsUrl + "/" + JOB + "/" + Helper.encodeValue(job.getJobName()) + "/" + API_JSON_URL);
				} else {
					jobUrlAPI = new URL(job.getUrl() + "/" + API_JSON_URL);
				}
				jobInfo = Deserialize.initializeObjectMapper().readValue(Http.get(jobUrlAPI, authenticationString()), JobInfo.class);
			} catch (IOException e) {
			}
			if (jobInfo == null) {
				delay(2000);
			}
			retries++;
		}
		return jobInfo;
	}
	
	public BuildInfo getJobInfoLastBuild(Job dataJobDTO) {
		return getJobInfoWithUrl(dataJobDTO.getJobInfo().getUrl() + "/" + LASTBUILD + "/" + API_JSON_JACOCO);
	}
	
	public BuildInfo getJobInfo(String url) {
		return getJobInfoWithUrl(url + "/" + API_JSON_JACOCO);
	}
	
	private BuildInfo getJobInfoWithUrl(String url) {
		BuildInfo buildInfo = null;
		int retries = 1;
		while (buildInfo == null && retries <= MAXRETRIES) {
			try {
				URL jobUrlAPILastBuild = new URL(url);
				buildInfo = Deserialize.initializeObjectMapper().readValue(Http.get(jobUrlAPILastBuild, authenticationString()), BuildInfo.class);
			} catch (IOException e) {
			}
			retries++;
			if (buildInfo == null) {
				delay(2000);
			}
		}
		return buildInfo;
	}
	
	public CoberturaCoverage getCobertura(String url) {
		try {
			URL jobUrlAPILastBuild = new URL(url + "/" + API_JSON_COBERTURA);
			// Get Latest
			String reply = Http.get(jobUrlAPILastBuild, authenticationString());
			return Deserialize.initializeObjectMapper().readValue(reply, CoberturaCoverage.class);
		} catch (IOException e) {
		}
		return null;
	}
	
	public Results calculateResults(Job job, boolean compareWithPreviousRun) {
		if (job != null && job.getBuildInfo() != null) {
			Results results = new Results();
			// Set Urls
			results.setUrl(job.getJobInfo().getUrl().toString());
			// Set Building status
			results.setBuilding(job.getBuildInfo().getBuilding());
			// Set Current Result
			results.setCurrentResult(job.getBuildInfo().getResult());
			// Set Description
			results.setDescription(job.getBuildInfo().getDescription());
			// Set Duration
			results.setDuration(job.getBuildInfo().getDuration());
			// Set Number
			results.setNumber(job.getBuildInfo().getNumber());
			// Set Duration
			results.setDuration(job.getBuildInfo().getDuration());
			// Set TimeStamp
			if (job.getBuildInfo().getTimestamp() != null) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss:SSS");
				String dateFormatted = formatter.format(new Date(job.getBuildInfo().getTimestamp()));
				results.setTimestamp(dateFormatted);
			}
			boolean foundJacocoResults = false;
			boolean foundCoberturaResults = false;
			if (results.isBuilding()) {
				results.setCurrentResult(JobStatus.RUNNING.name());
			} else {
				if (results.getCurrentResult() != null) {
					for (HashMap<Object, Object> temp : job.getBuildInfo().getActions()) {
						// Calculate FAIL,SKIP and TOTAL Test Results
						if (temp.containsKey(FAILCOUNT)) {
							results.setFail((Integer) temp.get(FAILCOUNT));
						}
						if (temp.containsKey(SKIPCOUNT)) {
							results.setSkip((Integer) temp.get(SKIPCOUNT));
						}
						if (temp.containsKey(TOTALCOUNT)) {
							results.setTotal((Integer) temp.get(TOTALCOUNT));
						}
						// Jacoco Coverage data exists on Default api url
						if (temp.containsKey(JACOCO_BRANCH)) {
							Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_BRANCH);
							results.setCcConditions((Integer) tempMap.get("percentage"));
							foundJacocoResults = true;
						}
						if (temp.containsKey(JACOCO_CLASS)) {
							Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_CLASS);
							results.setCcClasses((Integer) tempMap.get("percentage"));
							foundJacocoResults = true;
						}
						if (temp.containsKey(JACOCO_LINES)) {
							Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_LINES);
							results.setCcLines((Integer) tempMap.get("percentage"));
							foundJacocoResults = true;
						}
						if (temp.containsKey(JACOCO_METHODS)) {
							Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_METHODS);
							results.setCcMethods((Integer) tempMap.get("percentage"));
							foundJacocoResults = true;
						}
						if (temp.containsKey(SONAR_URL)) {
							results.setSonarUrl((String) temp.get(SONAR_URL));
						}
					}
					if (!foundJacocoResults) {
						// Check for Cobertura Results
						foundCoberturaResults = coberturaCoverage(job, results);
					}
					// Calculate Pass Results
					results.setPass(results.getTotal() - Math.abs(results.getFail()) - Math.abs(results.getSkip()));
					// Calculate Percentage
					results.setPercentage(Helper.countPercentage(results));
					// Calculate Previous Results and change sets
					if (compareWithPreviousRun && job.getBuildInfo().getPreviousBuild() != null) {
						BuildInfo jenkinsPreviousBuildDTO = null;
						if (job.getSavedJobUrl() == null) {
							// There is no Saved Job , get Previous
							jenkinsPreviousBuildDTO = getJobInfo(job.getBuildInfo().getPreviousBuild().getUrl().toString());
							if (jenkinsPreviousBuildDTO != null) {
								jenkinsPreviousBuildDTO.setUrl(job.getBuildInfo().getPreviousBuild().getUrl().toString());
								job.setUpdated("");
							}
						} else {
							String currentUrl = job.getJobInfo().getLastBuild().getUrl().toString();
							if (currentUrl.equals(job.getSavedJobUrl())) {
								// No new Run for this Job
								jenkinsPreviousBuildDTO = getJobInfo(job.getBuildInfo().getPreviousBuild().getUrl().toString());
								if (jenkinsPreviousBuildDTO != null) {
									jenkinsPreviousBuildDTO.setUrl(job.getBuildInfo().getPreviousBuild().getUrl().toString());
									job.setUpdated("");
								}
							} else {
								jenkinsPreviousBuildDTO = getJobInfo(job.getSavedJobUrl());
								if (jenkinsPreviousBuildDTO != null) {
									jenkinsPreviousBuildDTO.setUrl(job.getSavedJobUrl());
									job.setUpdated("");
								}
							}
						}
						int previouslyFail = 0;
						int previouslyPass = 0;
						int previouslySkip = 0;
						if (jenkinsPreviousBuildDTO != null) {
							results.setPreviousResult(jenkinsPreviousBuildDTO.getResult());
							for (HashMap<Object, Object> temp : jenkinsPreviousBuildDTO.getActions()) {
								// Calculate FAIL,SKIP and TOTAL of the Previous Test
								if (temp.containsKey(FAILCOUNT)) {
									results.setFailDif((Integer) temp.get(FAILCOUNT));
									previouslyFail += (Integer) temp.get(FAILCOUNT);
								}
								if (temp.containsKey(SKIPCOUNT)) {
									results.setSkipDif((Integer) temp.get(SKIPCOUNT));
									previouslySkip += (Integer) temp.get(SKIPCOUNT);
								}
								if (temp.containsKey(TOTALCOUNT)) {
									results.setTotalDif((Integer) temp.get(TOTALCOUNT));
									previouslyPass += (Integer) temp.get(TOTALCOUNT);
								}
								if (foundJacocoResults) {
									// Jacoco Coverage
									if (temp.containsKey(JACOCO_BRANCH)) {
										Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_BRANCH);
										results.setCcConditionsDif((Integer) tempMap.get("percentage"));
									}
									if (temp.containsKey(JACOCO_CLASS)) {
										Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_CLASS);
										results.setCcClassesDif((Integer) tempMap.get("percentage"));
									}
									if (temp.containsKey(JACOCO_LINES)) {
										Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_LINES);
										results.setCcLinesDif((Integer) tempMap.get("percentage"));
									}
									if (temp.containsKey(JACOCO_METHODS)) {
										Map<String, Object> tempMap = (Map<String, Object>) temp.get(JACOCO_METHODS);
										results.setCcMethodsDif((Integer) tempMap.get("percentage"));
									}
								}
							}
							if (foundCoberturaResults) {
								// Check for Cobertura Results
								coberturaCoverage(jenkinsPreviousBuildDTO.getUrl(), results);
							}
							calculateChangeSets(job, results, jenkinsPreviousBuildDTO);
						}
						// Calculate Pass Difference Results
						results.setPassDif(previouslyPass - Math.abs(previouslyFail) - Math.abs(previouslySkip));
					}
				}
			}
			return results;
		}
		return null;
	}
	
	private void calculateChangeSets(Job job, Results results, BuildInfo jenkinsPreviousBuildDTO) {
		if (job.getBuildInfo().getPreviousBuild().getUrl().toString().equalsIgnoreCase(jenkinsPreviousBuildDTO.getUrl())) {
			// Set Change Set
			if (job.getBuildInfo().getChangeSets() != null) {
				int changes = 0;
				for (ChangeSet tempI : job.getBuildInfo().getChangeSets()) {
					changes += tempI.getItems().size();
				}
				results.setNumberOfChanges(changes);
			} else {
				results.setNumberOfChanges(0);
			}
			// Set Changes URL
			results.setChangesUrl(job.getJobInfo().getUrl() + "/" + job.getBuildInfo().getNumber() + "/" + CHANGES);
		} else {
			// More jobs and possible change sets , resolve them
			int changes = 0;
			for (int i = jenkinsPreviousBuildDTO.getNumber() + 1; i <= job.getBuildInfo().getNumber(); i++) {
				// Get sets
				BuildInfo temp = getJobInfo(job.getJobInfo().getUrl().toString() + i);
				if (temp != null && temp.getChangeSets() != null) {
					for (ChangeSet tempI : temp.getChangeSets()) {
						changes += tempI.getItems().size();
					}
				}
			}
			results.setNumberOfChanges(changes);
			// Set Changes URL
			results.setChangesUrl(job.getJobInfo().getUrl() + "/" + job.getBuildInfo().getNumber() + "/" + CHANGES);
		}
	}
	
	private boolean coberturaCoverage(Job job, Results results) {
		return coberturaCoverage(job.getJobInfo().getUrl().toString() + "/" + job.getBuildInfo().getNumber(), results);
	}
	
	private boolean coberturaCoverage(String url, Results results) {
		// Check for Cobertura Results
		CoberturaCoverage cobertura = getCobertura(url);
		if (cobertura != null) {
			Double packages = 0D;
			Double files = 0D;
			Double classes = 0D;
			Double methods = 0D;
			Double lines = 0D;
			Double conditions = 0D;
			for (Element tempElement : cobertura.getResults().getElements()) {
				if ("Packages".equalsIgnoreCase(tempElement.getName())) {
					packages = tempElement.getRatio();
				}
				if ("Files".equalsIgnoreCase(tempElement.getName())) {
					files = tempElement.getRatio();
				}
				if ("Classes".equalsIgnoreCase(tempElement.getName())) {
					classes = tempElement.getRatio();
				}
				if ("Methods".equalsIgnoreCase(tempElement.getName())) {
					methods = tempElement.getRatio();
				}
				if ("Lines".equalsIgnoreCase(tempElement.getName())) {
					lines = tempElement.getRatio();
				}
				if ("Conditionals".equalsIgnoreCase(tempElement.getName())) {
					conditions = tempElement.getRatio();
				}
			}
			results.setCcPackages(packages.intValue());
			results.setCcFiles(files.intValue());
			results.setCcClasses(classes.intValue());
			results.setCcMethods(methods.intValue());
			results.setCcLines(lines.intValue());
			results.setCcConditions(conditions.intValue());
			return true;
		}
		return true;
	}
	
	public class ReportThread extends Thread {
		
		Job job;
		boolean compareWithPreviousRun;
		
		public ReportThread(Job job, boolean compareWithPreviousRun) {
			this.job = job;
			this.compareWithPreviousRun = compareWithPreviousRun;
		}
		
		@Override
		public void run() {
			// Get Jenkins Job Info
			job.setJobInfo(getJobInfo(job));
			if (job.getJobInfo() == null) {
				// Job Not Found
				job.setJobInfo(new JobInfo());
				job.setResults(new Results(JobStatus.NOT_FOUND.name(), null));
				job.getResults().setUrl(null);
				job.setReport(new ReportJob());
				logger.println(LocalMessages.COLLECT_DATA.toString() + " '" + job.getJobName() + "' " + LocalMessages.JOB_NOT_FOUND.toString());
			} else if (!job.getJobInfo().getBuildable()) {
				// Job is Disabled/ Not Buildable
				String tempUrl = job.getJobInfo().getUrl().toString();
				job.setJobInfo(new JobInfo());
				job.setResults(new Results(JobStatus.DISABLED.name(), null));
				job.getResults().setUrl(tempUrl);
				job.setReport(new ReportJob());
				logger.println(LocalMessages.COLLECT_DATA.toString() + " '" + job.getJobName() + "' " + LocalMessages.JOB_IS_DISABLED.toString());
			} else if (job.getJobInfo() != null) {
				// Job Found and is Buildable
				// Get Job Results
				job.setBuildInfo(getJobInfoLastBuild(job));
				// Get Actual Results
				job.setResults(calculateResults(job, compareWithPreviousRun));
				logger.println(LocalMessages.COLLECT_DATA.toString() + " '" + job.getJobName() + "' " + LocalMessages.FINISHED.toString());
			} else {
				logger.println("...");
			}
		}
	}
	
}
