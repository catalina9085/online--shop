package com.online.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem,Long>{

}
