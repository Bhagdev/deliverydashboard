package com.ibm.big.deliverydashboard.ddcommon.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class DateHistogramRequest extends AggregationRequest
{
	String queryField;
	String queryFieldValue;
	String dateField;
	String interval;
	String timeZone;
	int minDocCount;
	String fromDate;
	String toDate;

	public String getQueryField()
	{
		return queryField;
	}

	public void setQueryField(String queryField)
	{
		this.queryField = queryField;
	}

	public String getQueryFieldValue()
	{
		return queryFieldValue;
	}

	public void setQueryFieldValue(String queryFieldValue)
	{
		this.queryFieldValue = queryFieldValue;
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
