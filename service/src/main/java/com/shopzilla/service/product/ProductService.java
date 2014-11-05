/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import com.google.common.collect.Lists;
import com.shopzilla.service.product.data.ProductDao;
import com.shopzilla.service.product.resource.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.metrics.util.DeadlockHealthCheck;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 * Main entry point for product service.
 */
public class ProductService extends Service<ProductServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new ProductService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProductServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(ProductServiceConfiguration configuration, Environment environment)
            throws Exception {

        /*
         * database setup
         */
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment,
                configuration.getDatabaseConfiguration(),
                "h2");
        Handle handle = jdbi.open();
        handle.execute("create table if not exists product_entry (product_id integer not null,product_name varchar(255) not null,product_rating integer not null,primary key (product_id))");

        /*
         * "real" database DAO
         */
        final ProductDao productDao = jdbi.onDemand(ProductDao.class);

        /*
        * health checks
         */
        environment.addHealthCheck(new DeadlockHealthCheck());

        /*
         * Dozer mapping
         */
        Mapper mapper = new DozerBeanMapper(Lists.newArrayList("dozerMappings.xml"));

        /*
        * endpoints
         */
        environment.addResource(new ProductResource(productDao, mapper));
        environment.addResource(new IndexResource());
    }

}
