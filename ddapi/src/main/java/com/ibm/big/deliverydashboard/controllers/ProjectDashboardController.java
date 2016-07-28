package com.ibm.big.deliverydashboard.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.big.deliverydashboard.ddcommon.analysis.AggregationResponse;
import com.ibm.big.deliverydashboard.ddcommon.beans.project.ProjectSnapshot;
import com.ibm.big.deliverydashboard.services.ProjectDashboardService;
import com.ibm.big.deliverydashboard.services.ProjectService;

@RestController
public class ProjectDashboardController extends AbstractController
{
	private static final Logger logger = LogManager.getLogger(ProjectController.class);

	@Autowired
	ProjectService projService;

	@Autowired
	ProjectDashboardService projDashService;

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<List<ProjectSnapshot>>> getProjectSnapshot(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "fromDate", required = false) String fromDate,
			@RequestHeader(value = "toDate", required = false) String toDate,
			@RequestHeader(value = "page", required = false) Integer page,
			@RequestHeader(value = "limit", required = false) Integer limit)
	{
		logger.debug("finding project snapshots for project.id: " + projectId);
		ResponseEntity<ResponseBean<List<ProjectSnapshot>>> response;
		ResponseBean<List<ProjectSnapshot>> rb = new ResponseBean<>();
		try
		{
			List<ProjectSnapshot> p = projService.getProjectSnapshotsById(projectId, fromDate, toDate, page, limit);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}/spentEffort", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<AggregationResponse>> getProjectSpentEffort(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "fromDate", required = false) String fromDate,
			@RequestHeader(value = "toDate", required = false) String toDate,
			@RequestHeader(value = "interval", required = false) String interval)
	{
		logger.debug("finding project snapshots for project.id: " + projectId);
		ResponseEntity<ResponseBean<AggregationResponse>> response;
		ResponseBean<AggregationResponse> rb = new ResponseBean<>();
		try
		{
			AggregationResponse p = projDashService.getProjectSpentEffortDateHistogram(projectId, fromDate, toDate, interval);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	
}
