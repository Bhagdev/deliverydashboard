package com.ibm.big.deliverydashboard.ddcommon.beans.project;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

public class TeamMember {

	User person;
	String role;
	String date;
	
	public User getPerson() {
		return person;
	}
	public void setPerson(User person) {
		this.person = person;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
