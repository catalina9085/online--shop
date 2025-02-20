package com.online.shop.components;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PayPalHttpClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${paypal.baseUrl}")
    private String baseUrl;

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.secret}")
    private String secret;

    public PayPalHttpClient(ObjectMapper objectMapper) {
        this.httpClient =HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();;
        this.objectMapper = objectMapper;
    }

    public AccessTokenResponseDTO getAccessToken() throws Exception {
        String url =baseUrl+"/v1/oauth2/token";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)  
                .header("Authorization", encodeBasicCredentials())
                .header("Accept-Language", "en_US")
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String content = response.body();
        return objectMapper.readValue(content, AccessTokenResponseDTO.class);
    }

    private String encodeBasicCredentials() {
        String credentials = clientId + ":" + secret;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    
    public OrderResponseDTO createOrder(OrderDTO orderDTO) throws Exception {
    	AccessTokenResponseDTO accessTokenDTO=getAccessToken();
    	String payload=objectMapper.writeValueAsString(orderDTO);
    	String url =baseUrl+"/v2/checkout/orders";
    	HttpRequest request=HttpRequest.newBuilder()
    			.uri(URI.create(url))
    			.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessTokenDTO.getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String content = response.body();
        return objectMapper.readValue(content,OrderResponseDTO.class);
    }
}
