package com.jenkins.testresultsaggregator.TestResultsAggregatorTestResultBuildAction

import com.jenkins.testresultsaggregator.data.JobStatus
import com.jenkins.testresultsaggregator.helper.Colors

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

script(src: "${app.rootUrl}/plugin/test-results-aggregator/js/toggle_table.js")
script(src: "${app.rootUrl}/plugin/test-results-aggregator/js/toggle_mthd_summary.js")

if (my.result.countJobFailures > 0) {
	h2(align: "left", style:"color:${Colors.htmlFAILED()}" ,"Failed Jobs")
	a(href: "javascript:toggleTable('fail-tbl')") {
		text("hide/expand the table")
	}
	table(id:"fail-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") {
					text("Job Name")
				}
				th(class: "pane-header" , width: "100px") {
					text("Tests")
				}
				th(class: "pane-header" , width: "100px") {
					text("Pass")
				}
				th(class: "pane-header" , width: "100px") {
					text("Fail")
				}
				th(class: "pane-header" , width: "100px") {
					text("Skip")
				}
				th(class: "pane-header" , width: "100px") {
					text("Link")
				}
			}
		}
		tbody() {
			for (data in my.result.getData()) {
				for (job in data.getJobs()) {
					if("${JobStatus.FAILURE.name()}".equalsIgnoreCase(job.getResultsDTO().getCurrentResult())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedTotal()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedPass()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedFail()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJenkinsJob().getUrl()}") {
									text(">>>")
								}
								text(" ")
							}
						}
					}
				}
			}
		}
	}
}

if (my.result.countJobUnstable > 0) {
	h2(align: "left", style:"color:${Colors.htmlUNSTABLE()}" ,"Unstable Jobs")
	a(href: "javascript:toggleTable('unstable-tbl')") {
		text("hide/expand the table")
	}
	table(id:"unstable-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") {
					text("Job Name")
				}
				th(class: "pane-header" , width: "100px") {
					text("Tests")
				}
				th(class: "pane-header" , width: "100px") {
					text("Pass")
				}
				th(class: "pane-header" , width: "100px") {
					text("Fail")
				}
				th(class: "pane-header" , width: "100px") {
					text("Skip")
				}
				th(class: "pane-header" , width: "100px") {
					text("Link")
				}
			}
		}
		tbody() {
			for (data in my.result.getData()) {
				for (job in data.getJobs()) {
					if("${JobStatus.UNSTABLE.name()}".equalsIgnoreCase(job.getResultsDTO().getCurrentResult())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedTotal()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedPass()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedFail()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJenkinsJob().getUrl()}") {
									text(">>>")
								}
								text(" ")
							}
						}
					}
				}
			}
		}
	}
}

if (my.result.countJobAborted > 0) {
	h2(align: "left", style:"color:${Colors.htmlABORTED()}" ,"Aborted Jobs")
	a(href: "javascript:toggleTable('aborted-tbl')") {
		text("hide/expand the table")
	}
	table(id:"aborted-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") {
					text("Job Name")
				}
				th(class: "pane-header" , width: "100px") {
					text("Tests")
				}
				th(class: "pane-header" , width: "100px") {
					text("Pass")
				}
				th(class: "pane-header" , width: "100px") {
					text("Fail")
				}
				th(class: "pane-header" , width: "100px") {
					text("Skip")
				}
				th(class: "pane-header" , width: "100px") {
					text("Link")
				}
			}
		}
		tbody() {
			for (data in my.result.getData()) {
				for (job in data.getJobs()) {
					if("${JobStatus.ABORTED.name()}".equalsIgnoreCase(job.getResultsDTO().getCurrentResult())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedTotal()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedPass()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedFail()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJenkinsJob().getUrl()}") {
									text(">>>")
								}
								text(" ")
							}
						}
					}
				}
			}
		}
	}
}

if (my.result.countJobSuccess > 0) {
	h2(align: "left", style:"color:${Colors.htmlSUCCESS()}" , "Success Jobs")
	a(href: "javascript:toggleTable('Success-tbl')") {
		text("hide/expand the table")
	}
	table(id:"Success-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") {
					text("Job Name")
				}
				th(class: "pane-header" , width: "100px") {
					text("Tests")
				}
				th(class: "pane-header" , width: "100px") {
					text("Pass")
				}
				th(class: "pane-header" , width: "100px") {
					text("Fail")
				}
				th(class: "pane-header" , width: "100px") {
					text("Skip")
				}
				th(class: "pane-header" , width: "100px") {
					text("Link")
				}
			}
		}
		tbody() {
			for (data in my.result.getData()) {
				for (job in data.getJobs()) {
					if("${JobStatus.SUCCESS.name()}".equalsIgnoreCase(job.getResultsDTO().getCurrentResult())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedTotal()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedPass()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedFail()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJenkinsJob().getUrl()}") {
									text(">>>")
								} 
								text(" ")
							}
						}
					}
				}
			}
		}
	}
}

if (my.result.countJobRunning > 0) {
	h2(align: "left", style:"color:${Colors.htmlRUNNING()}" ,"Running Jobs")
	a(href: "javascript:toggleTable('running-tbl')") {
		text("hide/expand the table")
	}
	table(id:"running-tbl", border:"1px", class:"pane sortable") {
		thead() {
			tr() {
				th(class: "pane-header") {
					text("Job Name")
				}
				th(class: "pane-header" , width: "100px") {
					text("Tests")
				}
				th(class: "pane-header" , width: "100px") {
					text("Pass")
				}
				th(class: "pane-header" , width: "100px") {
					text("Fail")
				}
				th(class: "pane-header" , width: "100px") {
					text("Skip")
				}
				th(class: "pane-header" , width: "100px") {
					text("Link")
				}
			}
		}
		tbody() {
			for (data in my.result.getData()) {
				for (job in data.getJobs()) {
					if("${JobStatus.RUNNING.name()}".equalsIgnoreCase(job.getResultsDTO().getCurrentResult())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedTotal()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedPass()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedFail()}")
							}
							td(align: "center") {
								text("${job.getResultsDTO().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJenkinsJob().getUrl()}") {
									text(">>>")
								} 
								text(" ")
							}
						}
					}
				}
			}
		}
	}
}