package com.online.shop.dtos;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductRequest {
	private String name;
	private String description;
	private double price;
	private int stock;
	private List<MultipartFile> files;
	private String newCategory;
	private String productCategory;

}
