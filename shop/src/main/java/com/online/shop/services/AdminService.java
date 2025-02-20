package com.online.shop.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.dtos.AddProductRequest;
import com.online.shop.entities.Category;
import com.online.shop.entities.Order;
import com.online.shop.entities.OrderProduct;
import com.online.shop.entities.Product;
import com.online.shop.entities.ProductImage;
import com.online.shop.entities.Review;
import com.online.shop.entities.User;
import com.online.shop.entities.WishList;
import com.online.shop.repositories.CategoryRepository;
import com.online.shop.repositories.OrderProductRepository;
import com.online.shop.repositories.OrderRepository;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.ReviewRepository;
import com.online.shop.repositories.UserRepository;
import com.online.shop.repositories.WishListRepository;

@Service
public class AdminService {
	
	private ProductRepository productRepo;
	private ReviewRepository reviewRepo;
	private UserRepository userRepo;
	private WishListRepository wishRepo;
	private CloudinaryService cloudinaryService;
	private OrderProductRepository orderProdRepo;
	private OrderRepository orderRepo;
	private NotificationsService notifService;
	
    private CategoryRepository categoryRepo;

	AdminService(ProductRepository productRepo,ReviewRepository reviewRepo,UserRepository userRepo,
			WishListRepository wishRepo,CloudinaryService cloudinaryService,OrderProductRepository orderProdRepo,
			OrderRepository orderRepo,CategoryRepository categoryRepo,NotificationsService notifService) {
		this.productRepo=productRepo;
		this.reviewRepo=reviewRepo;
		this.userRepo=userRepo;
		this.wishRepo=wishRepo;
		this.cloudinaryService=cloudinaryService;
		this.orderProdRepo=orderProdRepo;
		this.orderRepo=orderRepo;
		this.categoryRepo=categoryRepo;
		this.notifService=notifService;
	}
	
	public Category getExistentCategory(String name,Product product) {
		Category category= categoryRepo.findByName(name).orElseThrow(()->new ResourceNotFoundException("Category not found!"));
		category.getProducts().add(product);
		categoryRepo.save(category);
		return category;
	}
	
	public Category createNewCategory(String name,Product product) {
		Optional<Category> cat=categoryRepo.findByName(name);
		if(!cat.isEmpty()) {
			return getExistentCategory(name,product);
		}
		Category category=new Category();
		category.setName(name);
		category.setProducts(Collections.singletonList(product));
		categoryRepo.save(category);
		return category;
	}
	
	
	public void addNewProduct(AddProductRequest request,int savingOption) throws IOException {
		Product product=new Product(request.getName(),request.getDescription(),request.getPrice(),request.getStock(),LocalDateTime.now());
		if(request.getNewCategory()!=null  && !request.getNewCategory().isBlank()) product.setCategory(createNewCategory(request.getNewCategory(),product));
		else product.setCategory(getExistentCategory(request.getProductCategory(),product));
		List<ProductImage> images=new ArrayList<>();
		List<MultipartFile> files=request.getFiles();
		if(files!=null) {
		for(MultipartFile file:files) {
			ProductImage image=new ProductImage();
			image.setSavingOption(savingOption);
			image.setName(file.getOriginalFilename());
			image.setProduct(product);
			if(savingOption==0) {
				List<String> result=cloudinaryService.uploadFile(file,"products");
				String url=result.get(0);
				String publicId=result.get(1);
				if(url!=null) {
					image.setUrl(url);
					image.setPublic_id(publicId);
				}
			}
			else {
				image.setData(file.getBytes());
			}
			images.add(image);
		}
		product.setImages(images);
		productRepo.save(product);
		notifService.notifyAboutNewArrival(product);
	}
	}
	
	public List<Product> getAllProducts(){
		return productRepo.findAll();
	}
	
	public Product getProduct(Long productId) {
		return productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
	}
	
	public void deleteProductFromCategory(Product product) {
		Category category=product.getCategory();
		category.getProducts().remove(product);
		categoryRepo.save(category);
	}
	public void deleteProduct(Long productId) {
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		deleteProductFromCategory(product);
		for(WishList wishlist:product.getWishList()) {
			wishlist.getProducts().remove(product);
			wishRepo.save(wishlist);
		}
		Iterator<ProductImage> iterator=product.getImages().iterator();
		int cnt=0;
		while(iterator.hasNext()) {
			ProductImage img=iterator.next();
			if(cnt>0) {//pastram prima imagine pt a o folosi in istoricul comenzilor
				deleteImage(img.getId(),productId);
			}
			iterator.remove();
			cnt++;
		}
		List<OrderProduct> orderProducts=orderProdRepo.findByProductId(productId);
		Iterator<OrderProduct> it=orderProducts.iterator();
		while(it.hasNext()) {
			it.next().setProductId(null);
		}
		productRepo.deleteById(productId);
	}
	
