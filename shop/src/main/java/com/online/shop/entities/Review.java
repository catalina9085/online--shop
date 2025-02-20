package com.online.shop.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String text;
	private String postedBy;
	private int rating;
	
	@ManyToOne
	@JsonIgnore
	private Product product;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	private LocalDateTime createdAt;
	
	public Review() {}
	public Review(Product product,String text,User user,LocalDateTime createdAt) {
		this.createdAt=createdAt;
		this.product=product;
		this.text=text;
		this.user=user;
	}
	
}
