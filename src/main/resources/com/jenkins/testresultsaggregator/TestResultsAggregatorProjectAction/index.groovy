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
		table(width:'100%'){
			tr(){
				th(){
					h3("Job Results Trend")
				}
				th(){
					
				}
				th(){
					h3("Test Results Trend")
				}
			}
			tr(){
				th(){
					if (my.isGraphActive()) {
						img(lazymap: "graphMapJob?rel=../", alt: "[Job results trend chart]", src: "graphJob" , width:"80%")
					} else {
						p("Need at least 2 builds with results to show trend graph")
					}
				}
				th(){
					
				}
				th(){
					if (my.isGraphActive()) {
						img(lazymap: "graphMapTests?rel=../", alt: "[Test results trend chart]", src: "graphTests", width:"80%")
					} else {
						p("Need at least 2 builds with results to show trend graph")
					}
				}
			}
			tr(){
				th(align:'left'){
					raw("${TestResultHistoryUtil.toSummary(lastCompletedBuildAction)}")
				}
				th(){
				
				}
				th(align:'left'){
					raw("${TestResultHistoryUtil.toSummaryTest(lastCompletedBuildAction)}")
				}
			}
			
			tr(){
				th(){
				}
				th(){
				}
				th(align:'right'){
					br()
					 def buildNumber = my.project.lastCompletedBuild.number
					 h3() {
						 text("Latest Job Results (")
						 a(href: "${my.upUrl}${buildNumber}/${my.urlName}") {
							 text("build #${buildNumber}")
						 }
						 text(")")
					 }
				}
			}
			
		}
    }
}