package com.ibm.big.deliverydashboard.ddcommon.beans.user;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(indexName = "users", type = "user")
@JsonInclude(value = Include.NON_EMPTY)
public class User
{
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@Id
	String id;
	String email;
	String phone;
	String firstname;
	String lastname;
	String dateOfBirth;
	String dateOfJoiningIBM;
	String careerStartDate;
	Designation designation;
	String band;
	Set<SkillTag> tags;
	Set<Role> roles;
	String creationdate;
	String updateddate;
	String password;
	boolean locked;
	String diversity;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getDiversity()
	{
		return diversity;
	}

	public void setDiversity(String diversity)
	{
		this.diversity = diversity;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstName)
	{
		this.firstname = firstName;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastName)
	{
		this.lastname = lastName;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfJoiningIBM()
	{
		return dateOfJoiningIBM;
	}

	public void setDateOfJoiningIBM(String dateOfJoining)
	{
		this.dateOfJoiningIBM = dateOfJoining;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String emailAddress)
	{
		this.email = emailAddress;
	}

	public Designation getDesignation()
	{
		return designation;
	}

	public void setDesignation(Designation designation)
	{
		this.designation = designation;
	}

	public String getBand()
	{
		return band;
	}

	public void setBand(String band)
	{
		this.band = band;
	}

	public Set<SkillTag> getTags()
	{
		return tags;
	}

	public void setTags(Set<SkillTag> tags)
	{
		this.tags = tags;
	}

	public void addTag(SkillTag tag)
	{
		if (this.tags == null)
		{
			this.tags = new HashSet<>();
		}
		this.tags.add(tag);
	}

	public Set<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<Role> roles)
	{
		this.roles = roles;
	}

	public void addRole(Role role)
	{
		if (this.roles == null)
		{
			this.roles = new HashSet<>();
		}
		this.roles.add(role);
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isLocked()
	{
		return locked;
	}

	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}

	public String getCreationdate()
	{
		return creationdate;
	}

	public void setCreationdate(String creationDate)
	{
		this.creationdate = creationDate;
	}

	public String getUpdateddate()
	{
		return updateddate;
	}

	public void setUpdateddate(String updateddate)
	{
		this.updateddate = updateddate;
	}

	public String getCareerStartDate()
	{
		return careerStartDate;
	}

	public void setCareerStartDate(String careerStartDate)
	{
		this.careerStartDate = careerStartDate;
	}
	
	public String toString()
	{
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
