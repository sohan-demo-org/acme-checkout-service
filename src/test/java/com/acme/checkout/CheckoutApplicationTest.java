package com.acme.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CheckoutApplicationTest {
    @Autowired
    private TestRestTemplate rest;

    @Test
    void endToEndCartTotal() {
        CartRequest body = new CartRequest(
                List.of(new CartRequest.Line("SKU-9", new BigDecimal("25.00"), 4)), "GB", null);

        ResponseEntity<CartTotalResponse> resp = rest.postForEntity("/carts/e2e/total", body, CartTotalResponse.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().subtotal()).isEqualByComparingTo("100.00");
        assertThat(resp.getBody().total()).isEqualByComparingTo("120.00");
    }
}
