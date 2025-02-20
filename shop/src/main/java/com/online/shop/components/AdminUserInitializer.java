package com.online.shop.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;


@Component
public class AdminUserInitializer implements CommandLineRunner{
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
	
	@Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("admin").isEmpty()) {
        	User adminUser = new User();
        	adminUser.setEmail("admin@gmail.com");
        	adminUser.setUsername("admin");
        	adminUser.setPassword(passwordEncoder.encode("admin"));
        	adminUser.setRole("ROLE_ADMIN");
        	userRepository.save(adminUser);
        }
    }
}
