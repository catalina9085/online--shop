package com.online.shop.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findByVerificationCode(String code);
	
	boolean existsByEmail(String email);
	boolean existsByUsername(String username);
}
