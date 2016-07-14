package com.ibm.big.deliverydashboard.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;
import com.mongodb.WriteResult;

public class MongoUserRepositoryImpl implements MongoUserRepositoryCustom
{

	private static final Logger logger = LogManager.getLogger(MongoConfiguration.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public long updateUserLock(String email, boolean locked)
	{
		logger.debug("updating lock status of user = " + email + " to locked = " + locked);
		WriteResult wr = mongoTemplate.updateFirst(query(where("email").is(email)),
				update("locked", locked).set("updateddate", User.dateFormat.format(new Date())), User.class);
		logger.debug("update result = " + wr.getN());
		return wr.getN();
	}
}
