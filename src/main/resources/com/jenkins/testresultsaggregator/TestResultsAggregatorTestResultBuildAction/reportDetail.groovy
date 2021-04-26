package com.jenkins.testresultsaggregator.TestResultsAggregatorTestResultBuildAction

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

import com.jenkins.testresultsaggregator.data.JobStatus
import com.jenkins.testresultsaggregator.helper.Colors

script(src: "${app.rootUrl}/plugin/test-results-aggregator/js/toggle_table.js")
script(src: "${app.rootUrl}/plugin/test-results-aggregator/js/toggle_mthd_summary.js")

if (my.result.abortedJobs > 0) {
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
					if("${JobStatus.ABORTED.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.failedJobs > 0) {
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
					if(job.getResults()!=null && "${JobStatus.FAILURE.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.keepFailJobs > 0) {
	h2(align: "left", style:"color:${Colors.htmlFAILED()}" ,"Still Failling Jobs")
	a(href: "javascript:toggleTable('failk-tbl')") {
		text("hide/expand the table")
	}
	table(id:"failk-tbl", border:"1px", class:"pane sortable") {
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
					if("${JobStatus.STILL_FAILING.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.unstableJobs > 0) {
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
					if("${JobStatus.UNSTABLE.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.keepUnstableJobs > 0) {
	h2(align: "left", style:"color:${Colors.htmlUNSTABLE()}" ,"Still Unstable Jobs")
	a(href: "javascript:toggleTable('unstablek-tbl')") {
		text("hide/expand the table")
	}
	table(id:"unstablek-tbl", border:"1px", class:"pane sortable") {
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
					if("${JobStatus.STILL_UNSTABLE.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.fixedJobs > 0) {
	h2(align: "left", style:"color:${Colors.htmlSUCCESS()}" , "Fixed Jobs")
	a(href: "javascript:toggleTable('fixed-tbl')") {
		text("hide/expand the table")
	}
	table(id:"fixed-tbl", border:"1px", class:"pane sortable") {
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
					if("${JobStatus.FIXED.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.successJobs > 0) {
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
					if("${JobStatus.SUCCESS.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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

if (my.result.runningJobs > 0) {
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
					if("${JobStatus.RUNNING.name()}".equalsIgnoreCase(job.getResults().getStatus())) {
						tr() {
							td(align: "left") {
								text("${job.getJobName()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedTotal()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedPass()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedFail()}")
							}
							td(align: "center") {
								raw("${job.getResults().getCalculatedSkip()}")
							}
							td(align: "center") {
								a(href:"${job.getJobInfo().getUrl()}") {
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