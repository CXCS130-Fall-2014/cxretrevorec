/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product.resource;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.shopzilla.service.product.Format;
import com.shopzilla.service.product.data.ProductDao;
import com.shopzilla.service.product.ProductQuery;
import com.shopzilla.site.service.product.model.jaxb.ProductEntry;
import com.shopzilla.site.service.product.model.jaxb.ProductResponse;
import com.yammer.metrics.annotation.Timed;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Controller for handling CRUD operations for a product.
 * @author Chris McAndrews
 */
@Path("/product")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
public class ProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    private ProductDao dao;
    private Mapper mapper;

    public ProductResource(ProductDao dao, Mapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Timed(name = "getProduct")
    @GET
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("productName/{productName}")
    public Response get(@PathParam("productName") String productName,
                        @QueryParam("format") Format format) {

        if (productName == null) {
            LOG.debug("A valid product name must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        ProductResponse response = new ProductResponse();
        ProductQuery query = ProductQuery.builder().productName(productName).build();
        List<com.shopzilla.service.product.data.ProductEntry> daoResults =
                dao.getProductEntries(query);
        for (com.shopzilla.service.product.data.ProductEntry product : daoResults) {
            response.getProductEntry().add(mapper.map(product, ProductEntry.class));
        }
        return buildResponse(response, format);
    }

    @Timed(name = "getProductByCategory")
    @GET
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("productCategory/{productCategory}")
    public Response getCategory(@PathParam("productCategory") String productCategory,
                        @QueryParam("format") Format format) {

        if (productCategory == null) {
            LOG.debug("A valid product category must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        ProductResponse response = new ProductResponse();
        ProductQuery query = ProductQuery.builder().productCategory(productCategory).build();
        List<com.shopzilla.service.product.data.ProductEntry> daoResults =
                dao.getProductByCategory(query);
        for (com.shopzilla.service.product.data.ProductEntry product : daoResults) {
            response.getProductEntry().add(mapper.map(product, ProductEntry.class));
        }
        return buildResponse(response, format);
    }


    @Timed(name = "createProduct")
    @POST
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("create")
    public Response create(@Valid ProductEntry product,
                           @QueryParam("format") Format format) {

        if (product == null || product.getProductId() == null) {

            LOG.debug("A valid product id must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        // Verify that the entry doesn't already exist
        ProductQuery query = ProductQuery.builder()
                .productName(product.getProductName())
                .build();
        List<com.shopzilla.service.product.data.ProductEntry> entries =
                dao.getProductEntries(query);
        if (CollectionUtils.isNotEmpty(entries)) {
            LOG.debug("A product entry with the given product id already exists");
            return Response.status(Response.Status.CONFLICT).build();
        }

        ProductResponse response = new ProductResponse();
        dao.createProductEntry(mapper.map(product, com.shopzilla.service.product.data.ProductEntry.class));
        return buildResponse(response, format);
    }
    /*
    @Timed(name = "updateShoppingCart")
    @POST
    @JSONP
    @Path("shopperId/{shopperId}/productId/{productId}")
    public Response update(@PathParam("shopperId") Long shopperId,
                       @PathParam("productId") Long productId,
                       @QueryParam("productName") String productName,
                       @QueryParam("productCost") Long productCost,
                       @QueryParam("format") Format format) {

        if (shopperId == null || productId == null) {
            LOG.debug("A valid shopper id and product id must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        // Check to make sure that the entry exists
        ShoppingCartQuery query = ShoppingCartQuery.builder()
                .shopperId(shopperId)
                .productId(productId)
                .build();
        List<com.shopzilla.service.shoppingcart.data.ShoppingCartEntry> shoppingCarts =
                dao.getShoppingCartEntries(query);
        if (CollectionUtils.isEmpty(shoppingCarts)) {
            LOG.debug("Could not find an existing shopping cart entry to update.");
            return Response.status(Response.Status.CONFLICT).build();
        }

        com.shopzilla.service.shoppingcart.data.ShoppingCartEntry shoppingCart = shoppingCarts.get(0);
        shoppingCart.setProductName(productName);
        shoppingCart.setProductCost(productCost);

        dao.updateShoppingCartEntry(shoppingCart);
        return buildResponse(new ShoppingCartResponse(), format);
    }

    @Timed(name = "deleteShoppingCart")
    @DELETE
    @JSONP
    @Path("shopperId/{shopperId}/productId/{productId}")
    public Response delete(@PathParam("shopperId") Long shopperId,
                       @PathParam("productId") Long productId,
                       @QueryParam("format") Format format) {

        if (shopperId == null || productId == null) {
            LOG.debug("A valid shopper id and product id must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        ShoppingCartQuery shoppingCartQuery = ShoppingCartQuery.builder()
                .shopperId(shopperId)
                .productId(productId)
                .build();
        dao.deleteShoppingCartEntry(shoppingCartQuery);
        return buildResponse(new ShoppingCartResponse(), format);
    }
    */
    private Response buildResponse(Object response, Format format) {
        return Response.ok(response)
                .type(format != null ? format.getMediaType() : Format.xml.getMediaType())
                .build();
    }
}
