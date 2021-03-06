package com.ibm.big.deliverydashboard.ddcommon.analysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public abstract class AggregationRequest
{

	private String name;
	private List<AggregationBean> subAggregations;
	List<FieldQuery> mustCriteria;
	String dateField;
	String timeZone;
	String fromDate;
	String toDate;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDateField()
	{
		return dateField;
	}

	public void setDateField(String dateField)
	{
		this.dateField = dateField;
	}

	public String getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(String timeZone)
	{
		this.timeZone = timeZone;
	}

	public String getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate()
	{
		return toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public List<AggregationBean> getSubAggregations()
	{
		return subAggregations;
	}

	public void setSubAggregations(List<AggregationBean> subaggregations)
	{
		this.subAggregations = subaggregations;
	}

	public void addSubAggregations(AggregationBean ab)
	{
		if (subAggregations == null)
		{
			subAggregations = new ArrayList<>();
		}
		subAggregations.add(ab);
	}

	public List<FieldQuery> getMustCriteria()
	{
		return mustCriteria;
	}

	public void setMustCriteria(List<FieldQuery> mustCriteria)
	{
		this.mustCriteria = mustCriteria;
	}

	public void addMustCriteria(FieldQuery query)
	{
		if (mustCriteria == null)
		{
			mustCriteria = new LinkedList<>();
		}
		mustCriteria.add(query);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AggregationRequest other = (AggregationRequest) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
