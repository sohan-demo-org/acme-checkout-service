package com.acme.checkout;

import java.math.BigDecimal;

/** What the API returns for a priced cart. */
public record CartTotalResponse(
        String cartId,
        String region,
        String promoCode,
        BigDecimal subtotal,
        BigDecimal total) {
}
