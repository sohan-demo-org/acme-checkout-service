package com.acme.checkout;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@Import({CheckoutService.class, PromoCatalog.class})
class CartControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void returnsSubtotalAndTotal() throws Exception {
        mvc.perform(post("/carts/abc/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"sku":"SKU-1","unitPrice":19.99,"quantity":2}],
                                 "region":"US-CA","promoCode":"WELCOME10"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("abc"))
                .andExpect(jsonPath("$.promoCode").value("WELCOME10"))
                .andExpect(jsonPath("$.subtotal").value(closeTo(39.98, 0.001)))
                .andExpect(jsonPath("$.total").value(closeTo(38.59, 0.001)));
    }

    @Test
    void rejectsEmptyCart() throws Exception {
        mvc.perform(post("/carts/abc/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[],\"region\":\"US-CA\"}"))
                .andExpect(status().isBadRequest());
    }
}
