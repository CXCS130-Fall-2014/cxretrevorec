/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import com.google.common.base.Objects;

/**
 * Query parameters for the shopping cart service.
 * @author Chris McAndrews
 */
public class ProductQuery {

    private Long productId;

    private ProductQuery() {
        // use the builder
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        ProductQuery rhs = (ProductQuery) o;
        return Objects.equal(productId, rhs.productId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProductQuery.class)
                .add("productId", productId)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ProductQuery q = new ProductQuery();

        public Builder productId(Long productId) {
            q.productId = productId;
            return this;
        }

        public ProductQuery build() {
            return q;
        }
    }
}
