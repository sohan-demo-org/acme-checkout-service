package com.acme.checkout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/** Incoming cart: lines, the customer's tax region and an optional promo code. */
public record CartRequest(
        @NotEmpty @Valid List<Line> items,
        @NotBlank String region,
        String promoCode) {

    public record Line(
            @NotBlank String sku,
            @NotNull BigDecimal unitPrice,
            @Min(1) int quantity) {
    }
}
