package com.ibm.big.deliverydashboard.dao.elastic;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
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
		SearchQuery search = new NativeSearchQueryBuilder().withIndices("projectsnapshots")
				.withQuery(mqb)
				.withSort(SortBuilders.fieldSort("logDate").order(SortOrder.DESC))
				.build();
		
		logger.debug("search query: " + search);
		return elasticTemplate.queryForList(search, ProjectSnapshot.class);
	}

}
