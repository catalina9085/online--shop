package com.online.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
import com.online.shop.entities.Review;
import com.online.shop.entities.User;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired 
	private ProductRepository productRepo;
	@Autowired
	private UserRepository userRepo;
	
	@Autowired 
	private ObjectMapper mapper;
	
	private Long id;
	
	@BeforeEach
	public void setup() {
		productRepo.deleteAll();
		Product product1=new Product("item1","testItem1",200,10,LocalDateTime.now());
		Product product2=new Product("item2","testItem2",100,10,LocalDateTime.now());
		productRepo.save(product1);
		productRepo.save(product2);
		id=product1.getId();
		
		User user=new User();
		user.setUsername("user");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
		userRepo.save(user);
		
		
	}
	
	@Test
	@WithMockUser
	public void testGetAllProducts() throws Exception {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/products/getAllProducts"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
						.andReturn();
		String jsonResponse=result.getResponse().getContentAsString();
		List<Product> products=mapper.readValue(jsonResponse, new TypeReference<List<Product>>() {});
		
		assertNotNull(products);
		assertTrue(products.size()==2);
		assertEquals(products.get(0).getName(),"item1");
	}
	
	@Test
	@WithMockUser
	public void testGetSortedProducts() throws Exception {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/products/getSortedProducts/desc"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
						.andReturn();
		String jsonResponse=result.getResponse().getContentAsString();
		List<Product> products=mapper.readValue(jsonResponse, new TypeReference<List<Product>>() {});
		
		assertNotNull(products);
		assertTrue(products.size()==2);
		assertEquals(products.get(0).getName(),"item1");
		assertEquals(products.get(1).getName(),"item2");
		
		result=mockMvc.perform(MockMvcRequestBuilders.get("/user/products/getSortedProducts/cresc"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		jsonResponse=result.getResponse().getContentAsString();
		products=mapper.readValue(jsonResponse, new TypeReference<List<Product>>() {});

		assertNotNull(products);
		assertTrue(products.size()==2);
		assertEquals(products.get(0).getName(),"item2");
		assertEquals(products.get(1).getName(),"item1");
		
	}
	
	@Test
	@WithMockUser
	public void testGetProductById() throws Exception {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/products/getProduct/"+id))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
						.andReturn();
		String jsonResponse=result.getResponse().getContentAsString();
		Product product=mapper.readValue(jsonResponse,Product.class);
		
		assertNotNull(product);
		assertEquals(product.getName(),"item1");
	}
	
	@Test
	@WithMockUser
	public void testAddAndGetReviews() throws Exception {
		Map<String,String> map=Map.of("productId",id.toString(),"review","Good!","ratingValue","4");
		String jsonRequest=mapper.writeValueAsString(map);
		mockMvc.perform(MockMvcRequestBuilders.post("/user/products/addReview")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Review successfully added!"));
		
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/products/getReviews/"+id))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		
		String jsonResponse=result.getResponse().getContentAsString();
		List<Review> reviews=mapper.readValue(jsonResponse, new TypeReference<List<Review>>() {});
		
		assertNotNull(reviews);
		assertTrue(reviews.size()==1);
		assertEquals(reviews.get(0).getRating(),4);
			
	}
	
	
}
