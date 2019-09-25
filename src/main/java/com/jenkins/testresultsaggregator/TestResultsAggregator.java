package com.jenkins.testresultsaggregator;

import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.helper.Collector;
import com.jenkins.testresultsaggregator.helper.Reporter;
import com.jenkins.testresultsaggregator.helper.Validate;

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
import net.sf.json.JSONObject;

public class TestResultsAggregator extends Notifier {
	
	private final static String displayName = "Aggregate Test Results";
	private String recipientsList;
	private String outOfDateResults;
	
	private List<DataDTO> dataJob;
	
	@DataBoundConstructor
	public TestResultsAggregator(final String recipientsList, final String outOfDateResults, final List<DataDTO> dataJob) {
		this.setRecipientsList(recipientsList);
		this.setOutOfDateResults(outOfDateResults);
		this.setDataJob(dataJob);
	}
	
	@Override
	public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
		try {
			listener.getLogger().println("Starting Aggregate Test Results Action");
			new Collector(listener, getDescriptor().getUsername(), getDescriptor().getPassword(), getDescriptor().getJenkinsUrl()).collectResults(getDataJob());
			new Reporter(listener, build.getProject().getSomeWorkspace(), getDescriptor().getMailhost(), getDescriptor().getMailNotificationFrom()).publishResuts(getRecipientsList(), getOutOfDateResults(), getDataJob());
		} catch (Exception e) {
			listener.getLogger().printf("Error Occurred : %s ", e);
		}
		listener.getLogger().println("Finished Aggregate Test Results Action");
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
		private String password;
		private String mailhost;
		private String mailNotificationFrom;
		
		public String getUsername() {
			return username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public String getMailhost() {
			return mailhost;
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
			password = jsonObject.getString("password");
			mailhost = jsonObject.getString("mailhost");
			jenkinsUrl = jsonObject.getString("jenkinsUrl");
			mailNotificationFrom = jsonObject.getString("mailNotificationFrom");
			save();
			return super.configure(req, jsonObject);
		}
		
		public FormValidation doCheckOutOfDateResults(@QueryParameter String outOfDateResults) {
			if (!Strings.isNullOrEmpty(outOfDateResults)) {
				try {
					int hours = Integer.parseInt(outOfDateResults);
					if (hours < 0) {
						return FormValidation.error("Please enter a positive integer number.");
					} else {
						return FormValidation.ok();
					}
				} catch (NumberFormatException e) {
					return FormValidation.error("Please enter an integer number.");
				}
			} else {
				// No OutOfDate
				return FormValidation.ok();
			}
		}
		
		public FormValidation doTestApiConnection(@QueryParameter String jenkinsUrl, @QueryParameter String username, @QueryParameter String password) {
			try {
				new Collector(null, username, password, jenkinsUrl).getAPIConnection();
				return FormValidation.ok("Success");
			} catch (Exception e) {
				return FormValidation.error("Client error : " + e.getMessage());
			}
		}
		
		public FormValidation doTestSMTPConnection(@QueryParameter String mailhost) {
			try {
				Validate.confirmSMTP(mailhost, -1, null, null, false, "TLS");
				return FormValidation.ok("Success");
			} catch (Exception e) {
				return FormValidation.error("Client error : " + e.getMessage());
			}
		}
		
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
	
}