	public void deleteImage(Long imageId,Long productId) {
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		Iterator<ProductImage> iterator=product.getImages().iterator();
		while(iterator.hasNext()) {
			ProductImage img=iterator.next();
			if(img.getId().equals(imageId)) {
				if(img.getSavingOption()==0) cloudinaryService.deleteFile(img.getPublic_id());
				iterator.remove();
				productRepo.save(product);
				return;
			}
		}
	}

	
	public void editProductInfo(HashMap<String,String> map) {
		System.out.println("\n\n"+map.get("productCategory")+","+map.get("newCategory")+"\n\n");
		Product product=productRepo.findById(Long.parseLong(map.get("productId"))).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		product.setName(map.get("name"));
		product.setDescription(map.get("description"));
		product.setPrice(Double.parseDouble(map.get("price")));
		product.setStock(Integer.parseInt(map.get("stock")));
		product.setDiscount(Integer.parseInt(map.get("discount")));
		if(map.get("newCategory")!=null && !map.get("newCategory").isBlank()) product.setCategory(createNewCategory(map.get("newCategory"),product));
		else product.setCategory(getExistentCategory(map.get("productCategory"),product));
		productRepo.save(product);
		
		if(product.getDiscount()>0) {
			notifService.notifyAboutOffer(product);
		}
	}
	
	public void addNewFiles(Long productId,List<MultipartFile> files,int savingOption) throws IOException {
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		if(product.getImages()==null) product.setImages(new ArrayList<>());
		List<ProductImage> images=product.getImages();
		for(MultipartFile file:files) {
			ProductImage image=new ProductImage();
			image.setName(file.getOriginalFilename());
			image.setProduct(product);
			image.setSavingOption(savingOption);
			if(savingOption==0) {
				List<String> result=cloudinaryService.uploadFile(file,"products");
				String url=result.get(0);
				String publicId=result.get(1);
				if(url!=null) {
					image.setUrl(url);
					image.setPublic_id(publicId);
				}
			}
			else {
				image.setData(file.getBytes());
			}
			images.add(image);
		}
		productRepo.save(product);
	}
	
	public void deleteReview(Long reviewId) {
		Review review=reviewRepo.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("Review not found!"));
		Product product=review.getProduct();
		User user =review.getUser();
		Iterator<Review> iterator=user.getReviews().iterator();
		while(iterator.hasNext()) {
			Review curent=iterator.next();
			if(curent.equals(review)) {
				iterator.remove();
				userRepo.save(user);
				break;
			}
		}
		iterator=product.getReviews().iterator();
		while(iterator.hasNext()) {
			Review curent=iterator.next();
			if(curent.equals(review)) {
				iterator.remove();
				productRepo.save(product);
				return;
			}
		}
	}
	
	public List<User> getAllUsers(){
		return userRepo.findAll();
	}
	
	public void deleteUser(Long userId) {
	    User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    for (Order order : user.getOrders()) {
	        order.getProducts().clear();
	    }

	    user.getOrders().clear();
	    userRepo.save(user);
	    userRepo.deleteById(userId);
	}
	
	public void changeRole(Long userId) {
		User user=userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
		if(user.getRole().equals("ROLE_USER")) {
			user.setRole("ROLE_ADMIN");
		}
		else {
			user.setRole("ROLE_USER");
		}
		userRepo.save(user);
	}
	
	public List<Product> getProductsByKeyword(String keyword){
		List<Product> products=productRepo.findAll();
		return products.stream().filter(product->contains(product.getName(),keyword)|| contains(product.getDescription(),keyword))
				.toList();
	}
	
	public boolean contains(String a,String b) {
		if(a.toLowerCase().contains(b.toLowerCase())) return true;
		return false;
	}
	
	public Map<String,Object> getOrdersInfo() {
		List<Order> orders=orderRepo.findAll();
		Double total=0.0;
		Long paidOrdersCnt=0L;
		Long soldProductsCnt=0L;
		for(Order order:orders) {
			if(order.getStatus().equals("PAID")) {
				paidOrdersCnt++;
				total+=order.getTotal();
				for(OrderProduct orderProduct:order.getProducts())
					soldProductsCnt+=orderProduct.getQuantity();
			}
		}
		
		Map<String,Object> result=Map.of("total",total,
				"soldProductsCnt", soldProductsCnt,
				"pendingOrdersCnt", orders.size()-paidOrdersCnt,
				"paidOrdersCnt",paidOrdersCnt,
				"ordersCnt", orders.size(),
				"bestSellers", getBestSellers(),
				"allOrders", getAllOrders());
		return result;
	}
	
	public List<Product> getBestSellers() {
		Sort sort=Sort.by("sales");
		sort=sort.descending();
		List<Product> products=productRepo.findAll(sort);
		return products.subList(0, 5);
	}
	
	public List<Order> getAllOrders() {
		return orderRepo.findAll();
	}
}
