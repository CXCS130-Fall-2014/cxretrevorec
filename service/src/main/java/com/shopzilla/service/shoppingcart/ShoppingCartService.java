/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.shoppingcart;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.google.common.collect.Lists;
import com.shopzilla.service.shoppingcart.data.ShoppingCartDao;
import com.shopzilla.service.shoppingcart.resource.*;
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
 * Main entry point for shopping cart service.
 */
public class ShoppingCartService extends Service<ShoppingCartServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new ShoppingCartService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ShoppingCartServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(ShoppingCartServiceConfiguration configuration, Environment environment)
            throws Exception {

        /*
         * database setup
         */
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment,
                configuration.getDatabaseConfiguration(),
                "h2");
        Handle handle = jdbi.open();
        handle.execute("create table if not exists shopping_cart_entry (shopper_id integer not null,product_id integer not null,product_name varchar(255) not null,product_cost integer not null,primary key (shopper_id, product_id))");
        
        /*
         * importing dataset2.xml
         */
        importProductData(handle);

        /*
         * "real" database DAO
         */
        final ShoppingCartDao shoppingCartDao = jdbi.onDemand(ShoppingCartDao.class);

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
        environment.addResource(new ShoppingCartResource(shoppingCartDao, mapper));
        environment.addResource(new ConfigurationResource(configuration));
        environment.addResource(new IndexResource());
        environment.addResource(new VersionResource());
    }
    
    void importProductData(Handle handle) throws IOException {
    	handle.execute("create table if not exists products (product_id bigint not null,product_category varchar(255) not null,product_name varchar(255) not null,primary key (product_id))");
    	handle.execute("delete from products");
    	System.out.println("The directory is " + new java.io.File( "." ).getCanonicalPath());
    	RandomAccessFile raf = new RandomAccessFile("products.xml", "r");
    	try {
    		for (String s = raf.readLine(); s != null; s=raf.readLine()) {
				if (s.equals("<doc>")) {
					long pid = Long.parseLong(fieldFromLine(raf.readLine()));
					String cat = fieldFromLine(raf.readLine()).replaceAll("'", "''");
					String name = fieldFromLine(raf.readLine()).replaceAll("'", "''");
					cat = 	cat.replace("\\", "/");
					name = 	name.replace("\\", "/");
					
					handle.execute("insert into products values (" + pid + ", \'" + cat + "\', \'" + name + "\')");
				}
    		}
    		System.out.println("Product data loaded successfully");
    	} finally {
    		raf.close();
    	}
    }
    
    String fieldFromLine(String xmlLine) {
		int start = xmlLine.indexOf(">")+1;
		int end = xmlLine.indexOf("<", start);
		return xmlLine.substring(start, end);
	}

}
