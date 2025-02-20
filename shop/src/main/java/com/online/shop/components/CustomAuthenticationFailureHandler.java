package com.online.shop.components;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,HttpServletResponse response,AuthenticationException exception) throws IOException,ServletException{
		 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		 
	     String errorMessage = exception.getMessage();
	     response.setContentType("application/json");
	     response.getWriter().write(errorMessage+"!");
	}
	/*
	 Fara aceasta,comportamentul implicit ar fi fost o redirectionare la /login?error
	 */
}
