package com.online.shop.services;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;

@Service
public class NotificationsService {
	private UserRepository userRepo;
	private MailService mailService;
	private SimpMessagingTemplate messagingTemplate;
	
	public NotificationsService(UserRepository userRepo,MailService mailService,SimpMessagingTemplate messagingTemplate) {
		this.userRepo=userRepo;
		this.mailService=mailService;
		this.messagingTemplate=messagingTemplate;
	}
	
	public User getUser(Principal principal) {
		return userRepo.findByUsername(principal.getName()).orElseThrow(()->new ResourceNotFoundException("User not found!"));
	}
	
	public void notifyAboutNewArrival(Product newProduct) {
		List<User> users=userRepo.findAll();
		for(User user:users) {
			if(user.getNotifications()==null) continue;
			if(user.getNotifications().isNewArrivals()) {
				if(user.getNotifications().isViaEmail())
					mailService.sendEmailAboutNewArrivals(newProduct, user);
				if(user.getNotifications().isViaPush())
					messagingTemplate.convertAndSend("/topic/newArrivals","We are excited to announce the arrival of a new product in our store!<br>Check out the latest additions and grab your favorites before they're gone.");
			}
		}
	}
	
	public void notifyAboutPaidOrder(Principal principal,Long orderId) {
		User user=getUser(principal);
		Long orderNumber=999+orderId;
		if(user.getNotifications()!=null && user.getNotifications().isOrderStatus()) {
				if(user.getNotifications().isViaEmail())
					mailService.sendEmailAboutPaidOrder(user,orderNumber);
				if(user.getNotifications().isViaPush())
					messagingTemplate.convertAndSend("/topic/orderStatus/"+user.getId(),"The order with number #"+orderNumber+" has been successfully paid!");
		}
	}
	
	public void notifyAboutPendingOrder(Principal principal,Long orderId) {
		User user=getUser(principal);
		Long orderNumber=999+orderId;
		if(user.getNotifications()!=null && user.getNotifications().isOrderStatus()) {
			if(user.getNotifications().isViaEmail())
				mailService.sendEmailAboutPendingOrder(user,orderNumber);
			if(user.getNotifications().isViaPush())
				messagingTemplate.convertAndSend("/topic/orderStatus/"+user.getId(),"The order with number  #" + orderNumber +  " is pending.<br>It will be automatically deleted after 30 minutes!");
		}
	}
	
	public void notifyAboutOffer(Product product) {
		List<User> users=userRepo.findAll();
		for(User user:users) {
			if(user.getNotifications()!=null && user.getNotifications().isOffers()) {
				if(user.getNotifications().isViaEmail())
					mailService.sendEmailAboutOffer(user,product);
				if(user.getNotifications().isViaPush()) {
					String accessProduct="/user/showProduct/"+product.getId();
					messagingTemplate.convertAndSend("/topic/offers","Great news!This product is now available at a " + product.getDiscount() + "% discount.<br>" +
						    "Check it out <a href='" + accessProduct + "'>here</a>!");
				}
					
			}
		}
	}
	
}
