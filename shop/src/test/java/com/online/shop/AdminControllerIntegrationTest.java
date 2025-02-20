package com.online.shop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import com.online.shop.entities.User;
import com.online.shop.repositories.UserRepository;
import com.online.shop.services.CloudinaryService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepo;

	@MockBean
    private CloudinaryService cloudinaryService;
    
	@Test
	@WithMockUser(roles= {"ADMIN"})
	public void testAddNewProduct() throws Exception {
		
		when(cloudinaryService.uploadFile(any(MultipartFile.class),eq("products"))).thenReturn(List.of("https://fakeurl.com/fake-image-url", "fake-public-id"));
		
		MockMultipartFile file=new MockMultipartFile(
				"files","testImage.jpg","image/jpeg","the file content".getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/addNewProduct/1")
				.file(file)
				.param("name", "Iphone")
		        .param("description", "16")
		        .param("price", "6899")
		        .param("stock", "10")
		        .param("newCategory", "Electronics")
		        .contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Product successfully added!"));	
		
		verify(cloudinaryService,times(0)).uploadFile(any(MultipartFile.class),eq("products"));
	}
	
	
	@Test
	@WithMockUser(roles={"ADMIN"})
	public void testChangeUserRoleAndDeleteUser() throws Exception {
		User user=new User();
		user.setUsername("testUser");
		user.setEmail("testUser@gmail.com");
		user.setPassword("testUser");
		user.setRole("ROLE_USER");
		userRepo.save(user);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/changeUserRole/"+user.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Role successfully changed!"));	
		
		User updatedUser=userRepo.findByUsername("testUser").get();
		assertTrue(updatedUser.getRole().equals("ROLE_ADMIN"));
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/admin/deleteUser/"+user.getId()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("User successfully deleted!"));
		
		assertFalse(userRepo.existsByUsername("testUser"));
		
	}
	
}
