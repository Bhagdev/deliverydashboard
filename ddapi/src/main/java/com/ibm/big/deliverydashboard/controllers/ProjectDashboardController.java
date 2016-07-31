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

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}/spentEffort/dateHistogram", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<AggregationResponse>> getProjectSpentEffortDateHistogram(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "sprintId", required = false) String sprintId,
			@RequestHeader(value = "fromDate", required = true) String fromDate,
			@RequestHeader(value = "toDate", required = true) String toDate,
			@RequestHeader(value = "interval", required = true) String interval)
	{
		logger.debug("spentEffort Date Histogram for: " + projectId);
		ResponseEntity<ResponseBean<AggregationResponse>> response;
		ResponseBean<AggregationResponse> rb = new ResponseBean<>();
		try
		{
			AggregationResponse p = projDashService.getProjectSpentEffortDateHistogram(projectId, sprintId, fromDate, toDate, interval);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}/effort/burndown", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<AggregationResponse>> getProjectBurndown(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "sprintId", required = false) String sprintId,
			@RequestHeader(value = "fromDate", required = true) String fromDate,
			@RequestHeader(value = "toDate", required = true) String toDate,
			@RequestHeader(value = "interval", required = true) String interval)
	{
		logger.debug("Effort Burndown for project.id: " + projectId);
		ResponseEntity<ResponseBean<AggregationResponse>> response;
		ResponseBean<AggregationResponse> rb = new ResponseBean<>();
		try
		{
			AggregationResponse p = projDashService.getProjectBurnDownDateHistogram(projectId, sprintId, fromDate, toDate, interval);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}/effort/extendedStats", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<AggregationResponse>> getProjectEffortExtendedStats(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "sprintId", required = false) String sprintId,
			@RequestHeader(value = "fromDate", required = true) String fromDate,
			@RequestHeader(value = "toDate", required = true) String toDate)
	{
		logger.debug("Effort Extended Stats for project.id: " + projectId);
		ResponseEntity<ResponseBean<AggregationResponse>> response;
		ResponseBean<AggregationResponse> rb = new ResponseBean<>();
		try
		{
			AggregationResponse p = projDashService.getProjectEffortExtendedStats(projectId, sprintId, fromDate, toDate);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}	

	@RequestMapping(method = RequestMethod.GET, value = "/projectsnapshot/{projectId}/spentEffort/extendedStats", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<AggregationResponse>> getProjectSpentEffortExtendedStats(
			@PathVariable(value = "projectId") String projectId,
			@RequestHeader(value = "sprintId", required = false) String sprintId,
			@RequestHeader(value = "fromDate", required = true) String fromDate,
			@RequestHeader(value = "toDate", required = true) String toDate)
	{
		logger.debug("Spent Effort Extended Stats for project.id: " + projectId);
		ResponseEntity<ResponseBean<AggregationResponse>> response;
		ResponseBean<AggregationResponse> rb = new ResponseBean<>();
		try
		{
			AggregationResponse p = projDashService.getProjectSpentEffortExtendedStats(projectId, sprintId, fromDate, toDate);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}	
}
