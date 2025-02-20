package com.online.shop.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPaymentRequest {
	private String firstname;
	private String lastname;
	private String country;
	private String province;
	private String town;
	private String addressLine1;
	private String addressLine2;
	private String postalCode;
	private String phoneNumber;
}
