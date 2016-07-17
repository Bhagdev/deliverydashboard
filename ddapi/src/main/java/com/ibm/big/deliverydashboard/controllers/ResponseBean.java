package com.ibm.big.deliverydashboard.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class ResponseBean <T>
{
	public static final String SUCCESS_STATUS = "success";
	public static final String ERROR_STATUS = "error";
	
	T response;
	String status;
	String errorMessage;
	String version;
	
	public T getResponse()
	{
		return response;
	}
	public void setResponse(T response)
	{
		this.response = response;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	
}
