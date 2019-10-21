package com.jenkins.testresultsaggregator.TestResultsAggregatorTestResultBuildAction

import hudson.Functions
import com.jenkins.testresultsaggregator.helper.TestResultHistoryUtil
import com.jenkins.testresultsaggregator.helper.Colors;

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

def prevResult = TestResultHistoryUtil.getPreviousBuildTestResults(my.run)

div() {
	if (my.result.getTotalJobs() == 0) {
		text("No Job Results")
	} else {
		div(id: "fail-skip") {
			text("${my.result.failedJobs} failure${my.result.failedJobs != 1 ? "s" : ""}")
			if (prevResult) {
				text("(${Functions.getDiffString(my.result.failedJobs - prevResult.failedJobs)})")
			}
			if (my.result.unstableJobs > 0) {
				text(", ${my.result.unstableJobs} unstable")
				if (prevResult) {
					text("(${Functions.getDiffString(my.result.unstableJobs - prevResult.unstableJobs)})")
				}
			}
			if (my.result.abortedJobs > 0) {
				text(", ${my.result.abortedJobs} aborted")
				if (prevResult) {
					text("(${Functions.getDiffString(my.result.abortedJobs - prevResult.abortedJobs)})")
				}
			}
			if (my.result.runningJobs > 0) {
				text(", ${my.result.runningJobs} running")
				if (prevResult) {
					text("(${Functions.getDiffString(my.result.runningJobs - prevResult.runningJobs)})")
				}
			}
		}
		
		div(style: "width:100%; height:1em; background-color: ${Colors.htmlSUCCESS()}") {
			def failpc = my.result.failedJobs * 100 / my.result.getTotalJobs()
			def skippc = my.result.unstableJobs * 100 / my.result.getTotalJobs()
			def abortpc = my.result.abortedJobs * 100 / my.result.getTotalJobs()
			def runnpc = my.result.runningJobs * 100 / my.result.getTotalJobs()
			div(style: "width:${failpc}%; height: 1em; background-color: ${Colors.htmlFAILED()}; float: left")
			div(style: "width:${skippc}%; height: 1em; background-color: ${Colors.htmlUNSTABLE()}; float: left")
			div(style: "width:${abortpc}%; height: 1em; background-color: ${Colors.htmlABORTED()}; float: left")
			div(style: "width:${runnpc}%; height: 1em; background-color: ${Colors.htmlRUNNING()}; float: left")
		}
		
		div(id: "pass", align: "right") {
			text("${my.result.getTotalJobs()} job${my.result.getTotalJobs() != 1 ? "s" : ""}")
			if (prevResult) {
				text("(${Functions.getDiffString(my.result.getTotalJobs() - prevResult.getTotalJobs())})")
			}
		}
	}
}