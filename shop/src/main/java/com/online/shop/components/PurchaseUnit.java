package com.online.shop.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PurchaseUnit {
	@JsonProperty("amount")
    private Amount amount; 
	
	public PurchaseUnit() {}
	
	public PurchaseUnit(Amount amount) {
		this.amount=amount;
	}
}
