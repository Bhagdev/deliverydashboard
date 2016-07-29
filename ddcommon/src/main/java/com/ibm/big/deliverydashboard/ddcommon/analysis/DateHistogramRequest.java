package com.ibm.big.deliverydashboard.ddcommon.analysis;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class DateHistogramRequest extends AggregationRequest
{
	List<FieldQuery> mustCriteria;

	String dateField;
	String interval;
	String timeZone;
	int minDocCount;
	String fromDate;
	String toDate;

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

	public String getDateField()
	{
		return dateField;
	}

	public void setDateField(String field)
	{
		this.dateField = field;
	}

	public String getInterval()
	{
		return interval;
	}

	public void setInterval(String interval)
	{
		this.interval = interval;
	}

	public String getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(String timeZone)
	{
		this.timeZone = timeZone;
	}

	public int getMinDocCount()
	{
		return minDocCount;
	}

	public void setMinDocCount(int i)
	{
		this.minDocCount = i;
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

}
