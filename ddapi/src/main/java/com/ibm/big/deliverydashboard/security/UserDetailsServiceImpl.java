package com.ibm.big.deliverydashboard.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ibm.big.deliverydashboard.dao.mongo.MongoUserRepository;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.Role;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
	private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	MongoUserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		logger.debug("loading user details for username = " + username);
		User user = userRepo.findByEmail(username);

		if (user == null)
		{
			throw new UsernameNotFoundException("User not found for username = " + username);
		}
		if (user.isLocked())
		{
			throw new UsernameNotFoundException("User " + username + " is locked");

		}

		Set<Role> roles = user.getRoles();
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (roles != null)
		{
			for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();)
			{
				Role role = iterator.next();
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			}
		}

		UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword(), authorities);

		return userDetails;
	}

}
