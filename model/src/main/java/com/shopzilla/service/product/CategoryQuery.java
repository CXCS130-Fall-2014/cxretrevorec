/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import com.google.common.base.Objects;

/**
 * Query parameters for the category service.
 * @author Chris McAndrews
 */
public class CategoryQuery {

    private String productCategory;

    private CategoryQuery() {
        // use the builder
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
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

        CategoryQuery rhs = (CategoryQuery) o;
        return Objects.equal(productCategory, rhs.productCategory);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(CategoryQuery.class)
                .add("productCategory", productCategory)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CategoryQuery q = new CategoryQuery();

        public CategoryQuery build() {
            return q;
        }
    }
}
