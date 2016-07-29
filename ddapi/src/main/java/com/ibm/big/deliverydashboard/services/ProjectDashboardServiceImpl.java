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
import com.ibm.big.deliverydashboard.ddcommon.analysis.FieldQuery;
import com.ibm.big.deliverydashboard.ddcommon.analysis.ScriptBean;
import com.ibm.big.deliverydashboard.ddcommon.analysis.SimpleAggregationRequest;

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
	public AggregationResponse getProjectSpentEffortExtendedStats(String projectId, String sprintId, String fromDate,
			String toDate)
	{
		SimpleAggregationRequest sar = new SimpleAggregationRequest();
		sar.setName(projectId + ".SpentEffortExtendedStats");
		sar.setFromDate(fromDate);
		sar.setToDate(toDate);
		sar.setDateField("logDate");

		FieldQuery fq = new FieldQuery();
		fq.setField("project.id");
		fq.setValue(projectId);

		sar.addMustCriteria(fq);

		if (sprintId != null)
		{
			fq = new FieldQuery();
			fq.setField("sprint.id");
			fq.setValue(sprintId);
			sar.addMustCriteria(fq);
		}

		sar.addSubAggregations(createAggregation("spentRequirementsEffort",
				AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS, "sprint.spentHours.requirements"));
		sar.addSubAggregations(createAggregation("spentDesignEffort", AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS,
				"sprint.spentHours.design"));
		sar.addSubAggregations(createAggregation("spentBuildEffort", AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS,
				"sprint.spentHours.build"));
		sar.addSubAggregations(createAggregation("spentTestEffort", AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS,
				"sprint.spentHours.test"));
		sar.addSubAggregations(createAggregation("spentUnproductiveEffort",
				AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS, "sprint.spentHours.unproductive"));

		return elasticProjSnapshotRepo.getExtendedStats(sar);
	}

	@Override
	public AggregationResponse getProjectEffortExtendedStats(String projectId, String sprintId, String fromDate, String toDate)
	{
		SimpleAggregationRequest sar = new SimpleAggregationRequest();
		sar.setName(projectId + ".EffortExtendedStats");
		sar.setFromDate(fromDate);
		sar.setToDate(toDate);
		sar.setDateField("logDate");

		FieldQuery fq = new FieldQuery();
		fq.setField("project.id");
		fq.setValue(projectId);

		sar.addMustCriteria(fq);

		if (sprintId != null)
		{
			fq = new FieldQuery();
			fq.setField("sprint.id");
			fq.setValue(sprintId);
			sar.addMustCriteria(fq);
		}
		
		sar.addSubAggregations(createAggregation("SpentEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"SpentEffort",
				"doc['sprint.spentHours.build']+doc['sprint.spentHours.test']+doc['sprint.spentHours.design']+doc['sprint.spentHours.support']+doc['sprint.spentHours.requirements']+doc['sprint.spentHours.unproductive']"));

		sar.addSubAggregations(createAggregation("RemainingEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"RemainingEffort",
				"doc['sprint.remainingHours.build']+doc['sprint.remainingHours.test']+doc['sprint.remainingHours.design']+doc['sprint.remainingHours.support']+doc['sprint.remainingHours.requirements']"));

		sar.addSubAggregations(createAggregation("EstimatedEffort", AggregationBean.AGGREGATION_TYPE_MAX,
				"EstimatedEffort",
				"doc['sprint.estimatedHours.build']+doc['sprint.estimatedHours.test']+doc['sprint.estimatedHours.design']+doc['sprint.estimatedHours.support']+doc['sprint.estimatedHours.requirements']"));
		return elasticProjSnapshotRepo.getExtendedStats(sar);
	}
	
	@Override
	public AggregationResponse getProjectBurnDownDateHistogram(String projectId, String sprintId, String fromDate,
			String toDate, String interval)
	{
		DateHistogramRequest aggRequest = new DateHistogramRequest();
		aggRequest.setName(projectId + ".EffortHistogram");
		aggRequest.setFromDate(fromDate);
		aggRequest.setMinDocCount(1);
		aggRequest.setToDate(toDate);
		aggRequest.setInterval(interval);
		aggRequest.setDateField("logDate");

		FieldQuery fq = new FieldQuery();
		fq.setField("project.id");
		fq.setValue(projectId);

		aggRequest.addMustCriteria(fq);

		if (sprintId != null)
		{
			fq = new FieldQuery();
			fq.setField("sprint.id");
			fq.setValue(sprintId);
			aggRequest.addMustCriteria(fq);
		}

		aggRequest.addSubAggregations(createAggregation("SpentEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"SpentEffort",
				"doc['sprint.spentHours.build']+doc['sprint.spentHours.test']+doc['sprint.spentHours.design']+doc['sprint.spentHours.support']+doc['sprint.spentHours.requirements']+doc['sprint.spentHours.unproductive']"));

		aggRequest.addSubAggregations(createAggregation("RemainingEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"RemainingEffort",
				"doc['sprint.remainingHours.build']+doc['sprint.remainingHours.test']+doc['sprint.remainingHours.design']+doc['sprint.remainingHours.support']+doc['sprint.remainingHours.requirements']"));

		aggRequest.addSubAggregations(createAggregation("EstimatedEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"EstimatedEffort",
				"doc['sprint.estimatedHours.build']+doc['sprint.estimatedHours.test']+doc['sprint.estimatedHours.design']+doc['sprint.estimatedHours.support']+doc['sprint.estimatedHours.requirements']"));

		return elasticProjSnapshotRepo.getDateHistogramForProject(aggRequest);

	}

	@Override
	public AggregationResponse getProjectSpentEffortDateHistogram(String projectId, String sprintId, String fromDate,
			String toDate, String interval)
	{
		DateHistogramRequest aggRequest = new DateHistogramRequest();
		aggRequest.setName(projectId + ".EffortHistogram");
		aggRequest.setFromDate(fromDate);
		aggRequest.setMinDocCount(1);
		aggRequest.setToDate(toDate);
		aggRequest.setInterval(interval);
		aggRequest.setDateField("logDate");

		FieldQuery fq = new FieldQuery();
		fq.setField("project.id");
		fq.setValue(projectId);

		aggRequest.addMustCriteria(fq);

		if (sprintId != null)
		{
			fq = new FieldQuery();
			fq.setField("sprint.id");
			fq.setValue(sprintId);
			aggRequest.addMustCriteria(fq);
		}

		// TODO aggRequest.setTimeZone(timeZone);

		aggRequest.addSubAggregations(createAggregation("spentRequirementsEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"sprint.spentHours.requirements"));
		aggRequest.addSubAggregations(createAggregation("spentDesignEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"sprint.spentHours.design"));
		aggRequest.addSubAggregations(
				createAggregation("spentBuildEffort", AggregationBean.AGGREGATION_TYPE_SUM, "sprint.spentHours.build"));
		aggRequest.addSubAggregations(
				createAggregation("spentTestEffort", AggregationBean.AGGREGATION_TYPE_SUM, "sprint.spentHours.test"));
		aggRequest.addSubAggregations(
				createAggregation("spentSupportEffort", AggregationBean.AGGREGATION_TYPE_SUM, "sprint.spentHours.support"));
		aggRequest.addSubAggregations(
				createAggregation("spentUnproductiveEffort", AggregationBean.AGGREGATION_TYPE_SUM, "sprint.spentHours.unproductive"));		
		
		aggRequest.addSubAggregations(createAggregation("TotalSpentEffort", AggregationBean.AGGREGATION_TYPE_SUM,
				"TotalSpentEffort",
				"doc['sprint.spentHours.build']+doc['sprint.spentHours.test']+doc['sprint.spentHours.design']+doc['sprint.spentHours.support']+doc['sprint.spentHours.requirements']+doc['sprint.spentHours.unproductive']"));

		return elasticProjSnapshotRepo.getDateHistogramForProject(aggRequest);
	}

	private AggregationBean createAggregation(String name, String type, String field)
	{
		AggregationBean ab = new AggregationBean();
		ab.setName(name);
		ab.setType(type);
		ab.setField(field);
		return ab;
	}

	private AggregationBean createAggregation(String name, String type, String scriptName, String scriptText)
	{
		AggregationBean ab = new AggregationBean();
		ab.setName(name);
		ab.setType(type);
		ScriptBean sb = new ScriptBean();
		sb.setLanguage("expression");
		sb.setName(scriptName);
		sb.setScriptText(scriptText);
		ab.setScript(sb);
		return ab;
	}
}
