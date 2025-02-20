package com.online.shop;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.shop.entities.Notifications;
import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SettingsControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired 
	private UserRepository userRepo;
	
	@Autowired 
	private PasswordEncoder encoder;
	
	@Autowired
	private ObjectMapper mapper;
	
	@BeforeEach
	public void setup() {
		userRepo.deleteAll();
		User user=new User();
		user.setUsername("user");
		user.setPassword(encoder.encode("password"));
		userRepo.save(user);
	}
	
	@Test
	@WithMockUser
	public void testChangePassword() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/settings/changePassword")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"currentPassword\":\"password\",\"newPassword\":\"password2\",\"confirmPassword\":\"password2\"}"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Your password has been successfully changed!"));
		
		User user=userRepo.findByUsername("user").get();
		assertTrue(encoder.matches("password2",user.getPassword()));
	}
	
	@Test
	@WithMockUser
	public void testDeleteAccount() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/settings/deleteAccount"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Your account has been successfully deleted!"));
		
		assertFalse(userRepo.existsByUsername("user"));
	}
	
	@Test
	@WithMockUser
	public void testSetAndGetNotificationsPreferences() throws Exception {
		Notifications notif=new Notifications();
		notif.setViaEmail(true);
		notif.setOffers(true);
		
		String jsonRequest=mapper.writeValueAsString(notif);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/user/settings/setNotificationsPreferences")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Preferences successfully saved!"));
		
		
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/settings/getNotificationsPreferences"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
		.andReturn();
		
		String jsonResult=result.getResponse().getContentAsString();
		Notifications notifResult=mapper.readValue(jsonResult, Notifications.class);
		assertTrue(notifResult.isOffers() && notifResult.isViaEmail() && !notifResult.isNewArrivals());
		
	}
	
	
	
}
