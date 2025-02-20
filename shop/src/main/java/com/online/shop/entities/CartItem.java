package com.online.shop.entities;

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
public class CartItem {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JsonIgnore
    private ShoppingCart cart;
	//un cart are mai multe itemuri,un cart e unic pt un item
	
	@ManyToOne
	private Product product;
	//un produs poate avea asociate mai multe itemuri,el poate fi adaugat in mai multe,de useri diferiti
	//dar un item are acel produs o data ,nu de mai multe ori
	
	private int quantity;
	
	public CartItem(Product product,int quantity,ShoppingCart cart) {
		this.product=product;
		this.quantity=quantity;
		this.cart=cart;
	}
	public CartItem() {};
}

