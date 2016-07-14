package com.ibm.big.deliverydashboard.dao.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
@EnableMongoRepositories(basePackages = "com/ibm/big/deliverydashboard/dao/mongo")
public class MongoConfiguration extends AbstractMongoConfiguration
{
	private static final Logger logger = LogManager.getLogger(MongoConfiguration.class);

	@Value("${mongodb.uri}")
	String uri;

	@Override
	protected String getDatabaseName()
	{
		MongoClientURI mcuri= new MongoClientURI(uri);
		return mcuri.getDatabase();
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception
	{
		logger.debug("Creating Mongo client with uri = " + uri);
		MongoClientURI mcuri = new MongoClientURI(uri);
		return new MongoClient(mcuri);
	}

	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception
	{
		MongoClientURI mcuri = new MongoClientURI(uri);
		return new SimpleMongoDbFactory(mcuri);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception
	{
		return new MongoTemplate(mongoDbFactory());
	}
}
