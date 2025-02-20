package com.online.shop.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToMany(mappedBy="order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> products=new ArrayList<>();
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	private Double total;
	private String status;//pending sau paid
	private LocalDateTime createdAt;
	
	public Order() {}
	public Order(User user,Double total,String status,LocalDateTime createdAt) {
		this.user=user;
		this.total=total;
		this.status=status;
		this.createdAt=createdAt;
	}
}
