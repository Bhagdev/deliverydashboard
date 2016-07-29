package com.ibm.big.deliverydashboard.dao.elastic;

import java.util.List;

import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;
import com.ibm.big.deliverydashboard.ddcommon.analysis.DateHistogramRequest;
import com.ibm.big.deliverydashboard.ddcommon.analysis.SimpleAggregationRequest;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public interface ElasticProjectSnapshotRepositoryCustom
{
	public static String AGGREGATION_TYPE_SUM = "sum";
	
	
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId, Integer page, Integer limit);
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId, String fromDate, String toDate, Integer page, Integer limit);
	public AggregationResponse getDateHistogramForProject(DateHistogramRequest dhr);
	public AggregationResponse getExtendedStats(SimpleAggregationRequest sar);
}
