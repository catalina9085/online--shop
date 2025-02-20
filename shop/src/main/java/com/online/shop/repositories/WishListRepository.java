package com.online.shop.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.User;
import com.online.shop.entities.WishList;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Long>{
	Optional<WishList> findByUser(User user);
}

