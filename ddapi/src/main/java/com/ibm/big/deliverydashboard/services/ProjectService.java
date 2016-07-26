package com.ibm.big.deliverydashboard.services;

import java.util.List;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public interface ProjectService
{
	public Project createProject(Project project);
	public ProjectSnapshot createProjectSnapshot(ProjectSnapshot ps);
	public Project updateProject(Project project);
	public List<ProjectSnapshot> getProjectSnapshotsById(String id, String fromDate, String toDate);


}
