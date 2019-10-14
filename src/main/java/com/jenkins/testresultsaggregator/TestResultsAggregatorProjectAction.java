package com.jenkins.testresultsaggregator;

import java.io.IOException;
import java.util.Calendar;
import java.util.SortedMap;

import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.jenkins.testresultsaggregator.helper.GraphHelper;

import hudson.Functions;
import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.test.TestResultProjectAction;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import jenkins.model.lazy.LazyBuildMixIn;
import jenkins.model.lazy.LazyBuildMixIn.LazyLoadingJob;

/**
 * Action to associate the TestNG reports with the project
 *
 * @author nullin
 */
public class TestResultsAggregatorProjectAction extends TestResultProjectAction implements ProminentProjectAction {
	
	// For Jobs
	public static final String SUCCESS = "Success";
	public static final String FAILED = "Failed";
	public static final String UNSTABLE = "Unstable";
	public static final String ABORTED = "Aborted";
	public static final String RUNNING = "Running";
	public static final String TOTAL = "Total";
	public static final String TOTAL_P = "Total_P";
	public static final String SUCCESS_P = "Success_P";
	public static final String ABORTED_P = "Aborted_P";
	public static final String FAILED_P = "Failed_P";
	
	// For Tests
	public static final String TOTAL_TEST = "Total_TEST";
	public static final String TOTAL_P_TEST = "Total_P_TEST";
	public static final String SUCCESS_TEST = "Success_TEST";
	public static final String SUCCESS_P_TEST = "Success_P_TEST";
	public static final String ABORTED_TEST = "Aborted_TEST";
	public static final String ABORTED_P_TEST = "Aborted_P_TEST";
	public static final String FAILED_TEST = "Failed_TEST";
	public static final String FAILED_P_TEST = "Failed_P_TEST";
	
	public TestResultsAggregatorProjectAction(Job<?, ?> project) {
		super(project);
	}
	
	protected Class<TestResultsAggregatorTestResultBuildAction> getBuildActionClass() {
		return TestResultsAggregatorTestResultBuildAction.class;
	}
	
	public Job<?, ?> getProject() {
		return super.job;
	}
	
	public String getIconFileName() {
		return TestResultsAggregator.ICON_FILE_NAME;
	}
	
	public String getDisplayName() {
		return TestResultsAggregator.DISPLAY_NAME;
	}
	
	public String getGraphNameJobs() {
		return TestResultsAggregator.GRAPH_NAME_JOBS;
	}
	
	public String getGraphNameTests() {
		return TestResultsAggregator.GRAPH_NAME_TESTS;
	}
	
	public String getUrlName() {
		return TestResultsAggregator.URL;
	}
	
	public String getSearchUrl() {
		return TestResultsAggregator.URL;
	}
	
	public void doGraphJob(final StaplerRequest req, StaplerResponse rsp) throws IOException {
		if (newGraphNotNeeded(req, rsp)) {
			return;
		}
		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		populateDataSetBuilderJobs(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChartJob(req, dataSetBuilder.build());
			}
		}.doPng(req, rsp);
	}
	
	public void doGraphTests(final StaplerRequest req, StaplerResponse rsp) throws IOException {
		if (newGraphNotNeeded(req, rsp)) {
			return;
		}
		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		populateDataSetBuilderTest(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChartTests(req, dataSetBuilder.build());
			}
		}.doPng(req, rsp);
	}
	
	public String getUpUrl() {
		return Functions.getNearestAncestorUrl(Stapler.getCurrentRequest(), job) + '/';
	}
	
	private boolean newGraphNotNeeded(final StaplerRequest req, StaplerResponse rsp) {
		Calendar t = getProject().getLastCompletedBuild().getTimestamp();
		return req.checkIfModified(t, rsp);
	}
	
	public void doGraphMapJob(final StaplerRequest req, StaplerResponse rsp) throws IOException {
		if (newGraphNotNeeded(req, rsp)) {
			return;
		}
		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		populateDataSetBuilderJobs(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChartJob(req, dataSetBuilder.build());
			}
		}.doMap(req, rsp);
	}
	
	public void doGraphMapTests(final StaplerRequest req, StaplerResponse rsp) throws IOException {
		if (newGraphNotNeeded(req, rsp)) {
			return;
		}
		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		populateDataSetBuilderTest(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChartTests(req, dataSetBuilder.build());
			}
		}.doMap(req, rsp);
	}
	
	public boolean isGraphActive() {
		Run<?, ?> build = getProject().getLastBuild();
		// in order to have a graph, we must have at least two points.
		int numPoints = 0;
		while (numPoints < 2) {
			if (build == null) {
				return false;
			}
			if (build.getAction(getBuildActionClass()) != null) {
				numPoints++;
			}
			build = build.getPreviousBuild();
		}
		return true;
	}
	
	public TestResultsAggregatorTestResultBuildAction getLastCompletedBuildAction() {
		for (Run<?, ?> build = getProject().getLastCompletedBuild(); build != null; build = build.getPreviousCompletedBuild()) {
			final TestResultsAggregatorTestResultBuildAction action = build.getAction(getBuildActionClass());
			if (action != null) {
				return action;
			}
		}
		return null;
	}
	
	protected void populateDataSetBuilderJobs(DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataset) {
		if (!(job instanceof LazyBuildMixIn.LazyLoadingJob)) {
			return;
		}
		SortedMap<Integer, Run<?, ?>> loadedBuilds = (SortedMap<Integer, Run<?, ?>>) ((LazyLoadingJob<?, ?>) job).getLazyBuildMixIn()._getRuns().getLoadedBuilds();
		for (Run<?, ?> build : loadedBuilds.values()) {
			ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(build);
			TestResultsAggregatorTestResultBuildAction action = build.getAction(getBuildActionClass());
			Result result = build.getResult();
			if (result == null || result.isWorseThan(Result.FAILURE)) {
				// We don't want to add aborted or builds with no results into the graph
				continue;
			}
			if (action != null) {
				dataset.add(action.getSuccess(), SUCCESS, label);
				dataset.add(action.getFailCount(), FAILED, label);
				dataset.add(action.getUnstableCount(), UNSTABLE, label);
				dataset.add(action.getAborted(), ABORTED, label);
				dataset.add(action.getRunning(), RUNNING, label);
			}
		}
	}
	
	protected void populateDataSetBuilderTest(DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataset) {
		if (!(job instanceof LazyBuildMixIn.LazyLoadingJob)) {
			return;
		}
		SortedMap<Integer, Run<?, ?>> loadedBuilds = (SortedMap<Integer, Run<?, ?>>) ((LazyLoadingJob<?, ?>) job).getLazyBuildMixIn()._getRuns().getLoadedBuilds();
		for (Run<?, ?> build : loadedBuilds.values()) {
			ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(build);
			TestResultsAggregatorTestResultBuildAction action = build.getAction(getBuildActionClass());
			Result result = build.getResult();
			if (result == null || result.isWorseThan(Result.FAILURE)) {
				// We don't want to add aborted or builds with no results into the graph
				continue;
			}
			if (action != null) {
				dataset.add(action.getSuccessTTests(), SUCCESS, label);
				dataset.add(action.getFailedTTests(), FAILED, label);
				dataset.add(action.getSkippedTTests(), UNSTABLE, label);
			}
		}
	}
	
	public int getGraphWidth() {
		return 500;
	}
	
	public int getGraphHeight() {
		return 200;
	}
}
