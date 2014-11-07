/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.shoppingcart;

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
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.IOException;

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
        //handle.execute("create table if not exists reviews (pid integer not null,title varchar(50) not null, rating integer not null, comment varchar(255) not null,primary key (shopper_id, product_id))");

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

    public boolean loadReviews(Handle handle) {
        Document dom;
        // Make an instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the XML file
            dom = db.parse("reviews.xml");

            String pid = null;
            String title = null;
            String rating = null;
            String comment = null;

            Node product, review;
            NodeList products = dom.getFirstChild().getChildNodes();

            for (int i = 0; i < products.getLength(); i++) {
                product = products.item(i);
                if (product.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap nnm = product.getAttributes();
                    pid = nnm.getNamedItem("PID").getNodeValue();
                    Node child = product.getChildNodes().item(1);
                    NodeList reviews = child.getChildNodes();

                    for (int j = 0; j < reviews.getLength(); j++) {
                        review = reviews.item(j);
                        if (review.getNodeType() == Node.ELEMENT_NODE) {
                            NamedNodeMap nnm2 = review.getAttributes();
                            title = nnm2.getNamedItem("Title").getNodeValue();
                            comment = nnm2.getNamedItem("Value").getNodeValue();
                            rating = nnm2.getNamedItem("Value").getNodeValue();
                            System.out.println(pid + title + rating + comment);
                        }
                    }
                }
            }

            return true;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return false;
    }
}
