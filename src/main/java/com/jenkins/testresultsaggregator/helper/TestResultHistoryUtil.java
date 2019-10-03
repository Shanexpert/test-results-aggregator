package com.jenkins.testresultsaggregator.helper;

import com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction;
import com.jenkins.testresultsaggregator.TestResultsAggregatorTestResultBuildAction;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;

import hudson.model.Run;

public class TestResultHistoryUtil {
	
	private TestResultHistoryUtil() {
	}
	
	public static AggregatedDTO getPreviousBuildTestResults(Run<?, ?> owner) {
		Run<?, ?> previousBuild = owner.getPreviousCompletedBuild();
		if (previousBuild != null && previousBuild.getAction(TestResultsAggregatorTestResultBuildAction.class) != null) {
			return previousBuild.getAction(TestResultsAggregatorTestResultBuildAction.class).getResult();
		} else {
			return new AggregatedDTO();
		}
	}
	
	public static String toSummary(TestResultsAggregatorTestResultBuildAction action) {
		int prevFailed, prevUnstable, prevSucces, prevAborted, prevRunning, prevTotal;
		Run<?, ?> run = action.run;
		AggregatedDTO previousResult = TestResultHistoryUtil.getPreviousBuildTestResults(run);
		prevFailed = previousResult.getCountJobFailures();
		prevUnstable = previousResult.getCountJobUnstable();
		prevSucces = previousResult.getCountJobSuccess();
		prevAborted = previousResult.getCountJobAborted();
		prevRunning = previousResult.getCountJobRunning();
		prevTotal = prevFailed + prevUnstable + prevSucces + prevAborted + prevRunning;
		AggregatedDTO result = action.getResult();
		int total = result.getCountJobAborted() + result.getCountJobFailures() + result.getCountJobRunning() + result.getCountJobSuccess() + result.getCountJobUnstable();
		return "<ul>" + Helper.diff(prevTotal, total, "Total Jobs", true) +
				Helper.diff(prevFailed, result.getCountJobFailures(), TestResultsAggregatorProjectAction.FAILED + " Jobs", true) +
				Helper.diff(prevUnstable, result.getCountJobUnstable(), TestResultsAggregatorProjectAction.UNSTABLE + " Jobs", true) +
				Helper.diff(prevAborted, result.getCountJobAborted(), TestResultsAggregatorProjectAction.ABORTED + " Jobs", true) +
				Helper.diff(prevRunning, result.getCountJobRunning(), TestResultsAggregatorProjectAction.RUNNING + " Jobs", true) +
				"</ul>";
	}
	
}
