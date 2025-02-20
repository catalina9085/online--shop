package com.online.shop.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Amount {
	@JsonProperty("currency_code")
    private String currencyCode; 
    
    @JsonProperty("value")
    private String value; 
    
    public Amount() {}
    
    public Amount(String currencyCode,String value) {
    	this.currencyCode=currencyCode;
    	this.value=value;
    }
}
