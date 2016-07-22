package com.ibm.big.deliverydashboard.dao.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public interface ElasticProjectSnapshotRepository extends ElasticsearchRepository<ProjectSnapshot, String>
{
	public ProjectSnapshot findTopByProjectIdOrderByLogDateDesc(String id);
	
	public List<ProjectSnapshot> findByProjectId(String id);
}
