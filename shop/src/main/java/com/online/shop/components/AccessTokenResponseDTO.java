package com.online.shop.components;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccessTokenResponseDTO {
	private String scope;
	
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("app_id")
    private String applicationId;
    
    @JsonProperty("expires_in")
    private int expiresIn;
    
    private String nonce;
    
    @JsonIgnore
    private Instant expiration;
}
