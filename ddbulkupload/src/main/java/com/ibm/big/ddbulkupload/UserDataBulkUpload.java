package com.ibm.big.ddbulkupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.Designation;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.Role;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.SkillTag;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class UserDataBulkUpload implements CommandLineRunner 
{
    public static void main( String[] args )
    {
    	
    	if (args != null && args.length == 0)
    	{
    		System.out.println("Usage: java -jar <path>/target/ddbulkupload-<version>.jar <Input File Absolute Path>");
    		System.exit(1);
    	}
    	
    	SpringApplication.run(UserDataBulkUpload.class);
    }

	@Override
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
			user.setDateOfJoining(userAttribs[5]);
			user.setCreationdate(User.dateFormat.format(new Date()));
			Designation d = new Designation();
			d.setProfession(userAttribs[6]);
			d.setSpecialization(userAttribs[7]);
			user.setDesignation(d);
			user.setBand(userAttribs[8]);
			
			String[] tags = userAttribs[9].split(";");
			List<SkillTag> lskill = new ArrayList<SkillTag>();
			for (int i = 0; i < tags.length; i++)
			{
				SkillTag st = new SkillTag();
				st.setName(tags[i]);
				lskill.add(st);
			}
			
			user.setTags(lskill);
			
			String[] roles = userAttribs[10].split(";");
			List<Role> lRole = new ArrayList<Role>();
			for (int i = 0; i < tags.length; i++)
			{
				Role r = new Role();
				r.setName(tags[i]);
				lRole.add(r);
			}
			
			user.setRoles(lRole);
			user.setPhone(userAttribs[11]);
			user.setPassword("password01");
			user.setLocked(true);
			
			System.out.println(user);
		}
		
	}
    
    
}
