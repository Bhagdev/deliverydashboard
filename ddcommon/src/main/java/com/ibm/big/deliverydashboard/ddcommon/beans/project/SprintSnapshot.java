package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class SprintSnapshot
{
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static String ACTIVE_STATUS = "active";
	public static String INACTIVE_STATUS = "inactive";
	public static String EFFORT_DESIGN = "design";
	public static String EFFORT_BUILD = "build";
	public static String EFFORT_TEST = "test";
	public static String EFFORT_REQUIREMENTS = "requirements";
	public static String EFFORT_SUPPORT = "support";
	public static String EFFORT_UNPRODUCTIVE = "unproductive";

	String id;
	int sprintNumber;

	String status;

	String startDate;
	String endDate;

	int userStoryCount;

	Set<TeamMember> teamMembers;

	Map<String, Double> spentHours;
	Map<String, Double> remainingHours;
	Map<String, Double> estimatedHours;

	Map<String, Double> qualityMetrics;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Set<TeamMember> getTeamMembers()
	{
		return teamMembers;
	}

	public void addTeamMember(TeamMember t)
	{
		if (this.teamMembers == null)
		{
			this.teamMembers = new HashSet<>();
		}
		teamMembers.add(t);
	}

	public void setTeamMembers(Set<TeamMember> teamMembers)
	{
		this.teamMembers = teamMembers;
	}

	public int getSprintNumber()
	{
		return sprintNumber;
	}

	public void setSprintNumber(int sprintNumber)
	{
		this.sprintNumber = sprintNumber;
	}

	public int getUserStoryCount()
	{
		return userStoryCount;
	}

	public void setUserStoryCount(int userStoryCount)
	{
		this.userStoryCount = userStoryCount;
	}

	public Map<String, Double> getSpentHours()
	{
		return spentHours;
	}

	public void setSpentHours(Map<String, Double> spentHours)
	{
		this.spentHours = spentHours;
	}

	public void addSpentHours(String key, Double value)
	{
		if (this.spentHours == null)
		{
			this.spentHours = new HashMap<>();
		}
		this.spentHours.put(key, value);
	}

	public Map<String, Double> getRemainingHours()
	{
		return remainingHours;
	}

	public void setRemainingHours(Map<String, Double> remainingHours)
	{
		this.remainingHours = remainingHours;
	}

	public void addRemainingHours(String key, Double value)
	{
		if (this.remainingHours == null)
		{
			this.remainingHours = new HashMap<>();
		}
		remainingHours.put(key, value);
	}

	public Map<String, Double> getEstimatedHours()
	{
		return estimatedHours;
	}

	public void setEstimatedHours(Map<String, Double> estimatedHours)
	{
		this.estimatedHours = estimatedHours;
	}

	public void addEstimatedHours(String key, Double value)
	{
		if (this.estimatedHours == null)
		{
			this.estimatedHours = new HashMap<>();
		}
		estimatedHours.put(key, value);
	}

	public Map<String, Double> getQualityMetrics()
	{
		return qualityMetrics;
	}

	public void setQualityMetrics(Map<String, Double> qualityMetrics)
	{
		this.qualityMetrics = qualityMetrics;
	}

	public void addQualityMetrics(String key, Double value)
	{
		if (this.qualityMetrics == null)
		{
			this.qualityMetrics = new HashMap<>();
		}
		qualityMetrics.put(key, value);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SprintSnapshot other = (SprintSnapshot) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
