package com.online.shop.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	private String username;
	private String password;
	private String confirmPassword;
	private String email;
}
