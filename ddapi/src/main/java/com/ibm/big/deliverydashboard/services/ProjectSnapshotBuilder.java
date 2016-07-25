package com.ibm.big.deliverydashboard.services;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.big.deliverydashboard.dao.mongo.MongoProjectRepository;
import com.ibm.big.deliverydashboard.dao.mongo.MongoUserRepository;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.TeamInfo;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.TeamMember;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.SkillTag;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProjectSnapshotBuilder
{
	private static final Logger logger = LogManager.getLogger(ProjectSnapshotBuilder.class);

	@Autowired
	MongoProjectRepository mongoProjRepo;

	@Autowired
	MongoUserRepository mongoUserRepo;

	public ProjectSnapshotBuilder initializeSnapshot(ProjectSnapshot ps)
	{
		ps.setId(UUID.randomUUID().toString());
		return this;
	}

	public ProjectSnapshotBuilder buildProject(ProjectSnapshot ps)
	{
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

		ps.setProject(p);
		ps.getSprint().setId(ps.getProject().getId() + p.getCurrentSprint());
		return this;
	}

	public ProjectSnapshotBuilder buildTeamInfo(ProjectSnapshot ps)
	{
		TeamInfo ti = new TeamInfo();
		double totalExp = 0;
		int count = 0;
		for (Iterator<TeamMember> iterator = ps.getSprint().getTeamMembers().iterator(); iterator.hasNext();)
		{
			TeamMember tm = iterator.next();
			User u = mongoUserRepo.findByEmail(tm.getUser().getEmail());
			if (u == null)
			{
				throw new IllegalArgumentException("Invalid Team Member Specified. User with email: "
						+ tm.getUser().getEmail() + " doesn't exist");
			}

			count++;
//			 u.setPassword(null);
//			 tm.setUser(u);

			try
			{
				if (u.getCareerStartDate() != null)
				{
					Date date = ProjectSnapshot.DATE_FORMAT.parse(u.getCareerStartDate());
					Date today = new Date();
					totalExp += (today.getTime() - date.getTime()) / (1000 * 60 * 60 * 24 * 30 * 12);
				}
			} catch (Exception e)
			{
				logger.error("error calculating yrs of exp", e);
			}

			Set<SkillTag> sts = u.getTags();
			if (sts != null)
			{
				for (SkillTag skillTag : sts)
				{
					ti.addSkillsMatrix(skillTag.getName());
				}
			}

			String teamRole = tm.getRole();
			ti.addTeamRoleCount(teamRole);
		}

		if (count > 0)
		{
			ti.setAvgYearsOfExperience(totalExp / count);
			ti.setTeamStrength(count);
		}

		ps.getSprint().setTeamInfo(ti);

		return this;
	}
}
