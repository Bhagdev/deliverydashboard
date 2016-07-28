package com.ibm.big.deliverydashboard.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.big.deliverydashboard.dao.elastic.ElasticProjectSnapshotRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoProjectRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoUserRepository;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;

@Service
public class ProjectServiceImpl implements ProjectService
{
	private static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

	@Autowired
	MongoProjectRepository mongoProjRepo;

	@Autowired
	MongoUserRepository mongoUserRepo;

	@Autowired
	ElasticProjectSnapshotRepository elasticProjSnapshotRepo;

	@Autowired
	ProjectSnapshotBuilderFactory psbuilderFactory;

	@Override
	public Project createProject(Project project)
	{
		logger.debug("About to save project: " + project);
		if (project == null)
		{
			throw new IllegalArgumentException("Argument Project can't be null");
		}

		if (project.getId() == null || project.getName() == null || project.getStartDate() == null
				|| project.getEndDate() == null || project.getTechnologies() == null)
		{
			throw new IllegalArgumentException("Missing Mandatory Project Parameters");
		}

		if (mongoProjRepo.findOne(project.getId()) != null)
		{
			throw new IllegalArgumentException("Project Already Exists");
		}

		mongoProjRepo.save(project);
		return project;
	}

	@Override
	public Project updateProject(Project project)
	{
		if (project == null)
		{
			throw new IllegalArgumentException("Project can't be null");
		}

		Project p = mongoProjRepo.findOne(project.getId());

		if (p == null)
		{
			throw new IllegalArgumentException("Project not found");
		}

		if (project.getName() != null)
		{
			p.setName(project.getName());
		}

		if (project.getStartDate() != null)
		{
			p.setStartDate(project.getStartDate());
		}

		if (project.getEndDate() != null)
		{
			p.setEndDate(project.getEndDate());
		}

		if (project.getTechnologies() != null)
		{
			p.setTechnologies(project.getTechnologies());
		}

		if (project.getCurrentSprint() != p.getCurrentSprint())
		{
			p.setCurrentSprint(project.getCurrentSprint());
		}

		mongoProjRepo.save(p);
		return p;
	}

	@Override
	public ProjectSnapshot createProjectSnapshot(ProjectSnapshot ps)
	{
		logger.debug("About to create projectsnapshot: " + ps);

		if (ps == null)
		{
			throw new IllegalArgumentException("Argument ProjectSnapshot can't be null");
		}

		if (ps.getProject() == null || ps.getSprint() == null || ps.getSprint().getTeamMembers() == null)
		{
			throw new IllegalArgumentException("Missing Mandatory ProjectSnapshot Parameters");
		}

		ProjectSnapshotBuilder psb = psbuilderFactory.getBuilderInstance();
		psb.initializeSnapshot(ps).buildProject(ps).buildTeamInfo(ps);
		elasticProjSnapshotRepo.save(ps);

		return ps;
	}

	@Override
	public List<ProjectSnapshot> getProjectSnapshotsById(String id, String fromDate, String toDate, Integer page,
			Integer limit)
	{
		List<ProjectSnapshot> p = null;

		if (fromDate != null && toDate != null)
		{
			p = elasticProjSnapshotRepo.getProjectSnapshotsByProjectId(id, fromDate, toDate, page, limit);
		} 
		else
		{
			p = elasticProjSnapshotRepo.getProjectSnapshotsByProjectId(id, page, limit);
		}
		return p;
	}

}
