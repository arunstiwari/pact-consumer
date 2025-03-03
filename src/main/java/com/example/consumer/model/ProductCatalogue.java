package com.example.consumer.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductCatalogue {
    private final String name;
    private final List<Product> products;
}
