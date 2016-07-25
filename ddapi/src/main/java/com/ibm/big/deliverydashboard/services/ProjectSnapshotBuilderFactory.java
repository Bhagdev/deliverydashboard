package com.ibm.big.deliverydashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProjectSnapshotBuilderFactory
{

	@Autowired
	ApplicationContext context;
	
	public ProjectSnapshotBuilder getBuilderInstance()
	{
		return context.getBean(ProjectSnapshotBuilder.class);
	}

	
	
}
