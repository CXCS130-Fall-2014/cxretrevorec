package com.shopzilla.service.product;

import com.shopzilla.site.service.product.model.jaxb.ProductEntry;
import com.shopzilla.site.service.product.model.jaxb.ProductResponse;

/**
 * @author Chris McAndrews <cmcandrews@shopzilla.com>
 */
public interface ProductClient {
    ProductResponse getProductEntries(ProductQuery query) throws ProductException;
    ProductResponse createProductEntry(ProductEntry entry) throws ProductException;
/*
    ShoppingCartResponse updateShoppingCartEntry(ShoppingCartEntry entry) throws ShoppingCartException;
    ShoppingCartResponse deleteShoppingCartEntry(ShoppingCartEntry entry) throws ShoppingCartException;
*/
}
