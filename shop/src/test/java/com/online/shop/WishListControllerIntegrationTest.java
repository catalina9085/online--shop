package com.online.shop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WishListControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long productId;
	
	@BeforeEach
	public void setup() {
		userRepo.deleteAll();
		User user=new User();
		user.setEmail("user@gmail.com");
		user.setUsername("user");
		user.setPassword("password");
		userRepo.save(user);
		
		productRepo.deleteAll();
		Product product=new Product("product1","description1",100,10,LocalDateTime.now());
		productRepo.save(product);
		productId=product.getId();
	}
	
	@Test
	@WithMockUser
	public void testAddingDeletingAndGettingProducts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/wishList/add/"+productId))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Product successfully added!"));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/user/wishList/existsById/"+productId))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Found"));
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/wishList/delete/"+productId))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Product successfully removed!"));
		
		
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/wishList/getProducts"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		
		String jsonResult=result.getResponse().getContentAsString();
		List<Product> products=mapper.readValue(jsonResult,new TypeReference<List<Product>>() {});
		
		assertTrue(products!=null && products.size()==0);
		
		
	}
}
