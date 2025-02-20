package com.online.shop.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.online.shop.configurations.ResourceNotFoundException;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;

@Service
public class MailService {
	
	private JavaMailSender mailSender;
	private UserRepository userRepository;
	
	public MailService(JavaMailSender mailSender,UserRepository userRepository) {
		this.mailSender=mailSender;
		this.userRepository=userRepository;
	}
	
	public void sendEmail(String to,String from,String subject,String body) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setFrom(from);
		message.setText(body);
	
		mailSender.send(message);
	}
	
	public void sendVerificationCode(String email) {
		User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found!"));
		String code=generateVerificationCode();
		user.setVerificationCode(code);
		user.setCodeExpiration(LocalDateTime.now().plusMinutes(30));
		userRepository.save(user);
		
		sendEmail(email,"catalina_ionela7@yahoo.com","Verification Code","This is your verification code: "+code);
	}
	
	public void verifyUser(String code,String email) {
		User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found!"));
		if(!user.getVerificationCode().equals(code))
			throw new RuntimeException("Invalid token");
		
		if(LocalDateTime.now().isAfter(user.getCodeExpiration()))
			throw new RuntimeException("Expired token!");
		
		user.setVerified(true);
		userRepository.save(user);
	}
	
	
	public String generateVerificationCode() {
		return UUID.randomUUID().toString().substring(0,6);//cod de 6 caractere
	}
	
	public void sendEmailAboutNewArrivals(Product newProduct,User user) {
		String body="Dear " + user.getUsername() + ",\n\n"
		        + "We are excited to announce the arrival of a new product in our store! Check out the latest additions and grab your favorites before they're gone.\n\n"+
        "Best regards,\n" +
        "The Online Shop Team";

		sendEmail(user.getEmail(),"catalina_ionela7@yahoo.com","New Arrival",body);
		
	}
	
	public void sendEmailAboutPaidOrder(User user,Long orderNumber) {
		String body=
			        "Dear " + user.getUsername() + ",\n\n" +
			        "We are happy to inform you that your order #" + orderNumber + " has been successfully paid.\n" +
			        "Thank you for shopping with us!\n\n"+
			        "Best regards,\n" +
			        "The Online Shop Team"
			    ;
		
		 sendEmail(user.getEmail(),"catalina_ionela7@yahoo.com","Order Status",body);
	}
	
	public void sendEmailAboutPendingOrder(User user,Long orderNumber) {
			String body=
				"Dear " + user.getUsername() + ",\n\n" +
				"We are informing you that your order #" + orderNumber + " is pending.It will be automatically deleted after 30 minutes!\n" +
				"Best regards,\n" +
				"The Online Shop Team";
		
			sendEmail(user.getEmail(),"catalina_ionela7@yahoo.com","Order Status",body);
	}
	
	public void sendEmailAboutOffer(User user,Product product) {
		String accessProduct="http://localhost:8080/user/showProduct/"+product.getId();
		String body=
			"Dear " + user.getUsername() + ",\n\n" +
			"Great news!This product is now available at a " + product.getDiscount() + "% discount.\n" +
			"Check it out here: " +accessProduct+"\n"+
			"Best regards,\n" +
			"The Online Shop Team";
	
		sendEmail(user.getEmail(),"catalina_ionela7@yahoo.com","Special Offer",body);
	}
	
}
