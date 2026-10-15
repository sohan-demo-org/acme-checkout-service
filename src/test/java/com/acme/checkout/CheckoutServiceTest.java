package com.acme.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {
    private final CheckoutService service = new CheckoutService(new PromoCatalog());

    private static CartRequest cart(String region, String promo, CartRequest.Line... lines) {
        return new CartRequest(List.of(lines), region, promo);
    }

    @Test
    void pricesCartWithTax() {
        CartTotalResponse r = service.price("c1", cart("US-CA", null,
                new CartRequest.Line("SKU-1", new BigDecimal("19.99"), 2)));

        assertThat(r.subtotal()).isEqualByComparingTo("39.98");
        assertThat(r.total()).isEqualByComparingTo("42.88");
        assertThat(r.promoCode()).isNull();
    }

    @Test
    void appliesKnownPromoBeforeTax() {
        CartTotalResponse r = service.price("c2", cart("US-NY", "welcome10",
                new CartRequest.Line("SKU-1", new BigDecimal("100.00"), 1)));

        assertThat(r.promoCode()).isEqualTo("WELCOME10");
        // (100 - 10%) * 1.04
        assertThat(r.total()).isEqualByComparingTo("93.60");
    }

    @Test
    void ignoresUnknownPromo() {
        CartTotalResponse r = service.price("c3", cart("US-OR", "NOPE",
                new CartRequest.Line("SKU-1", new BigDecimal("10.00"), 1)));

        assertThat(r.promoCode()).isNull();
        assertThat(r.total()).isEqualByComparingTo("10.00");
    }

    @Test
    void fixedPromoCannotMakeTotalNegative() {
        CartTotalResponse r = service.price("c4", cart("US-OR", "SAVE5",
                new CartRequest.Line("SKU-1", new BigDecimal("3.00"), 1)));

        assertThat(r.total()).isEqualByComparingTo("0.00");
    }
}
