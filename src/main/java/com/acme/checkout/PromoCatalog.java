package com.acme.checkout;

import com.acme.pricing.DiscountCode;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** In-memory promo codes. A real service would load these from a database. */
@Component
public class PromoCatalog {
    private final Map<String, DiscountCode> codes = Map.of(
            "WELCOME10", DiscountCode.percent("WELCOME10", BigDecimal.TEN),
            "SAVE5", DiscountCode.fixed("SAVE5", new BigDecimal("5.00")),
            "HALFOFF", DiscountCode.percent("HALFOFF", BigDecimal.valueOf(50)));

    public Optional<DiscountCode> find(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(codes.get(code.trim().toUpperCase(Locale.ROOT)));
    }
}
