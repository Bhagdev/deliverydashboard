package com.ibm.big.deliverydashboard.dao.elastic;

import java.util.List;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public interface ElasticProjectSnapshotRepositoryCustom
{
	public List<ProjectSnapshot> getProjectSnapshotsByProjectId(String projectId);
}
