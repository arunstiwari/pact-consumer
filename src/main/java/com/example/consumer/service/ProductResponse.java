package com.example.consumer.service;

import com.example.consumer.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private final List<Product> products;
}
