package com.ibm.big.deliverydashboard.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;


public interface MongoUserRepository extends MongoRepository<User, String>, MongoUserRepositoryCustom
{
	List<User> findByBand(String band);
	User findByEmail(String email);
	List<User> findByFirstname(String firstname);
}
