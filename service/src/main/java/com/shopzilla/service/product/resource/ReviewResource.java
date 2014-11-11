/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product.resource;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.shopzilla.service.product.Format;
import com.shopzilla.service.product.data.ReviewDao;
import com.shopzilla.service.product.ReviewQuery;
import com.shopzilla.site.service.product.model.jaxb.ReviewEntry;
import com.shopzilla.site.service.product.model.jaxb.ReviewResponse;
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
@Path("/review")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
public class ReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewResource.class);

    private ReviewDao dao;
    private Mapper mapper;

    public ReviewResource(ReviewDao dao, Mapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Timed(name = "getReview")
    @GET
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("pid/{pid}")
    public Response get(@PathParam("pid") Long pid,
                        @QueryParam("format") Format format) {

        if (pid == null) {
            LOG.debug("A valid product id must be provided");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        ReviewResponse response = new ReviewResponse();
        ReviewQuery query = ReviewQuery.builder().pid(pid).build();
        List<com.shopzilla.service.product.data.ReviewEntry> daoResults =
                dao.getReviewEntries(query);
        for (com.shopzilla.service.product.data.ReviewEntry review : daoResults) {
            response.getReviewEntry().add(mapper.map(review, ReviewEntry.class));
        }
        return buildResponse(response, format);
    }

    private Response buildResponse(Object response, Format format) {
        return Response.ok(response)
                .type(format != null ? format.getMediaType() : Format.xml.getMediaType())
                .build();
    }
}
