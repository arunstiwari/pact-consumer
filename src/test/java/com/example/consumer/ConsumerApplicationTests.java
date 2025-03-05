package com.example.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.consumer.model.Product;
import com.example.consumer.service.ProductServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProductService")
class ConsumerApplicationTests {
    @Autowired
    private ProductServiceClient productServiceClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact allProducts(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
                .given("product_exists")
                .uponReceiving("get all products")
                .path("/products")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(
                        new PactDslJsonBody()
                                .minArrayLike("products", 1, 1)
                                .integerType("id", 9L)
                                .stringType("name", "Test Product 1")
                                .stringMatcher("type", "CREDIT_CARD")
                                .closeObject()
                                .closeArray()
                ).toPact();
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact productById(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
                .given("product_exists_by_id")
                .uponReceiving("get products by id")
                .path("/products/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(
                        new PactDslJsonBody()
                                .integerType("id", 1L)
                                .stringType("name", "Test Product 1")
                                .stringMatcher("type", "CREDIT_CARD")
                ).toPact();
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact createPactForPostProduct(PactDslWithProvider builder) {
        return builder
                .given("create_new_product")
                .uponReceiving("A request to create a product")
                .method("POST")
                .path("/product")
                .headers("Content-Type", "application/json")
                .body(new PactDslJsonBody()
                        .integerType("id", 0)
                        .stringType("name", "Test Product 2")
                        .stringType("type", "CREDIT_CARD")
                        .stringType("version", "2.0")
                        .stringType("code", "SP200")
                )
                .willRespondWith()
                .status(201)
                .body(new PactDslJsonBody()
                        .integerType("id", 100)
                        .stringType("name", "Test Product 2")
                        .stringType("type", "CREDIT_CARD")
                        .stringType("version", "2.0")
                        .stringType("code", "SP200")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V1)
    void testAllProducts(MockServer mockServer){
        productServiceClient.setBaseUrl(mockServer.getUrl());
        List<Product> products = productServiceClient.fetchProducts().getProducts();
        assertThat(products, hasSize(1));
        assertThat(products.get(0).getName(), is(equalTo("Test Product 1")));
    }

    @Test
    @PactTestFor(pactMethod = "productById", pactVersion = PactSpecVersion.V1)
    void testProductsWithId(MockServer mockServer){
        productServiceClient.setBaseUrl(mockServer.getUrl());
        Product product = productServiceClient.fetchProductById(1L);
        assertThat(product.getName(), is(equalTo("Test Product 1")));
    }

    @Test
    @PactTestFor(pactMethod = "createPactForPostProduct", pactVersion = PactSpecVersion.V1)
    void testCreateProduct(MockServer mockServer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Product productRequest = new Product(0L, "Test Product 2", "CREDIT_CARD", "2.0", "SP200");

        HttpEntity<Product> request = new HttpEntity<>(productRequest, headers);
        ResponseEntity<Product> response = restTemplate.postForEntity(mockServer.getUrl() + "/product", request, Product.class);

        assertThat(response.getStatusCode(),is(equalTo(HttpStatus.CREATED)));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getId(),is(equalTo(100L)));
        assertThat(response.getBody().getName(),is(equalTo("Test Product 2")));
        assertThat(response.getBody().getType(),is(equalTo("CREDIT_CARD")));
        assertThat(response.getBody().getVersion(),is(equalTo("2.0")));
        assertThat(response.getBody().getCode(), is(equalTo("SP200")));
    }
}
