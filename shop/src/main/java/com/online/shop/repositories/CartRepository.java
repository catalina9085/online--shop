package com.online.shop.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.ShoppingCart;
import com.online.shop.entities.User;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart,Long>{
	Optional<ShoppingCart> findByUser(User user);
}
