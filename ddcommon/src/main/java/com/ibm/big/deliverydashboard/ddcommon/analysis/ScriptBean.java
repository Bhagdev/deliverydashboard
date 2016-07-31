package com.ibm.big.deliverydashboard.ddcommon.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class ScriptBean
{
	String name;
	String scriptText;
	String language;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getScriptText()
	{
		return scriptText;
	}

	public void setScriptText(String scriptText)
	{
		this.scriptText = scriptText;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

}
