# acme-checkout-service

Spring Boot REST service that prices a shopping cart at checkout. It is the storefront's source of
truth for "what does this cart cost?" and delegates all money math to the shared
[`com.acme:pricing`](https://github.com/sohan-demo-org/acme-pricing-lib) library, so promo and tax
rules stay consistent with the rest of the platform (for example the
[invoicing worker](https://github.com/sohan-demo-org/acme-invoicing-worker)).

```
storefront ──POST /carts/{id}/total──►  acme-checkout-service  ──uses──►  com.acme:pricing
                                        CartController                    PricingEngine
                                        CheckoutService                   DiscountCode / TaxRules
                                        PromoCatalog (promo code → DiscountCode)
```

## API

`POST /carts/{cartId}/total`

```bash
curl -s -X POST localhost:8080/carts/demo/total \
  -H 'Content-Type: application/json' \
  -d '{"items":[{"sku":"SKU-1","unitPrice":19.99,"quantity":2}],"region":"US-CA","promoCode":"WELCOME10"}'
```

```json
{"cartId":"demo","region":"US-CA","promoCode":"WELCOME10","subtotal":39.98,"total":38.59}
```

- `items[]` — sku, unitPrice, quantity (validated with Bean Validation)
- `region` — tax region understood by `TaxRules` (`US-CA`, `US-NY`, `US-TX`, `US-OR`, `GB`, `DE`)
- `promoCode` — optional; resolved by `PromoCatalog` (`WELCOME10` = 10 %, `SAVE5` = $5 off, `HALFOFF` = 50 %); unknown codes are ignored

## Code map

| Class | Responsibility |
|---|---|
| `CartController` | HTTP layer, request validation |
| `CheckoutService` | maps the request to `LineItem`s, calls `PricingEngine.calculateTotal`, builds the response |
| `PromoCatalog` | promo code → `DiscountCode` |
| `CartRequest` / `CartTotalResponse` | JSON contracts |

Tests: unit tests on `CheckoutService` and `PromoCatalog`, `@WebMvcTest` on the controller, and one
`@SpringBootTest` end-to-end test through the HTTP layer.

## Running locally

```bash
# GitHub Packages requires a token even for public packages (classic PAT with read:packages).
export GH_PACKAGES_USER=<github-user> GH_PACKAGES_TOKEN=<token>

mvn verify              # all tests
mvn spring-boot:run     # http://localhost:8080
```

`.mvn/maven.config` points Maven at the project-local `.mvn/settings.xml`, which supplies those
credentials for the `github` repository declared in `pom.xml`. CI (`.github/workflows/ci.yml`) does the
same with the org secret `GH_PACKAGES_TOKEN`.

## Dependency on the pricing library

The version is pinned once, in `pom.xml`:

```xml
<pricing.version>1.0.0</pricing.version>
```

When `acme-pricing-lib` publishes a new release, its release workflow notifies a Devin Automation,
which starts a session for this repo that bumps `pricing.version`, runs `mvn verify`, adapts the code
and tests to any API change, starts the app and exercises the endpoint, and opens a PR here for review.
Nothing is merged automatically.
