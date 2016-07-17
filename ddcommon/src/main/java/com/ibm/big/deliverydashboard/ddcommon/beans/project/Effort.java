package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class Effort
{

	public static final String REQUIREMENTS_EFFORT_TYPE = "requirements";
	public static final String DESIGN_EFFORT_TYPE = "design";
	public static final String BUILD_EFFORT_TYPE = "build";
	public static final String TEST_EFFORT_TYPE = "test";
	public static final String SUPPORT_EFFORT_TYPE = "support";
	
	public static final String DISCUSSION_EFFORT_SUBTYPE = "Discussion";
	public static final String WAIT_EFFORT_SUBTYPE = "wait";
	
	String type;
	String subType;
	double effortInHours;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getSubType()
	{
		return subType;
	}

	public void setSubType(String subType)
	{
		this.subType = subType;
	}

	public double getEffortInHours()
	{
		return effortInHours;
	}

	public void setEffortInHours(double effortInHours)
	{
		this.effortInHours = effortInHours;
	}
}
