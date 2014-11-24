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
 * Database model that represents a category.
 * @author Chris McAndrews
 */
public class CategoryEntry {

    private String productCategory;

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    /**
     * Map database rows to the {@link com.shopzilla.service.product.data.CategoryEntry} class.
     */
    public static class CategoryEntryMapper implements ResultSetMapper<CategoryEntry> {
        @Override
        public CategoryEntry map(int index, ResultSet rs, StatementContext stx) throws SQLException {
            final CategoryEntry toReturn = new CategoryEntry();
            toReturn.setProductCategory(rs.getString("product_category"));
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

        CategoryEntry rhs = (CategoryEntry) o;
        return Objects.equal(productCategory, rhs.productCategory);
    }


}
