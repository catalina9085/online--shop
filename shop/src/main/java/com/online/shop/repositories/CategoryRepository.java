package com.online.shop.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
	Optional<Category> findByName(String name);
}