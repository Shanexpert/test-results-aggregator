package com.jenkins.testresultsaggregator.TestNGTestResultBuildAction

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

l.layout(title: "Report for Build #${my.run.number}") {
    st.include(page: "sidepanel.jelly", it: my.run)
    l.main_panel() {

        h1("${my.displayName}")
        st.include(page: "bar.groovy")
        st.include(page: "reportDetail.groovy")
    }
}