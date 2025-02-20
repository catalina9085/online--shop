package com.online.shop.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String username;
	private String password;
	private String email;
	private String role;
	
	private String verificationCode;
	private boolean verified;
	private LocalDateTime codeExpiration;
	private String fcmToken;
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private ShoppingCart cart;
	
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	private Notifications notifications;
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private WishList wishList;
	
	@OneToMany(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Review> reviews=new ArrayList<>();
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Shipment shipment;
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Transaction transaction;
	
	@OneToMany(mappedBy="user",cascade=CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
	private List<Order> orders=new ArrayList<>();
	
	@ElementCollection
	private List<Order> orderHistory=new ArrayList<>();
	
	public User() {
        this.cart = new ShoppingCart();
        this.cart.setUser(this); 
        
        this.wishList = new WishList();
        this.wishList.setUser(this);
        this.notifications=new Notifications(); 
    }
	
    
}

