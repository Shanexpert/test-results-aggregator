package com.jenkins.testresultsaggregator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.Aggregated;
import com.jenkins.testresultsaggregator.data.Data;
import com.jenkins.testresultsaggregator.data.Job;
import com.jenkins.testresultsaggregator.helper.Analyzer;
import com.jenkins.testresultsaggregator.helper.Collector;
import com.jenkins.testresultsaggregator.helper.Helper;
import com.jenkins.testresultsaggregator.helper.LocalMessages;
import com.jenkins.testresultsaggregator.helper.TestResultHistoryUtil;
import com.jenkins.testresultsaggregator.reporter.Reporter;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.Secret;
import hudson.util.VariableResolver;
import net.sf.json.JSONObject;

public class TestResultsAggregator extends Notifier {
	
	private static final String displayName = "Aggregate Test Results";
	
	private String subject;
	private String recipientsList;
	private String beforebody;
	private String afterbody;
	private String theme;
	private String sortresults;
	private String outOfDateResults;
	private Boolean compareWithPreviousRun;
	private Boolean ignoreNotFoundJobs;
	private Boolean ignoreDisabledJobs;
	private Boolean ignoreAbortedJobs;
	private String selectedColumns;
	private List<LocalMessages> columns;
	private List<Data> data;
	
	private Properties properties;
	public static final String DISPLAY_NAME = "Job Results Aggregated";
	public static final String GRAPH_NAME_JOBS = "Job Results Trend";
	public static final String GRAPH_NAME_TESTS = "Test Results Trend";
	public static final String URL = "reports";
	public static final String ICON_FILE_NAME = "/plugin/test-results-aggregator/icons/report.png";
	
	public enum AggregatorProperties {
		OUT_OF_DATE_RESULTS_ARG,
		TEST_PERCENTAGE_PREFIX,
		TEXT_BEFORE_MAIL_BODY,
		TEXT_AFTER_MAIL_BODY,
		THEME,
		SORT_JOBS_BY,
		SUBJECT_PREFIX,
		RECIPIENTS_LIST
	}
	
	public enum SortResultsBy {
		NAME,
		STATUS,
		TOTAL_TEST,
		PASS,
		FAIL,
		SKIP,
		LAST_RUN,
		COMMITS,
		DURATION,
		PERCENTAGE,
		CC_PACKAGES,
		CC_FILES,
		CC_CLASSES,
		CC_METHODS,
		CC_LINES,
		CC_CONDITIONS,
		SONAR_URL,
		BUILD_NUMBER
	}
	
	public enum Theme {
		dark,
		light
	}
	
	@DataBoundConstructor
	public TestResultsAggregator(final String subject, final String recipientsList, final String outOfDateResults, final List<Data> data, String beforebody, String afterbody, String theme, String sortresults,
			String selectedColumns, Boolean compareWithPreviousRun, Boolean ignoreNotFoundJobs, Boolean ignoreDisabledJobs, Boolean ignoreAbortedJobs) {
		this.setRecipientsList(recipientsList);
		this.setOutOfDateResults(outOfDateResults);
		this.setData(data);
		this.setBeforebody(beforebody);
		this.setAfterbody(afterbody);
		this.setTheme(theme);
		this.setSortresults(sortresults);
		this.setSubject(subject);
		this.setSelectedColumns(selectedColumns);
		this.setCompareWithPreviousRun(compareWithPreviousRun);
		this.setIgnoreDisabledJobs(ignoreDisabledJobs);
		this.setIgnoreNotFoundJobs(ignoreNotFoundJobs);
		this.setIgnoreAbortedJobs(ignoreAbortedJobs);
	}
	
