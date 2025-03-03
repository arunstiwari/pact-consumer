package com.example.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${serviceClients.products.baseUrl}")
    private String baseUrl;

    public ProductResponse fetchProducts(){
        return restTemplate.getForObject(baseUrl+"/products", ProductResponse.class);
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
}
