package com.jenkins.testresultsaggregator.TestNGProjectAction

import com.jenkins.testresultsaggregator.helper.TestResultHistoryUtil

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

l.layout(title: "Job Results Trend") {
    st.include(page: "sidepanel.jelly", it: my.project)
    l.main_panel() {

        h1("Job Results Trend")
        if (my.isGraphActive()) {
            img(lazymap: "graphMap?rel=../", alt: "[Job results trend chart]", src: "graph")
        } else {
            p("Need at least 2 builds with results to show trend graph")
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

        def lastCompletedBuildAction = my.lastCompletedBuildAction
        if (lastCompletedBuildAction) {
            p() {
                raw("${TestResultHistoryUtil.toSummary(lastCompletedBuildAction)}")
            }
        } else {
            p("No builds have successfully recorded Aggregated results yet")
        }
    }
}