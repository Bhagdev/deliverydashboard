package com.ibm.big.deliverydashboard.dao.elastic;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.avg;
import static org.elasticsearch.search.aggregations.AggregationBuilders.dateHistogram;
import static org.elasticsearch.search.aggregations.AggregationBuilders.sum;

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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram.Bucket;
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
import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;
import com.ibm.big.deliverydashboard.ddcommon.analysis.DateHistogramRequest;
import com.ibm.big.deliverydashboard.ddcommon.analysis.FieldQuery;
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

	@Override
	public AggregationResponse getDateHistogramForProject(DateHistogramRequest dhr)
	{
		AggregationResponse aggregationResponse = null;
		try
		{
			DateTime dtFrom = DateTime.parse(dhr.getFromDate(),
					DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));
			DateTime dtTo = DateTime.parse(dhr.getToDate(),
					DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));

			dtFrom.getMillis();

			long fromEpoch = dtFrom.getMillis();
			long toEpoch = dtTo.getMillis();

			if (dhr.getMustCriteria() == null)
			{
				return null;
			}

			BoolQueryBuilder bqb = boolQuery();
			for (Iterator<FieldQuery> iterator = dhr.getMustCriteria().iterator(); iterator.hasNext();)
			{
				FieldQuery fq = iterator.next();
				MatchQueryBuilder mqb = matchQuery(fq.getField(), fq.getValue());
				bqb.must(mqb);
			}

			QueryBuilder qb = bqb
					.filter(rangeQuery(dhr.getDateField()).gte(fromEpoch).lte(toEpoch).format("epoch_millis"));

			logger.debug("Datehistogram Query = " + qb.toString());

			DateHistogramBuilder dhb = dateHistogram(dhr.getName()).field(dhr.getDateField())
					.interval(new DateHistogramInterval(dhr.getInterval()))
					.timeZone(Calendar.getInstance().getTimeZone().getID()).minDocCount(1).extendedBounds(dtFrom, dtTo);

			if (dhr.getSubAggregations() != null)
			{
				for (Iterator<AggregationBean> iterator = dhr.getSubAggregations().iterator(); iterator.hasNext();)
				{
					AggregationBean ab = iterator.next();
					logger.debug("setting aggregation - " + ab.getName() + " field = " + ab.getField());

					if (AggregationBean.AGGREGATION_TYPE_SUM.equals(ab.getType()))
					{
						if (ab.getField() != null)
						{
							dhb = dhb.subAggregation(sum(ab.getName()).field(ab.getField()));
						} else if (ab.getScript() != null)
						{
							dhb = dhb.subAggregation(sum(ab.getName()).script(new Script(ab.getScript().getScriptText(),
									ScriptService.ScriptType.INLINE, ab.getScript().getLanguage(), null)));
						}
					}
					if (AggregationBean.AGGREGATION_TYPE_AVERAGE.equals(ab.getType()))
					{
						if (ab.getField() != null)
						{
							dhb = dhb.subAggregation(avg(ab.getName()).field(ab.getField()));
						} else if (ab.getScript() != null)
						{
							dhb = dhb.subAggregation(avg(ab.getName()).script(new Script(ab.getScript().getScriptText(),
									ScriptService.ScriptType.INLINE, ab.getScript().getLanguage(), null)));
						}
					}
				}
			}


			

			NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
					.withTypes("projectsnapshot").withQuery(qb).addAggregation(dhb);
			
			
			logger.debug("Datehistogram Query = " + nsqb.build().getQuery().toString());


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
							logger.debug(
									"aggregation name - " + agg.getName() + " value - " + agg.getProperty("value"));
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

		} catch (Exception e)
		{
			logger.error("exception in aggregation", e);
		}
		return aggregationResponse;
	}
}
