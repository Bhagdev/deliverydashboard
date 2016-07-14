package com.ibm.big.deliverydashboard.services;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

public interface UserService
{
	public User createUser(User u) throws Exception;
	public User findByEmail(String email) throws Exception;
	public User findById(String id) throws Exception;
	public User updateUserRestricted(User user) throws Exception;
	public User updateUser(User user) throws Exception;
	public long updateUserLock(String email, boolean locked) throws Exception;
}
