package com.online.shop.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.online.shop.components.CustomAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private CustomUserDetailsService userDetailsService;
	
	public SecurityConfig(CustomUserDetailsService userDetailsService) {
		this.userDetailsService=userDetailsService;
	}
	
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().and().authorizeHttpRequests(request->request
				.requestMatchers("/auth/**","/js/**", "/css/**", "/images/**","/favicon.ico","/uploads/**","/").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				)
			.csrf().disable()
			.formLogin(form->form
					.loginPage("/auth/login")
					.failureHandler(new CustomAuthenticationFailureHandler())
					.loginProcessingUrl("/auth/login")
					.defaultSuccessUrl("/user/main",true)
					.permitAll())
			.logout().logoutUrl("/logout")
            .logoutSuccessUrl("/auth/login")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .permitAll();
			return http.build();
    
	}
}
