package com.online.shop;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;
import com.online.shop.services.MailService;



@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MailIntegrationTest {
	
	@Autowired 
	private MockMvc mockMvc;
	
	@MockBean
	private JavaMailSender mailSender;
	
	@Autowired
	private MailService mailService;
	
	@Autowired 
	private UserRepository userRepo;
	
	@Test
	public void testSendEmail() {
		mailService.sendEmail("testUser@gmai.com","catalina_ionela7@yahoo.com","test", "This is a test email!");
		
		SimpleMailMessage expectedMessage=new SimpleMailMessage();
		expectedMessage.setFrom("catalina_ionela7@yahoo.com");
		expectedMessage.setSubject("test");
		expectedMessage.setTo("testUser@gmai.com");
		expectedMessage.setText("This is a test email!");
		
		verify(mailSender,times(1)).send(expectedMessage);
	}
	
	@Test
	@Transactional
	public void testSendVerificationCode() {
		User user=new User();
		user.setUsername("user");
		user.setEmail("testUser@gmail.com");
		userRepo.save(user);
		
		mailService.sendVerificationCode(user.getEmail());
		mailService.verifyUser(userRepo.findByUsername("user").get().getVerificationCode(), user.getEmail());
		
	}
	
}
