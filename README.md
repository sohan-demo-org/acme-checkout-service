# acme-checkout-service

Spring Boot API that prices shopping carts with the shared
[`com.acme:pricing`](https://github.com/sohan-demo-org/acme-pricing-lib) library.

```bash
# GitHub Packages needs a token even for public packages
export GH_PACKAGES_USER=<github-user> GH_PACKAGES_TOKEN=<token with read:packages>

mvn verify                       # unit + API + Spring Boot tests
mvn spring-boot:run              # http://localhost:8080
```

```bash
curl -s -X POST localhost:8080/carts/demo/total \
  -H 'Content-Type: application/json' \
  -d '{"items":[{"sku":"SKU-1","unitPrice":19.99,"quantity":2}],"region":"US-CA","promoCode":"WELCOME10"}'
# {"cartId":"demo","region":"US-CA","promoCode":"WELCOME10","subtotal":39.98,"total":38.59}
```

## Upgrading the pricing library

The version is pinned by `<pricing.version>` in `pom.xml`. New releases of the
library trigger a Devin session that bumps it, runs the test suite, fixes any
breakage and opens a PR here.
