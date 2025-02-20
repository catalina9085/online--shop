package com.online.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
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
import com.online.shop.entities.CartItem;
import com.online.shop.entities.Order;
import com.online.shop.entities.Product;
import com.online.shop.entities.User;
import com.online.shop.repositories.ProductRepository;
import com.online.shop.repositories.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired 
	private UserRepository userRepo;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long productId;
	private Long itemId;
	
	@BeforeEach
	public void setup() {
		productRepo.deleteAll();
		Product product1=new Product("item1","testItem1",200,10,LocalDateTime.now());
		Product product2=new Product("item2","testItem2",100,10,LocalDateTime.now());
		productRepo.save(product1);
		productRepo.save(product2);
		productId=product1.getId();
		
		userRepo.deleteAll();
		User user=new User();
		user.setUsername("user");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
		
		CartItem item=new CartItem(product2,2,user.getCart());
		user.getCart().getItems().add(item);
		userRepo.save(user);
		itemId=user.getCart().getItems().get(0).getId();
		
	}
	
	@Test
	@WithMockUser
	public void testAddingGettingAndDeletingCartItems() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/add/"+productId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("The product was added to cart!"));
		
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/cart/getCartItems"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
		.andReturn();
		
		String jsonResult=result.getResponse().getContentAsString();
		List<CartItem> items=mapper.readValue(jsonResult, new TypeReference<List<CartItem>>() {});
		
		assertNotNull(items);
		assertEquals(items.size(),2);
		assertEquals(items.get(1).getProduct().getName(),"item1");
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/cart/delete/"+productId))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("The product was deleted from the cart!"));
		
		result=mockMvc.perform(MockMvcRequestBuilders.get("/user/cart/getCartItems"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		
		jsonResult=result.getResponse().getContentAsString();
		items=mapper.readValue(jsonResult, new TypeReference<List<CartItem>>() {});
		
		assertNotNull(items);
		assertEquals(items.size(),1);
	}
	
	@Test
	@WithMockUser
	public void checkCartContent() throws Exception {
		Integer newQuantity=5;
		String requestJson=mapper.writeValueAsString(newQuantity);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/updateItemQuantity/"+itemId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("The product quantity is updated!"));
		
		
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.get("/user/cart/total"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
		.andReturn();
		
		String resultJson=result.getResponse().getContentAsString();
		Double total=mapper.readValue(resultJson, Double.class);
		
		assertEquals(total,Double.valueOf(500));
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/user/cart/clearCart"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("The cart has been cleared!"));
		
		assertEquals(userRepo.findByUsername("user").get().getCart().getItems().size(),0);
	}
	
	@Test
	@WithMockUser
	public void testAddingAndDeletingOrders() throws Exception {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/addOrder"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		
		String resultJson=result.getResponse().getContentAsString();
		Long orderId=mapper.readValue(resultJson, Long.class);
		
		List<Order> orders=userRepo.findByUsername("user").get().getOrders();
		assertTrue(orders.get(0).getId().equals(orderId));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/removeOrder/"+orderId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("Order successfully removed!"));
		
		orders=userRepo.findByUsername("user").get().getOrders();
		
		assertNotNull(orders);
		assertEquals(orders.size(),0);
		
		
	}
	
	@Test
	@WithMockUser
	public void testPlaceOrder() throws Exception {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/addOrder"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		
		String resultJson=result.getResponse().getContentAsString();
		Long orderId=mapper.readValue(resultJson, Long.class);
		
		result=mockMvc.perform(MockMvcRequestBuilders.post("/user/cart/placeOrder/"+orderId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		String link=result.getResponse().getContentAsString();
		//String link=mapper.readValue(resultJson, String.class);
		assertTrue(isValidURL(link));
		
	}
	private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
	
}
