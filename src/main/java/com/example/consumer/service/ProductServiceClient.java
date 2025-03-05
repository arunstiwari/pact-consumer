package com.example.consumer.service;

import com.example.consumer.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    public Product fetchProductById(Long id){
        return restTemplate.getForObject(baseUrl+"/products/"+id, Product.class);
    }

    public Product createProduct(Product product){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(product, headers);
        ResponseEntity<Product> response = restTemplate.postForEntity(baseUrl + "/product", request, Product.class);
        return response.getBody();
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
}
