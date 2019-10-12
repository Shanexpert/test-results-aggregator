package com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction

import com.jenkins.testresultsaggregator.helper.TestResultHistoryUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

l.layout(title: "Job Results Trend") {
    st.include(page: "sidepanel.jelly", it: my.project)
    l.main_panel() {
		def lastCompletedBuildAction = my.lastCompletedBuildAction
		if (lastCompletedBuildAction) {
			h1("Jobs")
			p() {
				raw("${TestResultHistoryUtil.toSummary(lastCompletedBuildAction)}")
			}
			if (my.isGraphActive()) {
				img(lazymap: "graphMap?rel=../", alt: "[Job results trend chart]", src: "graphJob")
			} else {
				p("Need at least 2 builds with results to show trend graph")
			}
		} else {
			p("No builds have successfully recorded Aggregated results yet")
		}

		if (lastCompletedBuildAction) {
			h1("Tests")
			p() {
				raw("${TestResultHistoryUtil.toSummaryTest(lastCompletedBuildAction)}")
			}
			if (my.isGraphActive()) {
				img(lazymap: "graphMap2?rel=../", alt: "[Test results trend chart]", src: "graphTests")
			} else {
				p("Need at least 2 builds with results to show trend graph")
			}
		} else {
			p("No builds have successfully recorded Aggregated results yet")
		}
		
        br()
        def buildNumber = my.project.lastCompletedBuild.number
        h2() {
            text("Latest Job Results (")
            a(href: "${my.upUrl}${buildNumber}/${my.urlName}") {
                text("build #${buildNumber}")
            }
            text(")")
        }

        
    }
}