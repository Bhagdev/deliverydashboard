package com.ibm.big.deliverydashboard.dao.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;

public interface MongoProjectRepository  extends MongoRepository<Project, String>
{
	public Project findByName(String name);
}
