package com.online.shop.services;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.shop.dtos.RegisterRequest;
import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;

@Service
public class AuthService {

	private UserRepository userRepo;
	private PasswordEncoder encoder;
	
	public AuthService(UserRepository userRepo, PasswordEncoder encoder) {
		this.userRepo=userRepo;
		this.encoder=encoder;
	}
	
	public void registerUser(RegisterRequest request) {
		if(userRepo.existsByEmail(request.getEmail())) {
			User user=userRepo.findByEmail(request.getEmail()).get();
			if(!user.isVerified())
				throw new RuntimeException("This email is associated with an unverified account!");
			else throw new RuntimeException("This email is associated with another account!");
		}
		if(userRepo.existsByUsername(request.getUsername()))
			throw new RuntimeException("This username is already used!");
		if(!request.getPassword().equals(request.getConfirmPassword()))
			throw new RuntimeException("The passwords do not match!");
		
		User user=new User();
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setUsername(request.getUsername());
		user.setRole("ROLE_USER");
		userRepo.save(user);
	}
	
	public void verifyResetRequest(String code,String email,String newPassword,String confirmPassword) {
		User user=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("There's no account associated with this email!"));
		if(user.getCodeExpiration().isBefore(LocalDateTime.now()))
			throw new RuntimeException("Expired code!");
		if(!user.getVerificationCode().equals(code))
			throw new RuntimeException("Invalid code!");
		if(!newPassword.equals(confirmPassword))
			throw new RuntimeException("The passwords do not match!");
		user.setPassword(encoder.encode(newPassword));
		userRepo.save(user);
	}
}
