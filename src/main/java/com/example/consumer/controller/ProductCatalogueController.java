package com.example.consumer.controller;

import com.example.consumer.model.Product;
import com.example.consumer.model.ProductCatalogue;
import com.example.consumer.service.ProductServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductCatalogueController {

    @Autowired
    private ProductServiceClient productService;

    @GetMapping("/catalogue")
    public List<Product> catalogue(){
        ProductCatalogue catalogue = new ProductCatalogue("default", productService.fetchProducts().getProducts());
        return catalogue.getProducts();
    }

}
