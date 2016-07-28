package com.ibm.big.deliverydashboard.services;

import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;

public interface ProjectDashboardService
{
	public AggregationResponse getProjectSpentEffortDateHistogram(String projectId, String fromDate, String toDate, String interval);
}
