package com.online.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.ProductImage;
@Repository
public interface ImageRepo extends JpaRepository<ProductImage,Long> {

}
