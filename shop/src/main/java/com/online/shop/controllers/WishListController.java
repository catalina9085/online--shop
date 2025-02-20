package com.online.shop.controllers;

import java.security.Principal;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.shop.entities.Product;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.WishListRepository;
import com.online.shop.services.WishListService;

@RestController
@RequestMapping("/user/wishList")
public class WishListController {
	private WishListRepository wishRepo;
	private ProductRepository productRepo;
	private WishListService wishListService;
	
	public WishListController(WishListRepository wishRepo,ProductRepository productRepo,WishListService wishListService) {
		this.wishRepo=wishRepo;
		this.productRepo=productRepo;
		this.wishListService=wishListService;
	}
	
	@PostMapping("/add/{productId}")
	public ResponseEntity<String> addToWishList(@PathVariable Long productId,Principal principal) {
		wishListService.addWishList(productId,principal);
		return ResponseEntity.ok("Product successfully added!");
	}
	
	@GetMapping("/getProducts")
	public ResponseEntity<Set<Product>> getWishListProducts(Principal principal){
		System.out.println("produse:"+wishListService.getWishListProducts(principal));
		return ResponseEntity.ok(wishListService.getWishListProducts(principal));
	}
	
	@DeleteMapping("/delete/{productId}")
	public ResponseEntity<String> deleteFromWishList(@PathVariable Long productId,Principal principal){
		wishListService.deleteFromWishList(productId,principal);
		return ResponseEntity.ok("Product successfully removed!");
	}
	
	@GetMapping("/existsById/{productId}")
	public ResponseEntity<String> existsById(Principal principal,@PathVariable Long productId){
		if(wishListService.existsById(principal,productId))
			return ResponseEntity.ok("Found");
		else
			return ResponseEntity.ok("Not found");
	}
}

