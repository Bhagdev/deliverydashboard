package com.ibm.big.ddbulkupload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.Designation;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.Role;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.SkillTag;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

public class UserDataBulkUpload 
{
    public static void main( String[] args ) throws Exception
    {
    	
    	if (args != null && args.length == 0)
    	{
    		System.out.println("Usage: java -jar <path>/target/ddbulkupload-<version>.jar <Input File Absolute Path>");
    		System.exit(1);
    	}

    	UserDataBulkUpload ud = new UserDataBulkUpload();
    	System.out.println(new Date().getTime());
    	ud.run(args);
    }

	public void run(String... args) throws Exception
	{	
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
	
		String inputLine;
		while ((inputLine = br.readLine()) != null)
		{
			String[] userAttribs = inputLine.split(",");

			User user = new User();
			user.setId(userAttribs[0]);
			user.setEmail(userAttribs[1]);
			user.setFirstname(userAttribs[2]);
			user.setLastname(userAttribs[3]);
			user.setDateOfBirth(userAttribs[4]);
			user.setDateOfJoiningIBM(userAttribs[5]);
			user.setCreationdate(User.DATE_FORMAT.format(new Date()));
			Designation d = new Designation();
			d.setProfession(userAttribs[6]);
			d.setSpecialization(userAttribs[7]);
			user.setDesignation(d);
			user.setBand(userAttribs[8]);
			
			String[] tags = userAttribs[9].split(";");
			Set<SkillTag> lskill = new HashSet<>();
			for (int i = 0; i < tags.length; i++)
			{
				SkillTag st = new SkillTag();
				st.setName(tags[i]);
				lskill.add(st);
			}
			
			user.setTags(lskill);
			
			String[] roles = userAttribs[10].split(";");
			Set<Role> lRole = new HashSet<>();
			for (int i = 0; i < roles.length; i++)
			{
				Role r = new Role();
				r.setName(roles[i]);
				lRole.add(r);
			}
			
			user.setRoles(lRole);
			user.setPhone(userAttribs[11]);
			user.setPassword("password01");
			user.setLocked(false);
			user.setDiversity(userAttribs[12]);
			
			RestTemplate template =new RestTemplate();
			User u = template.postForObject("http://localhost:8090/deliverydashboard/signup", user, User.class);
			System.out.println(u);
		}
		
	}
    
    
}
