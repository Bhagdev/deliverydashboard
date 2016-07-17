package com.ibm.big.deliverydashboard.services;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

public interface ProjectService
{
	public Project createProject(Project project);
	public ProjectSnapshot createProjectSnapshot(ProjectSnapshot ps);
	public Project updateProject(Project project);

}
