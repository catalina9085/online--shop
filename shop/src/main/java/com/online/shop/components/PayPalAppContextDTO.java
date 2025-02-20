package com.online.shop.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PayPalAppContextDTO {
	@JsonProperty("brand_name")
    private String brandName;
	
    @JsonProperty("landing_page")
    private String landingPage;//LOGIN,BILLING,PAYMENT
    
    @JsonProperty("return_url")
    private String returnUrl;
    
    @JsonProperty("cancel_url")
    private String cancelUrl;
    
    public PayPalAppContextDTO() {}
    
    public PayPalAppContextDTO(String brandName,String landingPage,String returnUrl,String cancelUrl) {
    	this.brandName=brandName;
    	this.landingPage=landingPage;
    	this.returnUrl=returnUrl;
    	this.cancelUrl=cancelUrl;
    }
}
