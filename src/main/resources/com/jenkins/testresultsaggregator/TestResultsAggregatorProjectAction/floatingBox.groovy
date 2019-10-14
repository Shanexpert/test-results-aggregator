package com.jenkins.testresultsaggregator.TestResultsAggregatorProjectAction

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

if (from.graphActive) {
    div(class: "test-trend-caption") {
        text("${from.graphNameJobs}")
    }
    img(lazymap: "${from.urlName}/graphMapJob", alt: "[Job result trend chart]", src: "${from.urlName}/graphJob")
	p()
	div(class: "test-trend-caption") {
		text("${from.graphNameTests}")
	}
	img(lazymap: "${from.urlName}/graphMapTests", alt: "[Test result trend chart]", src: "${from.urlName}/graphTests")
}
