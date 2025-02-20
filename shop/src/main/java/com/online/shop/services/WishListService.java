package com.online.shop.services;

import java.security.Principal;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.entities.WishList;
import com.online.shop.repositories.CategoryRepository;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

@Service
public class WishListService {
	private ProductRepository productRepo;
	private CategoryRepository categoryRepo;
	private UserRepository userRepo;
	
	public WishListService(ProductRepository productRepo,CategoryRepository categoryRepo, UserRepository userRepo) {
		this.productRepo=productRepo;
		this.categoryRepo=categoryRepo;
		this.userRepo=userRepo;
	}
	public User getUser(Principal principal) {
		return userRepo.findByUsername(principal.getName()).orElseThrow(()->new ResourceNotFoundException("User not found!"));
	}
	
	public void addWishList(Long productId,Principal principal) {
		User user=getUser(principal);
		Product product=productRepo.findById(productId).get();
		//nu mai e nev de verificare daca e deja acolo,ca oricum am folosit Set
		user.getWishList().getProducts().add(product);
		userRepo.save(user);
	}
	
	public Set<Product> getWishListProducts(Principal principal){
		User user=getUser(principal);
		return user.getWishList().getProducts();
	}
	
	public void deleteFromWishList(Long productId,Principal principal) {
		WishList wishlist=getUser(principal).getWishList();
		Product product=productRepo.findById(productId).get();
		wishlist.getProducts().remove(product);
		userRepo.save(getUser(principal));
	}
	
	public boolean existsById(Principal principal,Long productId) {
		Product product=productRepo.findById(productId).get();
		Set<Product> list=getUser(principal).getWishList().getProducts();
		return list.contains(product);
	}
}
