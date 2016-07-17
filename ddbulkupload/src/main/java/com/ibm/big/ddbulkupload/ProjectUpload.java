package com.ibm.big.ddbulkupload;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.QualityMetric;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.SprintSnapshot;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.TeamMember;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.Technology;
import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;

public class ProjectUpload
{
	public static void main(String[] args) throws Exception
	{
		Project proj = new Project();
		proj.setId("CI001");
		
		proj.setStartDate("2016-01-01T00:00:00.000+0530");
		proj.setEndDate("2018-12-31T00:00:00.000+0530");
		proj.setName("Customer Journey Automation");
		
		Set<Technology> technologies = new HashSet<>();
		
		Technology t = new Technology();
		t.setName("JAVA");
		technologies.add(t);
		
		t = new Technology();
		t.setName("BPM");
		technologies.add(t);
		
		proj.setTechnologies(technologies);
//		RestTemplate template =new RestTemplate();
//		Project p = template.postForObject("http://localhost:8090/deliverydashboard/project", proj, Project.class);
//		System.out.println("project saved = " + p);
		
		ProjectSnapshot ps = new ProjectSnapshot();
		ps.setId(UUID.randomUUID().toString());
//		ps.setLogDate(ProjectSnapshot.DATE_FORMAT.format(new Date()));
		ps.setProject(proj);
		
		SprintSnapshot ss = new SprintSnapshot();
		ss.setStartDate("2016-07-15T00:00:00.000+0530");
		ss.setEndDate("2016-08-31T00:00:00.000+0530");
		ss.setId(proj.getId() + proj.getCurrentSprint());
		ss.setSprintNumber(proj.getCurrentSprint());

		ss.addQualityMetrics(QualityMetric.DEFECT_DENSITY_TYPE, 0.099);
		ss.addQualityMetrics(QualityMetric.SONAR_TYPE + "CRITICAL", 1.00);
		ss.addQualityMetrics(QualityMetric.SONAR_TYPE + "MAJOR", 10.00);
		ss.addQualityMetrics(QualityMetric.JUNIT_COVERAGE_TYPE, 59.6);
	
		ss.addRemainingHours(SprintSnapshot.EFFORT_DESIGN, 10.00);
		ss.addRemainingHours(SprintSnapshot.EFFORT_BUILD, 200.00);
		ss.addRemainingHours(SprintSnapshot.EFFORT_TEST, 20.00);
		ss.addRemainingHours(SprintSnapshot.EFFORT_SUPPORT, 20.00);
		
		ss.addSpentHours(SprintSnapshot.EFFORT_DESIGN, 90.00);
		ss.addSpentHours(SprintSnapshot.EFFORT_BUILD, 20.0);
		
		ss.addEstimatedHours(SprintSnapshot.EFFORT_DESIGN, 100.0);
		ss.addEstimatedHours(SprintSnapshot.EFFORT_BUILD, 220.0);
		ss.addEstimatedHours(SprintSnapshot.EFFORT_TEST, 20.0);
		ss.addEstimatedHours(SprintSnapshot.EFFORT_SUPPORT, 20.0);
		
		TeamMember tm = new TeamMember();
		tm.setRole("ScrumMaster");
		User u = new User();
		u.setEmail("ashutosh.velhankar@barclays.com");
		u.setId("00731J");
		tm.setUser(u);
		ss.addTeamMember(tm);
		
		tm = new TeamMember();
		tm.setRole("Developer");
		u = new User();
		u.setEmail("abhinav.thakare@barclays.com");
		u.setId("07150X");
		tm.setUser(u);
		ss.addTeamMember(tm);
		
		tm = new TeamMember();
		tm.setRole("Designer");
		u = new User();
		u.setEmail("ravindra.baviskar@barclays.com");
		u.setId("05418B");
		tm.setUser(u);
		ss.addTeamMember(tm);
		
		tm = new TeamMember();
		tm.setRole("BuildLead");
		u = new User();
		u.setEmail("siddharth.mishra@barclays.com");
		u.setId("06903P");
		tm.setUser(u);
		ss.addTeamMember(tm);
		
		ss.setUserStoryCount(30);
		
		ps.setSprint(ss);
		
		System.out.println(ps.toString());

	}

}
