package com.online.shop.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.online.shop.dtos.AddProductRequest;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.services.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	private AdminService adminService;
	
	public AdminController(AdminService adminService) {
		this.adminService=adminService;
	}
	
	@PostMapping("/addNewProduct/{savingOption}")
	public ResponseEntity<String> addNewProduct(@ModelAttribute AddProductRequest request,@PathVariable int savingOption) throws IOException {
		adminService.addNewProduct(request,savingOption);
		return ResponseEntity.ok("Product successfully added!");
	}
	
	@GetMapping("/getAllProducts")
	public ResponseEntity<List<Product>> getAllProducts(){
		return ResponseEntity.ok(adminService.getAllProducts());
	}
	
	@GetMapping("/getProduct/{productId}")
	public ResponseEntity<Object> getProduct(@PathVariable Long productId) {
		try {
			Product product=adminService.getProduct(productId);
			return ResponseEntity.ok(product);
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteProduct/{productId}")
	public ResponseEntity<String> deletetProduct(@PathVariable Long productId) {
			adminService.deleteProduct(productId);
			return ResponseEntity.ok("Object successfully deleted!");
		
	}
	
	@DeleteMapping("/deleteImage")
	public ResponseEntity<String> deleteImage(@RequestBody Map<String,Long> map) {
		adminService.deleteImage(map.get("imageId"),map.get("productId"));
		return ResponseEntity.ok("Object successfully deleted!");
	
	}
	
	@PostMapping("/editProductInfo")
	public ResponseEntity<String> editProductInfo(@RequestBody HashMap<String,String> map){
		adminService.editProductInfo(map);
		return ResponseEntity.ok("Product info successfully changed!");
	}
	
	@PostMapping("/addNewFiles/{savingOption}")
	public ResponseEntity<String> addNewFiles(@RequestParam Long productId,@RequestParam List<MultipartFile> files,@PathVariable int savingOption){
		try{
			adminService.addNewFiles(productId,files,savingOption);
			return ResponseEntity.ok("Files successfully added!");
		}catch(IOException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteReview/{reviewId}")
	public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){
		try{
			adminService.deleteReview(reviewId);
			return ResponseEntity.ok("Review successfully deleted!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/getAllUsers")
	public ResponseEntity<List<User>> getAllUsers(){
		return ResponseEntity.ok(adminService.getAllUsers());
	}
	
	@PostMapping("/changeUserRole/{userId}")
	public ResponseEntity<String> changeUserRole(@PathVariable Long userId) {
		adminService.changeRole(userId);
		return ResponseEntity.ok("Role successfully changed!");
	}
	
	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		adminService.deleteUser(userId);
		return ResponseEntity.ok("User successfully deleted!");
	}
	
	@GetMapping("/getProductsByKeyword/{keyword}")
	public ResponseEntity<List<Product>> getProductsByKeyword(@PathVariable String keyword){
		return ResponseEntity.ok(adminService.getProductsByKeyword(keyword));
	}
	
	@GetMapping("/getOrdersInfo")
	public ResponseEntity<Map<String,Object>> getOrdersCnt(){
		return ResponseEntity.ok(adminService.getOrdersInfo());
	}
	
}
