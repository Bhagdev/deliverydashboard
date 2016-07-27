package com.ibm.big.deliverydashboard.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.big.deliverydashboard.ddcommon.beans.user.User;
import com.ibm.big.deliverydashboard.services.UserService;

@RestController
public class UserController extends AbstractController
{
	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@RequestMapping(method = RequestMethod.POST, value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<ResponseBean<User>> createUser(@RequestBody(required = true) User user)
	{
		ResponseEntity<ResponseBean<User>> response;
		ResponseBean<User> rb = new ResponseBean<>();
		try
		{
			User p = userService.createUser(user);
			rb.setResponse(p);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("#email == authentication.name")
	public @ResponseBody ResponseEntity<ResponseBean<User>> getUserByEmail(
			@PathVariable(value="email") String email)
	{
		ResponseEntity<ResponseBean<User>> response;
		ResponseBean<User> rb = new ResponseBean<>();
		try
		{
			User user = null;
			if (email != null)
			{
				user = userService.findByEmail(email);
			}
				
			rb.setResponse(user);
			response = ResponseEntity.ok(rb);
			
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody ResponseEntity<ResponseBean<User>> getUserByEmailOrId(
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "id", required = false) String id)
	{
		ResponseEntity<ResponseBean<User>> response;
		ResponseBean<User> rb = new ResponseBean<>();
		try
		{
			User user = null;
			if (id != null)
			{
				user = userService.findById(id);
			}
			else if (email != null)
			{
				user = userService.findByEmail(email);
			}
				
			rb.setResponse(user);
			response = ResponseEntity.ok(rb);
			
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/user/profile", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("#user.email == authentication.name")
	public @ResponseBody ResponseEntity<ResponseBean<User>> updateUserProfile(@RequestBody(required = true) User user)
	{
		ResponseEntity<ResponseBean<User>> response;
		ResponseBean<User> rb = new ResponseBean<>();
		try
		{
			User u = userService.updateUser(user);
			rb.setResponse(u);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/user/restricted", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody ResponseEntity<ResponseBean<User>> updateUserRestricted(@RequestBody(required = true) User user)
	{
		ResponseEntity<ResponseBean<User>> response;
		ResponseBean<User> rb = new ResponseBean<>();
		try
		{
			User u = userService.updateUserRestricted(user);
			rb.setResponse(u);
			response = ResponseEntity.ok(rb);
		} catch (Exception e)
		{
			response = handelException(e, rb);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.PATCH, value = "/user/lock/{email}/{locked}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody ResponseEntity<String> updateUserLock(@PathVariable(value="email") String email, @PathVariable(value="locked") boolean locked)
	{
		ResponseEntity<String> response;
		try
		{
			long l = userService.updateUserLock(email, locked);
			response = ResponseEntity.ok("{\"status\": " + l + "}");
		} catch (Exception e)
		{
			logger.error(e);
			response = ResponseEntity.badRequest().body("{\"status\": " + 0 + "}");
		}
		return response;
	}	
}
