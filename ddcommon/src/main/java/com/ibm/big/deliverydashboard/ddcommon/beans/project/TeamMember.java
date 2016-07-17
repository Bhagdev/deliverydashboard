package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

@JsonInclude(value = Include.NON_EMPTY)
public class TeamMember {

	User user;
	String role;
	Date startDate;
	Date endDate;
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User person) {
		this.user = person;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamMember other = (TeamMember) obj;
		if (user == null)
		{
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	
}
