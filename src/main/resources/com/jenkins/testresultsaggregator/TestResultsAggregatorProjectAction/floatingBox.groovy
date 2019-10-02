package com.jenkins.testresultsaggregator.TestNGProjectAction

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

if (from.graphActive) {
    div(class: "test-trend-caption") {
        text("${from.graphName}")
    }
    img(lazymap: "${from.urlName}/graphMap", alt: "[Job result trend chart]", src: "${from.urlName}/graph")
}
