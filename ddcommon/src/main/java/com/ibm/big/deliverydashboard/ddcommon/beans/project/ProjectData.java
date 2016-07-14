package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import java.util.Date;
import java.util.List;

public class ProjectData
{

	String id;
	Date logDate;
	Project project;
	List<TeamMember> teamMembers;
	double effortSpentInHours;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Date getLogDate()
	{
		return logDate;
	}

	public void setLogDate(Date logDate)
	{
		this.logDate = logDate;
	}

	public List<TeamMember> getTeamMembers()
	{
		return teamMembers;
	}

	public void setTeamMembers(List<TeamMember> teamMembers)
	{
		this.teamMembers = teamMembers;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

	public double getEffortSpentInHours()
	{
		return effortSpentInHours;
	}

	public void setEffortSpentInHours(double effortSpentInHours)
	{
		this.effortSpentInHours = effortSpentInHours;
	}

}
