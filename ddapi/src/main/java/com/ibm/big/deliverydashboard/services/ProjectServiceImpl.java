package com.ibm.big.deliverydashboard.services;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.big.deliverydashboard.dao.elastic.ElasticProjectSnapshotRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoProjectRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoUserRepository;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.TeamMember;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

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

		Project p = mongoProjRepo.findOne(ps.getProject().getId());

		if (p == null)
		{
			throw new IllegalArgumentException("Invalid Project Specified");
		}

		if (ps.getProject().getCurrentSprint() != p.getCurrentSprint()
				|| ps.getProject().getCurrentSprint() != ps.getSprint().getSprintNumber())
		{
			throw new IllegalArgumentException("Invalid Project Sprint Specified");
		}
		
		for (Iterator<TeamMember> iterator = ps.getSprint().getTeamMembers().iterator(); iterator.hasNext();)
		{
			TeamMember tm = iterator.next();
			User u = mongoUserRepo.findByEmail(tm.getUser().getEmail());
			if (u == null)
			{
				throw new IllegalArgumentException("Invalid Team Member Specified. User with email: " + tm.getUser().getEmail() + " doesn't exist");
			}
			
			u.setPassword(null);
			tm.setUser(u);
		}

		ps.setId(UUID.randomUUID().toString());
		ps.setProject(p);
		ps.getSprint().setId(ps.getProject().getId() + ps.getProject().getCurrentSprint());
//		ps.setLogDate(ProjectSnapshot.DATE_FORMAT.format(new Date()));

		ProjectSnapshot lastSnapshot = null;
		try
		{
			lastSnapshot = elasticProjSnapshotRepo
					.findTopByProjectIdOrderByLogDateDesc(ps.getProject().getId());
		} catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		if (lastSnapshot == null)
		{
			logger.debug("No Snapshot for the project found");
		}

		elasticProjSnapshotRepo.save(ps);

		return ps;
	}
	
	@Override
	public List<ProjectSnapshot> getProjectSnapshotsById(String id)
	{
		return elasticProjSnapshotRepo.findByProjectId(id);
	}

}
