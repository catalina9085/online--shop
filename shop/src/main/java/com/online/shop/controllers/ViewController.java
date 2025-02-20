package com.online.shop.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class ViewController {
	
	@GetMapping("/auth/register")
	public String register() {
		return "auth/register";
	}
	
	@GetMapping("/auth/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model) {
		if(error!=null) {
			model.addAttribute("error", error);
			System.out.println("Error parameter received: " + error);
		}
		return "auth/login";
	}
	
	@GetMapping("/")
	public String principal(){
		return "auth/login";
	}
	
	@GetMapping("/auth/verify")
	public String verify() {
		return "auth/verify";
	}
	
	@GetMapping("/auth/resetPassword")
	public String resetPassword() {
		return "auth/resetPassword";
	}
	
	@GetMapping("/user/main")
	public String main(Model model) throws FileNotFoundException, IOException {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("role",currentUser.getAuthorities().iterator().next().getAuthority());
		return "user/main";
	}
	
	@GetMapping("/user/cart")
	public String shoppingCart() {
		return "user/cart";
	}
	
	@GetMapping("/user/cart/checkout/{orderId}")
	public String checkout() {
		return "user/checkout";
	}
	
	@GetMapping("/user/cart/canceledPayment/{orderId}/")
	public String canceledPayment() {
		return "user/canceledPayment";
	}
	
	@GetMapping("/user/cart/successfulPayment/{orderId}/")
	public String successfulPayment() {
		return "user/successfulPayment";
	}
	
	@GetMapping("/user/wishList")
	public String wishList() {
		return "user/wishList";
	}

	@GetMapping("/user/showProduct/{productId}")
	public String showProduct() {
		return "user/product";
	}
	
	
	@GetMapping("/user/settings/changePassword")
	public String changePassword() {
		return "settings/changePassword";
	}
	
	@GetMapping("/user/settings/deleteAccount")
	public String deleteAccount() {
		return "settings/deleteAccount";
	}
	
	@GetMapping("/user/settings/shipment")
	public String shipment() {
		return "settings/shipment";
	}
	
	@GetMapping("/user/settings/notifications")
	public String notifications() {
		return "settings/notifications";
	}
	
	@GetMapping("/user/picture/{picId}/{productId}")
	public 	String getPicture() {
		return "user/picture";
	}
	
	@GetMapping("/admin/products")
	public String products() {
		return "admin/products";
	}
	
	@GetMapping("/admin/editProduct/{productId}")
	public String editProduct() {
		return "admin/editProduct";
	}
	
	@GetMapping("/admin/users")
	public String users() {
		return "admin/users";
	}

	
	@GetMapping("/admin/addProduct")
	public String addProduct() {
		return "admin/addProduct";
	}
	
	@GetMapping("/admin/overview")
	public String overview() {
		return "admin/overview";
	}
	
	@GetMapping("/user/myAccount")
	public String myAccount() {
		return "user/myAccount";
	}
	@GetMapping("/user/allOrders")
	public String allOrders() {
		return "user/orders";
	}
	
	
	@GetMapping("/user/verify")
	public String verifyUser() {
		return "user/verify";
	}
	
	
}

