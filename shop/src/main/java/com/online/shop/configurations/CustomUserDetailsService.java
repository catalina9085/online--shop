package com.online.shop.configurations;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.online.shop.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private UserRepository userRepo;
	
	public CustomUserDetailsService(UserRepository userRepo) {
		this.userRepo=userRepo;
	}
		
	@Override
	public UserDetails loadUserByUsername(String identifier) {
		Optional<com.online.shop.entities.User> user=userRepo.findByUsername(identifier);
		if(user.isEmpty()) user=userRepo.findByEmail(identifier);
		
		com.online.shop.entities.User foundUser=user.orElseThrow(()->new UsernameNotFoundException("User not found!"));
		
		return new org.springframework.security.core.userdetails.User(foundUser.getUsername(),foundUser.getPassword(),
				List.of(new SimpleGrantedAuthority(foundUser.getRole())));
	}
}