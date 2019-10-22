package com.jenkins.testresultsaggregator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.Aggregated;
import com.jenkins.testresultsaggregator.data.Data;
import com.jenkins.testresultsaggregator.data.Job;
import com.jenkins.testresultsaggregator.helper.Analyzer;
import com.jenkins.testresultsaggregator.helper.Collector;
import com.jenkins.testresultsaggregator.helper.LocalMessages;
import com.jenkins.testresultsaggregator.reporter.Reporter;

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
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

public class TestResultsAggregator extends Notifier {
	
	private final static String displayName = "Aggregate Test Results";
	private String subject;
	private String recipientsList;
	private String beforebody;
	private String afterbody;
	private String theme;
	private String sortresults;
	private String outOfDateResults;
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
		SUBJECT_PREFIX
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
		PERCENTAGE
	}
	
	public enum Theme {
		dark,
		light
	}
	
	@DataBoundConstructor
	public TestResultsAggregator(final String subject, final String recipientsList, final String outOfDateResults, final List<Data> data, String beforebody, String afterbody, String theme, String sortresults,
			String selectedColumns) {
		this.setRecipientsList(recipientsList);
		this.setOutOfDateResults(outOfDateResults);
		this.setData(data);
		this.setBeforebody(beforebody);
		this.setAfterbody(afterbody);
		this.setTheme(theme);
		this.setSortresults(sortresults);
		this.setSubject(subject);
		this.setSelectedColumns(selectedColumns);
	}
	
	@Override
	public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
		PrintStream logger = listener.getLogger();
		Descriptor desc = getDescriptor();
		try {
			logger.println(LocalMessages.START_AGGREGATE.toString());
			// Set up Properties
			properties = new Properties();
			properties.put(AggregatorProperties.OUT_OF_DATE_RESULTS_ARG.name(), outOfDateResults);
			properties.put(AggregatorProperties.TEST_PERCENTAGE_PREFIX.name(), "");
			properties.put(AggregatorProperties.THEME.name(), getTheme());
			properties.put(AggregatorProperties.TEXT_BEFORE_MAIL_BODY.name(), getBeforebody());
			properties.put(AggregatorProperties.TEXT_AFTER_MAIL_BODY.name(), getAfterbody());
			properties.put(AggregatorProperties.SORT_JOBS_BY.name(), getSortresults());
			properties.put(AggregatorProperties.SUBJECT_PREFIX.name(), getSubject());
			columns = calculateColumns(selectedColumns);
			// Validate Input Data
			List<Data> validatedData = validateInputData(getData());
			// Collect Data
			Collector collector = new Collector(logger, desc.getUsername(), desc.getPassword(), desc.getJenkinsUrl());
			collector.collectResults(validatedData);
			// Analyze Results
			Aggregated aggregated = new Analyzer(logger).analyze(validatedData, properties);
			// Reporter for HTML and mail
			Reporter reporter = new Reporter(logger, build.getProject().getSomeWorkspace(), build.getRootDir(), desc.getMailNotificationFrom());
			reporter.publishResuts(getRecipientsList(), aggregated, properties, columns);
			// Add Build Action
			build.addAction(new TestResultsAggregatorTestResultBuildAction(aggregated));
		} catch (Exception e) {
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + " : %s ", e);
		}
		logger.println(LocalMessages.FINISHED_AGGREGATE.toString());
		return true;
	}
	
	private List<LocalMessages> calculateColumns(String selectedColumns) {
		List<LocalMessages> columns = new ArrayList<>(Arrays.asList(LocalMessages.COLUMN_GROUP, LocalMessages.COLUMN_JOB));
		if (!Strings.isNullOrEmpty(selectedColumns)) {
			String[] splitter = selectedColumns.split(",");
			for (String temp : splitter) {
				if (temp != null) {
					temp = temp.trim();
					if (temp.equalsIgnoreCase("Status")) {
						columns.add(LocalMessages.COLUMN_JOB_STATUS);
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
					}
				}
			}
		}
		
		return columns;
	}
	
	@Override
	public Descriptor getDescriptor() {
		return (Descriptor) super.getDescriptor();
	}
	
	@Extension
	public static class Descriptor extends BuildStepDescriptor<Publisher> {
		
		/**
		 * Global configuration information variables. If you don't want fields to be persisted, use <tt>transient</tt>.
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
			if (Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
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
			} else {
				return FormValidation.error(LocalMessages.ERROR_OCCURRED.toString() + " no permissions");
			}
		}
		
		public FormValidation doTestApiConnection(@QueryParameter final String jenkinsUrl, @QueryParameter final String username, @QueryParameter final Secret password) {
			if (Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
				try {
					new Collector(null, username, password, jenkinsUrl).getAPIConnection();
					return FormValidation.ok(LocalMessages.SUCCESS.toString());
				} catch (Exception e) {
					return FormValidation.error(LocalMessages.ERROR_OCCURRED.toString() + ": " + e.getMessage());
				}
			} else {
				return FormValidation.error(LocalMessages.ERROR_OCCURRED.toString() + " no permissions");
			}
		}
	}
	
	private List<Data> validateInputData(List<Data> data) {
		List<Data> validateData = new ArrayList<Data>();
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
		return validateData;
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
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
	
	public void setData(List<Data> data) {
		this.data = data;
	}
	
	public void setRecipientsList(String recipientsList) {
		this.recipientsList = recipientsList;
	}
	
	public void setOutOfDateResults(String outOfDateResults) {
		this.outOfDateResults = outOfDateResults;
	}
	
	public String getBeforebody() {
		return beforebody;
	}
	
	public void setBeforebody(String beforebody) {
		this.beforebody = beforebody;
	}
	
	public String getAfterbody() {
		return afterbody;
	}
	
	public void setAfterbody(String afterbody) {
		this.afterbody = afterbody;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public String getSortresults() {
		return sortresults;
	}
	
	public void setSortresults(String sortresults) {
		this.sortresults = sortresults;
	}
	
	public String getSubject() {
		if (subject == null) {
			subject = "";
		}
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public List<LocalMessages> getColumns() {
		return columns;
	}
	
	public void setColumns(List<LocalMessages> columns) {
		this.columns = columns;
	}
	
	public String getSelectedColumns() {
		return selectedColumns;
	}
	
	public void setSelectedColumns(String selectedColumns) {
		this.selectedColumns = selectedColumns;
	}
	
}
