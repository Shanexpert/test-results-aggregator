package com.jenkins.testresultsaggregator.TestNGTestResultBuildAction

import hudson.Functions
import com.jenkins.testresultsaggregator.helper.TestResultHistoryUtil
import com.jenkins.testresultsaggregator.helper.Colors;

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

def prevResult = TestResultHistoryUtil.getPreviousBuildTestResults(my.run)

div() {
    if (my.result.countTotal == 0) {
        text("No Job Results")
    } else {
        div(id: "fail-skip") {
            text("${my.result.countJobFailures} failure${my.result.countJobFailures != 1 ? "s" : ""}")
            if (prevResult) {
                text("(${Functions.getDiffString(my.result.countJobFailures - prevResult.countJobFailures)})")
            }
            if (my.result.countJobUnstable > 0) {
                text(", ${my.result.countJobUnstable} unstable")
                if (prevResult) {
                    text("(${Functions.getDiffString(my.result.countJobUnstable - prevResult.countJobUnstable)})")
                }
            }
			if (my.result.countJobAborted > 0) {
				text(", ${my.result.countJobAborted} aborted")
				if (prevResult) {
					text("(${Functions.getDiffString(my.result.countJobAborted - prevResult.countJobAborted)})")
				}
			}
			if (my.result.countJobRunning > 0) {
				text(", ${my.result.countJobRunning} running")
				if (prevResult) {
					text("(${Functions.getDiffString(my.result.countJobRunning - prevResult.countJobRunning)})")
				}
			}
        }

        div(style: "width:100%; height:1em; background-color: ${Colors.htmlSUCCESS()}") {
            def failpc = my.result.countJobFailures * 100 / my.result.countTotal
            def skippc = my.result.countJobUnstable * 100 / my.result.countTotal
			def abortpc = my.result.countJobAborted * 100 / my.result.countTotal
			def runnpc = my.result.countJobRunning * 100 / my.result.countTotal
            div(style: "width:${failpc}%; height: 1em; background-color: ${Colors.htmlFAILED()}; float: left")
            div(style: "width:${skippc}%; height: 1em; background-color: ${Colors.htmlUNSTABLE()}; float: left")
			div(style: "width:${abortpc}%; height: 1em; background-color: ${Colors.htmlABORTED()}; float: left")
			div(style: "width:${runnpc}%; height: 1em; background-color: ${Colors.htmlRUNNING()}; float: left")
        }

        div(id: "pass", align: "right") {
            text("${my.result.countTotal} job${my.result.countTotal != 1 ? "s" : ""}")
            if (prevResult) {
                text("(${Functions.getDiffString(my.result.countTotal - prevResult.countTotal)})")
            }
        }
    }
}