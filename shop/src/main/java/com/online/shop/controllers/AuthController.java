package com.online.shop.controllers;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.online.shop.dtos.RegisterRequest;
import com.online.shop.services.AuthService;
import com.online.shop.services.MailService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private AuthService authService;
	private AuthenticationManager authManager;
	private MailService mailService;
	
	public AuthController(AuthService service,AuthenticationManager authManager,MailService mailService) {
		this.authManager=authManager;
		this.authService=service;
		this.mailService=mailService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
			try{
				authService.registerUser(request);
				mailService.sendVerificationCode(request.getEmail());
				return ResponseEntity.ok("We have sent a verification code to your email!");
			}catch(RuntimeException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyUser(@RequestParam String code,@RequestParam String email){
		try{
			mailService.verifyUser(code, email);
			return ResponseEntity.ok("Your account is now verified!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body("Invalid or expired token!");
		}
	}
	
	@PostMapping("/resetPassword")
	public ResponseEntity<String> sendVerificationCode(@RequestBody HashMap<String,String> map) {
		try{
			mailService.sendVerificationCode(map.get("email"));
			return ResponseEntity.ok("We have sent a verification code to your email!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/resetPassword/resend")
	public ResponseEntity<String> resendVerificationCode(@RequestBody HashMap<String,String> map) {
		try{
			mailService.sendVerificationCode(map.get("email"));
			return ResponseEntity.ok("We have sent a new verification code to your email!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/resetPassword/request")
	public ResponseEntity<String> verifyResetRequest(@RequestBody HashMap<String,String> map) {
		try{
			authService.verifyResetRequest(map.get("code"),map.get("email"),map.get("newPassword"),map.get("confirmPassword"));
			return ResponseEntity.ok("Your password has been successfully changed!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	
}
