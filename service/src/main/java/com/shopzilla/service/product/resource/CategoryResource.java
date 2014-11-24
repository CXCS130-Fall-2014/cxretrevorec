/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product.resource;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.shopzilla.service.product.Format;
import com.shopzilla.service.product.data.CategoryDao;
import com.shopzilla.service.product.CategoryQuery;
import com.shopzilla.site.service.product.model.jaxb.CategoryEntry;
import com.shopzilla.site.service.product.model.jaxb.CategoryResponse;
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
@Path("/category")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
public class CategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryResource.class);

    private CategoryDao dao;
    private Mapper mapper;

    public CategoryResource(CategoryDao dao, Mapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }
    @Timed(name = "getCategory")
    @GET
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("categoryList")
    public Response getCategory(@QueryParam("format") Format format) {
        CategoryResponse response = new CategoryResponse();
        CategoryQuery query = CategoryQuery.builder().build();
        List<com.shopzilla.service.product.data.CategoryEntry> daoResults =
                dao.getCategoryList(query);
        for (com.shopzilla.service.product.data.CategoryEntry product : daoResults) {
            response.getCategoryEntry().add(mapper.map(product, CategoryEntry.class));
        }
        return buildResponse(response, format);
    }

    private Response buildResponse(Object response, Format format) {
        return Response.ok(response)
                .type(format != null ? format.getMediaType() : Format.xml.getMediaType())
                .build();
    }
}
