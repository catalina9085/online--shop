package com.online.shop.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.Category;
import com.online.shop.entities.Product;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long>{
	List<Product> findByCategory(Category category);
	List<Product> findAll(Sort sort);
}