	@Override
	public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
		PrintStream logger = listener.getLogger();
		Descriptor desc = getDescriptor();
		try {
			logger.println(LocalMessages.START_AGGREGATE.toString());
			// Set up Properties
			properties = new Properties();
			properties.put(AggregatorProperties.OUT_OF_DATE_RESULTS_ARG.name(), getOutOfDateResults());
			properties.put(AggregatorProperties.TEST_PERCENTAGE_PREFIX.name(), "");
			properties.put(AggregatorProperties.THEME.name(), getTheme());
			properties.put(AggregatorProperties.TEXT_BEFORE_MAIL_BODY.name(), getBeforebody());
			properties.put(AggregatorProperties.TEXT_AFTER_MAIL_BODY.name(), getAfterbody());
			properties.put(AggregatorProperties.SORT_JOBS_BY.name(), getSortresults());
			properties.put(AggregatorProperties.SUBJECT_PREFIX.name(), getSubject());
			properties.put(AggregatorProperties.RECIPIENTS_LIST.name(), getRecipientsList());
			// Resolve Variables
			resolveVariables(properties, build, listener);
			// Resolve Columns
			columns = calculateColumns(getSelectedColumns());
			// Validate Input Data
			List<Data> validatedData = validateInputData(getData(), desc.getJenkinsUrl());
			if (compareWithPrevious()) {
				// Get Previous Saved Results
				Aggregated previousSavedAggregatedResults = TestResultHistoryUtil.getTestResults(build.getPreviousSuccessfulBuild());
				// Check previous Data
				previousSavedResults(validatedData, previousSavedAggregatedResults);
			}
			// Collect Data
			Collector collector = new Collector(logger, desc.getUsername(), desc.getPassword(), desc.getJenkinsUrl());
			collector.collectResults(validatedData, compareWithPrevious());
			// Analyze Results
			Aggregated aggregated = new Analyzer(logger).analyze(validatedData, properties);
			// Reporter for HTML and mail
			Reporter reporter = new Reporter(logger, build.getProject().getSomeWorkspace(), build.getRootDir(), desc.getMailNotificationFrom(), ignoreDisabledJobs, ignoreNotFoundJobs, ignoreAbortedJobs);
			reporter.publishResuts(aggregated, properties, getColumns(), build.getRootDir());
			// Add Build Action
			build.addAction(new TestResultsAggregatorTestResultBuildAction(aggregated));
		} catch (Exception e) {
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + " : %s ", e);
			e.printStackTrace();
		}
		logger.println(LocalMessages.FINISHED_AGGREGATE.toString());
		return true;
	}
	
	@Override
	public Descriptor getDescriptor() {
		return (Descriptor) super.getDescriptor();
	}
	
	@Extension
	public static class Descriptor extends BuildStepDescriptor<Publisher> {
		/**
		 * Global configuration information variables.
		 */
		private String jenkinsUrl;
		private String username;
		private Secret password;
		private String mailNotificationFrom;
		
		public String getUsername() {
			return username;
		}
		
		public Secret getPassword() {
			return password;
		}
		
		public String getJenkinsUrl() {
			return jenkinsUrl;
		}
		
		public String getMailNotificationFrom() {
			return mailNotificationFrom;
		}
		
		public String defaultMailNotificationFrom() {
			return "Jenkins";
		}
		
		/**
		 * In order to load the persisted global configuration, you have to call load() in the constructor.
		 */
		public Descriptor() {
			load();
		}
		
		@Override
		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType) {
			// Indicates that this builder can be used with all kinds of project types.
			return jobType == FreeStyleProject.class;
		}
		
		@Override
		public String getDisplayName() {
			return displayName;
		}
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject jsonObject) throws FormException {
			username = jsonObject.getString("username");
			password = Secret.fromString((String) jsonObject.get("password"));
			jenkinsUrl = jsonObject.getString("jenkinsUrl");
			mailNotificationFrom = jsonObject.getString("mailNotificationFrom");
			save();
			return super.configure(req, jsonObject);
		}
		
		public FormValidation doCheckOutOfDateResults(@QueryParameter final String outOfDateResults) {
			if (!Strings.isNullOrEmpty(outOfDateResults)) {
				try {
					int hours = Integer.parseInt(outOfDateResults);
					if (hours < 0) {
						return FormValidation.error(LocalMessages.VALIDATION_POSITIVE_NUMBER.toString());
					} else {
						return FormValidation.ok();
					}
				} catch (NumberFormatException e) {
					return FormValidation.error(LocalMessages.VALIDATION_INTEGER_NUMBER.toString());
				}
			} else {
				// No OutOfDate
				return FormValidation.ok();
			}
		}
		
		public FormValidation doTestApiConnection(@QueryParameter final String jenkinsUrl, @QueryParameter final String username, @QueryParameter final Secret password) {
			try {
				new Collector(null, username, password, jenkinsUrl).getAPIConnection();
				return FormValidation.ok(LocalMessages.SUCCESS.toString());
			} catch (Exception e) {
				return FormValidation.error(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
			}
			
		}
	}
	
	private void resolveVariables(Properties properties, AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
		// Variables
		VariableResolver<?> buildVars = build.getBuildVariableResolver();
		EnvVars envVars = build.getEnvironment(listener);
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			String originalValue = entry.getValue().toString();
			if (!Strings.isNullOrEmpty(originalValue)) {
				while (originalValue.contains("${")) {
					String tempValue = null;
					if (originalValue.contains("${")) {
						tempValue = originalValue.substring(originalValue.indexOf("${") + 2, originalValue.indexOf('}'));
					}
					// Resolve from building variables
					Object buildVariable = buildVars.resolve(tempValue);
					// If null try resolve it from env variables
					if (buildVariable == null) {
						buildVariable = envVars.get(tempValue);
					}
					if (buildVariable != null) {
						originalValue = originalValue.replaceAll("\\$\\{" + tempValue + "}", buildVariable.toString());
					} else {
						originalValue = originalValue.replaceAll("\\$\\{" + tempValue + "}", "\\$[" + tempValue + "]");
					}
				}
				entry.setValue(originalValue);
			}
		}
	}
	
	private void previousSavedResults(List<Data> validatedData, Aggregated previousAggregated) {
		if (previousAggregated != null && previousAggregated.getData() != null) {
			for (Data data : validatedData) {
				for (Job job : data.getJobs()) {
					for (Data pdata : previousAggregated.getData()) {
						for (Job pjob : pdata.getJobs()) {
							if (job.getJobName().equals(pjob.getJobName())) {
								if (pjob.getJobInfo().getUrl() != null) {
									job.setSavedJobUrl(pjob.getJobInfo().getUrl().toString());
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private List<LocalMessages> calculateColumns(String selectedColumns) {
		List<LocalMessages> columns = new ArrayList<>(Arrays.asList(LocalMessages.COLUMN_GROUP));
		if (!Strings.isNullOrEmpty(selectedColumns)) {
			String[] splitter = selectedColumns.split(",");
			for (String temp : splitter) {
				if (temp != null) {
					temp = temp.trim();
					if (temp.equalsIgnoreCase("Status")) {
						columns.add(LocalMessages.COLUMN_JOB_STATUS);
					} else if (temp.equalsIgnoreCase("Job")) {
						columns.add(LocalMessages.COLUMN_JOB);
					} else if (temp.equalsIgnoreCase("Percentage")) {
						columns.add(LocalMessages.COLUMN_PERCENTAGE);
					} else if (temp.equalsIgnoreCase("Total")) {
						columns.add(LocalMessages.COLUMN_TESTS);
					} else if (temp.equalsIgnoreCase("Pass")) {
						columns.add(LocalMessages.COLUMN_PASS);
					} else if (temp.equalsIgnoreCase("Fail")) {
						columns.add(LocalMessages.COLUMN_FAIL);
					} else if (temp.equalsIgnoreCase("Skip")) {
						columns.add(LocalMessages.COLUMN_SKIP);
					} else if (temp.equalsIgnoreCase("Commits")) {
						columns.add(LocalMessages.COLUMN_COMMITS);
					} else if (temp.equalsIgnoreCase("LastRun")) {
						columns.add(LocalMessages.COLUMN_LAST_RUN);
					} else if (temp.equalsIgnoreCase("Duration")) {
						columns.add(LocalMessages.COLUMN_DURATION);
					} else if (temp.equalsIgnoreCase("Description")) {
						columns.add(LocalMessages.COLUMN_DESCRIPTION);
					} else if (temp.equalsIgnoreCase("Health")) {
						columns.add(LocalMessages.COLUMN_HEALTH);
					} else if (temp.equalsIgnoreCase("Packages")) {
						columns.add(LocalMessages.COLUMN_CC_PACKAGES);
					} else if (temp.equalsIgnoreCase("Files")) {
						columns.add(LocalMessages.COLUMN_CC_FILES);
					} else if (temp.equalsIgnoreCase("Classes")) {
						columns.add(LocalMessages.COLUMN_CC_CLASSES);
					} else if (temp.equalsIgnoreCase("Methods")) {
						columns.add(LocalMessages.COLUMN_CC_METHODS);
					} else if (temp.equalsIgnoreCase("Lines")) {
						columns.add(LocalMessages.COLUMN_CC_LINES);
					} else if (temp.equalsIgnoreCase("Conditions")) {
						columns.add(LocalMessages.COLUMN_CC_CONDITIONS);
					} else if (temp.equalsIgnoreCase("Sonar")) {
						columns.add(LocalMessages.COLUMN_SONAR_URL);
					} else if (temp.equalsIgnoreCase("Build")) {
						columns.add(LocalMessages.COLUMN_BUILD_NUMBER);
					}
				}
			}
		}
		return columns;
	}
	
	private List<Data> validateInputData(List<Data> data, String jenkinsUrl) throws UnsupportedEncodingException, MalformedURLException {
		List<Data> validateData = new ArrayList<>();
		for (Data tempDataDTO : data) {
			if (tempDataDTO.getJobs() != null && !tempDataDTO.getJobs().isEmpty()) {
				boolean allJobsareEmpty = true;
				List<Job> validateDataJobs = new ArrayList<>();
				for (Job temp : tempDataDTO.getJobs()) {
					if (!Strings.isNullOrEmpty(temp.getJobName())) {
						allJobsareEmpty = false;
						validateDataJobs.add(temp);
					}
				}
				if (!allJobsareEmpty) {
					tempDataDTO.setJobs(validateDataJobs);
					validateData.add(tempDataDTO);
				}
			}
		}
		return evaluateInputData(validateData, jenkinsUrl);
	}
	
	private List<Data> evaluateInputData(List<Data> data, String jenkinsUrl) throws UnsupportedEncodingException, MalformedURLException {
		for (Data jobs : data) {
			for (Job job : jobs.getJobs()) {
				if (job.getJobName().contains("/")) {
					String[] spliter = job.getJobName().split("/");
					if (spliter[spliter.length - 1].equals("*")) {
						// Do nothing for now
					} else {
						StringBuilder folders = new StringBuilder();
						for (int i = 0; i < spliter.length - 1; i++) {
							folders.append(spliter[i] + "/");
						}
						job.setFolder(folders.toString().replaceAll("/", "/" + Collector.JOB + "/"));
						job.setUrl(jenkinsUrl + "/" + Collector.JOB + "/" + Helper.encodeValue(job.getFolder()).replace("%2F", "/")
								+ Helper.encodeValue(spliter[spliter.length - 1]));
					}
				} else {
					job.setFolder("root");
					if (Strings.isNullOrEmpty(job.getUrl())) {
						job.setUrl(jenkinsUrl + "/" + Collector.JOB + "/" + Helper.encodeValue(job.getJobName()));
					}
				}
			}
		}
		return data;
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
	@DataBoundSetter
	public void setData(@CheckForNull List<Data> data) {
		this.data = data;
	}
	
	@DataBoundSetter
	public void setRecipientsList(@CheckForNull String recipientsList) {
		this.recipientsList = recipientsList;
	}
	
	@DataBoundSetter
	public void setOutOfDateResults(@CheckForNull String outOfDateResults) {
		this.outOfDateResults = outOfDateResults;
	}
	
	@DataBoundSetter
	public void setBeforebody(@CheckForNull String beforebody) {
		this.beforebody = beforebody;
	}
	
	@DataBoundSetter
	public void setAfterbody(@CheckForNull String afterbody) {
		this.afterbody = afterbody;
	}
	
	@DataBoundSetter
	public void setTheme(@CheckForNull String theme) {
		this.theme = theme;
	}
	
	@DataBoundSetter
	public void setSortresults(@CheckForNull String sortresults) {
		this.sortresults = sortresults;
	}
	
	@DataBoundSetter
	public void setSubject(@CheckForNull String subject) {
		this.subject = subject;
	}
	
	@DataBoundSetter
	public void setColumns(@CheckForNull List<LocalMessages> columns) {
		this.columns = columns;
	}
	
	@DataBoundSetter
	public void setSelectedColumns(@CheckForNull String selectedColumns) {
		this.selectedColumns = selectedColumns;
	}
	
	@DataBoundSetter
	public void setCompareWithPreviousRun(Boolean compareWithPreviousRun) {
		this.compareWithPreviousRun = compareWithPreviousRun;
	}
	
	@DataBoundSetter
	public void setIgnoreNotFoundJobs(Boolean ignoreNotFoundJobs) {
		this.ignoreNotFoundJobs = ignoreNotFoundJobs;
	}
	
	@DataBoundSetter
	public void setIgnoreDisabledJobs(Boolean ignoreDisabledJobs) {
		this.ignoreDisabledJobs = ignoreDisabledJobs;
	}
	
	@DataBoundSetter
	public void setIgnoreAbortedJobs(Boolean ignoreAbortedJobs) {
		this.ignoreAbortedJobs = ignoreAbortedJobs;
	}
	
	public String getRecipientsList() {
		return recipientsList;
	}
	
	public String getOutOfDateResults() {
		return outOfDateResults;
	}
	
	public List<Data> getData() {
		return data;
	}
	
	public List<LocalMessages> getColumns() {
		return columns;
	}
	
	public String getSelectedColumns() {
		return selectedColumns;
	}
	
	public String getSubject() {
		if (subject == null) {
			subject = "";
		}
		return subject;
	}
	
	public String getSortresults() {
		return sortresults;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public String getBeforebody() {
		return beforebody;
	}
	
	public String getAfterbody() {
		return afterbody;
	}
	
	public Boolean isCompareWithPreviousRun() {
		return compareWithPreviousRun;
	}
	
	public boolean compareWithPrevious() {
		if (compareWithPreviousRun == null) {
			compareWithPreviousRun = true;
		}
		return compareWithPreviousRun.booleanValue();
	}
	
	public Boolean isIgnoreNotFoundJobs() {
		return ignoreNotFoundJobs;
	}
	
	public Boolean isIgnoreDisabledJobs() {
		return ignoreDisabledJobs;
	}
	
	public boolean ignoreNotFoundJobs() {
		if (ignoreNotFoundJobs == null) {
			ignoreNotFoundJobs = false;
		}
		return ignoreNotFoundJobs.booleanValue();
	}
	
	public boolean ignoreDisabledJobs() {
		if (ignoreDisabledJobs == null) {
			ignoreDisabledJobs = false;
		}
		return ignoreDisabledJobs.booleanValue();
	}
	
	public boolean ignoreAbortedJobs() {
		if (ignoreAbortedJobs == null) {
			ignoreAbortedJobs = false;
		}
		return ignoreAbortedJobs.booleanValue();
	}
}
