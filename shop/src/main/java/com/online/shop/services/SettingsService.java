package com.online.shop.services;

import java.security.Principal;
import java.util.HashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.Notifications;
import com.online.shop.entities.Shipment;
import com.online.shop.entities.User;
import com.online.shop.repositories.ShipmentRepo;
import com.online.shop.repositories.UserRepository;

@Service
public class SettingsService {
	private UserRepository userRepo;
	private ShipmentRepo shipmentRepo;
	private PasswordEncoder encoder;
	private MailService mailService;
	
	public SettingsService(UserRepository userRepo,ShipmentRepo shipmentRepo,PasswordEncoder encoder,MailService mailService) {
		this.userRepo=userRepo;
		this.shipmentRepo=shipmentRepo;
		this.encoder=encoder;
		this.mailService=mailService;
	}
	
	public User getUser(Principal principal) {
		return userRepo.findByUsername(principal.getName()).orElseThrow(()->new ResourceNotFoundException("User not found!"));
	}
	
	public void addShipment(Shipment shipment,Principal principal) {
		User user=getUser(principal);
		if(user.getShipment()==null) {
			shipment.setUser(user);
			user.setShipment(shipment);
		}
		else {
			Shipment current=user.getShipment();
			current.setAddressLine1(shipment.getAddressLine1());
	        current.setAddressLine2(shipment.getAddressLine2());
	        current.setCountry(shipment.getCountry());
	        current.setFirstName(shipment.getFirstName());
	        current.setLastName(shipment.getLastName());
	        current.setPhoneNumber(shipment.getPhoneNumber());
	        current.setPostalCode(shipment.getPostalCode());
	        current.setProvince(shipment.getProvince());
	        current.setTown(shipment.getTown());
	        shipmentRepo.delete(shipment);
		}
		userRepo.save(user);
	}
	
	public void deleteAccount(Principal principal) {
		User user=getUser(principal);
		userRepo.delete(user);
	}
	
	public void changePassword(Principal principal,String currentPassword,String newPassword,String confirmPassword) {
		User user=getUser(principal);
		if(!encoder.matches(currentPassword, user.getPassword()))
			throw new RuntimeException("Your current password is incorrect!");
		if(!newPassword.equals(confirmPassword))
			throw new RuntimeException("New password and confirm password do not match!");
		user.setPassword(encoder.encode(newPassword));
		userRepo.save(user);
	}
	
	public void setNotificationsPreferences(HashMap<String,Boolean> map,Principal principal) {
		User user=getUser(principal);
		if(user.getNotifications()==null)  user.setNotifications(new Notifications());
		Notifications notif=user.getNotifications();
		notif.setNewArrivals(map.get("newArrivals"));
		notif.setOrderStatus(map.get("orderStatus"));
		notif.setOffers(map.get("offers"));
		notif.setViaEmail(map.get("viaEmail"));
		notif.setViaPush(map.get("viaPush"));
		notif.setUser(user);
		
		userRepo.save(user);
	}
	
	public Notifications getNotificationsPreferences(Principal principal) {
		User user=getUser(principal);
		return user.getNotifications();
	}
	
	public void saveFcmToken(Principal principal,String fcmToken) {
		User user=getUser(principal);
		user.setFcmToken(fcmToken);
		userRepo.save(user);
	}
	
	public void sendVerificationCode(Principal principal) {
		User user=getUser(principal);
		mailService.sendVerificationCode(user.getEmail());
	}
	
	public void verifyCode(String code,Principal principal) {
		User user=getUser(principal);
		mailService.verifyUser(code, user.getEmail());
	}
}
