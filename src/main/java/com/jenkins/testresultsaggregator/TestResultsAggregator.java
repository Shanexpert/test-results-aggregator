package com.jenkins.testresultsaggregator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
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
import net.sf.json.JSONObject;

public class TestResultsAggregator extends Notifier {
	
	private final static String displayName = "Aggregate Test Results";
	private String recipientsList;
	private String beforebody;
	private String afterbody;
	private String theme;
	private String sortresults;
	private String outOfDateResults;
	
	private List<DataDTO> dataJob;
	
	private Properties properties;
	public static final String DISPLAY_NAME = "Job Results Aggregated";
	public static final String GRAPH_NAME_JOBS = "Job Results Trend";
	public static final String GRAPH_NAME_TESTS = "Test Results Trend";
	public static final String URL = "reports";
	public static final String ICON_FILE_NAME = "/plugin/test-results-aggregator/icons/report.png";
	
	public enum AggregatorProperties {
		OUT_OF_DATE_RESULTS_ARG,
		PERCENTAGE_PREFIX,
		TEXT_BEFORE_MAIL_BODY,
		TEXT_AFTER_MAIL_BODY,
		THEME,
		PRINT_GROUP_STATUS_IN_NEW_COLUMN,
		SORT_JOBS_BY
	}
	
	public enum SortResultsBy {
		NAME,
		STATUS,
		TOTAL_TEST,
		PASS,
		FAIL,
		SKIP,
		LAST_RUN,
		COMMITS
	}
	
	public enum Theme {
		dark,
		light
	}
	
	@DataBoundConstructor
	public TestResultsAggregator(final String recipientsList, final String outOfDateResults, final List<DataDTO> dataJob, String beforebody, String afterbody, String theme, String sortresults) {
		this.setRecipientsList(recipientsList);
		this.setOutOfDateResults(outOfDateResults);
		this.setDataJob(dataJob);
		this.setBeforebody(beforebody);
		this.setAfterbody(afterbody);
		this.setTheme(theme);
		this.setSortresults(sortresults);
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
			properties.put(AggregatorProperties.PERCENTAGE_PREFIX.name(), "");
			properties.put(AggregatorProperties.THEME.name(), getTheme());
			properties.put(AggregatorProperties.TEXT_BEFORE_MAIL_BODY.name(), getBeforebody());
			properties.put(AggregatorProperties.TEXT_AFTER_MAIL_BODY.name(), getAfterbody());
			properties.put(AggregatorProperties.PRINT_GROUP_STATUS_IN_NEW_COLUMN.name(), false);
			properties.put(AggregatorProperties.SORT_JOBS_BY.name(), getSortresults());
			// Validate Input Data
			List<DataDTO> validatedData = validateInputData(getDataJob());
			// Collect Data
			Collector collector = new Collector(logger, desc.getUsername(), desc.getPassword(), desc.getJenkinsUrl());
			collector.collectResults(validatedData);
			// Analyze Results
			AggregatedDTO aggregated = new Analyzer(logger).analyze(validatedData, properties);
			// Reporter for HTML and mail
			Reporter reporter = new Reporter(logger, build.getProject().getSomeWorkspace(), build.getRootDir(), desc.getMailNotificationFrom());
			reporter.publishResuts(getRecipientsList(), aggregated, properties);
			// Add Build Action
			build.addAction(new TestResultsAggregatorTestResultBuildAction(aggregated));
		} catch (Exception e) {
			logger.printf(LocalMessages.ERROR_OCCURRED.toString() + " : %s ", e);
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
	
	private List<DataDTO> validateInputData(List<DataDTO> data) {
		List<DataDTO> validateData = new ArrayList<DataDTO>();
		for (DataDTO tempDataDTO : data) {
			if (tempDataDTO.getJobs() != null && !tempDataDTO.getJobs().isEmpty()) {
				boolean allJobsareEmpty = true;
				List<DataJobDTO> validateDataJobs = new ArrayList<>();
				for (DataJobDTO temp : tempDataDTO.getJobs()) {
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
	
	public List<DataDTO> getDataJob() {
		return dataJob;
	}
	
	public void setDataJob(List<DataDTO> dataJob) {
		this.dataJob = dataJob;
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
	
}
