package com.jenkins.testresultsaggregator.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.AggregateJobDTO;
import com.jenkins.testresultsaggregator.data.AggregatedDTO;
import com.jenkins.testresultsaggregator.data.AggregatedGroupDTO;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ResultsDTO;

import hudson.model.BuildListener;

public class Analyzer {
	
	private BuildListener listener;
	
	public Analyzer(BuildListener listener) {
		this.listener = listener;
	}
	
	public AggregatedDTO analyze(List<DataDTO> dataJob, String outOfDateResults) throws Exception {
		// Check if Groups/Names are used
		List<DataJobDTO> listDataJobDTO = new ArrayList<>();
		boolean foundAtLeastOneGroupName = false;
		for (DataDTO data : dataJob) {
			if (!Strings.isNullOrEmpty(data.getGroupName())) {
				foundAtLeastOneGroupName = true;
			}
			if (data.getJobs() != null && !data.getJobs().isEmpty()) {
				listDataJobDTO.addAll(data.getJobs());
			}
		}
		// Order List per Group Name
		if (foundAtLeastOneGroupName) {
			Collections.sort(dataJob, new Comparator<DataDTO>() {
				@Override
				public int compare(DataDTO dataDTO1, DataDTO dataDTO2) {
					return dataDTO1.getGroupName().compareTo(dataDTO2.getGroupName());
				}
			});
		}
		// Order Jobs per Group
		for (DataDTO data : dataJob) {
			Collections.sort(data.getJobs(), new Comparator<DataJobDTO>() {
				
				@Override
				public int compare(DataJobDTO dataJobDTO1, DataJobDTO dataJobDTO2) {
					return dataJobDTO1.getJobNameFromFriendlyName().compareTo(dataJobDTO2.getJobNameFromFriendlyName());
				}
			});
		}
		AggregatedDTO aggregatedDTO = new AggregatedDTO();
		// Calculate Aggregated Results for Reporting
		ResultsDTO totalResults = new ResultsDTO();
		for (DataDTO tempDataJob : dataJob) {
			boolean foundFailure = false;
			boolean foundSkip = false;
			ResultsDTO totalResultsPerGroup = new ResultsDTO();
			tempDataJob.setAggregatedGroup(new AggregatedGroupDTO());
			for (DataJobDTO job : tempDataJob.getJobs()) {
				job.setAggregate(new AggregateJobDTO());
				// Calculate Job Status
				job.getAggregate().calculateJobStatus(job.getResultsDTO());
				// Calculate Total
				job.getAggregate().calculateTotal(job.getResultsDTO());
				// Calculate Pass
				job.getAggregate().calculatePass(job.getResultsDTO());
				// Calculate Fail
				job.getAggregate().calculateFailed(job.getResultsDTO());
				// Calculate Skipped
				job.getAggregate().calculateSkipped(job.getResultsDTO());
				// Calculate timestamp
				job.getAggregate().calculateTimestamp(job.getResultsDTO(), outOfDateResults);
				// Calculate Changes
				job.getAggregate().calculateChanges(job.getResultsDTO());
				// Calculate Report
				job.getAggregate().calculateReport(job.getResultsDTO());
				// Calculate Group
				if (JobStatus.SUCCESS.name().equals(job.getAggregate().getCalculatedJobStatus()) || JobStatus.FIXED.name().equals(job.getAggregate().getCalculatedJobStatus())) {
					tempDataJob.getAggregatedGroup().setJobSuccess(tempDataJob.getAggregatedGroup().getJobSuccess() + 1);
					aggregatedDTO.setCountJobSuccess(aggregatedDTO.getCountJobSuccess() + 1);
				} else if (JobStatus.FAILURE.name().equals(job.getAggregate().getCalculatedJobStatus()) || JobStatus.STILL_FAILING.name().equals(job.getAggregate().getCalculatedJobStatus())) {
					tempDataJob.getAggregatedGroup().setJobFailed(tempDataJob.getAggregatedGroup().getJobFailed() + 1);
					foundFailure = true;
					aggregatedDTO.setCountJobFailures(aggregatedDTO.getCountJobFailures() + 1);
				} else if (JobStatus.UNSTABLE.name().equals(job.getAggregate().getCalculatedJobStatus()) || JobStatus.STILL_UNSTABLE.name().equals(job.getAggregate().getCalculatedJobStatus())) {
					tempDataJob.getAggregatedGroup().setJobUnstable(tempDataJob.getAggregatedGroup().getJobUnstable() + 1);
					foundSkip = true;
					aggregatedDTO.setCountJobUnstable(aggregatedDTO.getCountJobUnstable() + 1);
				} else if (JobStatus.RUNNING.name().equals(job.getAggregate().getCalculatedJobStatus())) {
					tempDataJob.getAggregatedGroup().setJobRunning(tempDataJob.getAggregatedGroup().getJobRunning() + 1);
					aggregatedDTO.setCountJobRunning(aggregatedDTO.getCountJobRunning() + 1);
				} else if (JobStatus.ABORTED.name().equals(job.getAggregate().getCalculatedJobStatus())) {
					tempDataJob.getAggregatedGroup().setJobAborted(tempDataJob.getAggregatedGroup().getJobAborted() + 1);
					foundSkip = true;
					aggregatedDTO.setCountJobAborted(aggregatedDTO.getCountJobAborted() + 1);
				}
				// Calculate Total Per Group
				totalResultsPerGroup.setPass(totalResultsPerGroup.getPass() + job.getResultsDTO().getPass());
				totalResultsPerGroup.setSkip(totalResultsPerGroup.getSkip() + job.getResultsDTO().getSkip());
				totalResultsPerGroup.setTotal(totalResultsPerGroup.getTotal() + job.getResultsDTO().getTotal());
				// Calculate Total for Summary Column
				totalResults.sub(job.getResultsDTO());
			}
			// Set Results Per Group
			tempDataJob.getAggregatedGroup().setResultsDTO(totalResultsPerGroup);
			// Calculate Group Status
			if (foundFailure) {
				tempDataJob.getAggregatedGroup().setCalculatedGroupStatus(JobStatus.FAILURE.name());
			} else if (foundSkip) {
				tempDataJob.getAggregatedGroup().setCalculatedGroupStatus(JobStatus.UNSTABLE.name());
			} else {
				tempDataJob.getAggregatedGroup().setCalculatedGroupStatus(JobStatus.SUCCESS.name());
			}
			// Calculate Percentage Per Group
			tempDataJob.getAggregatedGroup().setCalculatedGroupPercentage(Helper.countPercentage(totalResultsPerGroup));
		}
		aggregatedDTO.setData(dataJob);
		aggregatedDTO.setResults(totalResults);
		return aggregatedDTO;
	}
}
