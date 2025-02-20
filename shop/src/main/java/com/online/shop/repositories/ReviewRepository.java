package com.online.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review ,Long>{

}
