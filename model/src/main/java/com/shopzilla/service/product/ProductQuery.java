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
    private String productName;
    private String productCategory;
    private Long productUPC;
    private String productDescription;
    private Long productPrice;

    private ProductQuery() {
        // use the builder
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Long getProductUPC() {
        return productUPC;
    }

    public void setProductUPC(Long productUPC) {
        this.productUPC = productUPC;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
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

        public Builder productName(String productName) {
            q.productName = productName;
            return this;
        }
        
        public Builder productCategory(String productCategory) {
            q.productCategory = productCategory;
            return this;
        }

        public Builder productDescription(String productDescription) {
            q.productDescription = productDescription;
            return this;
        }
        public ProductQuery build() {
            return q;
        }
    }
}
