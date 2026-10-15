package com.acme.checkout;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CheckoutService checkout;

    public CartController(CheckoutService checkout) {
        this.checkout = checkout;
    }

    @PostMapping("/{cartId}/total")
    public CartTotalResponse total(@PathVariable String cartId, @Valid @RequestBody CartRequest request) {
        return checkout.price(cartId, request);
    }
}
