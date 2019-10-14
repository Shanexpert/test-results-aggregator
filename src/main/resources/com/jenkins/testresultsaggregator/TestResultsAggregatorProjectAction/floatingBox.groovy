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
	
	div(align:'right'){
		a(href: "${from.urlName}/graphJob?width=1200&height=800"){
			text("Enlarge")
		}
    }
	p()
	div(class: "test-trend-caption") {
		text("${from.graphNameTests}")
	}
	img(lazymap: "${from.urlName}/graphMapTests", alt: "[Test result trend chart]", src: "${from.urlName}/graphTests")
	div(align:'right'){
		a(href: "${from.urlName}/graphTests?width=1200&height=800"){
			text("Enlarge")
		}
	}
}
