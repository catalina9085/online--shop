package com.online.shop.entities;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderProduct {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    private String productName;
    private Double price;
    private Integer quantity;
    private Long productId;
    
    //pentru imagine
    private String name;
	//generate de cloudinary
	private String url;
	private String public_id;
	
	//baza de date
	@Lob
	private byte[] data;
	
	@JsonProperty("imageBase64")
	public String getImageAsBase64() {
		return Base64.getEncoder().encodeToString(data);
	}
	
	private int savingOption;
	
	@ManyToOne
	@JsonIgnore
	private Order order;

	public OrderProduct() {}
	
	public OrderProduct(String productName,Double price,Integer quantity,Long productId,Order order) {
		this.productName=productName;
		this.price=price;
		this.quantity=quantity;
		this.productId=productId;
		this.order=order;
	}
}
