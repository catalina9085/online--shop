package com.online.shop.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.shop.entities.Category;
import com.online.shop.entities.Product;
import com.online.shop.entities.ProductImage;
import com.online.shop.entities.Review;
import com.online.shop.entities.User;
import com.online.shop.services.ProductService;

@RestController
@RequestMapping("/user/products")
public class ProductController {
	private ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService=productService;
	}
	
	@GetMapping("getAllProducts")
	public ResponseEntity<List<Product>> getAllProducts(){
		List<Product> products = productService.getAllProducts();
	    return ResponseEntity.ok(products);
	}
	
	@GetMapping("getProductsByCategory/{productCategory}")
	public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String productCategory){
		return ResponseEntity.ok(productService.getProductsByCategory(productCategory));
	}
	
	@GetMapping("/getSortedProducts/{sortDirection}")
	public ResponseEntity<List<Product>> getProductsSorted(@PathVariable String sortDirection){
		return ResponseEntity.ok(productService.getProductsSorted(sortDirection));
	}
	
	@PostMapping("/getProductsByCategoryAndPrice")
	public ResponseEntity<List<Product>> productsByCategoryAndPrice(@RequestBody Map<String,String> map){
		return ResponseEntity.ok(productService.getProductsByCategoryAndPrice(map.get("sortDirection"),map.get("productCategory")));
	}
	
	
	@GetMapping("/getReviews/{productId}")
	public ResponseEntity<Object> getReviews(@PathVariable Long productId){
			List<Review> list=productService.getReviews(productId);
			return ResponseEntity.ok(list);
	}
	
	@GetMapping("/getProduct/{productId}")
	public ResponseEntity<Product> getProduct(@PathVariable Long productId){
		return ResponseEntity.ok(productService.getProductById(productId));
	}
	
	@GetMapping("/getProductsByKeyword/{keyword}")
	public ResponseEntity<List<Product>> getProductsByKeyword(@PathVariable String keyword){
		return ResponseEntity.ok(productService.getProductsByKeyword(keyword));
	}
	
	@PostMapping("/addReview")
	public ResponseEntity<String> addReview(@RequestBody HashMap<String,String> map,Principal principal){
		productService.addReview(map.get("productId"),map.get("review"),map.get("ratingValue"),principal);
		return ResponseEntity.ok("Review successfully added!");
	}
	
	@GetMapping("/getPictureRequest/{picId}")
	public ResponseEntity<ProductImage> getPicture(@PathVariable Long picId){
		return ResponseEntity.ok(productService.getImage(picId));
	}
	
	@GetMapping("/getUser")
	public ResponseEntity<String> getUser(Principal principal){
		return ResponseEntity.ok(principal.getName());
	}
	@GetMapping("/getUser1")
	public ResponseEntity<User> getUser1(Principal principal){
		return ResponseEntity.ok(productService.getUser(principal));
	}
	
	@GetMapping("getAllCategories")
	public ResponseEntity<List<Category>> getCategories() {
		return ResponseEntity.ok(productService.getCategories());
	}
	
	@GetMapping("/getRecommendedProducts/{recommendSelect}")
	public ResponseEntity<List<Product>> getRecommendedProducts(@PathVariable String recommendSelect){
		return ResponseEntity.ok(productService.getRecommendedProducts(recommendSelect));
	}
}

