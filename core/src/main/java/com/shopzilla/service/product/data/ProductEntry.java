/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product.data;

import com.google.common.base.Objects;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database model that represents a product.
 * @author Chris McAndrews
 */
public class ProductEntry {

    private Long productId;
    private String productName;
    private Long productRating;

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

    public Long getProductRating() {
        return productRating;
    }

    public void setProductRating(Long productRating) {
        this.productRating = productRating;
    }

    /**
     * Map database rows to the {@link com.shopzilla.service.product.data.ProductEntry} class.
     */
    public static class ProductEntryMapper implements ResultSetMapper<ProductEntry> {
        @Override
        public ProductEntry map(int index, ResultSet rs, StatementContext stx) throws SQLException {
            final ProductEntry toReturn = new ProductEntry();
            toReturn.setProductId(rs.getLong("product_id"));
            toReturn.setProductName(rs.getString("product_name"));
            toReturn.setProductRating(rs.getLong("product_rating"));
            return toReturn;
        }
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

        ProductEntry rhs = (ProductEntry) o;
        return Objects.equal(productId, rhs.productId);
    }


}
