package com.ibm.big.deliverydashboard.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

public abstract class AbstractController
{
	private static final Logger logger = LogManager.getLogger(AbstractController.class);

	public <T> ResponseEntity<ResponseBean<T>> handelException(Exception e, ResponseBean<T> rb)
	{
		logger.error(e);
		rb.setStatus(ResponseBean.ERROR_STATUS);
		rb.setErrorMessage(e.getMessage());
		return ResponseEntity.badRequest().body(rb);
	}
}
