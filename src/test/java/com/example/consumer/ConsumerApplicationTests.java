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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProductService")
class ConsumerApplicationTests {
    @Autowired
    private ProductServiceClient productServiceClient;

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact allProducts(PactDslWithProvider builder) {
        return builder
                .given("product_exists")
                .uponReceiving("get all products")
                .path("/products")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .minArrayLike("products", 1, 2)
                                .integerType("id", 9L)
                                .stringType("name", "Test Product 1")
                                .stringMatcher("type", "CREDIT_CARD")
                                .closeObject()
                                .closeArray()
                ).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V3)
    void testAllProducts(MockServer mockServer){
        productServiceClient.setBaseUrl(mockServer.getUrl());
        List<Product> products = productServiceClient.fetchProducts().getProducts();
        assertThat(products, hasSize(2));
        assertThat(products.get(0).getName(), is(equalTo("Test Product 1")));
    }
}
