package com.ibm.big.deliverydashboard.dao.elastic;

import java.text.ParseException;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public class ElasticProjectSnapshotRepositoryImpl implements ElasticProjectSnapshotRepositoryCustom
{
	private static final Logger logger = LogManager.getLogger(ElasticProjectSnapshotRepositoryImpl.class);

	@Autowired
	ElasticsearchOperations elasticTemplate;
	
	@Override
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId)
	{
		MatchQueryBuilder mqb = matchQuery("project.id", projectId);
		
		QueryBuilder qb = boolQuery().must(mqb);
		SearchQuery search = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
				.withQuery(qb)
				.withSort(SortBuilders.fieldSort("logDate").order(SortOrder.DESC))
				.build();
		
		logger.debug("search query: " + qb);
		return elasticTemplate.queryForList(search, ProjectSnapshot.class);
	}

	@Override
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId, String fromDate, String toDate)
	{
		List<ProjectSnapshot> response = null;
		
		try
		{
			long fromEpoch = ProjectSnapshot.DATE_FORMAT.parse(fromDate).getTime();
			long toEpoch = ProjectSnapshot.DATE_FORMAT.parse(toDate).getTime();
			
			MatchQueryBuilder mqb = matchQuery("project.id", projectId);
			
			QueryBuilder qb = boolQuery().must(mqb).filter(rangeQuery("logDate").gte(fromEpoch).lte(toEpoch).format("epoch_millis"));
			
			logger.debug(qb.toString());
			
			SearchQuery search = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
					.withQuery(qb)
					.withSort(SortBuilders.fieldSort("logDate").order(SortOrder.DESC))
					.build();
			
			logger.debug("search query: " + search);
			response = elasticTemplate.queryForList(search, ProjectSnapshot.class);
		} catch (ParseException e)
		{
			logger.error("error occurred", e);
		}
		
		return response;
	}
}
