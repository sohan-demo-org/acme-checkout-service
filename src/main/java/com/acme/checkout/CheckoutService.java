package com.acme.checkout;

import com.acme.pricing.DiscountCode;
import com.acme.pricing.LineItem;
import com.acme.pricing.PricingEngine;
import com.acme.pricing.TaxRules;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** Prices a cart using the shared pricing library. */
@Service
public class CheckoutService {
    private final PricingEngine engine = new PricingEngine();
    private final PromoCatalog promos;

    public CheckoutService(PromoCatalog promos) {
        this.promos = promos;
    }

    public CartTotalResponse price(String cartId, CartRequest request) {
        List<LineItem> items = request.items().stream()
                .map(l -> new LineItem(l.sku(), l.unitPrice(), l.quantity()))
                .toList();
        DiscountCode discount = promos.find(request.promoCode()).orElse(null);
        BigDecimal taxRate = TaxRules.rateFor(request.region());

        BigDecimal subtotal = engine.subtotal(items);
        BigDecimal total = engine.calculateTotal(items, taxRate, discount);

        return new CartTotalResponse(cartId, request.region(),
                discount == null ? null : discount.code(), subtotal, total);
    }
}
