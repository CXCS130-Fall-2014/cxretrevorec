/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc.
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */

package com.shopzilla.service.product;

import com.shopzilla.site.service.product.model.jaxb.ProductEntry;
import com.shopzilla.site.service.product.model.jaxb.ProductResponse;
import com.shopzilla.site.service.product.model.jaxb.ReviewEntry;
import com.shopzilla.site.service.product.model.jaxb.ReviewResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

/**
 * @author Chris McAndrews <cmcandrews@shopzilla.com>
 */
public class HttpProductClient implements ProductClient {
    private final WebResource resource;

    public HttpProductClient(WebResource resource) {
        this.resource = resource;
    }

    @Override
    public ProductResponse getProductEntries(ProductQuery query)
            throws ProductException {

        WebResource request = resource.path(
                String.format("/productId/%d", query.getProductId()));
        try {
            return request.accept(MediaType.APPLICATION_XML_TYPE).get(ProductResponse.class);
        } catch (UniformInterfaceException e) {
            throw new ProductException(e);
        } catch (ClientHandlerException e) {
            throw new ProductException(e);
        }
    }

    @Override
    public ProductResponse createProductEntry(ProductEntry entry)
            throws ProductException {

        WebResource request = resource.path("/create");

        try {
            return request.accept(MediaType.APPLICATION_XML_TYPE).entity(entry)
                    .post(ProductResponse.class);
        } catch (UniformInterfaceException e) {
            throw new ProductException(e);
        } catch (ClientHandlerException e) {
            throw new ProductException(e);
        }
    }
   
    @Override
    public ReviewResponse getReviewEntries(ReviewQuery query) 
            throws ReviewException {

        WebResource request = resource.path(
                String.format("/pid/%d", query.getPid()));
        try {
            return request.accept(MediaType.APPLICATION_XML_TYPE).get(ReviewResponse.class);
        } catch (UniformInterfaceException e) {
            throw new ReviewException(e);
        } catch (ClientHandlerException e) {
            throw new ReviewException(e);
        }
    }
 
/*
    @Override
    public ShoppingCartResponse updateShoppingCartEntry(ShoppingCartEntry entry)
            throws ShoppingCartException {
        WebResource request = resource.path(String.format("/shopperId/%d/productId/%d", entry.getShopperId(), entry.getProductId()));

        try {
            return request.accept(MediaType.APPLICATION_XML_TYPE).entity(entry)
                    .post(ShoppingCartResponse.class);
        } catch (UniformInterfaceException e) {
            throw new ShoppingCartException(e);
        } catch (ClientHandlerException e) {
            throw new ShoppingCartException(e);
        }
    }

    @Override
    public ShoppingCartResponse deleteShoppingCartEntry(ShoppingCartEntry entry)
            throws ShoppingCartException {

        WebResource request = resource.path("/delete");

        try {
            return request.accept(MediaType.APPLICATION_XML_TYPE)
                    .delete(ShoppingCartResponse.class);
        } catch (UniformInterfaceException e) {
            throw new ShoppingCartException(e);
        } catch (ClientHandlerException e) {
            throw new ShoppingCartException(e);
        }
    }
*/
}
