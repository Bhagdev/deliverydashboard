package com.ibm.big.deliverydashboard.dao.elastic;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.dateHistogram;
import static org.elasticsearch.search.aggregations.AggregationBuilders.sum;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
	public void getDateHistogram(String projectId, String fromDate, String toDate, String subAggType,
			String subAggField)
	{
		// String subAgg, String subAggField
		try
		{
			long fromEpoch = ProjectSnapshot.DATE_FORMAT.parse(fromDate).getTime();
			long toEpoch = ProjectSnapshot.DATE_FORMAT.parse(toDate).getTime();

			MatchQueryBuilder mqb = matchQuery("project.id", projectId);
			QueryBuilder qb = boolQuery().must(mqb)
					.filter(rangeQuery("logDate").gte(fromEpoch).lte(toEpoch).format("epoch_millis"));

			DateTime dt1 = DateTime.parse(fromDate, DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));
			DateTime dt2 = DateTime.parse(toDate, DateTimeFormat.forPattern(ProjectSnapshot.DATE_FORMAT.toPattern()));

			DateHistogramBuilder dhb = dateHistogram("date_histo").field("logDate").interval(DateHistogramInterval.WEEK)
					.timeZone("Asia/Kolkata").minDocCount(1).extendedBounds(dt1, dt2);

			if (AGGREGATION_TYPE_SUM.equals(subAggType))
			{
				dhb = dhb.subAggregation(sum(subAggField).field(subAggField));
			}

			NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
					.withTypes("projectsnapshot").withQuery(qb).addAggregation(dhb);

			SearchQuery search = nsqb.build();

			Aggregations aggs = elasticTemplate.query(search, new ResultsExtractor<Aggregations>()
			{
				@Override
				public Aggregations extract(SearchResponse response)
				{
					return response.getAggregations();
				}
			});

			InternalHistogram<Bucket> agg = aggs.get("date_histo");
			List<Bucket> buckets = agg.getBuckets();

			for (Iterator<Bucket> iterator = buckets.iterator(); iterator.hasNext();)
			{
				Bucket bucket = iterator.next();

				logger.debug("Bucket - " + bucket.toString());
				logger.debug("key - " + bucket.getKeyAsString());
				logger.debug("value - " + bucket.getAggregations().get(subAggField).getProperty("value"));
			}

		} catch (Exception e)
		{
			logger.error("exception in aggregation", e);
		}
	}
}
