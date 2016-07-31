package com.ibm.big.deliverydashboard.services;

import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;

public interface ProjectDashboardService
{
	public AggregationResponse getProjectSpentEffortDateHistogram(String projectId, String sprintId, String fromDate, String toDate, String interval);
	public AggregationResponse getProjectSpentEffortExtendedStats(String projectId, String sprintId, String fromDate, String toDate);
	public AggregationResponse getProjectEffortExtendedStats(String projectId, String sprintId, String fromDate, String toDate);
	public AggregationResponse getProjectBurnDownDateHistogram(String projectId, String sprintId, String fromDate, String toDate, String interval);

}
