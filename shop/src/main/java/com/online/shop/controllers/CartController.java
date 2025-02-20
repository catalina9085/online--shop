package com.online.shop.controllers;


import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.shop.entities.CartItem;
import com.online.shop.entities.Order;
import com.online.shop.entities.Shipment;
import com.online.shop.services.CartService;

@RestController
@RequestMapping("/user/cart")
public class CartController {
	private CartService cartService;
	
	public CartController(CartService cartService) {
		this.cartService=cartService;
	}
	
	@GetMapping("/getCartItems")
	public ResponseEntity<List<CartItem>> getCartItems(Principal principal){
		return ResponseEntity.ok(cartService.getCartItems(principal));
	}
	
	@PostMapping("/add/{productId}")
	public ResponseEntity<String> addProductToCart(@PathVariable Long productId,Principal principal){
		cartService.addProductToCart(productId,principal);
		return ResponseEntity.ok("The product was added to cart!");
	}
	
	@DeleteMapping("/delete/{productId}")
	public ResponseEntity<String> deleteProductFromCart(Principal principal,@PathVariable Long productId){
		cartService.deleteProductFromCart(principal,productId);
		return ResponseEntity.ok("The product was deleted from the cart!");
	}
	
	@GetMapping("/total")
	public ResponseEntity<Double> getCartTotal(Principal principal){
		return ResponseEntity.ok(cartService.getCartTotal(principal));
	}
	
	@PostMapping("/updateItemQuantity/{itemId}")
	public ResponseEntity<String> updateProductQuantity(Principal principal,@PathVariable Long itemId,@RequestBody Integer newQuantity){
		cartService.updateProductQuantity(principal,itemId,newQuantity.intValue());
		return ResponseEntity.ok("The product quantity is updated!");
	}
	
	@DeleteMapping("/clearCart")
	public ResponseEntity<String> clearCart(Principal principal){
		cartService.clearCart(principal);
		return ResponseEntity.ok("The cart has been cleared!");
	}
	
	@GetMapping("/getShipmentDetails")
	public ResponseEntity<Shipment> getShipmentDetails(Principal principal){
		Shipment shipment=cartService.getShipmentDetails(principal);
		System.out.println("\n\n"+shipment+"\n\n");
		if(shipment!=null)
			return ResponseEntity.ok(shipment);
		else return ResponseEntity.badRequest().build();
	}
	
	/*@PostMapping("/checkout")
	public ResponseEntity<String> checkout(){
		return ResponseEntity.ok("Your order has been successfully placed!");
	}*/
	
	@PostMapping("/placeOrder/{orderId}")
	public ResponseEntity<String> placeOrder(Principal principal,@PathVariable Long orderId) throws Exception{
		String link=cartService.placeOrder(principal,orderId);
		System.out.println("\n\n\n"+link+"\n\n\n");
		return ResponseEntity.ok(link);
	}
	@PostMapping("/addOrder")
	public ResponseEntity<Long> addOrder(Principal principal){
		return ResponseEntity.ok(cartService.addOrder(principal));
	}
	
	@PostMapping("/removeOrder/{orderId}")
	public ResponseEntity<String> removeOrder(Principal principal,@PathVariable Long orderId){
		cartService.removeOrder(principal,orderId);
		return ResponseEntity.ok("Order successfully removed!");
	}
	
	@PostMapping("/removeOrderFromHistory/{orderId}")
	public ResponseEntity<String> removeOrderFromHistory(Principal principal,@PathVariable Long orderId){
		cartService.removeOrderFromHistory(principal,orderId);
		return ResponseEntity.ok("Order successfully removed!");
	}
	
	@GetMapping("/getOrders")
	public ResponseEntity<List<Order>> getOrders(Principal principal){
		try {
			List<Order> orders=cartService.getOrders(principal);
			return ResponseEntity.ok(orders);
		}catch(Exception e) {
			return ResponseEntity.ok(null);
		}
	}
	
	@GetMapping("/getHistoryOrders")
	public ResponseEntity<List<Order>> getHistoryOrders(Principal principal){
		return ResponseEntity.ok(cartService.getHistoryOrders(principal));
	}
	
	@PostMapping("/updateOrderToPaid/{orderId}")
	public ResponseEntity<String> updateOrderToPaid(Principal principal,@PathVariable Long orderId){
		cartService.updateOrderToPaid(orderId,principal);
		return ResponseEntity.ok("Quantity successfully updated!");
	}
	
	@PostMapping("/notifyAboutPendingOrder/{orderId}")
	public void notifyAboutPendingOrder(Principal principal,@PathVariable Long orderId) {
		cartService.notifyAboutPendingOrder(principal,orderId);
	}
	
	@GetMapping("/itemTotalPrice/{itemId}")
	public ResponseEntity<Double> getItemTotalPrice(@PathVariable Long itemId){
		return ResponseEntity.ok(cartService.getItemTotalPrice(itemId));
	}
	
	@GetMapping("/getOrder/{orderId}")
	public ResponseEntity<Order> getOrder(@PathVariable Long orderId){
		return ResponseEntity.ok(cartService.getOrder(orderId));
	}
	
	
		
}

