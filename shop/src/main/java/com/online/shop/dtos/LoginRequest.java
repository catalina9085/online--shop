package com.online.shop.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String identifier;//username or email
	private String password;
	
}
