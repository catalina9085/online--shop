package com.online.shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.OrderProduct;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct,Long>{
	public List<OrderProduct> findByProductId(Long productId);
}
