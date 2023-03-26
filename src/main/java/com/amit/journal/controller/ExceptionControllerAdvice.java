package com.amit.journal.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;





@ControllerAdvice
public class ExceptionControllerAdvice implements IControllerIface {


	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> exceptionHandler(HttpServletRequest request, Exception ex) {
		

		return getErrorResponse(ex);
	}


	private ResponseEntity<String> getErrorResponse(Exception ex) {
		return new ResponseEntity<>("", HttpStatus.OK);
	}


	@ResponseBody
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public String handleHttpMediaTypeNotAcceptableException() {
		return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
	}
}