package com.online.shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.shop.entities.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{
	public List<Order> findByStatus(String status);
}
