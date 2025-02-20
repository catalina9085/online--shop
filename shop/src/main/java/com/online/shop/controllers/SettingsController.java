package com.online.shop.controllers;

import java.security.Principal;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.shop.entities.Notifications;
import com.online.shop.entities.Shipment;
import com.online.shop.services.SettingsService;

@RestController
@RequestMapping("/user/settings")
public class SettingsController {
	
	private SettingsService settingsService;
	
	public SettingsController(SettingsService settingsService) {
		this.settingsService=settingsService;
	}
	
	@PostMapping("/shipment")
	public ResponseEntity<String> addShipment(@RequestBody Shipment shipment,Principal principal) {
		try {
			settingsService.addShipment(shipment,principal);
			return ResponseEntity.ok("Billing information successfully added");
		}catch(RuntimeException e) {
			System.out.println("eroare");
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteAccount")
	public ResponseEntity<String> deleteAccount(Principal principal){
		settingsService.deleteAccount(principal);
		return ResponseEntity.ok("Your account has been successfully deleted!");
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(Principal principal,@RequestBody HashMap<String ,String> map) {
		try {
			settingsService.changePassword(principal,map.get("currentPassword"),map.get("newPassword"),map.get("confirmPassword"));
			return ResponseEntity.ok("Your password has been successfully changed!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/setNotificationsPreferences")
	public ResponseEntity<String> setNotificationsPreferences(@RequestBody HashMap<String,Boolean> map,Principal principal){
		settingsService.setNotificationsPreferences(map,principal);
		return ResponseEntity.ok("Preferences successfully saved!");
	}
	
	@GetMapping("/getNotificationsPreferences")
	public ResponseEntity<Notifications> getNotificationsPreferences(Principal principal){
		Notifications notif=settingsService.getNotificationsPreferences(principal);
		System.out.println("\n\n\n"+notif.isViaPush()+"\n\n\n");
		return ResponseEntity.ok(notif);
	}
	
	@PostMapping("/sendVerificationCode")
	public ResponseEntity<String> sendVerificationCode(Principal principal){
		try {
			settingsService.sendVerificationCode(principal);
			return ResponseEntity.ok("Verification code sent!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/verifyCode/{code}")
	public ResponseEntity<String> verifyCode(@PathVariable String code ,Principal principal){
		try {
			settingsService.verifyCode(code,principal);
			return ResponseEntity.ok("Your account is now verified!");
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}
