package com.ibm.big.deliverydashboard.dao.mongo;

public interface MongoUserRepositoryCustom
{
	public long updateUserLock(String email, boolean locked);
}
