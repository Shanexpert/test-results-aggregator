package com.jenkins.testresultsaggregator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.helper.ResultsParser;
import com.jenkins.testresultsaggregator.reporter.XMLReporter;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Api;
import hudson.model.Run;
import hudson.tasks.test.AbstractTestResultAction;
import jenkins.tasks.SimpleBuildStep;

/**
 * TestNG build action that exposes the results per build
 *
 * @author nullin
 * @since v1.0
 */
public class TestResultsAggregatorTestResultBuildAction extends AbstractTestResultAction implements Serializable, SimpleBuildStep.LastBuildAction {
	
	private static final long serialVersionUID = 31415926L;
	private static final Logger LOGGER = Logger.getLogger(TestResultsAggregatorTestResultBuildAction.class.getName());
	private transient Reference<AggregatedDTO> aggregatedResults;
	
	protected Integer success;
	protected int failed;
	protected int unstable;
	protected int aborted;
	protected int running;
	
	public TestResultsAggregatorTestResultBuildAction(AggregatedDTO aggregatedResults) {
		if (aggregatedResults != null) {
			this.aggregatedResults = new WeakReference<AggregatedDTO>(aggregatedResults);
			count(aggregatedResults);
		}
	}
	
	private void count(AggregatedDTO testngResults) {
		this.success = testngResults.getCountJobSuccess();
		this.failed = testngResults.getCountJobFailures();
		this.aborted = testngResults.getCountJobAborted();
		this.unstable = testngResults.getCountJobUnstable();
		this.running = testngResults.getCountJobRunning();
	}
	
	private void countAndSave(AggregatedDTO aggregatedDTO) {
		int savedSuccessCount = success != null ? success : -1;
		int savedFailedCount = failed;
		int savedUnstableCount = unstable;
		int savedAbortedCount = aborted;
		int savedRunningCount = running;
		count(aggregatedDTO);
		if (success != savedSuccessCount || failed != savedFailedCount || unstable != savedUnstableCount || aborted != savedAbortedCount || running != savedRunningCount) {
			try {
				owner.save();
			} catch (IOException x) {
				LOGGER.log(Level.WARNING, "failed to save " + owner, x);
			}
		}
	}
	
	private void countAsNeeded() {
		if (success == null) {
			countAndSave(getResult());
		}
	}
	
	@Override
	public AggregatedDTO getResult() {
		return getResult(super.run);
	}
	
	public AggregatedDTO getResult(Run build) {
		AggregatedDTO tr = aggregatedResults != null ? aggregatedResults.get() : null;
		if (tr == null) {
			tr = loadResults(build);
			countAndSave(tr);
			aggregatedResults = new WeakReference<AggregatedDTO>(tr);
		}
		return tr;
	}
	
	static AggregatedDTO loadResults(Run<?, ?> owner) {
		FilePath resultDirectory = getAggregatedReport(owner);
		FilePath[] paths = null;
		try {
			paths = resultDirectory.list(XMLReporter.REPORT_XML_FILE);
		} catch (Exception e) {
			// do nothing
		}
		if (paths == null) {
			AggregatedDTO aggregatedDTO = new AggregatedDTO();
			aggregatedDTO.setRun(owner);
			return aggregatedDTO;
		}
		ResultsParser parser = new ResultsParser();
		AggregatedDTO aggregatedDTO = parser.parse(paths);
		aggregatedDTO.setRun(owner);
		return aggregatedDTO;
	}
	
	static FilePath getAggregatedReport(Run<?, ?> build) {
		return new FilePath(new File(build.getRootDir(), ""));
	}
	
	@Override
	public String getIconFileName() {
		return TestResultsAggregator.ICON_FILE_NAME;
	}
	
	@Override
	public int getTotalCount() {
		return failed + unstable + success + aborted + running;
	}
	
	public int getFailCount() {
		countAsNeeded();
		return failed;
	}
	
	public int getUnstableCount() {
		countAsNeeded();
		return unstable;
	}
	
	public int getSuccess() {
		countAsNeeded();
		return success;
	}
	
	public int getAborted() {
		countAsNeeded();
		return aborted;
	}
	
	public int getRunning() {
		countAsNeeded();
		return running;
	}
	
	@Override
	public String getDisplayName() {
		return TestResultsAggregator.DISPLAY_NAME;
	}
	
	@Override
	public String getUrlName() {
		return TestResultsAggregator.URL;
	}
	
	@Override
	public Api getApi() {
		return new Api(getResult());
	}
	
	@Override
	public Collection<? extends Action> getProjectActions() {
		return Collections.singleton(new TestResultsAggregatorProjectAction(run.getParent()));
	}
	
}
