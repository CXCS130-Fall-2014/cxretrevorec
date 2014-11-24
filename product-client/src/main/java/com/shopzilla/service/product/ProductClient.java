package com.shopzilla.service.product;

import com.shopzilla.site.service.product.model.jaxb.ProductEntry;
import com.shopzilla.site.service.product.model.jaxb.ProductResponse;
import com.shopzilla.site.service.product.model.jaxb.ReviewEntry;
import com.shopzilla.site.service.product.model.jaxb.ReviewResponse;
import com.shopzilla.site.service.product.model.jaxb.CategoryEntry;
import com.shopzilla.site.service.product.model.jaxb.CategoryResponse;

/**
 * @author Chris McAndrews <cmcandrews@shopzilla.com>
 */
public interface ProductClient {
    ProductResponse getProductEntries(ProductQuery query) throws ProductException;
    CategoryResponse getCategoryList(CategoryQuery query) throws CategoryException;
    ProductResponse createProductEntry(ProductEntry entry) throws ProductException;
    ReviewResponse getReviewEntries(ReviewQuery query) throws ReviewException;
/*
    ShoppingCartResponse updateShoppingCartEntry(ShoppingCartEntry entry) throws ShoppingCartException;
    ShoppingCartResponse deleteShoppingCartEntry(ShoppingCartEntry entry) throws ShoppingCartException;
*/
}
