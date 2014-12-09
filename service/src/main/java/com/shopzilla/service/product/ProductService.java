/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import java.io.IOException;
import java.io.RandomAccessFile;
import com.google.common.collect.Lists;
import com.shopzilla.service.product.data.ProductDao;
import com.shopzilla.service.product.data.ReviewDao;
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
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.IOException;

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
        // This is so I can change my table
        loadData(handle);

        /*
         * "real" database DAO
         */
        final ProductDao productDao = jdbi.onDemand(ProductDao.class);
        final ReviewDao reviewDao = jdbi.onDemand(ReviewDao.class);

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
        environment.addResource(new ReviewResource(reviewDao, mapper));
        environment.addResource(new IndexResource());
    }

    //Helper function to import product data
    String fieldFromLine(String xmlLine) {
        int start = xmlLine.indexOf(">")+1;
        int end = xmlLine.indexOf("<", start);
        return xmlLine.substring(start, end);
    }

    public boolean loadData(Handle handle) throws IOException{
        // max title length in the dataset is 150
        // max comment length in the dataset is 1952
        // title and comments need single quotes escaped

        //Create the two tables, product_entry and reviews
        handle.execute("DROP ALL OBJECTS");
        handle.execute("CREATE TABLE IF NOT EXISTS product_entry (" +
                "product_id long NOT NULL," +
                "product_category varchar(255) NOT NULL," +
                "product_name varchar(255) NOT NULL," +
                "PRIMARY KEY (product_id))");
        handle.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                "rid LONG NOT NULL," +
                "pid LONG NOT NULL," +
                "title VARCHAR(250) NOT NULL," +
                "rating INTEGER," +
                "comment VARCHAR(3000) NOT NULL," +
                "PRIMARY KEY (rid))");

        //Import all product data from dataset2.xml
    	RandomAccessFile raf = new RandomAccessFile("products.xml", "r");
    	try {
            for (String s = raf.readLine(); s != null; s=raf.readLine()) {
                if (s.equals("<doc>")) {
                    long pid = Long.parseLong(fieldFromLine(raf.readLine()));
                    String cat = fieldFromLine(raf.readLine()).replaceAll("'", "''");
                    String name = fieldFromLine(raf.readLine()).replaceAll("'", "''");
                    cat = cat.replace("\\", "/");
                    name = name.replace("\\", "/");
                    handle.execute("MERGE INTO product_entry (product_id, product_category, product_name) KEY(product_id) VALUES (" + pid + ", \'" + cat + "\', \'" + name + "\')");
                }
            }
        } finally {
            raf.close();
        }

        //Import review data from dataset.xml
        Document dom;
        // Make an instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the XML file
            dom = db.parse("reviews.xml");

            long rid = 0;
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
                            rating = nnm2.getNamedItem("Rating").getNodeValue();

                            // single quotes are escaped by single quotes in h2
                            title = title.replace("'", "''");
                            comment = comment.replace("'", "''");
                            // backslashs cause problems in h2
                            title = title.replace("\\", "/");
                            comment = comment.replace("\\", "/");
                            // some ratings are empty
                            rating = (rating.isEmpty()) ? "NULL" : rating;

                            String output = pid + title + rating + comment;
                            handle.execute("MERGE INTO reviews (rid, pid, title, rating, comment) KEY(rid) VALUES (" + rid + "," + pid + ",'" + title + "'," + rating + ",'" + comment + "')");
                            rid++;
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
