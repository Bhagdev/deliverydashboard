package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class QualityMetric
{
	public static final String SONAR_TYPE = "Sonar";
	public static final String JUNIT_COVERAGE_TYPE = "Junit";
	public static final String DEFECT_DENSITY_TYPE = "DefectDensity";
	
	String type;
	String subType;
	double value;
	
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
	public double getValue()
	{
		return value;
	}
	public void setValue(double value)
	{
		this.value = value;
	}
	
	
}
