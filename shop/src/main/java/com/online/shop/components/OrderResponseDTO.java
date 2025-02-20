package com.online.shop.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrderResponseDTO {

    private String id;
    private String status;
    private List<Link> links;

    @Getter
    @Setter
    public static class Link {

        private String href;
        private String rel;
        private String method;
    }
    
}
