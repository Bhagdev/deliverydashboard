package com.ibm.big.deliverydashboard.ddcommon.analysis;

import java.util.LinkedList;
import java.util.List;

public class AggregationResponse
{
	List<AggregationBucket> buckets;

	public List<AggregationBucket> getBuckets()
	{
		return buckets;
	}

	public void setBuckets(List<AggregationBucket> buckets)
	{
		this.buckets = buckets;
	}

	public void addBuckets(AggregationBucket bucket)
	{
		if (bucket != null)
		{
			if (buckets == null)
			{
				buckets = new LinkedList<>();
			}

			buckets.add(bucket);
		}
	}
}
