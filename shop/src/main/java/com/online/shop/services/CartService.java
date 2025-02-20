package com.online.shop.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.online.shop.components.Amount;
import com.online.shop.components.OrderDTO;
import com.online.shop.components.OrderResponseDTO;
import com.online.shop.components.OrderResponseDTO.Link;
import com.online.shop.components.PayPalAppContextDTO;
import com.online.shop.components.PayPalHttpClient;
import com.online.shop.components.PurchaseUnit;
import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.CartItem;
import com.online.shop.entities.Order;
import com.online.shop.entities.OrderProduct;
import com.online.shop.entities.Product;
import com.online.shop.entities.Shipment;
import com.online.shop.entities.ShoppingCart;
import com.online.shop.entities.User;
import com.online.shop.repositories.CartItemRepo;
import com.online.shop.repositories.CartRepository;
import com.online.shop.repositories.OrderRepository;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class CartService {
	private ProductRepository productRepo;
	private CartRepository cartRepo;
	private UserRepository userRepo;
	private CartItemRepo cartItemRepo;
	private PayPalHttpClient client;
	private OrderRepository orderRepo;
	private NotificationsService notifService;
	
	public CartService(ProductRepository productRepo,CartRepository cartRepo,UserRepository userRepo,CartItemRepo cartItemRepo,PayPalHttpClient client,OrderRepository orderRepo,NotificationsService notifService) {
		this.productRepo=productRepo;
		this.cartRepo=cartRepo;
		this.userRepo=userRepo;
		this.cartItemRepo=cartItemRepo;
		this.client=client;
		this.orderRepo=orderRepo;
		this.notifService=notifService;
	}
	
	public User getUser(Principal principal) {
		return userRepo.findByUsername(principal.getName()).orElseThrow(()->new ResourceNotFoundException("User not found!"));
	}
	
	public void addProductToCart(Long productId,Principal principal) {
		User user=getUser(principal);
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		//daca produsul este deja in cos,crestem cantitatea
		for(CartItem item:user.getCart().getItems()) {
			if(item.getProduct().equals(product)) {
				item.setQuantity(item.getQuantity()+1);
				userRepo.save(user);
				return;
			}
		}
		//daca nu este in cos,il adaugam
		user.getCart().getItems().add(new CartItem(product,1,user.getCart()));
		userRepo.save(user);
	}
	
	public void deleteProductFromCart(Principal principal,Long productId) {
		Product product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
		ShoppingCart cart=cartRepo.findByUser(getUser(principal)).get();
		Iterator<CartItem> iterator=cart.getItems().iterator();
		while(iterator.hasNext()) {
			CartItem item=iterator.next();
			if(item.getProduct().equals(product)) {
				iterator.remove();
				break;
			}
		}
		userRepo.save(getUser(principal));
		
	}
	
	public List<CartItem> getCartItems(Principal principal) {
		List<CartItem> list=getUser(principal).getCart().getItems();
		return list;
	}
	
	public Double getCartTotal(Principal principal) {
		ShoppingCart cart=cartRepo.findByUser(getUser(principal)).get();
		double rez=0;
		for(CartItem item: cart.getItems()) {
			Product product=item.getProduct();
			double price=product.getFinalPrice();
			rez += price* item.getQuantity();
		}
		if(rez==0) return 0.0;
		if(rez<300) rez+=15;
		return Double.valueOf(rez);
	}
	
	public void updateProductQuantity(Principal principal,Long itemId,int quantity) {
		CartItem item=cartItemRepo.findById(itemId).orElseThrow(()->new ResourceNotFoundException("Item not found!"));
		if(quantity>=1 && quantity<=item.getProduct().getStock()) {
			item.setQuantity(quantity);
			userRepo.save(getUser(principal));
		}
	}
	
	public void clearCart(Principal principal) {
		User user=getUser(principal);
		ShoppingCart cart=cartRepo.findByUser(user).get();
		cart.getItems().clear();
		userRepo.save(user);
	}
	
	public Shipment getShipmentDetails(Principal principal) {
		User user=getUser(principal);
		return user.getShipment();
	}
	
	public String placeOrder(Principal principal,Long orderId) throws Exception {
		clearCart(principal);
		Order order=orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
		increaseProductsSales(order.getProducts());
		Amount amount=new Amount("EUR",String.format("%.2f",order.getTotal()*0.201));
		PurchaseUnit unit=new PurchaseUnit(amount);
		
		PayPalAppContextDTO context=new PayPalAppContextDTO("OnlineShop","BILLING","https://online--shop-485507e5e9cf.herokuapp.com/user/cart/successfulPayment/"+orderId+"/","https://online--shop-485507e5e9cf.herokuapp.com/user/cart/canceledPayment/"+orderId+"/");
	
		OrderDTO orderDTO=new OrderDTO("CAPTURE",Collections.singletonList(unit),context);
		OrderResponseDTO response=client.createOrder(orderDTO);
		System.out.println("\n\n\nResponse:"+response.toString());
		List<Link> links=response.getLinks();
		for(Link link:links) {
			if("approve".equals(link.getRel())) {
				return link.getHref();
			}
		}
		return "NotFound";
	}
	public void increaseProductsSales(List<OrderProduct> orderProducts) {
		for(OrderProduct orderProduct:orderProducts) {
			Product product=productRepo.findById(orderProduct.getProductId()).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
			if(product.getSales()==null) product.setSales(0L);
			product.setSales(product.getSales()+1);
			productRepo.save(product);
		}
	}
	public void decreaseProductsSales(List<OrderProduct> orderProducts) {
		for(OrderProduct orderProduct:orderProducts) {
			Product product=productRepo.findById(orderProduct.getProductId()).orElseThrow(()->new ResourceNotFoundException("Product not found!"));
			if(product.getSales()==null) {
				product.setSales(0L);
				return;
			}
			product.setSales(product.getSales()-1);
			productRepo.save(product);
		}
	}
	public Long addOrder(Principal principal) {
		User user=getUser(principal);
		ShoppingCart cart=user.getCart();
		Order order=new Order(user,getCartTotal(principal),"PENDING",LocalDateTime.now());
		List<OrderProduct> list=new ArrayList<>();
		for(CartItem item:cart.getItems()) {
			Product product=item.getProduct();
			OrderProduct ordProd=new OrderProduct(product.getName(),product.getPrice(),item.getQuantity(),product.getId(),order);
			if(product.getImages()!=null && product.getImages().size()>0) {
				ordProd.setSavingOption(product.getImages().get(0).getSavingOption());
				ordProd.setName(product.getImages().get(0).getName());
				if(ordProd.getSavingOption()==0) {
					ordProd.setPublic_id(product.getImages().get(0).getPublic_id());
					ordProd.setUrl(product.getImages().get(0).getUrl());
				}
				else {
					ordProd.setData(product.getImages().get(0).getData());
				}
			}
			list.add(ordProd);
		}
		order.setProducts(list);
		if(user.getOrders()==null) user.setOrders(Arrays.asList(order));
		else user.getOrders().add(order);
		user.getOrderHistory().add(order);
		userRepo.save(user);
		Long id=user.getOrders().get(user.getOrders().size()-1).getId();  //id-ul order-ului creat
		updateProductsQuantity(id);
		return id;
	}
	
	//comanda anulata
	public void removeOrder(Principal principal,Long orderId) {
		Order order=orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
		User user=order.getUser();
		if(order.getStatus().equals("PENDING")) {
			for(OrderProduct orderProd:order.getProducts()) {
    			Product product=productRepo.findById(orderProd.getProductId()).get();
    			product.setStock(product.getStock()+orderProd.getQuantity());
    			productRepo.save(product);
    		}
		}
		order.getProducts().clear();
		decreaseProductsSales(order.getProducts());
		user.getOrderHistory().remove(order);
		user.getOrders().remove(order);
		userRepo.save(user);
	}
	
	public void removeOrderFromHistory(Principal principal,Long orderId) {
		Order order=orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
		User user=order.getUser();
		user.getOrderHistory().remove(order);
		userRepo.save(user);
	}
	
	
	
	public List<Order> getOrders(Principal principal){
		User user=getUser(principal);
		return user.getOrders();
	}
	
	//cand comanda este plasata,cantitatea produselor cumparate trebuie scazuta
	public void updateProductsQuantity(Long orderId) {
		Order order=orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
		User user=order.getUser();
		
		for(OrderProduct orderProd:order.getProducts()) {
			Product product=productRepo.findById(orderProd.getProductId()).get();
			product.setStock(product.getStock()-orderProd.getQuantity());
			productRepo.save(product);
		}
		userRepo.save(user);
	}
	
	public void updateOrderToPaid(Long orderId,Principal principal) {
		Order order=orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
		User user=order.getUser();
		order.setStatus("PAID");
		userRepo.save(user);
		notifService.notifyAboutPaidOrder(principal, orderId);
		
	}
	
	//din 30 in 30 de min
	@Scheduled(fixedRate = 1800000) 
	@Transactional
	public void cleanupPendingOrders() {
	    List<Order> pendingOrders = orderRepo.findByStatus("PENDING");
	    for (Order order : pendingOrders) {
	        if (order.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(30))) {
	        	for(OrderProduct orderProd:order.getProducts()) {
	    			Product product=productRepo.findById(orderProd.getProductId()).get();
	    			product.setStock(product.getStock()+orderProd.getQuantity());
	    			productRepo.save(product);
	    		}
	            User user = order.getUser();
	            if (user != null) {
	                user.getOrders().remove(order);
	                userRepo.save(user);
	            }
	         }
	    }
	}
	
	public void notifyAboutPendingOrder(Principal principal,Long orderId) {
		notifService.notifyAboutPendingOrder(principal,orderId);
	}
	
	public double getItemTotalPrice(Long itemId) {
		CartItem item=cartItemRepo.findById(itemId).orElseThrow(()->new ResourceNotFoundException("Item not found!"));
		return item.getQuantity()*item.getProduct().getFinalPrice();
	}
	
	
	public List<Order> getHistoryOrders(Principal principal){
		User user=getUser(principal);
		return user.getOrderHistory();
	}
	
	public Order getOrder(Long orderId) {
		return orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found!"));
	}
	
}