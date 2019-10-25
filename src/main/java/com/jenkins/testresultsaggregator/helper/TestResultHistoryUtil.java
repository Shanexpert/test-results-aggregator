package com.jenkins.testresultsaggregator.helper;

import com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction;
import com.jenkins.testresultsaggregator.TestResultsAggregatorTestResultBuildAction;
import com.jenkins.testresultsaggregator.data.Aggregated;

import hudson.model.Run;

public class TestResultHistoryUtil {
	
	private TestResultHistoryUtil() {
	}
	
	public static Aggregated getTestResults(Run<?, ?> owner) {
		if (owner != null) {
			if (owner.getAction(TestResultsAggregatorTestResultBuildAction.class) != null) {
				return owner.getAction(TestResultsAggregatorTestResultBuildAction.class).getResult();
			} else {
				return new Aggregated();
			}
		}
		return null;
	}
	
	public static Aggregated getPreviousBuildTestResults(Run<?, ?> owner) {
		Run<?, ?> previousBuild = owner.getPreviousCompletedBuild();
		if (previousBuild != null && previousBuild.getAction(TestResultsAggregatorTestResultBuildAction.class) != null) {
			return previousBuild.getAction(TestResultsAggregatorTestResultBuildAction.class).getResult();
		} else {
			return new Aggregated();
		}
	}
	
	public static String toSummary(TestResultsAggregatorTestResultBuildAction action) {
		int prevFailed, prevUnstable, prevSucces, prevAborted, prevRunning, prevTotal;
		Run<?, ?> run = action.run;
		Aggregated previousResult = TestResultHistoryUtil.getPreviousBuildTestResults(run);
		prevFailed = previousResult.getFailedJobs() + previousResult.getKeepFailJobs();
		prevUnstable = previousResult.getUnstableJobs() + previousResult.getKeepUnstableJobs();
		prevSucces = previousResult.getSuccessJobs() + previousResult.getFixedJobs();
		prevAborted = previousResult.getAbortedJobs();
		prevRunning = previousResult.getRunningJobs();
		prevTotal = prevFailed + prevUnstable + prevSucces + prevAborted + prevRunning;
		Aggregated result = action.getResult();
		int total = result.getAbortedJobs() +
				result.getFailedJobs() +
				result.getKeepFailJobs() +
				result.getRunningJobs() +
				result.getSuccessJobs() +
				result.getFixedJobs() +
				result.getUnstableJobs() +
				result.getKeepUnstableJobs();
		return "<ul>" + Helper.diff(prevTotal, total, "Total Jobs ", true) +
				Helper.diff(prevFailed, result.getFailedJobs() + result.getKeepFailJobs(), TestResultsAggregatorProjectAction.FAILED + " Jobs ", true) +
				Helper.diff(prevUnstable, result.getUnstableJobs() + result.getKeepUnstableJobs(), TestResultsAggregatorProjectAction.UNSTABLE + " Jobs ", true) +
				Helper.diff(prevAborted, result.getAbortedJobs(), TestResultsAggregatorProjectAction.ABORTED + " Jobs ", true) +
				Helper.diff(prevRunning, result.getRunningJobs(), TestResultsAggregatorProjectAction.RUNNING + " Jobs ", true) +
				"</ul>";
	}
	
	public static String toSummaryTest(TestResultsAggregatorTestResultBuildAction action) {
		int prevFailed = 0;
		int prevUnstable = 0;
		int prevSucces = 0;
		int prevTotal = 0;
		Run<?, ?> run = action.run;
		Aggregated previousResult = TestResultHistoryUtil.getPreviousBuildTestResults(run);
		if (previousResult != null && previousResult.getResults() != null) {
			prevFailed = previousResult.getResults().getFail();
			prevUnstable = previousResult.getResults().getSkip();
			prevSucces = previousResult.getResults().getPass();
			prevTotal = previousResult.getResults().getTotal();
		}
		Aggregated result = action.getResult();
		return "<ul>" + Helper.diff(prevTotal, result.getResults().getTotal(), "Total Tests ", true) +
				Helper.diff(prevFailed, result.getResults().getFail(), TestResultsAggregatorProjectAction.FAILED + " Tests ", true) +
				Helper.diff(prevUnstable, result.getResults().getSkip(), TestResultsAggregatorProjectAction.UNSTABLE + " Tests ", true) +
				Helper.diff(prevSucces, result.getResults().getPass(), TestResultsAggregatorProjectAction.SUCCESS + " Tests ", true) +
				"</ul>";
	}
	
}
