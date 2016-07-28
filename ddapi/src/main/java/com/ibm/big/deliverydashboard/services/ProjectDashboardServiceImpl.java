package com.ibm.big.deliverydashboard.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.big.deliverydashboard.dao.elastic.ElasticProjectSnapshotRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoProjectRepository;
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationBean;
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;
import com.ibm.big.deliverydashboard.ddcommon.analysis.DateHistogramRequest;

@Service
public class ProjectDashboardServiceImpl implements ProjectDashboardService
{
	private static final Logger logger = LogManager.getLogger(ProjectDashboardServiceImpl.class);

	@Autowired
	MongoProjectRepository mongoProjRepo;

	@Autowired
	ElasticProjectSnapshotRepository elasticProjSnapshotRepo;

	@Autowired
	ProjectSnapshotBuilderFactory psbuilderFactory;

	@Override
	public AggregationResponse getProjectSpentEffortDateHistogram(String projectId, String fromDate, String toDate, String interval)
	{
		DateHistogramRequest aggRequest = new DateHistogramRequest();
		aggRequest.setName(projectId + ".EffortHistogram");
		aggRequest.setFromDate(fromDate);
		aggRequest.setMinDocCount(1);
		aggRequest.setToDate(toDate);
		aggRequest.setInterval("1w");
		aggRequest.setDateField("logDate");
		aggRequest.setQueryField("project.id");
		aggRequest.setQueryFieldValue(projectId);
		// TODO aggRequest.setTimeZone(timeZone);
		
		AggregationBean spentBuildEffortAgg = new AggregationBean();
		spentBuildEffortAgg.setName("spentBuildEffort");
		spentBuildEffortAgg.setField("sprint.spentHours.build");
		spentBuildEffortAgg.setType(AggregationBean.AGGREGATION_TYPE_SUM);
		aggRequest.addSubAggregations(spentBuildEffortAgg);

		AggregationBean spentDesignEffortAgg = new AggregationBean();
		spentDesignEffortAgg.setName("spentDesignEffort");
		spentDesignEffortAgg.setField("sprint.spentHours.design");
		spentDesignEffortAgg.setType(AggregationBean.AGGREGATION_TYPE_SUM);
		aggRequest.addSubAggregations(spentDesignEffortAgg);
		
		AggregationBean spentTestEffortAgg = new AggregationBean();
		spentTestEffortAgg.setName("spentTestEffort");
		spentTestEffortAgg.setField("sprint.spentHours.test");
		spentTestEffortAgg.setType(AggregationBean.AGGREGATION_TYPE_SUM);
		aggRequest.addSubAggregations(spentTestEffortAgg);
		
		AggregationBean spentUnproductiveAgg = new AggregationBean();
		spentUnproductiveAgg.setName("spentUnproductiveEffort");
		spentUnproductiveAgg.setField("sprint.spentHours.unproductive");
		spentUnproductiveAgg.setType(AggregationBean.AGGREGATION_TYPE_SUM);
		aggRequest.addSubAggregations(spentUnproductiveAgg);

		AggregationBean spentRequirementsEffortAgg = new AggregationBean();
		spentRequirementsEffortAgg.setName("spentRequirementsEffort");
		spentRequirementsEffortAgg.setField("sprint.spentHours.requirements");
		spentRequirementsEffortAgg.setType(AggregationBean.AGGREGATION_TYPE_SUM);
		aggRequest.addSubAggregations(spentRequirementsEffortAgg);
		
		return elasticProjSnapshotRepo.getDateHistogramForProject(aggRequest);
	}

}
