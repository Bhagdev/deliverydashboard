package com.ibm.big.deliverydashboard.ddcommon.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class AggregationBean
{
	public static final String AGGREGATION_TYPE_SUM = "sum";
	public static final String AGGREGATION_TYPE_AVERAGE = "avg";
	public static final String AGGREGATION_TYPE_TERMS = "terms";
	public static final String AGGREGATION_TYPE_MIN = "min";
	public static final String AGGREGATION_TYPE_MAX = "max";

	private String type;
	private String name;
	private String field;
	private Object value;
	private ScriptBean script;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ScriptBean getScript()
	{
		return script;
	}

	public void setScript(ScriptBean script)
	{
		this.script = script;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
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
		AggregationBean other = (AggregationBean) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
