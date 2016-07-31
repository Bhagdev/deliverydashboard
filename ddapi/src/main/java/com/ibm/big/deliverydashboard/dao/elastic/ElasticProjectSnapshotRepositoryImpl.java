package com.ibm.big.deliverydashboard.dao.elastic;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.avg;
import static org.elasticsearch.search.aggregations.AggregationBuilders.dateHistogram;
import static org.elasticsearch.search.aggregations.AggregationBuilders.extendedStats;
import static org.elasticsearch.search.aggregations.AggregationBuilders.sum;
import static org.elasticsearch.search.aggregations.AggregationBuilders.min;
import static org.elasticsearch.search.aggregations.AggregationBuilders.max;
import static org.elasticsearch.search.aggregations.AggregationBuilders.count;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram.Bucket;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationBean;
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationBucket;
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationRequest;
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;
import com.ibm.big.deliverydashboard.ddcommon.analysis.DateHistogramRequest;
import com.ibm.big.deliverydashboard.ddcommon.analysis.FieldQuery;
import com.ibm.big.deliverydashboard.ddcommon.analysis.SimpleAggregationRequest;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public class ElasticProjectSnapshotRepositoryImpl implements ElasticProjectSnapshotRepositoryCustom
{
	private static final Logger logger = LogManager.getLogger(ElasticProjectSnapshotRepositoryImpl.class);

	@Autowired
	ElasticsearchOperations elasticTemplate;

	@Override
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId, Integer page, Integer limit)
	{
		MatchQueryBuilder mqb = matchQuery("project.id", projectId);

		QueryBuilder qb = boolQuery().must(mqb);

		NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
				.withSearchType(SearchType.DFS_QUERY_THEN_FETCH).withTypes("projectsnapshot").withQuery(qb)
				.withSort(SortBuilders.fieldSort("logDate").order(SortOrder.DESC));

		if (page != null && limit != null)
		{
			nsqb = nsqb.withPageable(new PageRequest(page, limit));
		}

		SearchQuery search = nsqb.build();
		logger.debug("search query: " + search);
		return elasticTemplate.queryForList(search, ProjectSnapshot.class);
	}

	@Override
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId, String fromDate, String toDate,
			Integer page, Integer limit)
	{
		List<ProjectSnapshot> response = null;

		try
		{
			long fromEpoch = ProjectSnapshot.DATE_FORMAT.parse(fromDate).getTime();
			long toEpoch = ProjectSnapshot.DATE_FORMAT.parse(toDate).getTime();

			MatchQueryBuilder mqb = matchQuery("project.id", projectId);

			QueryBuilder qb = boolQuery().must(mqb)
					.filter(rangeQuery("logDate").gte(fromEpoch).lte(toEpoch).format("epoch_millis"));

			NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
					.withSearchType(SearchType.DFS_QUERY_THEN_FETCH).withTypes("projectsnapshot").withQuery(qb)
					.withSort(SortBuilders.fieldSort("logDate").order(SortOrder.DESC));

			if (page != null && limit != null)
			{
				nsqb = nsqb.withPageable(new PageRequest(page, limit));
			}

			SearchQuery search = nsqb.build();
			logger.debug("search query: " + search);
			response = elasticTemplate.queryForList(search, ProjectSnapshot.class);
		} catch (ParseException e)
		{
			logger.error("error occurred", e);
		}

		return response;
	}

	private QueryBuilder buildQuery(AggregationRequest ar)
	{
		DateTime dtFrom = DateTime.parse(ar.getFromDate(),
				DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));
		DateTime dtTo = DateTime.parse(ar.getToDate(),
				DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));

		long fromEpoch = dtFrom.getMillis();
		long toEpoch = dtTo.getMillis();

		if (ar.getMustCriteria() == null)
		{
			return null;
		}

		BoolQueryBuilder bqb = boolQuery();
		for (Iterator<FieldQuery> iterator = ar.getMustCriteria().iterator(); iterator.hasNext();)
		{
			FieldQuery fq = iterator.next();
			MatchQueryBuilder mqb = matchQuery(fq.getField(), fq.getValue());
			bqb.must(mqb);
		}

		return bqb.filter(rangeQuery(ar.getDateField()).gte(fromEpoch).lte(toEpoch).format("epoch_millis"));

	}

	private AbstractAggregationBuilder buildAggregation(AggregationBean ab)
	{
		AbstractAggregationBuilder builder = null;
		if (AggregationBean.AGGREGATION_TYPE_EXTENDEDSTATS.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = extendedStats(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = extendedStats(ab.getName()).script(script);
			}
		} else if (AggregationBean.AGGREGATION_TYPE_SUM.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = sum(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = sum(ab.getName()).script(script);
			}
		} else if (AggregationBean.AGGREGATION_TYPE_AVERAGE.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = avg(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = avg(ab.getName()).script(script);
			}
		} else if (AggregationBean.AGGREGATION_TYPE_MAX.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = max(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = max(ab.getName()).script(script);
			}
		} else if (AggregationBean.AGGREGATION_TYPE_MIN.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = min(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = min(ab.getName()).script(script);
			}
		} else if (AggregationBean.AGGREGATION_TYPE_COUNT.equals(ab.getType()))
		{
			if (ab.getField() != null)
			{
				builder = count(ab.getName()).field(ab.getField());
			} else if (ab.getScript() != null)
			{
				Script script = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
						ab.getScript().getLanguage(), null);
				logger.debug("creating script - " + script);
				builder = count(ab.getName()).script(script);
			}
		}
		return builder;
	}

	private AggregationBucket buildAggregationBucket(AggregationBean ab, Aggregations aggs)
	{
		Object obj = aggs.get(ab.getName());
		if (obj == null)
		{
			return null;
		}

		AggregationBucket responseBucket = new AggregationBucket();
		responseBucket.setKey_as_string(ab.getName());

		if (obj instanceof ExtendedStats)
		{
			ExtendedStats stats = (ExtendedStats) obj;
			AggregationBean responseAggr = new AggregationBean();

			responseAggr.setName("min");
			responseAggr.setValue(stats.getMin());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("max");
			responseAggr.setValue(stats.getMax());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("avg");
			responseAggr.setValue(stats.getAvg());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("sum");
			responseAggr.setValue(stats.getSum());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("count");
			responseAggr.setValue(stats.getCount());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("stdDeviation");
			responseAggr.setValue(stats.getStdDeviation());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("sumOfSquares");
			responseAggr.setValue(stats.getSumOfSquares());
			responseBucket.addAggregations(responseAggr);

			responseAggr = new AggregationBean();
			responseAggr.setName("variance");
			responseAggr.setValue(stats.getVariance());
			responseBucket.addAggregations(responseAggr);
		} else if (obj instanceof Max)
		{
			Max max = (Max) obj;
			AggregationBean responseAggr = new AggregationBean();
			responseAggr.setName("max");
			responseAggr.setValue(max.getValue());
			responseBucket.addAggregations(responseAggr);
		} else if (obj instanceof Min)
		{
			Min min = (Min) obj;
			AggregationBean responseAggr = new AggregationBean();
			responseAggr.setName("min");
			responseAggr.setValue(min.getValue());
			responseBucket.addAggregations(responseAggr);
		} else if (obj instanceof Sum)
		{
			Sum sum = (Sum) obj;
			AggregationBean responseAggr = new AggregationBean();
			responseAggr.setName("sum");
			responseAggr.setValue(sum.getValue());
			responseBucket.addAggregations(responseAggr);
		} else if (obj instanceof Avg)
		{
			Avg avg = (Avg) obj;
			AggregationBean responseAggr = new AggregationBean();
			responseAggr.setName("avg");
			responseAggr.setValue(avg.getValue());
			responseBucket.addAggregations(responseAggr);
		} else if (obj instanceof ValueCount)
		{
			ValueCount count = (ValueCount) obj;
			AggregationBean responseAggr = new AggregationBean();
			responseAggr.setName("count");
			responseAggr.setValue(count.getValue());
			responseBucket.addAggregations(responseAggr);
		}
		return responseBucket;
	}

	@Override
	public AggregationResponse getExtendedStats(SimpleAggregationRequest sar)
	{
		logger.debug("SimpleAggregationRequest = " + sar);
		AggregationResponse aggregationResponse = null;

		QueryBuilder qb = buildQuery(sar);

		logger.debug("Simple Aggregation Query = " + qb.toString());

		if (sar.getSubAggregations() == null || sar.getSubAggregations().isEmpty())
		{
			return null;
		}

		NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
				.withTypes("projectsnapshot").withQuery(qb);

		for (Iterator<AggregationBean> iterator = sar.getSubAggregations().iterator(); iterator.hasNext();)
		{
			AbstractAggregationBuilder builder = null;
			AggregationBean ab = iterator.next();
			builder = buildAggregation(ab);
			if (builder != null)
			{
				nsqb = nsqb.addAggregation(builder);
			}
		}

		logger.debug("Simple Aggregation Query = " + nsqb.build().getAggregations().toString());

		Aggregations aggs = elasticTemplate.query(nsqb.build(), new ResultsExtractor<Aggregations>()
		{
			@Override
			public Aggregations extract(SearchResponse response)
			{
				return response.getAggregations();
			}
		});

		aggregationResponse = new AggregationResponse();

		for (Iterator<AggregationBean> iterator = sar.getSubAggregations().iterator(); iterator.hasNext();)
		{
			AggregationBean ab = iterator.next();
			AggregationBucket responseBucket = buildAggregationBucket(ab, aggs);

			if (responseBucket != null)
			{
				aggregationResponse.addBuckets(responseBucket);
			} else
			{
				logger.warn("Response Bucket Retrieved is NULL");
			}
		}
		return aggregationResponse;
	}

	@Override
	public AggregationResponse getDateHistogramForProject(DateHistogramRequest dhr)
	{
		logger.debug("DateHistogram Request = " + dhr);
		AggregationResponse aggregationResponse = null;

		DateTime dtFrom = DateTime.parse(dhr.getFromDate(),
				DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));
		DateTime dtTo = DateTime.parse(dhr.getToDate(),
				DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));

		QueryBuilder qb = buildQuery(dhr);

		logger.debug("Datehistogram Query = " + qb.toString());

		DateHistogramBuilder dhb = dateHistogram(dhr.getName()).field(dhr.getDateField())
				.interval(new DateHistogramInterval(dhr.getInterval()))
				.timeZone(Calendar.getInstance().getTimeZone().getID()).minDocCount(1).extendedBounds(dtFrom, dtTo);

		if (dhr.getSubAggregations() != null)
		{
			for (Iterator<AggregationBean> iterator = dhr.getSubAggregations().iterator(); iterator.hasNext();)
			{
				AggregationBean ab = iterator.next();
				logger.debug("setting aggregation - " + ab);

				dhb = dhb.subAggregation(buildAggregation(ab));
//				if (AggregationBean.AGGREGATION_TYPE_SUM.equals(ab.getType()))
//				{
//					logger.debug("aggregation - " + ab.getName() + " field: " + ab.getField() + " script: "
//							+ ab.getScript());
//
//					if (ab.getField() != null)
//					{
//						dhb = dhb.subAggregation(sum(ab.getName()).field(ab.getField()));
//					} else if (ab.getScript() != null)
//					{
//						Script s = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
//								ab.getScript().getLanguage(), null);
//						logger.debug("creating script - " + s);
//						dhb = dhb.subAggregation(sum(ab.getName()).script(s));
//					}
//				} else if (AggregationBean.AGGREGATION_TYPE_AVERAGE.equals(ab.getType()))
//				{
//					if (ab.getField() != null)
//					{
//						dhb = dhb.subAggregation(avg(ab.getName()).field(ab.getField()));
//					} else if (ab.getScript() != null)
//					{
//						Script s = new Script(ab.getScript().getScriptText(), ScriptService.ScriptType.INLINE,
//								ab.getScript().getLanguage(), null);
//						logger.debug("creating script - " + s);
//						dhb = dhb.subAggregation(avg(ab.getName()).script(s));
//					}
//				}
			}
		}

		NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
				.withTypes("projectsnapshot").withQuery(qb).addAggregation(dhb);

		Aggregations aggs = elasticTemplate.query(nsqb.build(), new ResultsExtractor<Aggregations>()
		{
			@Override
			public Aggregations extract(SearchResponse response)
			{
				return response.getAggregations();
			}
		});

		aggregationResponse = new AggregationResponse();

		InternalHistogram<Bucket> internalHistogram = aggs.get(dhr.getName());
		List<Bucket> fetchedBuckets = internalHistogram.getBuckets();

		for (Iterator<Bucket> iterator = fetchedBuckets.iterator(); iterator.hasNext();)
		{
			Bucket fetchedBucket = iterator.next();
			logger.debug("key - " + fetchedBucket.getKeyAsString());

			AggregationBucket responseBucket = new AggregationBucket();

			// responseBucket.setKey(fetchedBucket.getKey());
			responseBucket.setDoc_count(fetchedBucket.getDocCount());
			responseBucket.setKey_as_string(fetchedBucket.getKeyAsString());

			if (dhr.getSubAggregations() != null)
			{
				for (Iterator<AggregationBean> itr = dhr.getSubAggregations().iterator(); itr.hasNext();)
				{
					AggregationBean ab = itr.next();
					Aggregation agg = fetchedBucket.getAggregations().get(ab.getName());
					if (agg != null)
					{
						logger.debug("aggregation name - " + agg.getName() + " value - " + agg.getProperty("value"));
						AggregationBean resAggBean = new AggregationBean();
						resAggBean.setName(agg.getName());
						resAggBean.setValue(agg.getProperty("value"));
						responseBucket.addAggregations(resAggBean);
					} else
					{
						logger.warn("Aggregation not found - " + ab.getName());
					}
				}
			}
			aggregationResponse.addBuckets(responseBucket);
		}

		return aggregationResponse;
	}
}
