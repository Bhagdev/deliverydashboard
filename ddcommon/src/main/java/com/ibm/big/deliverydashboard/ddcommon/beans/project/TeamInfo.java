package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import java.util.HashMap;
import java.util.Map;

public class TeamInfo
{
	private int teamStrength;
	private double avgYearsOfExperience;
	private Map<String, Integer> skillsMatrix;
	private Map<String, Integer> teamRoleCount;
	
	public int getTeamStrength()
	{
		return teamStrength;
	}
	public void setTeamStrength(int teamStrength)
	{
		this.teamStrength = teamStrength;
	}
	public double getAvgYearsOfExperience()
	{
		return avgYearsOfExperience;
	}
	public void setAvgYearsOfExperience(double avgYearsOfExperience)
	{
		this.avgYearsOfExperience = avgYearsOfExperience;
	}
	public Map<String, Integer> getSkillsMatrix()
	{
		return skillsMatrix;
	}
	public void setSkillsMatrix(Map<String, Integer> skillsMatrix)
	{
		this.skillsMatrix = skillsMatrix;
	}

	public void addSkillsMatrix (String skill)
	{
		if (skillsMatrix == null)
		{
			skillsMatrix = new HashMap<>();
		}
		
		if (skillsMatrix.get(skill) == null)
		{
			skillsMatrix.put(skill, 1);
		}
		else
		{
			skillsMatrix.put(skill, skillsMatrix.get(skill) + 1);
		}
	}
	public Map<String, Integer> getTeamRoleCount()
	{
		return teamRoleCount;
	}
	public void setTeamRoleCount(Map<String, Integer> teamRoleCount)
	{
		this.teamRoleCount = teamRoleCount;
	}
	
	public void addTeamRoleCount(String teamRole)
	{
		if (teamRoleCount == null)
		{
			teamRoleCount = new HashMap<>();
		}
		
		if (teamRoleCount.get(teamRole) == null)
		{
			teamRoleCount.put(teamRole, 1);
		}
		else
		{
			teamRoleCount.put(teamRole, teamRoleCount.get(teamRole) + 1);
		}
	}
	
}
