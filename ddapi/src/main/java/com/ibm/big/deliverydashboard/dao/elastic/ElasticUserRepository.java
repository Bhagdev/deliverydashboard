package com.ibm.big.deliverydashboard.dao.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;


public interface ElasticUserRepository extends ElasticsearchRepository<User, String>
{
	List<User> findByBand(String band);
	User findByEmail(String email);
}
