package com.ibm.big.deliverydashboard.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.big.deliverydashboard.ddcommon.beans.project.Project;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;
import com.ibm.big.deliverydashboard.services.ProjectService;

@RestController
public class ProjectController extends AbstractController
{
	private static final Logger logger = LogManager.getLogger(ProjectController.class);

	@Autowired
	ProjectService projService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody ResponseEntity<ResponseBean<Project>> createProject(@RequestBody(required = true) Project project)
	{
		logger.debug("Creating project: " + project);
		ResponseEntity<ResponseBean<Project>> response;
		ResponseBean<Project> rb = new ResponseBean<>();
		try
		{
			Project p = projService.createProject(project);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.PATCH, value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody ResponseEntity<ResponseBean<Project>> updateProject(@RequestBody(required = true) Project project)
	{
		logger.debug("Updating project: " + project);
		ResponseEntity<ResponseBean<Project>> response;
		ResponseBean<Project> rb = new ResponseBean<>();
		try
		{
			Project p = projService.updateProject(project);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/projectsnapshot", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<ProjectSnapshot>> createProjectSnapshot(@RequestBody(required = true) ProjectSnapshot projectSnapshot)
	{
		logger.debug("Creating project snapshot: " + projectSnapshot);
		ResponseEntity<ResponseBean<ProjectSnapshot>> response;
		ResponseBean<ProjectSnapshot> rb = new ResponseBean<>();
		try
		{
			ProjectSnapshot p = projService.createProjectSnapshot(projectSnapshot);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<List<ProjectSnapshot>>> getProjectSnapshot(@PathVariable(value="id") String id)
	{
		logger.debug("finding project snapshots for project.id: " + id);
		ResponseEntity<ResponseBean<List<ProjectSnapshot>>> response;
		ResponseBean<List<ProjectSnapshot>> rb = new ResponseBean<>();
		try
		{
			List<ProjectSnapshot> p = projService.getProjectSnapshotsById(id);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

}
