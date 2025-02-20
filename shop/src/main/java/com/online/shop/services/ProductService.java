package com.online.shop.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.Category;
import com.online.shop.entities.Product;
import com.online.shop.entities.ProductImage;
import com.online.shop.entities.Review;
import com.online.shop.entities.User;
import com.online.shop.repositories.CategoryRepository;
import com.online.shop.repositories.ImageRepo;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

@Service
public class ProductService {
	private ProductRepository productRepo;
	private CategoryRepository categoryRepo;
	private UserRepository userRepo;
	private ImageRepo imageRepo;
	
	
	public ProductService(ProductRepository productRepo,CategoryRepository categoryRepo, UserRepository userRepo,ImageRepo imageRepo) {
		this.productRepo=productRepo;
		this.categoryRepo=categoryRepo;
		this.userRepo=userRepo;
		this.imageRepo=imageRepo;
	}
	public User getUser(Principal principal) {
		return userRepo.findByUsername(principal.getName()).orElseThrow(()->new ResourceNotFoundException("User not found!"));
	}
	public List<Product> stockFilter(List<Product> list){
		return list.stream().filter(product->product.getStock()>0).collect(Collectors.toList());
	}
	
	public List<Product> getAllProducts() {
		return stockFilter(productRepo.findAll());
	}
	
	public Product getProductById(Long id) {
		Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		if(product.getReviews().size()>0) {
			double rating=0;
			for(Review review:product.getReviews())
				rating+=review.getRating();
			rating/=(product.getReviews().size());
			product.setRating(rating);
			productRepo.save(product);
		}
		return product;
	}
	
	public List<Product> getFilteredProducts(String category,Double minPrice,Double maxPrice){
		return stockFilter(productRepo.findAll()).stream()
				.filter(product->category!=null && compareByCategory(category,product))
				.filter(product->minPrice!=null && product.getPrice()>=minPrice.doubleValue())
				.filter(product->maxPrice!=null && product.getPrice()<=maxPrice.doubleValue())
				.collect(Collectors.toList());
	}
	
	public boolean compareByCategory(String category,Product product) {
		Category cat=categoryRepo.findByName(category).orElseThrow(()->new ResourceNotFoundException("Category not found!"));
		return cat.equals(product.getCategory());
	}
	
	public List<Product> getProductsSorted(String sortDirection){
		Sort sort =Sort.by("price");
		if(sortDirection.equals("desc"))
			sort=sort.descending();
		else
			sort=sort.ascending();
		return stockFilter(productRepo.findAll(sort));
	}
	
	public void addReview(String productId,String text,String rating,Principal principal) {
		User user=getUser(principal);
		Long id=Long.parseLong(productId);
		Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		Review newReview=new Review();
		newReview.setCreatedAt(LocalDateTime.now());
		newReview.setProduct(product);
		newReview.setText(text);
		newReview.setUser(user);
		newReview.setPostedBy(user.getUsername());
		newReview.setRating(Integer.parseInt(rating));
		product.getReviews().add(newReview);
		userRepo.save(user);
		productRepo.save(product);
		
	}
	
	public List<Review> getReviews(Long productId) {
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		return product.getReviews();
	}
	
	public List<Product> getProductsByKeyword(String keyword){
		List<Product> result=productRepo.findAll();
		Iterator<Product> iterator=result.iterator();
		while(iterator.hasNext()) {
			Product product=iterator.next();
			if(product.getDescription().toLowerCase().contains(keyword.toLowerCase()) || product.getName().toLowerCase().contains(keyword.toLowerCase())) continue;
			iterator.remove();
		}
		return result;
		
	}
	
	public ProductImage getImage(Long picId) {
		return imageRepo.findById(picId).orElseThrow(()->new ResourceNotFoundException("Image not found!"));
	}
	
	public List<Category> getCategories(){
		return categoryRepo.findAll();
	}
	
	public List<Product> getProductsByCategory(String productCategory){
		Category category=categoryRepo.findByName(productCategory).orElseThrow(()->new ResourceNotFoundException("Category not found!"));
		return category.getProducts();
	}
	
	public List<Product> getProductsByCategoryAndPrice(String sortDirection,String productCategory){
		Category category=categoryRepo.findByName(productCategory).orElseThrow(()->new ResourceNotFoundException("Category not found!"));
		List<Product> products=getProductsSorted(sortDirection);
		List<Product> result=products.stream().filter(product->
			product.getCategory()!=null && product.getCategory().equals(category)).toList();
		return result;
	}
	
	public List<Product> getRecommendedProducts(String recommendSelect){
		if(recommendSelect.equals("Top Rated"))
			return getTopRatedProducts();
		else
			return getNewArrivals();
	}
	
	public List<Product> getTopRatedProducts(){
		Sort sort=Sort.by("rating");
		sort=sort.descending();
		return productRepo.findAll(sort);
	}
	public List<Product> getNewArrivals(){
		Sort sort=Sort.by("createdAt");
		sort=sort.descending();
		return productRepo.findAll(sort);
	}
	
}

