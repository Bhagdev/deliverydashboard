package com.ibm.big.deliverydashboard.ddcommon.analysis;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class AggregationBucket
{
	private List<AggregationBean> aggregations;
	private String key_as_string;
	private Object key;
	private long doc_count;

	public List<AggregationBean> getAggregations()
	{
		return aggregations;
	}

	public void setAggregations(List<AggregationBean> aggregations)
	{
		this.aggregations = aggregations;
	}

	public void addAggregations(AggregationBean aggregation)
	{
		if (aggregations == null)
		{
			aggregations = new LinkedList<>();
		}
		aggregations.add(aggregation);
	}

	public String getKey_as_string()
	{
		return key_as_string;
	}

	public void setKey_as_string(String key_as_string)
	{
		this.key_as_string = key_as_string;
	}

	public Object getKey()
	{
		return key;
	}

	public void setKey(Object key)
	{
		this.key = key;
	}

	public long getDoc_count()
	{
		return doc_count;
	}

	public void setDoc_count(long doc_count)
	{
		this.doc_count = doc_count;
	}
}
