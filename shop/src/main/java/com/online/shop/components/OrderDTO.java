package com.online.shop.components;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO implements Serializable{
	
	private String intent;
	
	@JsonProperty("purchase_units")
	private List<PurchaseUnit> units;
	
	@JsonProperty("application_context")
	private PayPalAppContextDTO context;
	
	public OrderDTO() {}
	
	public OrderDTO(String intent,List<PurchaseUnit> units,PayPalAppContextDTO context) {
		this.intent=intent;
		this.units=units;
		this.context=context;
		
	}
}
